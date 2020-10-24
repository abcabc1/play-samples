package utils.poi;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import interfaces.ExcelErrorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import services.base.EasyExcelDataService;

import javax.validation.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class EasyExcelDataListener<T> extends AnalysisEventListener<T> {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private LinkedList<T> validList = new LinkedList<>();
    private LinkedList<ExcelErrorData<T>> invalidList = new LinkedList<>();
    private Integer approximateTotalNum = 0;
    private Integer limitNum = 20000;
    private Integer headNum = 1;
    final private Integer batchNum = 100;

    EasyExcelDataService<T> easyExcelDataService;
    String processFlag = "1";

    public EasyExcelDataListener() {
        this(null);
    }

    public EasyExcelDataListener(EasyExcelDataService<T> easyExcelDataService) {
        this(easyExcelDataService, null, null);
    }

    public EasyExcelDataListener(EasyExcelDataService<T> easyExcelDataService, Integer headNum, Integer limitNum) {
        this.easyExcelDataService = easyExcelDataService;
        if (headNum != null) {
            this.headNum = headNum;
        }
        if (limitNum != null) {
            this.limitNum = limitNum;
        }
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        logger.debug("解析到第" + context.readRowHolder().getRowIndex() + "条数据:{}", Json.prettyPrint(Json.toJson(data)));
        if (!easyExcelDataService.getProcessing()) {
            logger.info("手动取消, 终止数据处理。");
            return;
        }
        ReadSheetHolder readSheetHolder = context.readSheetHolder();
        //总行数
        if (approximateTotalNum == null || approximateTotalNum == 0) {
            approximateTotalNum = readSheetHolder.getApproximateTotalRowNumber();
            easyExcelDataService.setApproximateTotalNum(approximateTotalNum);
            logger.debug("解析到大约{}条数据", approximateTotalNum);
        }
        if (approximateTotalNum <= headNum || approximateTotalNum > limitNum + headNum) {
            easyExcelDataService.setProcessing(false);
            logger.info("数据条数超出范围(1, " + limitNum + "), 终止数据处理。");
            return;
        }
        boolean hasError = false;
        Map<String, String> headNameMap = new HashMap<>();
        if (headNameMap.size() == 0) {
            Map<Integer, Head> map = context.currentReadHolder().excelReadHeadProperty().getHeadMap();
            for (Map.Entry<Integer, Head> entry : map.entrySet()) {
                headNameMap.put(entry.getValue().getFieldName(), entry.getValue().getHeadNameList().get(0));
            }
        }
        Validator validator = VALIDATOR_FACTORY.getValidator();
        Set<ConstraintViolation<T>> result = validator.validate(data);
        ExcelErrorData<T> excelErrorData = new ExcelErrorData<>(data, context.readRowHolder().getRowIndex());
        if (result.size() != 0) {
            hasError = true;
            for (ConstraintViolation<T> constraintViolation : result) {
                Path propertyPath = constraintViolation.getPropertyPath();
                Object invalidValue = constraintViolation.getPropertyPath();
                String message = constraintViolation.getMessage();
                excelErrorData.addError(headNameMap.get((propertyPath == null) ? "" : propertyPath.toString())
                        + " "
                        + ((invalidValue == null) ? "" : invalidValue.toString())
                        + " "
                        + ((message == null) ? "" : message)
                );
            }
        }
        if (!hasError && easyExcelDataService.validHandler() != null) {
            String validResult = easyExcelDataService.validHandler().apply(data);
            if (!"".equals(validResult)) {
                hasError = true;
                excelErrorData.addError(validResult);
            }
        }
        if (!hasError && easyExcelDataService.uniqueHandler() != null) {
            Boolean uniqueResult = easyExcelDataService.uniqueHandler().apply(data);
            if (!uniqueResult) {
                hasError = true;
                excelErrorData.addError("Duplicated Row Data");
            }
        }
        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        if (hasError) {
            invalidList.add(excelErrorData);
            easyExcelDataService.setInvalidNum();
        } else {
            validList.add(data);
            easyExcelDataService.setValidNum();
        }
        easyExcelDataService.setTotalNum();
        // 达到batchNum了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (invalidList.size() == batchNum) {
            logger.debug("{}条无效数据，开始持久化！", invalidList.size());
            if (easyExcelDataService.persistenceInvalidHandler() != null) {
                easyExcelDataService.persistenceInvalidHandler().accept(invalidList);
                invalidList.clear();
            }
        }
        // 达到batchNum了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (validList.size() == batchNum) {
            logger.debug("{}条数据，开始持久化！", validList.size());
            if (easyExcelDataService.persistenceValidHandler() != null) {
                easyExcelDataService.persistenceValidHandler().accept(validList);
                validList.clear();
            }
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        logger.debug("{}条无效数据，开始持久化！", validList.size());
        if (invalidList.size() > 0 && easyExcelDataService.persistenceInvalidHandler() != null) {
            easyExcelDataService.persistenceInvalidHandler().accept(invalidList);
            invalidList.clear();
        }
        logger.debug("{}条数据，开始持久化！", validList.size());
        if (validList.size() > 0 && easyExcelDataService.persistenceValidHandler() != null) {
            easyExcelDataService.persistenceValidHandler().accept(validList);
            validList.clear();
        }
    }
}

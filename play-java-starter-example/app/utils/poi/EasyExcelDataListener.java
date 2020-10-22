package utils.poi;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import interfaces.ExcelDataListenerInterface;
import interfaces.ExcelErrorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import services.base.EasyExcelDataService;

import javax.validation.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class EasyExcelDataListener<T> extends AnalysisEventListener<T> implements ExcelDataListenerInterface<T> {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private LinkedList<T> linkedList = new LinkedList<>();
    private Map<String, String> headNameMap = new HashMap<>();
    private Integer successRowNum = 0;
    private Integer totalRowNumber = 0;
    private Integer approximateTotalRowNumber = 0;
    private Integer limitNum;
    private Integer batchNum;
    private Integer headNum;
    private List<ExcelErrorData<T>> excelErrorDataList = new ArrayList<>();
    Set<String> uniSet = new HashSet<>();
    Consumer<LinkedList<T>> persistenceHandler;
    Function<T, String> uniqueHandler;
    Function<T, String> validHandler;
    EasyExcelDataService<T> easyExcelDataService;
    String processFlag = "1";
    String saveFlag = "1";

    public EasyExcelDataListener(Consumer<LinkedList<T>> persistenceHandler, Function<T, String> uniqueHandler, Integer limitNum, Integer headNum, String saveFlag) {
        this.persistenceHandler = persistenceHandler;
        this.uniqueHandler = uniqueHandler;
        this.limitNum = limitNum;
        this.headNum = headNum;
        this.saveFlag = saveFlag;
    }

    public EasyExcelDataListener() {
        this(null, null, 10, 1, "1");
    }

    public EasyExcelDataListener(EasyExcelDataService<T> easyExcelDataService) {
        this.easyExcelDataService = easyExcelDataService;
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
        if ("0".equals(processFlag)) {
            logger.info("手动取消, 终止数据处理。");
            return;
        }
        ReadSheetHolder readSheetHolder = context.readSheetHolder();
        //总行数
        if (approximateTotalRowNumber == null || approximateTotalRowNumber == 0) {
            approximateTotalRowNumber = readSheetHolder.getApproximateTotalRowNumber();
            logger.debug("解析到大约{}条数据", approximateTotalRowNumber);
        }
        if (approximateTotalRowNumber <= headNum || approximateTotalRowNumber > limitNum + headNum) {
            processFlag = "0";
            logger.info("数据条数超出范围(1, " + limitNum + "), 终止数据处理。");
            return;
        }
        totalRowNumber = totalRowNumber + 1;
        boolean hasError = false;
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
        if (!hasError && validHandler != null) {
            String validResult = validHandler.apply(data);
            if (!"".equals(validResult)) {
                hasError = true;
                excelErrorData.addError(validResult);
            }
        }
        if (!hasError && uniqueHandler != null) {
            String uniCode = uniqueHandler.apply(data);
            if (uniSet.contains(uniCode)) {
                hasError = true;
                excelErrorData.addError("Duplicated Row Data");
            } else {
                uniSet.add(uniCode);
            }
        }
        if (hasError) {
            excelErrorDataList.add(excelErrorData);
        } else {
            successRowNum = successRowNum + 1;
            linkedList.add(data);
        }
        // 达到batchNum了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (linkedList.size() == batchNum) {
            logger.debug("{}条数据，开始存储数据库！", linkedList.size());
            if (linkedList.size() > 0 && persistenceHandler != null && "1".equals(saveFlag)) {
                persistenceHandler.accept(linkedList);
                linkedList.clear();
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
        logger.debug("{}条数据，开始存储数据库！", linkedList.size());
        if (linkedList.size() > 0 && persistenceHandler != null && "1".equals(saveFlag)) {
            persistenceHandler.accept(linkedList);
            linkedList.clear();
        }
    }

    public Integer getSuccessRowNum() {
        return successRowNum;
    }

    public Integer getTotalRowNumber() {
        return totalRowNumber;
    }

    public Integer getApproximateTotalRowNumber() {
        return approximateTotalRowNumber;
    }

    public List<ExcelErrorData<T>> getExcelErrorDataList() {
        return excelErrorDataList;
    }

}

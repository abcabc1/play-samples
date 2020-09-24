package utils.poi;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.Head;
import datas.DataListenerListener;
import interfaces.base.ExcelErrorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class EasyExcelListenerUtil {

    /**
     * 每隔threshold条存储数据库，实际使用中可以3000条，然后清理linkedList ，方便内存回收
     * <p>
     * 假设Consumer是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    public static <T> DataListenerListener<T> getListener(Consumer<List<T>> consumer, Function<T, String> getUniCode, int threshold) {
        return new DataListenerListener<T>() {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            private final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
            private LinkedList<T> linkedList = new LinkedList<>();
            private Map<String, String> headNameMap = new HashMap<>();
            private int successNum = 0;
            private int totalNum = 0;
            private List<ExcelErrorData> excelErrorDataList = new ArrayList<>();
            Set<String> uniSet = new HashSet<>();

            /**
             * 这个每一条数据解析都会来调用
             *
             * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
             * @param context
             */
            @Override
            public void invoke(T data, AnalysisContext context) {
                logger.debug("解析到" + context.readRowHolder().getRowIndex() + "条数据:{}", Json.prettyPrint(Json.toJson(data)));
                totalNum = totalNum + 1;
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
                        excelErrorData.addError(headNameMap.get(constraintViolation.getPropertyPath().toString())
                                + " "
                                + constraintViolation.getInvalidValue()
                                + " "
                                + constraintViolation.getMessage()
                        );
                    }
                }
                if (getUniCode != null) {
                    String uniCode = getUniCode.apply(data);
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
                    successNum = successNum + 1;
                    linkedList.add(data);
                }
                // 达到threshold了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
                if (linkedList.size() == threshold) {
                    logger.debug("{}条数据，开始存储数据库！", linkedList.size());
                    if (consumer != null) {
                        consumer.accept(linkedList);
                    }
                    // 存储完成清理 linkedList
                    linkedList.clear();
                }
            }

            /**
             * 所有数据解析完成了 都会来调用
             *
             * @param context
             */
            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                if (linkedList.size() > 0) {
                    // 这里也要保存数据，确保最后遗留的数据也存储到数据库
                    logger.debug("{}条数据，开始存储数据库！", linkedList.size());
                    if (consumer != null) {
                        consumer.accept(linkedList);
                    }
                }
            }

            public int getSuccessNum() {
                return successNum;
            }

            public int getTotalNum() {
                return totalNum;
            }

            public List<ExcelErrorData> getExcelErrorDataList() {
                return excelErrorDataList;
            }
        };
    }

    public static <T> DataListenerListener<T> getListener(Consumer<List<T>> consumer, Function<T, String> getUniCode) {
        return getListener(consumer, getUniCode, 10);
    }
}

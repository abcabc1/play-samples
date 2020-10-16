package interfaces;

import java.util.List;

public interface ExcelDataListenerInterface<T> {

    Integer getSuccessRowNum();

    Integer getTotalRowNumber();

    Integer getApproximateTotalRowNumber();

    List<ExcelErrorData<T>> getExcelErrorDataList();
}

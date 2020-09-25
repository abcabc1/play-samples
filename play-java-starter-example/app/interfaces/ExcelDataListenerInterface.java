package interfaces.base;

import java.util.List;

public interface ExcelDataListenerInterface<T> {

    int getSuccessNum();
    int getTotalNum();
    List<ExcelErrorData> getExcelErrorDataList();
}

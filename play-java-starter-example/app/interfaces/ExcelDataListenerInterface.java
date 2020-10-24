package interfaces;

import java.util.List;

public interface ExcelDataListenerInterface<T> {

    Integer getInvalidNum();

    Integer getTotalNumber();

    Integer getApproximateTotalNumber();

    List<ExcelErrorData<T>> getInvalidList();
}

package services.base;

import interfaces.ExcelDataServiceInterface;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class EasyExcelDataService<T> implements ExcelDataServiceInterface<T> {
    public abstract Consumer<List<T>> handle();

    public abstract Function<T, String> getUniCode();

    Integer threshold = 10;
}

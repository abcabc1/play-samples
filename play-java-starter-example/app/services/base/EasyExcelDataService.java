package services.base;

import interfaces.ExcelDataServiceInterface;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class EasyExcelDataService<T> implements ExcelDataServiceInterface<T> {

    protected Integer batchNum = 100;

    public abstract Function<T, String> validHandler();

    public abstract Function<T, String> uniqueHandler();

    public abstract Consumer<List<T>> persistenceHandler();
}

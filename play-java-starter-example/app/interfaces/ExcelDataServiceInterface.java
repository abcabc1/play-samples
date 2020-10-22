package interfaces;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ExcelDataServiceInterface<T> {

    Function<T, String> validHandler();

    Function<T, String> uniqueHandler();

    Consumer<List<T>> persistenceHandler();

}

package interfaces;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ExcelDataServiceInterface<T> {

    Function<T, String> validHandler();

    Function<T, Boolean> uniqueHandler();

    Consumer<LinkedList<T>> persistenceValidHandler();

    Consumer<LinkedList<ExcelErrorData<T>>> persistenceInvalidHandler();

}

package interfaces;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ExcelDataServiceInterface<T> {

    Consumer<List<T>> handle();

    Function<T, String> getUniCode();
}

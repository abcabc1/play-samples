package services.easyexcel;

import models.easyexcel.Data1;
import services.base.EasyExcelDataService;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Data11Service extends EasyExcelDataService<Data1> {

    public Function<Data1, String> validHandler() {
        return data -> "";
    }

    @Override
    public Function<Data1, String> uniqueHandler() {
        return null;
    }

    @Override
    public Consumer<List<Data1>> persistenceHandler() {
        return null;
    }
}

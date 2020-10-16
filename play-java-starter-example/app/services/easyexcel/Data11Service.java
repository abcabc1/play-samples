package services.easyexcel;

import models.easyexcel.Data1;
import services.base.EasyExcelDataService;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Data11Service extends EasyExcelDataService<Data1> {
    @Override
    public Consumer<List<Data1>> handle() {
        return null;
    }

    @Override
    public Function<Data1, String> getUniCode() {
        return null;
    }
}

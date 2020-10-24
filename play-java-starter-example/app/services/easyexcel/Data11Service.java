package services.easyexcel;

import com.google.common.collect.Lists;
import interfaces.ExcelErrorData;
import models.easyexcel.Data1;
import services.base.EasyExcelDataService;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

public class Data11Service extends EasyExcelDataService<Data1> {

    public Function<Data1, String> validHandler() {
        return data -> "";
    }

    @Override
    public Function<Data1, Boolean> uniqueHandler() {
        return null;
    }

    @Override
    public Consumer<LinkedList<Data1>> persistenceValidHandler() {
        return null;
    }

    public Consumer<LinkedList<ExcelErrorData<Data1>>> persistenceInvalidHandler() {
        return data -> {
            Lists.partition(data, batchNum).forEach(list -> System.out.println("save" + list.size()));
        };
    }
}

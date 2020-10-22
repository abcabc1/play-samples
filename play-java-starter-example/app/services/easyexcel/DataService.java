package services.easyexcel;

import com.google.common.collect.Lists;
import models.easyexcel.Data;
import org.apache.commons.codec.digest.DigestUtils;
import services.base.EasyExcelDataService;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class DataService extends EasyExcelDataService<Data> {

    public Function<Data, String> validHandler() {
        return data -> "";
    }

    public Function<Data, String> uniqueHandler() {
        return data -> {
            StringBuilder sb = new StringBuilder();
            sb.append(data.notNullStringData).append(data.doubleData).append(data.dateData);
            return DigestUtils.sha1Hex(sb.toString());
        };
    }

    public Consumer<List<Data>> persistenceHandler() {
        return data -> {
            Lists.partition(data, batchNum).forEach(list -> System.out.println("save" + list.size()));
        };
    }
}

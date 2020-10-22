package services.easyexcel;

import com.google.common.collect.Lists;
import models.easyexcel.Data1;
import org.apache.commons.codec.digest.DigestUtils;
import services.base.EasyExcelDataService;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Data1Service extends EasyExcelDataService<Data1> {

    public Function<Data1, String> validHandler() {
        return data -> "";
    }

    public Function<Data1, String> uniqueHandler() {
        return data -> {
            StringBuilder sb = new StringBuilder();
            sb.append(data.id).append(data.name).append(data.localName);
            return DigestUtils.sha1Hex(sb.toString());
        };
    }

    public Consumer<List<Data1>> persistenceHandler() {
        return data -> {
            Lists.partition(data, batchNum).forEach(list -> System.out.println("save" + list.size()));
        };
    }
}

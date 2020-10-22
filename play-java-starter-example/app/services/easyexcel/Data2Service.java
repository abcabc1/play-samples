package services.easyexcel;

import com.google.common.collect.Lists;
import models.easyexcel.Data2;
import org.apache.commons.codec.digest.DigestUtils;
import services.base.EasyExcelDataService;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Data2Service extends EasyExcelDataService<Data2> {

    public Function<Data2, String> validHandler() {
        return data -> "";
    }

    public Function<Data2, String> uniqueHandler() {
        return data -> {
            StringBuilder sb = new StringBuilder();
            sb.append(data.id).append(data.name).append(data.localName);
            return DigestUtils.sha1Hex(sb.toString());
        };
    }

    public Consumer<List<Data2>> persistenceHandler() {
        return data -> {
            Lists.partition(data, batchNum).forEach(list -> System.out.println("save" + list.size()));
        };
    }
}

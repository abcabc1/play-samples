package services.easyexcel;

import com.google.common.collect.Lists;
import interfaces.ExcelErrorData;
import models.easyexcel.Data;
import org.apache.commons.codec.digest.DigestUtils;
import services.base.EasyExcelDataService;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

public class DataService extends EasyExcelDataService<Data> {

    public Function<Data, String> validHandler() {
        return data -> "";
    }

    public Function<Data, Boolean> uniqueHandler() {
        return data -> {
            Boolean result = true;
            StringBuilder sb = new StringBuilder();
            sb.append(data.notNullStringData).append(data.doubleData).append(data.dateData);
            String code = DigestUtils.sha1Hex(sb.toString());
            if (uniqueSet.contains(code)) {
                result = false;
            } else {
                uniqueSet.add(code);
            }
            return result;
        };
    }

    public Consumer<LinkedList<Data>> persistenceValidHandler() {
        return data -> {
            Lists.partition(data, batchNum).forEach(list -> System.out.println("save" + list.size()));
        };
    }

    public Consumer<LinkedList<ExcelErrorData<Data>>> persistenceInvalidHandler() {
        return data -> {
            Lists.partition(data, batchNum).forEach(list -> System.out.println("save" + list.size()));
        };
    }
}

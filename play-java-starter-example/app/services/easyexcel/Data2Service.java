package services.easyexcel;

import com.google.common.collect.Lists;
import interfaces.ExcelErrorData;
import models.easyexcel.Data2;
import org.apache.commons.codec.digest.DigestUtils;
import services.base.EasyExcelDataService;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

public class Data2Service extends EasyExcelDataService<Data2> {

    public Function<Data2, String> validHandler() {
        return data -> "";
    }

    public Function<Data2, Boolean> uniqueHandler() {
        return data -> {
            Boolean result = true;
            StringBuilder sb = new StringBuilder();
            sb.append(data.id).append(data.name).append(data.localName);
            String code = DigestUtils.sha1Hex(sb.toString());
            if (uniqueSet.contains(code)) {
                result = false;
            } else {
                uniqueSet.add(code);
            }
            return result;
        };
    }

    public Consumer<LinkedList<Data2>> persistenceValidHandler() {
        return data -> {
            Lists.partition(data, batchNum).forEach(list -> System.out.println("save" + list.size()));
        };
    }

    public Consumer<LinkedList<ExcelErrorData<Data2>>> persistenceInvalidHandler() {
        return data -> {
            Lists.partition(data, batchNum).forEach(list -> System.out.println("save" + list.size()));
        };
    }
}

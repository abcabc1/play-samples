package services.easyexcel;

import interfaces.ExcelDataServiceInterface;
import models.easyexcel.Data;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class DataService implements ExcelDataServiceInterface<Data> {

    public Consumer<List<Data>> handle() {
        return data -> {
            for (int i = 0; i < data.size(); i++) {
                System.out.println(i + ":" + data.get(i));
            }
        };
    }

    public Function<Data, String> getUniCode() {
        return data -> {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(data.notNullStringData).append(data.doubleData).append(data.dateData);
            return DigestUtils.sha1Hex(stringBuffer.toString());
        };
    }
}

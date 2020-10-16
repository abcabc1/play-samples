package services.easyexcel;

import interfaces.ExcelDataServiceInterface;
import models.easyexcel.Data1;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Data1Service implements ExcelDataServiceInterface<Data1> {

    public Consumer<List<Data1>> handle() {
        return data -> {
            for (int i = 0; i < data.size(); i++) {
                System.out.println(i + ":" + data.get(i));
            }
        };
    }

    public Function<Data1, String> getUniCode() {
        return data -> {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(data.id).append(data.name).append(data.localName);
            return DigestUtils.sha1Hex(stringBuffer.toString());
        };
    }
}

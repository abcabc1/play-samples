package interfaces;

import io.ebean.Model;

import java.util.ArrayList;
import java.util.List;

public class ExcelErrorData<T> extends Model {

    public T data;
    public Integer rowNum;
    public List<String> errorList;

    public ExcelErrorData(T data, Integer rowNum) {
        this.data = data;
        this.rowNum = rowNum;
    }

    public void addError(String error) {
        if (this.errorList == null) {
            this.errorList = new ArrayList<>();
        }
        this.errorList.add(error);
    }
}

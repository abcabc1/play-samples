package datas;

import com.alibaba.excel.event.AnalysisEventListener;
import interfaces.base.ExcelDataListenerInterface;

public abstract class DataListenerListener<T> extends AnalysisEventListener<T> implements ExcelDataListenerInterface {

}

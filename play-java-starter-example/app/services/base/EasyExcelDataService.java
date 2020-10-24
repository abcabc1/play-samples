package services.base;

import interfaces.ExcelDataServiceInterface;
import interfaces.ExcelErrorData;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class EasyExcelDataService<T> implements ExcelDataServiceInterface<T> {

    protected String process = "";
    protected Boolean processing = false;
    protected Integer batchNum = 100;
    private Integer approximateTotalNum = 0;
    private Integer totalNum = 0;
    private Integer validNum = 0;
    private Integer invalidNum = 0;
    private LinkedList<T> validList = new LinkedList<>();
    private LinkedList<ExcelErrorData<T>> invalidList = new LinkedList<>();
    protected Set<String> uniqueSet = new HashSet<>();

    public abstract Function<T, String> validHandler();

    public abstract Function<T, Boolean> uniqueHandler();

    public abstract Consumer<LinkedList<T>> persistenceValidHandler();

    public abstract Consumer<LinkedList<ExcelErrorData<T>>> persistenceInvalidHandler();

    public Integer getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Integer batchNum) {
        this.batchNum = batchNum;
    }

    public void setApproximateTotalNum(Integer approximateTotalNum) {
        this.approximateTotalNum = approximateTotalNum;
    }

    public Integer getApproximateTotalNum() {
        return approximateTotalNum;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum() {
        this.totalNum = this.totalNum + 1;
    }

    public Integer getValidNum() {
        return validNum;
    }

    public void setValidNum() {
        this.validNum = this.validNum + 1;
    }

    public Integer getInvalidNum() {
        return invalidNum;
    }

    public void setInvalidNum() {
        this.invalidNum = this.invalidNum + 1;
    }

    public LinkedList<T> getValidList() {
        return validList;
    }

    public LinkedList<ExcelErrorData<T>> getInvalidList() {
        return invalidList;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public Boolean getProcessing() {
        return processing;
    }

    public void setProcessing(Boolean processing) {
        this.processing = processing;
    }

    public void start(String process) {
        this.process = process;
        this.processing = true;
        init();
    }

    public void init() {
        this.totalNum = 0;
        this.validNum = 0;
        this.invalidNum = 0;
        this.approximateTotalNum = 0;
        this.validList = new LinkedList<>();
        this.invalidList = new LinkedList<>();
        this.uniqueSet = new HashSet<>();
    }

    public void finish(String process) {
        if (getProcess().equals(process)) {
            this.processing = false;
            init();
        }
    }
}

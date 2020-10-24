package controllers;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import interfaces.ExcelErrorData;
import models.easyexcel.Data;
import models.easyexcel.Data1;
import models.easyexcel.Data2;
import org.springframework.beans.BeanUtils;
import play.Environment;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.easyexcel.Data1Service;
import services.easyexcel.Data2Service;
import services.easyexcel.DataService;
import utils.Constant;
import utils.RequestUtil;
import utils.ResultUtil;
import utils.TimeUtil;
import utils.exception.InternalException;
import utils.poi.EasyExcelDataListener;

import javax.inject.Inject;
import java.io.File;
import java.util.*;

import static utils.exception.ExceptionEnum.EXCEL_PROCESS_FAILURE;

public class ExcelController extends Controller {

    @Inject
    Environment environment;

    @Inject
    DataService dataService;

    @Inject
    Data1Service data1Service;

    @Inject
    Data2Service data2Service;

    private String poiPath = "public/poi/simple/";

    public Result testEnvironmentPath(Http.Request request) {
        File image = environment.getFile("/public/images/bg.jpg");
        return ok(image);
    }

    public Result demoWrite(Http.Request request) {
        String fileName = "测试.xls";
        EasyExcel.write(Constant.applicationPath + poiPath + fileName, Data.class).sheet("模版").doWrite(getOutputData());
        return ok();
    }

    public List<Data> getOutputData() {
        List<Data> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Data data = new Data();
            data.notNullStringData = "string";
            data.doubleData = 1.01;
            data.dateData = new Date();
            list.add(data);
        }
        return list;
    }

    public Result singleWrite() {
        List<Data1> list = new ArrayList<>();
        if(data1Service.getInvalidList() == null || data1Service.getInvalidList().size() == 0) {
            return ok(ResultUtil.failure());
        }
        for (ExcelErrorData<Data1> excelErrorData: data1Service.getInvalidList()) {
            Data1 data1 = new Data1();
            BeanUtils.copyProperties(excelErrorData.data, data1);
            data1.remark = excelErrorData.errorList.get(0);
            list.add(data1);
        }
        String fileName = "测试.xls";
        EasyExcel.write(poiPath + fileName, Data1.class).sheet("模版").doWrite(list);
        return ok(ResultUtil.success());
    }

    public Result watch() {
        Map<String, Object> map = new HashMap<>();
        map.put("process", data1Service.getProcess());
        map.put("processing", data1Service.getProcessing());
        map.put("totalNum", data1Service.getTotalNum());
        map.put("validNum", data1Service.getValidNum());
        map.put("invalidNum", data1Service.getInvalidNum());
        map.put("approximateTotalNum", data1Service.getApproximateTotalNum());
        return ok(ResultUtil.success(map));
    }

    public Result cancelRead(Http.Request request) {
        String process = RequestUtil.getString(request, "process");
        data1Service.finish(process);
        return watch();
    }

    public Result singleRead(Http.Request request) {
        String fileName = "test.xls";
        if (data1Service.getProcessing()) {
            throw InternalException.build(EXCEL_PROCESS_FAILURE);
        }
        EasyExcelDataListener<Data1> easyExcelDataListener = new EasyExcelDataListener<>(data1Service);
        data1Service.start(TimeUtil.getFormatNow());
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 可以读取文件，文件路径或者文件流(File.getInputStream)
        EasyExcel.read(poiPath + fileName, Data1.class, easyExcelDataListener)
                .sheet(0)
//                .headRowNumber(1)
                .doRead();
        return watch();
    }

    public Result multiRead(Http.Request request) {
        String fileName = "test.xls";
        Data1Service data1Service = new Data1Service();
        EasyExcelDataListener<Data1> dataDataListener = new EasyExcelDataListener<>(data1Service, 10, 1);
        EasyExcelDataListener<Data2> data1DataListener = new EasyExcelDataListener<>();
        ExcelReader excelReader = EasyExcel.read(Constant.applicationPath + poiPath + fileName).build();
        // 这里为了简单 所以注册了 同样的head 和Listener 自己使用功能必须不同的Listener
        ReadSheet readSheet1 =
                EasyExcel.readSheet("中西成药").head(Data2.class).registerReadListener(data1DataListener).build();
        ReadSheet readSheet2 =
                EasyExcel.readSheet("食品 保健食品").head(Data1.class).registerReadListener(dataDataListener).build();
        // 这里注意 一定要把sheet1 sheet2 一起传进去，不然有个问题就是03版的excel 会读取多次，浪费性能
        excelReader.read(readSheet1, readSheet2);
        // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
        excelReader.finish();
//        EasyExcel.read(Constant.applicationPath + poiPath + fileName, Test.class, dataDataListener).doReadAll();
        return watch();
    }
}

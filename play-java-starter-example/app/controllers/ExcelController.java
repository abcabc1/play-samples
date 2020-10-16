package controllers;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import models.easyexcel.Data;
import models.easyexcel.Data1;
import models.easyexcel.Data2;
import play.Environment;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.easyexcel.Data11Service;
import services.easyexcel.Data1Service;
import services.easyexcel.Data2Service;
import services.easyexcel.DataService;
import utils.Constant;
import utils.ResultUtil;
import utils.poi.EasyExcelDataListener;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class ExcelController extends Controller {

    @Inject
    Environment environment;

    @Inject
    DataService dataService;

    @Inject
    Data1Service data1Service;

    @Inject
    Data2Service data2Service;

    private String poiPath = "/public/poi/simple/";

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

    public Result demoRead(Http.Request request) {
        String path = "/public/poi/simple/";
        String fileName = "测试.xls";
        EasyExcelDataListener<Data> easyExcelDataListener = new EasyExcelDataListener<>();
        EasyExcelDataListener<Data1> easyExcelDataListener1 = new EasyExcelDataListener<>();
        Consumer consumer2 = System.out::println;
//        Consumer<List<Data1>> consumer = Data11Service::handle;
//        easyExcelDataListener1.setConsumer();

        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(Constant.applicationPath + path + fileName, Data.class, easyExcelDataListener).sheet().doRead();
        Map<String, Object> map = new HashMap<>();
        map.put("successRowNum", easyExcelDataListener.getSuccessRowNum());
        map.put("totalNum", easyExcelDataListener.getTotalRowNumber());
        map.put("dataList", easyExcelDataListener.getExcelErrorDataList());
        return ok(ResultUtil.success(map));
    }

    public Result multiRead(Http.Request request) {
        String fileName = "test.xls";
        Data1Service data1Service = new Data1Service();
        EasyExcelDataListener<Data1> dataDataListener = new EasyExcelDataListener<>(data1Service.handle(), data1Service.getUniCode(), 10);
        EasyExcelDataListener<Data2> data1DataListener = new EasyExcelDataListener<>();//EasyExcelListenerUtil.getListener(data2Service.handle(), data2Service.getUniCode(), 2);
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
        Map<String, Object> map = new HashMap<>();
        map.put("successRowNum", dataDataListener.getSuccessRowNum());
        map.put("totalNum", dataDataListener.getTotalRowNumber());
        map.put("dataList", dataDataListener.getExcelErrorDataList());
        return ok(ResultUtil.success(map));
    }
}

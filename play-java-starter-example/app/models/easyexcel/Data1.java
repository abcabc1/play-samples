package models.easyexcel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.ebean.Model;

public class Data1 extends Model {

    @ExcelProperty(value = "商品中心id(新增为空)", index = 0)
    public String id;
    @ExcelProperty(value = "*商品名称", index = 1)
    public String name;
    @ExcelProperty(value = "*通用名", index = 2)
    public String localName;
    @ExcelProperty(value = "*生产厂家", index = 3)
    public String factory;
    @ExcelProperty(value = "*规格", index = 4)
    public String spec;
    @ExcelProperty(value = "*包装单位", index = 5)
    public String unit;
    @ExcelProperty(value = "*商品条码", index = 6)
    public String code;
    @ExcelProperty(value = "保质期", index = 7)
    public String valid;
    @ExcelProperty(value = "备注", index = 8)
    public String remark;

    //不输出在表格中的字段
    @ExcelIgnore
    public Boolean status;
}

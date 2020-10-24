package models.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import io.ebean.Model;

public class Data2 extends Model {

    @ExcelProperty(value = "商品中心id(新增为空)", index = 0)
    public String id;
    @ExcelProperty(value = "*商品名称", index = 1)
    public String name;
    @ExcelProperty(value = "*通用名", index = 2)
    public String localName;
    @ExcelProperty(value = "*生产厂家", index = 3)
    public String factory;
    @ExcelProperty(value = "*处方分类", index = 4)
    public String type;
    @ExcelProperty(value = "*规格", index = 5)
    public String spec;
    @ExcelProperty(value = "*包装单位", index = 6)
    public String unit;
    @ExcelProperty(value = "*商品条码", index = 7)
    public String code;
    @ExcelProperty(value = "*批准文号", index = 8)
    public String approveNumber;
}

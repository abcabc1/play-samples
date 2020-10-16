package models.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import io.ebean.Model;

public class Data2 extends Model {

    @ExcelProperty(value = "商品中心id(新增为空)")
    public String id;
    @ExcelProperty(value = "*商品名称")
    public String name;
    @ExcelProperty(value = "*通用名")
    public String localName;
    @ExcelProperty(value = "*生产厂家")
    public String factory;
    @ExcelProperty(value = "*处方分类")
    public String type;
    @ExcelProperty(value = "*规格")
    public String spec;
    @ExcelProperty(value = "*包装单位")
    public String unit;
    @ExcelProperty(value = "*商品条码")
    public String code;
    @ExcelProperty(value = "*批准文号")
    public String approveNumber;
}

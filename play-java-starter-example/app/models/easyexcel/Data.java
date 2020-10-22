package models.easyexcel;

import annotations.CheckGender;
import annotations.NameInclude;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import datas.GenderConverter;
import io.ebean.Model;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

public class Data extends Model {

//    value用于写excel的标题，index从0开始，用于读取数据定位和headMap
    @ExcelProperty(value = "非null字符串标题", index = 0)
    @NotNull(message = "非null字符串")
    public String notNullStringData;

    @ExcelProperty(value = "非空字符串标题", index = 1)
    @NotNull(message = "名字不能为空字符串")
    public String notBlankStringData;

    @Future
    @ExcelProperty(value = "日期标题", index = 2)
//    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
//    @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒")
    @DateTimeFormat("yyyy/m/d h:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date dateData;

    @ExcelProperty(value = "正数数字标题", index = 3)
    @Positive(message = "正数数字")
    public Double doubleData;

    @Digits(integer = 4, fraction = 2)
    @ExcelProperty(value = "浮点数字标题", index = 4)
    public BigDecimal bigDecimalData;

    @ExcelProperty(value = "非负数数字标题", index = 5)
    @DecimalMin(value = "0", inclusive = true, message = "非负数数字")
    public Long longData;

    @ExcelProperty(value = "布尔标题", index = 6)
    public String booleanData;

    @ExcelProperty(value = "邮箱标题", index = 7)
    @Email(message = "邮箱")
    public String emailData;

    @ExcelProperty(value = "手机标题", index = 8)
    @Pattern(regexp = "^1(3|4|5|7|8)\\d{9}$", message = "手机")
    public String phoneData;

    @ExcelProperty(value = "身份证标题", index = 9)
    @Length(min = 18, max = 18, message = "身份证")
    public String idData;

    /**
     * 自定义注解校验
     */
    @ExcelProperty(value = "自定义标题", index = 10)
    @NameInclude(message = "类型必须是type value必须是HealerJean", type = "Mail")
    public String licensePlate;

    @ExcelProperty(value = "性别标题", converter = GenderConverter.class, index = 11)
    @CheckGender(message = "性别")
    public String genderData;

    public String toString() {
        return notNullStringData + ":" + dateData + ":" + doubleData + ":" + bigDecimalData + ":" + booleanData + ":" + notBlankStringData + ":" + longData;
    }

    /**
     * 1、如果变量传入的时候是NULL，则不会校验 类似于 @AssertTrue @Size(min=, max=) 等，除非有了后面3的代码
     *
     * 2、为了我们以后方便， 所有校验属性必须加组，方便阅读，如果不加group的话，会提供一个默认的组，校验的时候选择哪个组就会校验哪个
     *
     * 3、配置NULL的组group后，无需配置类似于@Size等里面的组group，
     *
     * 其中 	Hibernate Validator 附加的 constraint  （也就是说如果下面的内容中，不引入hibernate包就不会起作用）
     * @Email 验证是否是邮件地址，如果为null,不进行验证，算通过验证。
     * @Length(min=, max=) 验证字符串的长度
     * @Range(min=,max=,message=) 被注释的元素必须在合适的范围内
     * 空检查
     * @Null 验证对象是否为null
     * @NotNull 验证对象是否不为null, 无法查检长度为0的字符串
     * @NotBlank 检查约束字符串是不是Null还有被Trim的长度是否大于0, 只对字符串, 且会去掉前后空格.
     * @NotEmpty 检查约束元素是否为NULL或者是EMPTY.
     *
     * Booelan检查
     * @AssertTrue 验证 Boolean 对象是否为 true
     * @AssertFalse 验证 Boolean 对象是否为 false
     *
     * 长度检查
     * @Size(min=, max=) 验证对象（Array,Collection,Map,String）长度是否在给定的范围之内
     * @Length(min=, max=) 验证字符串的长度
     *
     * 日期检查
     * @Past 验证 Date 和 Calendar 对象是否在当前时间之前
     * @Future 验证 Date 和 Calendar 对象是否在当前时间之后
     *
     * 正则表达式
     * @Pattern(regexp="[1-9]{1,3}", message="数量X: 必须为正整数，并且0<X<1000")   验证 String 对象是否符合正则表达式的规则
     * 比如：@Pattern(message = "accountProperty此版本只支持对公",regexp = "1")
     *
     *
     * 数值检查，建议使用在Stirng,Integer类型，不建议使用在int类型上，因为表单值为“”时无法转换为int，但可以转换为Stirng为"",Integer为null
     * @Min 验证 Number 和 String 对象是否大等于指定的值
     * @Max 验证 Number 和 String 对象是否小等于指定的值
     * @DecimalMax 被标注的值必须不大于约束中指定的最大值. 这个约束的参数是一个通过BigDecimal定义的最大值的字符串表示.小数存在精度
     * @DecimalMin 被标注的值必须不小于约束中指定的最小值. 这个约束的参数是一个通过BigDecimal定义的最小值的字符串表示.小数存在精度
     * @Digits(integer=,fraction=) 验证字符串是否是符合指定格式的数字，interger指定整数精度，fraction指定小数精度。
     * 范围
     * @Range(min=10000,max=50000,message="range.bean.wage")
     *
     *
     * @Valid 对象传递参数的时候用到
     * public String doAdd(Model model, @Valid AnimalForm form, BindingResult result){}
     * 1.3、ValidatorConfiguration
     */
    @ExcelIgnore
    public Boolean booleanDataConvert;
}
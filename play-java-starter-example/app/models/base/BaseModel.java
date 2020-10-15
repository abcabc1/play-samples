package models.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.ebean.Model;
import utils.LocalDateTimeDeserializer;
import utils.LocalDateTimeSerializer;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.time.LocalDateTime;

//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@MappedSuperclass
public class BaseModel extends Model {

    /*
    set insertable = false for db default column
     */
    @Column(insertable = false, nullable = false, columnDefinition = "tinyint unsigned default 1 comment '数据是否有效[0 无效,1 有效]'")
    public Boolean status;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(insertable = false, nullable = false, columnDefinition = "datetime(6) default current_timestamp(6) comment '创建时间'")
    public LocalDateTime createTime;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(insertable = false, nullable = false, columnDefinition = "datetime(6) default current_timestamp(6) on update current_timestamp(6) comment '修改时间'")
    public LocalDateTime updateTime;

    @Transient
//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime timeFrom;
    @Transient
//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime timeTo;
}

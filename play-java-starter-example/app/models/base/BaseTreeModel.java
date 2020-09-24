package models.base;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseTreeModel extends BaseModel {

    @Id
    @Column(nullable = false, columnDefinition = "varchar(32) comment '配置节点'")
    public String node;

    @Column(nullable = false, columnDefinition = "varchar(32) default '' comment '节点名称'")
    public String nodeName;

    @Column(nullable = false, columnDefinition = "tinyint unsigned default 1 comment '节点次序'")
    public Integer nodeOrder;

    @Column(nullable = false, columnDefinition = "varchar(255) default '' comment '节点次序序列'")
    public String nodeOrderSeq;

    @Column(nullable = false, columnDefinition = "varchar(255) default '' comment '节点序列'")
    public String nodeSeq;

    @Column(nullable = false, columnDefinition = "tinyint unsigned default 1 comment '节点级别'")
    public Integer nodeLevel;
}

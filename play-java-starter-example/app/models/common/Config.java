package models.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.ebean.Finder;
import models.base.BaseTreeModel;

import javax.persistence.*;
import java.util.List;

@Table
@Entity
public class Config extends BaseTreeModel {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "parent")
    @JsonBackReference
    public Config parent;

    @OneToMany(mappedBy = "parent")
    @JsonManagedReference
    public List<Config> child;

    public static final Finder<String, Config> find = new Finder<>(Config.class, "word");
}
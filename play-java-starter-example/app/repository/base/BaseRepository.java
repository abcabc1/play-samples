package repository.base;

import interfaces.base.BaseInterface;
import io.ebean.*;
import models.base.BaseModel;
import play.db.ebean.EbeanConfig;
import utils.exception.InternalException;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static utils.exception.ExceptionEnum.MODEL_NOT_FOUND_IN_DB;

public abstract class BaseRepository<T extends BaseModel> implements BaseInterface<T> {

    protected final EbeanServer ebeanServer;

    @Inject
    public BaseRepository() {
        this.ebeanServer = Ebean.getServer("default");
    }

    @Inject
    public BaseRepository(EbeanConfig ebeanConfig, String serverName) {
        this.ebeanServer = Ebean.getServer(serverName == null ? ebeanConfig.defaultServer() : serverName);
    }

    @Inject
    public BaseRepository(EbeanConfig ebeanConfig) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    }

    public void insert(T model) {
        if (model == null) return;
        ebeanServer.insert(model);
    }

    public void insertAll(List<T> models) {
        if (models == null || models.size() == 0) return;
        ebeanServer.saveAll(models);
    }

    public void save(T model) {
        if (model == null) return;
        ebeanServer.save(model);
    }

    public void saveAll(List<T> models) {
        if (models == null || models.size() == 0) return;
        ebeanServer.saveAll(models);
    }

    public void update(T model) {
        if (model == null) return;
        ebeanServer.update(model);
    }

    public void updateAll(List<T> models) {
        if (models == null || models.size() == 0) return;
        ebeanServer.updateAll(models);
    }

    @SuppressWarnings("unchecked")
    public T get(T model) {
        T t = (T)ebeanServer.find(model.getClass()).setId(getPk(model)).findOne();
        if (t == null) {
            throw InternalException.build(MODEL_NOT_FOUND_IN_DB, new String[]{getSimpleName(model), getPk(model).toString()});
        }
        return t;
    }

    public boolean remove(T model) {
        T t = get(model);
        if(t == null) return false;
        return ebeanServer.delete(t);
    }

    public int removeAll(List<T> models) {
        if (models == null || models.size() == 0) return 0;
        return ebeanServer.deleteAll(models);
    }

    public void delete(T model) {
        model.status = false;
        ebeanServer.update(model);
    }

    public void deleteAll(List<T> models) {
        if (models == null || models.size() == 0) return;
        for (T model: models) {
            model.status = false;
        }
        ebeanServer.updateAll(models);
    }

    public PagedList<T> list(T model) {
        return list(model, getSort());
    }

    /*
    col1 asc, col2 desc, col3
     */
    public PagedList<T> list(T model, String sort) {
        return pagedList(model, sort, 0, 100);
    }

    public PagedList<T> pagedList(T model, int page, int pageSize) {
        return pagedList(model, getSort(), 0, 100);
    }

    public PagedList<T> pagedList(T model, String sort, int page, int pageSize) {
        return getExpr(model)
                .orderBy(sort)
                .setFirstRow(page * pageSize)
                .setMaxRows(pageSize)
                .findPagedList();
    }

    public abstract Object getPk(T model) ;

    /*
    col1 asc, col2 desc, col3
     */
    public String getSort() {
        return "id desc";
    };

    public abstract ExpressionList<T> getExpr(T model);

    @SuppressWarnings("unchecked")
    public ExpressionList<T> getExpressionList(T model) {
        ExpressionList<T> expressionList = (ExpressionList<T>) ebeanServer.find(model.getClass()).where();
        model.status = Optional.ofNullable(model.status).orElse(true);
        if (model.timeFrom != null && model.timeTo == null) {
            expressionList.ge("createTime", model.timeFrom);
        }
        if (model.timeFrom == null && model.timeTo != null) {
            expressionList.le("createTime", model.timeTo);
        }
        if (model.timeFrom != null && model.timeTo != null) {
            expressionList.inRange("createTime", model.timeFrom, model.timeTo);
        }
        if (model.status != null) {
            expressionList.eq("status", model.status);
        }
        return expressionList;
    }

    public int sqlUpdate(String updateSql) {
        int num = 0;
        Transaction txn = ebeanServer.beginTransaction();
        try {
            num = ebeanServer.createSqlUpdate(updateSql).execute();
            txn.commit();
        } finally {
            txn.close();
        }
        return num;
    }

    public List<SqlRow> sqlQuery(String querySql) {
        return ebeanServer.createSqlQuery(querySql).findList();
    }

    public void insertNode(T model) {
        // implement by child
    }

    public void updateNode(T model) {
        // implement by child
    }

    public void deleteNode(T model) {
        // implement by child
    }

    @NotNull
    protected String getSimpleName(T model) {
        return model.getClass().getSimpleName();
    }
}

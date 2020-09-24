package interfaces.base;

import io.ebean.PagedList;

import java.util.List;

public interface BaseInterface<T> {
    void insert(T model);
    void insertAll(List<T> models);
    void update(T model);
    void updateAll(List<T> models);
    void save(T model);
    void saveAll(List<T> models);
    T get(T model);
    boolean remove(T model);
    int removeAll(List<T> model);
    void delete(T model);
    void deleteAll(List<T> model);
    PagedList<T> list(T model);
    PagedList<T> list(T model, String sort);
    PagedList<T> pagedList(T model, int page, int pageSize);
    PagedList<T> pagedList(T model, String sort, int page, int pageSize);
    String getSort();
    Object getPk(T model);
    void insertNode(T model);
    void updateNode(T model);
    void deleteNode(T model);
}

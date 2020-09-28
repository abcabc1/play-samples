package interfaces;

import io.ebean.PagedList;

import java.util.Collection;
import java.util.List;

public interface BaseInterface<T> {
    void insert(T model);

    void insertAll(Collection<T> models);

    void update(T model);

    void updateAll(Collection<T> models);

    void save(T model);

    void saveAll(Collection<T> models);

    T get(T model);

    boolean remove(T model);

    int removeAll(Collection<T> model);

    void delete(T model);

    void deleteAll(Collection<T> model);

    List<T> list(T model);

    List<T> list(T model, int size);

    List<T> list(T model, String sort, int size);

    PagedList<T> pagedList(T model, int page, int pageSize);

    PagedList<T> pagedList(T model, String sort, int page, int pageSize);

    String getSort();

    Object getPk(T model);

    T getById(Object id);

    void insertNode(T model);

    void updateNode(T model);

    void deleteNode(T model);
}

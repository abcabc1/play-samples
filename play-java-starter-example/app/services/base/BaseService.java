package services.base;

import interfaces.BaseInterface;
import io.ebean.PagedList;
import models.base.BaseModel;
import repository.base.BaseRepository;
import utils.Constant;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

public abstract class BaseService<T extends BaseModel> implements BaseInterface<T> {

    private BaseRepository<T> repository;

    @Inject
    public BaseService(BaseRepository<T> repository) {
        this.repository = repository;
    }

    public void insert(T model) {
        repository.insert(model);
    }

    public void insertAll(Collection<T> models) {
        repository.insertAll(models);
    }

    public void save(T model) {
        repository.save(model);
    }

    public void saveAll(Collection<T> models) {
        repository.saveAll(models);
    }

    public void update(T model) {
        repository.update(model);
    }

    public void updateAll(Collection<T> models) {
        repository.updateAll(models);
    }

    public T get(T model) {
        return repository.get(model);
    }

    public boolean remove(T model) {
        return repository.remove(model);
    }

    public int removeAll(Collection<T> models) {
        return repository.removeAll(models);
    }

    public void delete(T model) {
        repository.delete(model);
    }

    public void deleteAll(Collection<T> models) {
        repository.deleteAll(models);
    }

    public List<T> list(T model) {
        return list(model, getSort());
    }

    public List<T> list(T model, int size) {
        return list(model, getSort(), size);
    }

    public List<T> list(T model, String sort) {
        return list(model, sort, Constant.MAX_PAGE_SIZE);
    }

    public List<T> list(T model, String sort, int size) {
        return repository.list(model, sort, size);
    }

    public PagedList<T> pagedList(T model, int page, int pageSize) {
        return repository.pagedList(model, getSort(), page, pageSize);
    }

    public PagedList<T> pagedList(T model, String sort, int page, int pageSize) {
        return repository.pagedList(model, sort, page, pageSize);
    }

    public String getSort() {
        return repository.getSort();
    }

    public Object getPk(T model) {
        return repository.getPk(model);
    }

    public void insertNode(T model) {
        repository.insertNode(model);
    }

    public void updateNode(T model) {
        repository.updateNode(model);
    }

    public void deleteNode(T model) {
        repository.deleteNode(model);
    }
}

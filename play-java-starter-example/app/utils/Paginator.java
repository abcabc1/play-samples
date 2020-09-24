package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class Paginator<T> {

    private static final Logger logger = LoggerFactory.getLogger(Paginator.class);

    private int page;
    private int total;
    private int pageSize;
    private int totalPage;
    private List<T> dataList;
    private List<T> pagedDataList;

    public Paginator(List<T> dataList) {
        this(dataList, 10);
    }

    public Paginator(List<T> dataList, int pageSize) {
        this(dataList, pageSize, 0);
    }

    public Paginator(List<T> dataList, int pageSize, int page) {
        this.dataList = dataList;
        this.pagedDataList = dataList;
        this.pageSize = pageSize;
        this.page = page;
        this.total = dataList.size();
        setTotalPage();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return dataList.size();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage() {
        if (this.pageSize <= 0) {
            this.totalPage = 0;
            logger.error("page size {} is invalid", pageSize);
            return;
        }
        this.totalPage = this.total / this.pageSize;
        if (this.total % this.pageSize != 0) {
            this.totalPage = this.totalPage + 1;
        }
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public boolean hasNext() {
        return pagedDataList.size() > 0 && page < totalPage;
    }

    public List<T> next() {
        this.pagedDataList = dataList.stream().skip(page * pageSize).limit(pageSize).collect(Collectors.toList());
        this.page = this.page + 1;
        return this.pagedDataList;
    }

    public List<T> pagedList(int page) {
        if (page < 0 || totalPage == 0) return null;
        return dataList.stream().skip(page * pageSize).limit(pageSize).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Paginator{" +
                "page=" + page +
                ", total=" + total +
                ", pageSize=" + pageSize +
                ", totalPage=" + totalPage +
                '}';
    }
}

package com.code.enhance;

import java.io.Serializable;
import java.util.List;

/**
 * @author jeffy
 * @date 2019/1/22
 **/
public class PageModel<T> implements Serializable {
    private static final long serialVersionUID = -8802145055234572275L;

    /**
     * total count
     */
    private int totalCount;

    /**
     * total page count
     */
    private int pageCount;

    /**
     * page size
     */
    private int pageSize;

    /**
     * current page
     */
    private int page = 1;

    /**
     * record list
     */
    private List<T> records;

    /**
     * sort field
     */
    private String sortField;

    /**
     * sort field
     */
    private boolean sortAsc = true;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public boolean isSortAsc() {
        return sortAsc;
    }

    public void setSortAsc(boolean sortAsc) {
        this.sortAsc = sortAsc;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @Override
    public String toString() {
        return "PageModel{" +
                "totalCount=" + totalCount +
                ", pageCount=" + pageCount +
                ", pageSize=" + pageSize +
                ", page=" + page +
                ", records=" + records +
                ", sortField='" + sortField + '\'' +
                ", sortAsc=" + sortAsc +
                '}';
    }
}

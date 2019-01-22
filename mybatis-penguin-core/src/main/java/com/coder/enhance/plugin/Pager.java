package com.coder.enhance.plugin;

import org.apache.ibatis.session.RowBounds;

/**
 * @author jeffy
 * @date 2019/1/22
 **/
public class Pager extends RowBounds {

    /**
     * total
     */
    private int total;
    /**
     * current page
     */
    private int page = 1;
    /**
     * page size
     */
    private int pageSize;
    /**
     * sorg field
     */
    private String sortField;
    /**
     * sort asc
     */
    private boolean sortAsc = true;

    public Pager(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public Pager(int offset, int limit, int page, int pageSize) {
        super(offset, limit);
        this.page = page;
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
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
}

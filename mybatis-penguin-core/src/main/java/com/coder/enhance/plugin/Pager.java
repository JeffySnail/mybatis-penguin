package com.coder.enhance.plugin;

import org.apache.ibatis.session.RowBounds;

import java.io.Serializable;

/**
 * @author jeffy
 * @date 2019/1/22
 **/
public class Pager extends RowBounds implements Serializable {

    /**
     * total count
     */
    private int totalCount;

    /**
     * total
     */
    private int totalPages;
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
        if (page <= 0) {
            throw new IllegalArgumentException("page begin at 1");
        }
        this.page = page;
        this.pageSize = pageSize;
    }

    public Pager(int offset, int limit, int page, int pageSize) {
        super(offset, limit);
        if (page <= 0) {
            throw new IllegalArgumentException("page begin at 1");
        }
        this.page = page;
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
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


    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "Pager{" +
                "totalCount=" + totalCount +
                ", totalPages=" + totalPages +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", sortField='" + sortField + '\'' +
                ", sortAsc=" + sortAsc +
                '}';
    }
}

package com.wu.framework.database.puls;

import lombok.Data;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe : 分页
 * @date : 2020/7/17 下午10:58
 */
@Data
public class Pagination {

    private static final long serialVersionUID = 1L;

    /**
     * 总数
     */
    private long total;

    /**
     * 每页显示条数，默认 10
     */
    private int size = 10;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 当前页
     */
    private int current = 1;

    /**
     * 查询总记录数（默认 true）
     */
    private boolean searchCount = true;

    /**
     * 开启排序（默认 true） 只在代码逻辑判断 并不截取sql分析
     *
     * @see com.baomidou.mybatisplus.mapper.SqlHelper#fillWrapper
     **/
    private boolean openSort = true;

    /**
     * 优化 Count Sql 设置 false 执行 select count(1) from (listSql)
     */
    private boolean optimizeCountSql = true;

    /**
     * <p>
     * SQL 排序 ASC 集合
     * </p>
     */
    private List<String> ascs;
    /**
     * <p>
     * SQL 排序 DESC 集合
     * </p>
     */
    private List<String> descs;

    /**
     * 是否为升序 ASC（ 默认： true ）
     *
     * @see #ascs
     * @see #descs
     */
    private boolean isAsc = true;

    /**
     * <p>
     * SQL 排序 ORDER BY 字段，例如： id DESC（根据id倒序查询）
     * </p>
     * <p>
     * DESC 表示按倒序排序(即：从大到小排序)<br>
     * ASC 表示按正序排序(即：从小到大排序)
     *
     * @see #ascs
     * @see #descs
     * </p>
     */
    private String orderByField;

    public Pagination() {
        super();
    }

    /**
     * <p>
     * 分页构造函数
     * </p>
     *
     * @param current 当前页
     * @param size    每页显示条数
     */
    public Pagination(int current, int size) {
        this(current, size, true);
    }

    public Pagination(int current, int size, boolean searchCount) {
        this(current, size, searchCount, true);
    }

    public Pagination(int current, int size, boolean searchCount, boolean openSort) {
        if (current > 1) {
            this.current = current;
        }
        this.size = size;
        this.searchCount = searchCount;
        this.openSort = openSort;
    }

    public boolean hasPrevious() {
        return this.current > 1;
    }

    public boolean hasNext() {
        return this.current < this.pages;
    }

    public long getTotal() {
        return total;
    }

    public Pagination setTotal(long total) {
        this.total = total;
        return this;
    }

    public int getSize() {
        return size;
    }

    public Pagination setSize(int size) {
        this.size = size;
        return this;
    }

    public long getPages() {
        if (this.size == 0) {
            return 0L;
        }
        this.pages = this.total / this.size;
        if (this.total % this.size != 0) {
            this.pages++;
        }
        return this.pages;
    }

    public int getCurrent() {
        return current;
    }

    public Pagination setCurrent(int current) {
        this.current = current;
        return this;
    }

    @Transient
    public boolean isSearchCount() {
        return searchCount;
    }

    public Pagination setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
        return this;
    }

    /**
     * @see #ascs
     * @see #descs
     */
    @Deprecated
    @Transient
    public String getOrderByField() {
        return orderByField;
    }

    /**
     * @see #ascs
     * @see #descs
     */
    public Pagination setOrderByField(String orderByField) {
        if ("".equals(orderByField)) {
            this.orderByField = orderByField;
        }
        return this;
    }

    @Transient
    public boolean isOpenSort() {
        return openSort;
    }

    public Pagination setOpenSort(boolean openSort) {
        this.openSort = openSort;
        return this;
    }

    @Transient
    public boolean isOptimizeCountSql() {
        return optimizeCountSql;
    }

    public void setOptimizeCountSql(boolean optimizeCountSql) {
        this.optimizeCountSql = optimizeCountSql;
    }

    @Transient
    public List<String> getAscs() {
        return orders(isAsc, ascs);
    }

    public Pagination setAscs(List<String> ascs) {
        this.ascs = ascs;
        return this;
    }

    private List<String> orders(boolean condition, List<String> columns) {
        if (condition && "".equals(orderByField)) {
            if (columns == null) {
                columns = new ArrayList<>();
            }
            if (!columns.contains(orderByField)) {
                columns.add(orderByField);
            }
        }
        return columns;
    }

    @Transient
    public List<String> getDescs() {
        return orders(!isAsc, descs);
    }

    public Pagination setDescs(List<String> descs) {
        this.descs = descs;
        return this;
    }

    /**
     * @see #ascs
     * @see #descs
     */
    @Deprecated
    @Transient
    public boolean isAsc() {
        return isAsc;
    }

    /**
     * @see #ascs
     * @see #descs
     */
    public Pagination setAsc(boolean isAsc) {
        this.isAsc = isAsc;
        return this;
    }


    @Override
    public String toString() {
        return "Pagination { total=" + total + " ,size=" + size + " ,pages=" + pages + " ,current=" + current + " }";
    }

}

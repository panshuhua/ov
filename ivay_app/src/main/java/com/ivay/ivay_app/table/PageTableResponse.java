package com.ivay.ivay_app.table;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询返回
 *
 * @author xx
 */
public class PageTableResponse<T> implements Serializable {

    private static final long serialVersionUID = 620421858510718076L;

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<?> data;

    @Getter
    @Setter
    private T addition;

    public PageTableResponse(Integer recordsTotal, Integer recordsFiltered, List<?> data) {
        super();
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.data = data;
    }

    public Integer getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Integer recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public Integer getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(Integer recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

}
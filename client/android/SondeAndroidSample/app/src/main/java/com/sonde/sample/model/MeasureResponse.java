
package com.sonde.sample.model;

import java.util.List;
import com.google.gson.annotations.Expose;


public class MeasureResponse {

    @Expose
    private List<Measure> measures;
    @Expose
    private String requestId;
    @Expose
    private Long totalPages;
    @Expose
    private Long totalRecords;

    public List<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

}

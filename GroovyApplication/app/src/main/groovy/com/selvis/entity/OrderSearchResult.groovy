package com.selvis.entity

public class OrderSearchResult<T> extends SearchResult<T> {

    List<Date> statDates;

    Totals totals;


    @Override
    public String toString() {
        return "OrderSearchResult{" +
                "statDates=" + statDates +
                ", totals=" + totals +
                ", list=" + list +
                ", skip=" + skip +
                ", count=" + count +
                '}';
    }
}

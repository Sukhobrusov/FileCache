package com.selvis.entity;

import java.util.List;

public class SearchResult<T> {

    List<T> list;

    int skip;

    int count;

    SearchResult() {
    }

    SearchResult(List<T> list,int skip,int count) {
        this.list = list;
        this.skip = skip;
        this.count = count;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "list=" + list +
                ", skip=" + skip +
                ", count=" + count +
                '}';
    }
}

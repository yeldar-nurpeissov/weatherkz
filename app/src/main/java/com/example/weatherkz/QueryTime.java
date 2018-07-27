package com.example.weatherkz;

public class QueryTime {
    private String query;
    private long time;

    public QueryTime(String query, long time) {
        this.query = query;
        this.time = time;
    }

    public String getQuery() {
        return query;
    }

    public long getTime() {
        return time;
    }

    public boolean shouldRefresh() {

        return System.currentTimeMillis() < (time + 3600000);
    }
}
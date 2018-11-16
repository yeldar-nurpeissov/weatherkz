package com.example.weatherkz.pojo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = "query", unique = true)})
public class QueryAndTime {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;
    private String query;
    private long time;

    public QueryAndTime(long id, String query, long time) {
        this.id = id;
        this.query = query;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public String getQuery() {
        return query;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean shouldRefresh() {
        return System.currentTimeMillis() > (time + 3600000);
    }
}
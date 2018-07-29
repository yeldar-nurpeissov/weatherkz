package com.example.weatherkz.pojo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index(value = "query", unique = true)})
public class QueryAndTime {
    @PrimaryKey(autoGenerate = true)
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

    public boolean shouldRefresh() {
        return System.currentTimeMillis() < (time + 3600000);
    }
}
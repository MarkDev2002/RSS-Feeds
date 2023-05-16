package com.example.myapplication;

import java.io.Serializable;
import java.util.Date;

public class Note implements Comparable<Note>, Serializable {
    private String title;
    private String desc;
    private Date date;

    public Note(String title, String desc, Date date) {
        this.title = title;
        this.desc = desc;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int compareTo(Note note) {
        if (note == null || getDate() == null || note.getDate() == null) {
            return 0;
        }
        return note.getDate().compareTo(this.date);
    }

    @Override
    public String toString() {
        return "Title=" + title + ", Desc=" + desc + ", Date=" + date;
    }
}


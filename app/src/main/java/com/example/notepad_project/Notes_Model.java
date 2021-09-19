package com.example.notepad_project;

public class Notes_Model
{

    String title;
    String description;
    String myKey;
    String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMyKey() {
        return myKey;
    }

    public void setMyKey(String myKey) {
        this.myKey = myKey;
    }

    public Notes_Model(String title) {
        this.title = title;
    }

    public Notes_Model(String myKey,String title, String description,String time) {
        this.title = title;
        this.description = description;
        this.myKey=myKey;
        this.time=time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
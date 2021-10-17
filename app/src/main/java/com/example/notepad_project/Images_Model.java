package com.example.notepad_project;

public class Images_Model {

    private String url;
    private String key;

    public Images_Model()
    {

    }

    public Images_Model(String url)
    {
        this.url = url;
    }

    public Images_Model(String url,String key)
    {
        this.url = url;
        this.key=key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

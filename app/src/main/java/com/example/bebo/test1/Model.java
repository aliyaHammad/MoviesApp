package com.example.bebo.test1;

import java.net.URL;

public class Model {

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private URL url;
    private long id;

    public Model(URL url, long id) {
        this.url = url;
        this.id = id;
    }
}
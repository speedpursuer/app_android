package com.lee.cliplay.adapters;

/**
 * Created by xl on 16/6/21.
 */
public class Clip {
    private String url;
    private String desc;

    public Clip(String url, String desc) {
        this.url = url;
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

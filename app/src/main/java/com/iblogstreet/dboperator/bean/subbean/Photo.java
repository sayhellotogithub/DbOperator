package com.iblogstreet.dboperator.bean.subbean;

import com.iblogstreet.dboperator.annotation.DbTable;

import java.util.Date;

/**
 * 类描述：照片信息
 * 创建人：@Armyone
 * 创建时间：2018/3/15
 */
@DbTable("tb_photo")
public class Photo {
    private String desc;
    private String date;
    private String url;

    public String getDesc() {
        return desc;
    }

    public Photo() {
    }

    public Photo(String desc, String date, String url) {
        this.desc = desc;
        this.date = date;
        this.url = url;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

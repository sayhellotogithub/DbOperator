package com.iblogstreet.dboperator.bean;

import com.iblogstreet.dboperator.annotation.DbField;
import com.iblogstreet.dboperator.annotation.DbTable;
import com.iblogstreet.dboperator.annotation.Transient;

/**
 * 类描述：用户表
 * 创建人：@Armyone
 * 创建时间：2018/3/13
 */
@DbTable("tb_user")
public class User {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    @DbField("_id")
    private String id;
    private String name;
    private String password;
    private Integer status;
    @Transient
    private boolean isTest;
    private Integer age;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    public User() {
    }

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", status=" + status +
                '}';
    }
}

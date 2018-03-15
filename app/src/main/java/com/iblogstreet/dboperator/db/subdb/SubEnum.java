package com.iblogstreet.dboperator.db.subdb;

import android.os.Environment;

import com.iblogstreet.dboperator.bean.User;
import com.iblogstreet.dboperator.dao.UserDao;
import com.iblogstreet.dboperator.db.BaseDaoFactory;
import com.iblogstreet.dboperator.util.ConstantField;

import java.io.File;

/**
 * 类描述：用于存储分库的常量字段
 * 创建人：@Armyone
 * 创建时间：2018/3/15
 */

public enum SubEnum {
    SUB_DB("");
    String value;

    SubEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        UserDao userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        if (null != userDao) {
            User currentUser = userDao.getCurrentUser();
            if (null != currentUser) {
                File file = new File(Environment.getExternalStorageDirectory(), "/" + ConstantField.PACKAGE_NAME + "/update" + "/" + currentUser.getId());
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file.getAbsolutePath() + "/" + ConstantField.SUB_DB_NAME;
            }
        }
        return value;
    }
}

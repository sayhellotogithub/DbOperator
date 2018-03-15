package com.iblogstreet.dboperator.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.iblogstreet.dboperator.util.ConstantField;
import com.iblogstreet.dboperator.util.FileUtil;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 类描述：数据库工厂类
 * 创建人：@Armyone
 * 创建时间：2018/3/13
 */

public class BaseDaoFactory {
    public static final BaseDaoFactory instance = new BaseDaoFactory();
    private static final String TAG = BaseDaoFactory.class.getSimpleName();

    public static BaseDaoFactory getInstance() {
        return instance;
    }

    /**
     * 默认数据名
     */
    private static final String DB_DEFAULT_NAME = "iblogstreet";

    //数据库连接池
    protected Map<String, BaseDao> mDaoPoolMap = Collections.synchronizedMap(new HashMap<String, BaseDao>());
    private SQLiteDatabase sqLiteDatabase;
    /**
     * 数据路径
     */
    private String mDataBasePath;
    private String mParentBasePath;

    protected BaseDaoFactory() {
        mParentBasePath = Environment.getExternalStorageDirectory() + "/" + ConstantField.PACKAGE_NAME;
        mDataBasePath = mParentBasePath + "/" + DB_DEFAULT_NAME;
        FileUtil.createFileDir(new File(mParentBasePath));
        Log.e(TAG, "mDataBasePath" + mDataBasePath);
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(mDataBasePath, null);
    }

    public <T extends BaseDao<M>, M> T getBaseDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        if (mDaoPoolMap.get(daoClass.getSimpleName()) != null) {
            Log.e(TAG, "返回链接池中的数据链接");
            return (T) mDaoPoolMap.get(daoClass.getSimpleName());
        }
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
            mDaoPoolMap.put(daoClass.getSimpleName(), baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }
}

package com.iblogstreet.dboperator.db.subdb;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iblogstreet.dboperator.db.BaseDao;
import com.iblogstreet.dboperator.db.BaseDaoFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 类描述：用于分库存储数据
 * 创建人：@Armyone
 * 创建时间：2018/3/15
 */

public class BaseDaoSubFactory extends BaseDaoFactory {
    private static final String TAG = BaseDaoSubFactory.class.getSimpleName();
    public static BaseDaoSubFactory sBaseDaoSubFactory = new BaseDaoSubFactory();

    private SQLiteDatabase mSubSqliteDataBase;

    public static BaseDaoSubFactory getSubInstance() {
        return sBaseDaoSubFactory;
    }

    //保存所有的dao层，实现单例
    protected Map<String, BaseDao> mSubMap = Collections.synchronizedMap(new HashMap<String, BaseDao>());

    private BaseDaoSubFactory() {

    }

    public synchronized <T extends BaseDao<M>, M> T getSubDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        Log.e(TAG, "" + SubEnum.SUB_DB.getValue());
        if (mSubMap.get(daoClass.getSimpleName() + SubEnum.SUB_DB.getValue()) != null) {
            return (T) mSubMap.get(daoClass.getSimpleName() + SubEnum.SUB_DB.getValue());
        }
        mSubSqliteDataBase = SQLiteDatabase.openOrCreateDatabase(SubEnum.SUB_DB.getValue(), null);
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(mSubSqliteDataBase, entityClass);
            mSubMap.put(daoClass.getSimpleName() + SubEnum.SUB_DB.getValue(), baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }
}

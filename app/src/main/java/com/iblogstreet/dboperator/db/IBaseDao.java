package com.iblogstreet.dboperator.db;

import java.util.List;

/**
 * 类描述：数据操作接口
 * 创建人：@Armyone
 * 创建时间：2018/3/13
 */

public interface IBaseDao<T> {
    long insert(T entity);

    long update(T entity, T where);

    int delete(T where);

    List<T> query(T where);
    void beginTran();
    void endTran();
    void setTransactionSuccessful();

    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);
}

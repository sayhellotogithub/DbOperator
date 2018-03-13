package com.iblogstreet.dboperator.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.iblogstreet.dboperator.annotation.DbField;
import com.iblogstreet.dboperator.annotation.DbTable;
import com.iblogstreet.dboperator.annotation.Transient;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * 类描述：数据操作基类
 * 创建人：@Armyone
 * 创建时间：2018/3/13
 */

public class BaseDao<T> implements IBaseDao<T> {
    /**
     * 数据库操作的引用
     */
    private SQLiteDatabase mSqLiteDatabase;
    /**
     * 表名
     */
    private String mTableName;
    /**
     * 操作数据库对应的java类型
     */
    private Class<T> mEntityClass;
    /**
     * 是否做过初始化操作
     */
    private boolean mIsInit = false;
    /**
     * 定义一个缓存空间(key一字段名 value-成员变量)
     */
    private HashMap<String, Field> mCacheMap;

    /**
     * 屏蔽构造，不提供构造方法给调用层使用
     */
    protected BaseDao() {

    }

    public boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        this.mSqLiteDatabase = sqLiteDatabase;
        this.mEntityClass = entityClass;
        if (!mIsInit) {
            //取到表名,如果注解是空的或者没有注解，则取类名作为表名,如果存在注解的话，则取注解中的值作为表名
            if (mEntityClass.getAnnotation(DbTable.class) == null) {
                mTableName = entityClass.getSimpleName();
            } else {
                mTableName = entityClass.getAnnotation(DbTable.class).value();
            }
            if (!mSqLiteDatabase.isOpen()) {
                return false;
            }
            //执行建表操作
            String createTableSql = getCreateTableSql();
            mSqLiteDatabase.execSQL(createTableSql);
            //todo 做下缓存处理
            mCacheMap = new HashMap<>();
            initCacheMap();
            mIsInit = true;
        }
        return true;
    }

    private void initCacheMap() {
        //取得所有的列名
        String sql = "select * from " + mTableName + " limit 1,0";//空表
        Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        //取得所有的成员变量
        Field[] columnFileds = mEntityClass.getDeclaredFields();

        for (Field field : columnFileds) {
            field.setAccessible(true);
        }
        for (String columnName : columnNames) {
            Field columnField = null;
            for (Field field : columnFileds) {
                String fieldName = null;
                if (field.getAnnotation(DbField.class) != null) {
                    fieldName = field.getAnnotation(DbField.class).value();
                } else {
                    fieldName = field.getName();
                }
                if (columnName.equals(fieldName)) {
                    columnField = field;
                    break;
                }
            }
            if (null != columnField) {
                mCacheMap.put(columnName, columnField);
            }
        }
    }

    @Override
    public long insert(T entity) {
        Map<String, String> map = getValues(entity);
        //把数据转移到ContentValues中
        ContentValues values = getContentValues(map);
        //开始插入
        long result = mSqLiteDatabase.insert(mTableName, null, values);

        return result;
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (value != null) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }

    private Map<String, String> getValues(T entity) {
        if (null == entity) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        Iterator<Field> fieldIterator = mCacheMap.values().iterator();
        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            // field.setAccessible(true);
            //获取成员变量的值
            try {
                Object object = field.get(entity);
                if (null == object) {
                    continue;
                }
                String value = object.toString();
                //获取列名
                String key = null;
                if (field.getAnnotation(DbField.class) != null) {
                    key = field.getAnnotation(DbField.class).value();
                } else {
                    key = field.getName();
                }
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    map.put(key, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @Override
    public long update(T entity, T where) {
        int result = -1;
        Map values = getValues(entity);
        ContentValues contentValues = getContentValues(values);
        Map whereCause = getValues(where);
        Condition condition = new Condition(whereCause);
        result = mSqLiteDatabase.update(mTableName, contentValues, condition.whereCasue, condition.whereArgs);
        return result;
    }

    @Override
    public int delete(T where) {
        Map map = getValues(where);
        Condition condition = new Condition(map);
        return mSqLiteDatabase.delete(mTableName, condition.whereCasue, condition.whereArgs);
    }


    private class Condition {
        private String whereCasue;//id=? and name=?
        private String[] whereArgs;//new String[]{"1","wangjun"}

        public Condition(Map<String, String> whereCasue) {
            if (null == whereCasue) {
                this.whereCasue = null;
                this.whereArgs = null;

            } else {
                ArrayList list = new ArrayList();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("1=1");
                //取所有的字段名
                Set keys = whereCasue.keySet();
                Iterator iterator = keys.iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    String value = whereCasue.get(key);
                    if (null != value) {
                        stringBuilder.append(" and " + key + "=?");
                        list.add(value);
                    }
                }
                this.whereCasue = stringBuilder.toString();
                this.whereArgs = (String[]) list.toArray(new String[list.size()]);
            }
        }
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public void beginTran() {
        mSqLiteDatabase.beginTransaction();
    }

    @Override
    public void endTran() {
        mSqLiteDatabase.endTransaction();
    }

    @Override
    public void setTransactionSuccessful() {
        mSqLiteDatabase.setTransactionSuccessful();
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        //query(tableName,null,"id=?",new String[],null,null,orderBy,"1,5");
        Map map = getValues(where);
        String limitString = null;
        if (null != startIndex && null != limit) {
            limitString = startIndex + " , " + limit;
        }
        Condition condition = new Condition(map);
        Cursor cursor = mSqLiteDatabase.query(mTableName, null, condition.whereCasue, condition.whereArgs, null, null, orderBy, limitString);
        if (null != cursor) {
            return getResult(cursor, where);
        }
        return null;
    }

    /**
     * 遍历结果
     *
     * @param cursor
     * @param obj
     * @return
     */
    private List<T> getResult(Cursor cursor, T obj) {
        if (null == cursor) {
            return null;
        }
        Object item;
        List<T> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            try {
                item = obj.getClass().newInstance();
                Iterator iterator = mCacheMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    //列名
                    String columnName = (String) entry.getKey();
                    Field field = (Field) entry.getValue();
                    Class<?> type = field.getType();
                    int columnIndex = cursor.getColumnIndex(columnName);
                    //列存在，就添加
                    if (columnIndex != -1) {
                        if (type.equals(String.class)) {
                            field.set(item, cursor.getString(columnIndex));
                        } else if (type.equals(Integer.class)) {
                            field.set(item, cursor.getInt(columnIndex));
                        } else if (type.equals(Long.class)) {
                            field.set(item, cursor.getLong(columnIndex));
                        } else if (type.equals(Double.class)) {
                            field.set(item, cursor.getDouble(columnIndex));
                        } else if (type.equals(Byte[].class)) {
                            field.set(item, cursor.getBlob(columnIndex));
                        } else {
                            continue;
                        }
                    }

                }
                list.add((T) item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
    }

    public String getCreateTableSql() {
        StringBuffer sql = new StringBuffer();
        sql.append("create table if not exists ");
        sql.append(mTableName + " (");
        //反射得到所有的成员变量
        Field[] fields = mEntityClass.getDeclaredFields();
        for (Field field : fields) {
            Class type = field.getType();//拿到成员的类型
            if (field.getAnnotation(DbField.class) != null) {
                if (type == String.class) {
                    sql.append(field.getAnnotation(DbField.class).value() + " TEXT,");
                } else if (type == Integer.class) {
                    sql.append(field.getAnnotation(DbField.class).value() + " INTEGER,");
                } else if (type == Long.class) {
                    sql.append(field.getAnnotation(DbField.class).value() + " BIGINT,");
                } else if (type == Double.class) {
                    sql.append(field.getAnnotation(DbField.class).value() + " DOUBLE,");
                } else if (type == byte[].class) {
                    sql.append(field.getAnnotation(DbField.class).value() + " BLOB,");
                } else {
                    Log.e(TAG, "不支持的类型" + type);
                    continue;
                }
            } else {
                //如果存在Transient这个注解
                if (field.getAnnotation(Transient.class) != null) {
                    continue;
                }
                if (type == String.class) {
                    sql.append(field.getName() + " TEXT,");
                } else if (type == Integer.class) {
                    sql.append(field.getName() + " INTEGER,");
                } else if (type == Long.class) {
                    sql.append(field.getName() + " BIGINT,");
                } else if (type == Double.class) {
                    sql.append(field.getName() + " DOUBLE,");
                } else if (type == byte[].class) {
                    sql.append(field.getName() + " BLOB,");
                } else {
                    Log.e(TAG, "不支持的类型" + type);
                    continue;
                }
            }

        }
        //去掉最后一个逗号
        if (sql.charAt(sql.length() - 1) == ',') {
            sql.deleteCharAt(sql.length() - 1);
        }
        sql.append(" )");
        return sql.toString();
    }
}

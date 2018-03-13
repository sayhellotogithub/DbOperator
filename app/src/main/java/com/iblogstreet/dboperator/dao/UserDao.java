package com.iblogstreet.dboperator.dao;

import com.iblogstreet.dboperator.bean.User;
import com.iblogstreet.dboperator.db.BaseDao;

import java.util.List;

/**
 * 用于维护公有数据
 */

public class UserDao extends BaseDao<User> {

    @Override
    public long insert(User entity) {
//        //查到表中所有的用户记录
//        List<User> list = query(new User());
//        User where = null;
//        if (null != list) {
//            for (User user : list) {
//                where = new User();
//                where.setId(user.getId());
//                user.setStatus(0);
//                update(user, where);
//            }
//        }
//        entity.setStatus(1);
        return super.insert(entity);
    }

    /**
     * 得到当前登录的User
     */
    public User getCurrentUser() {
        User user = new User();
        user.setStatus(1);
        List<User> list = query(user);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}















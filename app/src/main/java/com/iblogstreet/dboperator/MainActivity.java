package com.iblogstreet.dboperator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.iblogstreet.dboperator.bean.User;
import com.iblogstreet.dboperator.bean.subbean.Photo;
import com.iblogstreet.dboperator.dao.UserDao;
import com.iblogstreet.dboperator.dao.subdao.PhotoDao;
import com.iblogstreet.dboperator.db.BaseDao;
import com.iblogstreet.dboperator.db.BaseDaoFactory;
import com.iblogstreet.dboperator.db.subdb.BaseDaoSubFactory;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "" + getApplicationContext().getFilesDir());
    }

    public void onDelete(View view) {
        BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        User user = new User("1", "test1", "123245");
        baseDao.delete(user);
    }


    public void onAdd(View view) {
//        new Thread() {
//            @Override
//            public void run() {
//                long startTime = System.currentTimeMillis();
//                Log.e(TAG, "开始时间" + System.currentTimeMillis());
//                BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
//
//                for (int i = 4000; i < 4100; i++) {
//                    User user = new User("" + i, "test1", "123245");
//                    baseDao.insert(user);
//                }
//                Log.e(TAG, "总用时" + (System.currentTimeMillis() - startTime));
//            }
//        }.start();

        long startTime = System.currentTimeMillis();
        Log.e(TAG, "开始时间" + System.currentTimeMillis());
        UserDao baseDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        int maxId = baseDao.getMaxId();

        User user = new User("" + maxId, "test" + maxId, "123245");
        baseDao.insert(user);
        Log.e(TAG, "总用时" + (System.currentTimeMillis() - startTime));
    }

    public void onUpdate(View view) {
        BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        User user = new User("2", "wj", "99999");
        User user1 = new User();
        //user1.setId("2");
        baseDao.update(user, user1);
    }

    public void onSelect(View view) {
        BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);

        List<User> list = baseDao.query(new User());
        if (null != list) {
            Log.e(TAG, "onSelect " + list.toString());
        }
    }

    int loginId = 0;

    public void onLogin(View view) {
        UserDao baseDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);

        User user = new User("" + loginId, "test" + loginId, "123245");

        User user1 = new User();
        user1.setId("" + loginId);
        baseDao.update(user, user1);
        if (baseDao.getMaxId() <= loginId) {
            loginId = 0;
        } else {
            loginId++;
        }
        Log.e(TAG, "多用户登录" + loginId);
    }

    int pCount = 0;

    public void onSubInsert(View view) {
        PhotoDao subDao = BaseDaoSubFactory.getSubInstance().getSubDao(PhotoDao.class, Photo.class);
        Photo photo = new Photo("desc" + pCount, new Date().toString(), "photoPath" + pCount);
        subDao.insert(photo);
        pCount++;
    }
}

package com.vivi.asyncmvc.library.plugs.sqlite;

import android.content.Context;
import android.studio.os.EnvironmentUtils;
import android.text.TextUtils;

import com.vivi.asyncmvc.api.entity.CarLicense;
import com.vivi.asyncmvc.api.entity.DriverLicense;
import com.vivi.asyncmvc.api.entity.Message;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;

import com.vivi.asyncmvc.base.BaseApplication;
import com.vivi.asyncmvc.comm.managers.LoginManager;

/**
 * Description DB工具类
 *
 * @author gongwei
 * @created 2018/12/19
 */
public class AOrm {

    public static DataBase db;
    public static DataBase userDb;
    private static String userId;
    private static String COMMON_DB_NAME = "common_db";

    private AOrm() {
    }

    public static DataBase getCommonDb(Context context) {
        if (db == null) {
            db = newCascadeInstance(context, "hik_common_db");
        }
        return db;
    }

    public static DataBase getUserOrm() {
        return getUserOrm(BaseApplication.appContext);
    }

    public static DataBase getUserOrm(Context context) {
        //一个用户一个DB，若用户切换账号之后，要重新new UserDB
        if (userDb == null) {
            userId = LoginManager.getInstance().getUserId();
            if (TextUtils.isEmpty(userId)) userId = COMMON_DB_NAME;
            userDb = newUserDb(context, userId);
        } else {
            String userIdNow = LoginManager.getInstance().getUserId();
            if (!TextUtils.isEmpty(userIdNow) && !userIdNow.equals(userId)) {
                userDb.close();
                userId = userIdNow;
                userDb = newUserDb(context, userId);
            }
        }
        return userDb;
    }

    private static DataBase newUserDb(Context context, String dbName) {
        DataBase db = newCascadeInstance(context, dbName);
        //Class参数为需要映射为数据库表的Entity，可以映射多个实体
        db.getTableManager().checkOrCreateTable(db.getWritableDatabase(), DriverLicense.class);
        db.getTableManager().checkOrCreateTable(db.getWritableDatabase(), CarLicense.class);
        db.getTableManager().checkOrCreateTable(db.getWritableDatabase(), Message.class);
        return db;
    }

    private static DataBase newCascadeInstance(Context context, String dbName) {
        try {
            return LiteOrm.newCascadeInstance(context, getDbName(context, dbName));
        } catch (Exception e) {
            return LiteOrm.newCascadeInstance(context, dbName);
        }
    }

    private static String getDbName(Context context, String dbName) {
        boolean hasSDCard = EnvironmentUtils.hasMounted();
        return hasSDCard ? context.getExternalFilesDir(null) + "/" + dbName : dbName;
    }

    public static void close() {
        if (db != null) {
            db.close();
            db = null;
        }
        if (userDb != null) {
            userDb.close();
            userDb = null;
        }
    }
}

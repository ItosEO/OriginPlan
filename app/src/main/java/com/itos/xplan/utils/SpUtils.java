package com.itos.xplan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

public class SpUtils {

    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "share_date";


    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param context
     * @param key
     * @param object
     */
    public static void setParam(Context context , String key, Object object){

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        switch (type) {
            case "String":
                editor.putString(key, (String) object);
                break;
            case "Integer":
                editor.putInt(key, (Integer) object);
                break;
            case "Boolean":
                editor.putBoolean(key, (Boolean) object);
                break;
            case "Float":
                editor.putFloat(key, (Float) object);
                break;
            case "Long":
                editor.putLong(key, (Long) object);
                break;
        }

        editor.apply();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object getParam(Context context , String key, Object defaultObject){
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        switch (type) {
            case "String":
                return sp.getString(key, (String) defaultObject);
            case "Integer":
                return sp.getInt(key, (Integer) defaultObject);
            case "Boolean":
                return sp.getBoolean(key, (Boolean) defaultObject);
            case "Float":
                return sp.getFloat(key, (Float) defaultObject);
            case "Long":
                return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    public static void putSettingsParam(Context context , String fenqu,String key, Object object){
        String type = object.getClass().getSimpleName();
        switch (fenqu) {
            case "system":
                switch (type) {
                    case "String":
                    case "Boolean":
                        Settings.System.putString(context.getContentResolver(), key, (String) object);
                        break;
                    case "Integer":
                        Settings.System.putInt(context.getContentResolver(), key, (Integer) object);
                        break;
                    case "Float":
                        Settings.System.putFloat(context.getContentResolver(), key, (Float) object);
                        break;
                    case "Long":
                        Settings.System.putLong(context.getContentResolver(), key, (Long) object);
                        break;
                }
            case "global":
                switch (type) {
                    case "String":
                    case "Boolean":
                        Settings.Global.putString(context.getContentResolver(), key, (String) object);
                        break;
                    case "Integer":
                        Settings.Global.putInt(context.getContentResolver(), key, (Integer) object);
                        break;
                    case "Float":
                        Settings.Global.putFloat(context.getContentResolver(), key, (Float) object);
                        break;
                    case "Long":
                        Settings.Global.putLong(context.getContentResolver(), key, (Long) object);
                        break;
                }
            case "secure":
                switch (type) {
                    case "String":
                    case "Boolean":
                        Settings.Secure.putString(context.getContentResolver(), key, (String) object);
                        break;
                    case "Integer":
                        Settings.Secure.putInt(context.getContentResolver(), key, (Integer) object);
                        break;
                    case "Float":
                        Settings.Secure.putFloat(context.getContentResolver(), key, (Float) object);
                        break;
                    case "Long":
                        Settings.Secure.putLong(context.getContentResolver(), key, (Long) object);
                        break;
                }
        }
    }
    public static String getSettingsParam(Context context , String fenqu,String key){
        switch (fenqu) {
            case "system":
                return Settings.System.getString(context.getContentResolver(), key);
            case "global":
                return Settings.Global.getString(context.getContentResolver(), key);
            case "secure":
                return Settings.Secure.getString(context.getContentResolver(), key);
            default:
                return "null";
        }
    }

}

package com.itos.originplan;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;

import java.lang.reflect.Method;

import rikka.shizuku.Shizuku;
import rikka.shizuku.shared.BuildConfig;

public class UserService extends IUserService.Stub {
    @Override
    public void destroy() throws RemoteException {
        System.exit(0);
    }

    @Override
    public void exit() throws RemoteException {
        System.exit(0);
    }
    @SuppressLint("PrivateApi")
    public Object getIPackageManager() {
        try {
            Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
            Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);

            // 获取 "package" 服务的 Binder 对象
            IBinder binder = (IBinder) getServiceMethod.invoke(null, "package");

            // 调用 asInterface 方法获得 IPackageManager 接口对象
            Class<?> iPackageManagerClass = Class.forName("android.content.pm.IPackageManager");
            Method asInterfaceMethod = iPackageManagerClass.getMethod("asInterface", IBinder.class);
            return asInterfaceMethod.invoke(null, binder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean setApplicationEnabled(String Package, boolean isDisabled) throws RemoteException {
        Object iPackageManager = getIPackageManager();
        int newState = isDisabled ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED :
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        if (iPackageManager != null) {
            try {
                Method setApplicationEnabledSettingMethod =
                        iPackageManager.getClass().getMethod("setApplicationEnabledSetting",
                                String.class, int.class);

                // 调用 setApplicationEnabledSetting 方法
                setApplicationEnabledSettingMethod.invoke(iPackageManager, Package, newState);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // 处理获取 IPackageManager 失败的情况
            return false;
        }
    }

}

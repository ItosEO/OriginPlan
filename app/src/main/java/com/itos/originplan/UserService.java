package com.itos.originplan;

import android.os.RemoteException;

public class UserService extends IUserService.Stub{
    @Override
    public void destroy() throws RemoteException {
        System.exit(0);
    }

    @Override
    public void exit() throws RemoteException {
        System.exit(0);
    }
}

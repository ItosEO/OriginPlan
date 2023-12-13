// IUserService.aidl
package com.itos.originplan;
import android.content.Context;

// Declare any non-default types here with import statements

interface IUserService  {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void destroy()=16777114;
    void exit()=1;
    boolean setApplicationEnabled(String Package, boolean isDisabled)=2;

}
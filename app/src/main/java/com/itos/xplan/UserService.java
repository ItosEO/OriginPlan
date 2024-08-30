package com.itos.xplan;

import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class UserService extends IUserService.Stub {

    @Override
    public void destroy() throws RemoteException {
        System.exit(0);
    }

    @Override
    public void exit() throws RemoteException {
        destroy();
    }

    @Override
    public String exec(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec("sh");
            OutputStream out = process.getOutputStream();
            out.write(cmd.getBytes());
            out.flush();
            out.close();
            StringBuilder sb = new StringBuilder();
            new Thread(() -> {
                try {
                    StringBuilder outputNormal = new StringBuilder();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String inline;
                    while ((inline = bufferedReader.readLine()) != null) {
                        outputNormal.append(inline).append("\n");

                        sb.append(inline).append("\n");
                    }
                    bufferedReader.close();

                    Log.d("Shizuku错误输出", String.valueOf(outputNormal)); // 添加日志输出

                } catch (IOException ignored) { }
            }).start();
            new Thread(() -> {
                try {
                    StringBuilder outputError = new StringBuilder();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String inline;
                    while ((inline = bufferedReader.readLine()) != null) {
                        outputError.append(inline).append("\n");

                        sb.append(inline).append("\n");
                    }
                    bufferedReader.close();

                    Log.d("Shizuku错误输出", String.valueOf(outputError)); // 添加日志输出

                } catch (IOException ignored) { }
            }).start();

            process.waitFor();
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

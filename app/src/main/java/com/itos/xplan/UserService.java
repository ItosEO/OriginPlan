package com.itos.xplan;

import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class UserService extends IUserService.Stub {

    @Override
    public void destroy() {
        System.exit(0);
    }

    @Override
    public void exit() {
        destroy();
    }

    @Override
    public String exec(String cmd) {
        Log.d("[DEBUG] Shizuku执行：", cmd);
        try {
            Process process = Runtime.getRuntime().exec("sh");
            OutputStream out = process.getOutputStream();
            out.write(cmd.getBytes());
            out.flush();
            out.close();
            StringBuilder sb = new StringBuilder();
            StringBuilder outputError = new StringBuilder();
            StringBuilder outputNormal = new StringBuilder();
            new Thread(() -> {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String inline;
                    while ((inline = bufferedReader.readLine()) != null) {
                        outputNormal.append(inline).append("\n");

                        sb.append(inline).append("\n");
                    }
                    bufferedReader.close();
                } catch (IOException ignored) { }
            }).start();
            new Thread(() -> {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String inline;
                    while ((inline = bufferedReader.readLine()) != null) {
                        outputError.append(inline).append("\n");

                        sb.append(inline).append("\n");
                    }
                    bufferedReader.close();
                } catch (IOException ignored) { }
            }).start();
            process.waitFor();
            if (!outputError.toString().isEmpty()) Log.d("[DEBUG] Shizuku错误输出", outputError.toString());
            Log.d("[DEBUG] Shizuku正常输出", outputNormal.toString());
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

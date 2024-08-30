package com.itos.xplan;

import android.os.RemoteException;

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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String inline;
            while ((inline = bufferedReader.readLine()) != null) {
                sb.append(inline);
                sb.append("\n");
            }
            bufferedReader.close();
            process.waitFor();
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

package com.example.valer.myapplication;

import java.sql.Connection;
import java.sql.SQLException;
import android.util.Log;
import android.os.StrictMode;
import java.sql.DriverManager;
import android.annotation.SuppressLint;

public class ConnectionClass {
    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        String DBUserNameStr,DBPasswordStr,db,ip;
        ip = "sql5044.site4now.net";
        db="DB_A4D6ED_valera";
        DBUserNameStr = "DB_A4D6ED_valera_admin";
        DBPasswordStr = "564236Qaz";
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            ConnectionURL = "jdbc:jtds:sqlserver://" + ip +";databaseName="+ db + ";user=" + DBUserNameStr+ ";password=" + DBPasswordStr + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        }
        catch (java.sql.SQLException se) { Log.e("error here 1 : ", se.getMessage()); }
        catch (ClassNotFoundException e) { Log.e("error here 2 : ", e.getMessage()); }
        catch (Exception e) { Log.e("error here 3 : ", e.getMessage()); }
        return connection;
    }
}

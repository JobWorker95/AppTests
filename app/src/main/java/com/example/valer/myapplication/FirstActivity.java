package com.example.valer.myapplication;

import java.sql.Connection;
import android.content.Intent;
import android.os.AsyncTask;
import java.sql.Statement;
import java.sql.ResultSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.app.AlertDialog;

public class FirstActivity extends AppCompatActivity {

    public static int Access=0;
    public static int LoginID=0;
    String usernam,passwordd;
    ProgressBar progressBar;
    EditText username,password;
    private ConnectionClass CON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        Button butLogin = findViewById(R.id.butLogin);
        Button butGuest = findViewById(R.id.butGuest);

        TextView textCheck = findViewById(R.id.textCheck);
        TextView textForgot = findViewById(R.id.textForgot);
        username = findViewById(R.id.editLogin);
        password = findViewById(R.id.editPass);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        butLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                usernam = username.getText().toString();
                passwordd = password.getText().toString();
                CheckLogin checkLogin = new CheckLogin();
                checkLogin.execute("");
            }
        });
        butGuest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Access = 0;
                Intent intent = new Intent(FirstActivity.this, TeacherActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onClickAbout(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog_about,null);
        builder.setCancelable(false);
        builder.setView(dialogView);
        Button btn_negative = (Button) dialogView.findViewById(R.id.dialog_negative_btn);

        //TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
        //dialog_title.setText("");

        final AlertDialog dialog = builder.create();
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void onClickCheck(View view) {
        CON = new ConnectionClass();
        Connection con = CON.CONN();
        if (con == null) {
            Toast.makeText(FirstActivity.this, "Проверьте интернет соединение!", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(FirstActivity.this, CheckActivity.class);
            startActivity(intent);
        }
    }

    public class CheckLogin extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        @Override
        protected void onPreExecute()

        {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(FirstActivity.this, r, Toast.LENGTH_LONG).show();
            if(isSuccess) {
                //Toast.makeText(FirstActivity.this , "Login Successfull" , Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(String... params) {
            if(usernam.trim().equals("")|| passwordd.trim().equals(""))
                z = "Введите Логин и Пароль";
            else {
                try {
                    CON = new ConnectionClass();
                    Connection con = CON.CONN();
                    if (con == null) z = "Проверьте интернет соединение!";
                    else {
                        String query = "select * from LOGIN where USERNAME= '" + usernam.toString() + "' and PASSWORD = '"+ passwordd.toString() +"' ";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next()) {
                            z = "Добро пожаловать "+rs.getString("NAME");
                            isSuccess=true;
                            Access = rs.getInt("ACCESS");
                            LoginID = rs.getInt("LOGINID");
                            Intent intent = new Intent(FirstActivity.this, TeacherActivity.class);
                            startActivity(intent);
                            con.close();
                        }
                        else {
                            z = "Данные не найдены!";
                            isSuccess = false;
                        }
                    }
                }
                catch (Exception ex) {
                    isSuccess = false;
                    z = ex.getMessage();
                }
            }
            return z;
        }
    }

    @Override public void onBackPressed() {
        finish();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
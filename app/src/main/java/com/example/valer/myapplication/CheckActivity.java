package com.example.valer.myapplication;

import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.content.Context;
import java.io.IOException;
import android.os.AsyncTask;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;
import android.widget.Toast;

public class CheckActivity extends AppCompatActivity {

    private ArrayList<String> FacultyName = new ArrayList<String>();
    private ArrayList<Integer> FacultyID = new ArrayList<Integer>();
    private ArrayList<String> GroupName = new ArrayList<String>();
    private ConnectionClass CON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        Button butCheck = findViewById(R.id.butCheck);

        final EditText editLogin = findViewById(R.id.editLogin);
        final EditText editPass = findViewById(R.id.editPass);
        final EditText editFam = findViewById(R.id.editFam);
        final EditText editName = findViewById(R.id.editName);
        final EditText editOtch = findViewById(R.id.editOtch);
        final EditText editZach = findViewById(R.id.editZach);

        Spinner spinFac = (Spinner) findViewById(R.id.spinFac);

        butCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editLogin.length() < 2) { editLogin.setHintTextColor(Color.RED); return; }
                if(editPass.length() < 2) { editPass.setHintTextColor(Color.RED); return; }
                if(editFam.length() < 2) { editFam.setHintTextColor(Color.RED); return; }
                if(editName.length() < 2) { editName.setHintTextColor(Color.RED); return; }
                if(editOtch.length() < 2) { editOtch.setHintTextColor(Color.RED); return; }
                if(editZach.length() < 2) { editZach.setHintTextColor(Color.RED); return; }

                CON = new ConnectionClass();
                Connection con = CON.CONN();
                try{
                    String query = "select USERNAME from LOGIN where USERNAME= '" + editLogin.getText().toString() + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if(!rs.next()){
                        query = "INSERT INTO LOGIN(USERNAME, PASSWORD, ACCESS, FAM, NAME, OTCH, NOMZA) " + "VALUES (N'" + editLogin.getText() +
                                "',N'" + editPass.getText() +
                                "','3',N'" + editFam.getText() +
                                "',N'" + editName.getText() +
                                "',N'" + editOtch.getText() +
                                "','" + editZach.getText() + "');";
                        stmt.executeUpdate(query);
                        con.close();
                        Intent intent = new Intent(v.getContext(), FirstActivity.class);
                        v.getContext().startActivity(intent);

                        return;
                    }else{
                        editLogin.setText("");
                        editLogin.setHint("Логин существует,придумайте уникальный Логин");
                    }
                    con.close();
                }
                catch (Exception ex) { ex.getMessage(); }
            }
        });
        CON = new ConnectionClass();
        Connection con = CON.CONN();
        try{

            String query = "SELECT * FROM FACULTETS";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                FacultyName.add(rs.getString("FACULTETNAME"));
                FacultyID.add(rs.getInt("FACULTETID"));
            }
        }
        catch (Exception ex)
        {
            ex.getMessage();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FacultyName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinFac.setAdapter(adapter);
        // заголовок
        spinFac.setPrompt("Title");
        // выделяем элемент
        spinFac.setSelection(0);
        // устанавливаем обработчик нажатия
        spinFac.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                GroupRead(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // openQuitDialog();
        Intent intent = new Intent(this, FirstActivity.class);
        startActivity(intent);
    }
    public void GroupRead(final int selPos){

        CON = new ConnectionClass();
        Connection con = CON.CONN();
        try{
            String query = "SELECT * FROM GRUPPS WHERE FACULTETID = '"+FacultyID.get(selPos).toString()+"';";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            GroupName.clear();
            while (rs.next()) {
                GroupName.add(rs.getString("GRUPPNAME"));
                Toast.makeText(getBaseContext(), "Name = " + rs.getString("GRUPPNAME"), Toast.LENGTH_SHORT).show();
            }
            con.close();
        }
        catch (Exception ex)
        {
            ex.getMessage();
        }

        ArrayAdapter<String> adapterGroup = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, GroupName);

        adapterGroup.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinGroup = (Spinner) findViewById(R.id.spinGroup);
        spinGroup.setAdapter(adapterGroup);
        spinGroup.setSelection(0);
        spinGroup.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                Toast.makeText(getBaseContext(), "PositionGroup = " + FacultyID.get(selPos).toString(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
}

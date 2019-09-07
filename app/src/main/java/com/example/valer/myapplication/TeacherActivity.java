package com.example.valer.myapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.Context;
import java.util.ArrayList;
import android.os.AsyncTask;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import android.app.AlertDialog;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.database.SQLException;
import java.io.IOException;

public class TeacherActivity extends AppCompatActivity {

    private Menu menu;
    private MenuItem item;
    private DatabaseHelper mDBHelper;
    private Connection connectionClass;
    private SQLiteDatabase mDb;
    public static Boolean InternetView=false;
    private ArrayList<String> FacultyName = new ArrayList<String>();
    private ArrayList<Integer> FacultyID = new ArrayList<Integer>();
    private ConnectionClass CON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        InetnetCheck();
        Button but3 = findViewById(R.id.button3);
        Button but5 = findViewById(R.id.button5);
        if(FirstActivity.Access == 0){
            but3.setVisibility(View.GONE);
            but5.setVisibility(View.GONE);
        }
        but3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InternetView=false;
                InetnetCheck();
            }
        });
        but5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InternetView=true;
                InetnetCheck();
            }
        });

    }
    public void InetnetCheck() {
        ArrayList<Product> products = new ArrayList();
        Button but3 = findViewById(R.id.button3);
        Button but5 = findViewById(R.id.button5);
        if(InternetView == true){
            but5.setBackgroundColor(Color.parseColor("#878787"));
            but3.setBackgroundColor(Color.parseColor("#212121"));
            CON = new ConnectionClass();
            Connection con = CON.CONN();
            try{
                String query = "SELECT * FROM TESTSNAMES";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    products.add(new Product(rs.getString("TESTNAME")+" ID("+rs.getString("TESTSNAMESID")+")", "Ред.", "Удалить", "Скачать", "Контрольная", R.drawable.editnew, R.drawable.delete, R.drawable.download, R.drawable.control2, rs.getString("TESTSNAMESID")));
                }
            }
            catch (Exception ex) {
                ex.getMessage();
            }
        }
        else
        {
            but3.setBackgroundColor(Color.parseColor("#878787"));
            but5.setBackgroundColor(Color.parseColor("#212121"));
            mDBHelper = new DatabaseHelper(this);
            try {
                mDBHelper.updateDataBase();
            } catch (IOException mIOException) {
                throw new Error("UnableToUpdateDatabase");
            }
            try {
                mDb = mDBHelper.getWritableDatabase();
            } catch (SQLException mSQLException) {
                throw mSQLException;
            }
            mDb = mDBHelper.getWritableDatabase();
            String selectQuery = "SELECT * FROM TESTSNAMES";
            Cursor cursor3 = mDb.rawQuery(selectQuery, null);
            String[] txtData = new String[cursor3.getCount()];
            cursor3.moveToFirst();
            int i=0;
            while (!cursor3.isAfterLast()) {
                txtData[i] = cursor3.getString(cursor3.getColumnIndex("TESTNAME"));
                products.add(new Product(txtData[i]+" ID("+cursor3.getString(cursor3.getColumnIndex("TESTSNAMESID"))+")", "Ред.", "Удалить", "Обучение", "Просмотр", R.drawable.editnew, R.drawable.delete, R.drawable.study, R.drawable.view, cursor3.getString(cursor3.getColumnIndex("TESTSNAMESID"))));
                cursor3.moveToNext();
                i++;
            }
            cursor3.close();
        }
        ListView productList = (ListView) findViewById(R.id.lTeacher);
        ProductAdapter adapter = new ProductAdapter(this, R.layout.list_items, products);
        productList.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teacher, menu);
        if(FirstActivity.Access == 0 || FirstActivity.Access == 3){
            menu.findItem(R.id.aFaculty).setVisible(false);
            menu.findItem(R.id.aGroups).setVisible(false);
            menu.findItem(R.id.aResult).setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.item = item;
        int id = item.getItemId();
        switch (id) {
            case R.id.aCreate: {
                Intent intent = new Intent(TeacherActivity.this, CreateTestActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.aFAdd: {
                onClickCreateFac();
                return true;
            }
            case R.id.aFDelete: {
                return true;
            }
            case R.id.aGAdd: {
                onClickCreateGroup();
                return true;
            }
            case R.id.aGDelete: {
                return true;
            }
            case R.id.aResult: {
                onClickResult();
                return true;
            }
            default:
            return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // openQuitDialog();
        Intent intent = new Intent(this, FirstActivity.class);
        startActivity(intent);
    }

    public void onClickFoundResult(String Result,String Fam,String Name,String Otch,String NumRecord,String TestID,String TestName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog_finish_test,null);
        builder.setCancelable(false);
        builder.setView(dialogView);
        Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);
        Button btn_negative = (Button) dialogView.findViewById(R.id.dialog_negative_btn);
        TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);

        int numright=0;
        for(int x=0; x<Result.length(); x++){
            if(Result.charAt(x) == '1'){ numright++; }
        }

        dialog_title.setText("Результат теста: "+TestName+"("+TestID+")"+"\n\n"+
                "Фамилия: "+Fam+"\n"+
                "Имя: "+Name+"\n"+
                "Отчество: "+Otch+"\n"+
                "№ Зачетки: "+NumRecord+"\n\n"+
                "Всего вопросов: "+Result.length()+"\n"+
                "Ошибок: "+Integer.toString(Result.length()-numright)+"\n"+
                "Верных ответов: "+Integer.toString(numright));

        final AlertDialog dialog = builder.create();
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                dialog.cancel();
            }
        });
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss/cancel the alert dialog
                //dialog.cancel();
                dialog.dismiss();
                Toast.makeText(getApplication(),
                        "No button clicked", Toast.LENGTH_SHORT).show();
                }
        });
        dialog.show();
        return;
    }

    public void onClickResult() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog_result,null);
        builder.setCancelable(false);
        builder.setView(dialogView);
        Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);
        Button btn_negative = (Button) dialogView.findViewById(R.id.dialog_negative_btn);

        final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
        //dialog_title.setText("");
        final EditText editFam = (EditText) dialogView.findViewById(R.id.editFam);
        final EditText editName = (EditText) dialogView.findViewById(R.id.editName);
        final EditText editOtch = (EditText) dialogView.findViewById(R.id.editOtch);

        final AlertDialog dialog = builder.create();
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.cancel();
                //Toast.makeText(getApplication(), "Submitted name : " + name, Toast.LENGTH_SHORT).show();
                int F_len = editFam.getText().length();
                int N_len = editName.getText().length();
                int O_len = editOtch.getText().length();
                CON = new ConnectionClass();
                Connection con = CON.CONN();
                try {
                    String query="";
                    if(F_len>0 && N_len>0 && O_len>0)  query = "Select * from LOGIN l, TESTSRESULTS t,TESTSNAMES tn where l.LOGINID=t.LOGINID and tn.TESTSNAMESID=t.TESTSNAMESID and FAM=N'"+editFam.getText()+"' and NAME=N'"+editName.getText()+"' and OTCH=N'"+editOtch.getText()+"'";
                    else if(F_len>0 && N_len>0)  query = "Select * from LOGIN l, TESTSRESULTS t,TESTSNAMES tn where l.LOGINID=t.LOGINID and tn.TESTSNAMESID=t.TESTSNAMESID and FAM=N'"+editFam.getText()+"' and NAME=N'"+editName.getText()+"'";
                    else if(N_len>0 && O_len>0)  query = "Select * from LOGIN l, TESTSRESULTS t,TESTSNAMES tn where l.LOGINID=t.LOGINID and tn.TESTSNAMESID=t.TESTSNAMESID and NAME=N'"+editName.getText()+"' and OTCH=N'"+editOtch.getText()+"'";
                    else if(F_len>0)  query = "Select * from LOGIN l, TESTSRESULTS t,TESTSNAMES tn where l.LOGINID=t.LOGINID and tn.TESTSNAMESID=t.TESTSNAMESID and FAM=N'"+editFam.getText()+"'";
                    else if(N_len>0)  query = "Select * from LOGIN l, TESTSRESULTS t,TESTSNAMES tn where l.LOGINID=t.LOGINID and tn.TESTSNAMESID=t.TESTSNAMESID and NAME=N'"+editName.getText()+"'";
                    else if(O_len>0)  query = "Select * from LOGIN l, TESTSRESULTS t,TESTSNAMES tn where l.LOGINID=t.LOGINID and tn.TESTSNAMESID=t.TESTSNAMESID and OTCH=N'"+editOtch.getText()+"'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    onClickFoundResult(rs.getString("STUDRESULT"), rs.getString("FAM"), rs.getString("NAME"), rs.getString("OTCH"), rs.getString("NOMZA"), rs.getString("TESTSNAMESID"), rs.getString("TESTNAME"));
                    //dialog_title.setText(rs.getString("STUDRESULT").toString());
                    con.close();
                }
                catch (Exception ex) { ex.getMessage(); }
            }
        });
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void onClickCreateFac() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog_custom_view,null);
        builder.setCancelable(false);
        builder.setView(dialogView);
        Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);
        Button btn_negative = (Button) dialogView.findViewById(R.id.dialog_negative_btn);
        Spinner spinnerFac = (Spinner) dialogView.findViewById(R.id.spinnerFac);
        TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
        dialog_title.setText("Введите название факультета: ");
        spinnerFac.setVisibility(View.GONE);
        final EditText et_name = (EditText) dialogView.findViewById(R.id.et_name);
        et_name.setHint("Введите название факультета");
        final AlertDialog dialog = builder.create();
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                dialog.cancel();
                String name = et_name.getText().toString();
                Toast.makeText(getApplication(),
                        "Submitted name : " + name, Toast.LENGTH_SHORT).show();
                CON = new ConnectionClass();
                Connection con = CON.CONN();
                try {

                    String query = "INSERT INTO FACULTETS(FACULTETNAME) " + "VALUES (N'" + name + "');";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);
                    con.close();
                }
                catch (Exception ex) { ex.getMessage(); }
            }
        });
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss/cancel the alert dialog
                //dialog.cancel();
                dialog.dismiss();
                Toast.makeText(getApplication(),
                        "No button clicked", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
    public void onClickCreateGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog_custom_view,null);
        builder.setCancelable(false);
        builder.setView(dialogView);
        Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);
        Button btn_negative = (Button) dialogView.findViewById(R.id.dialog_negative_btn);
        final Spinner spinnerFac = (Spinner) dialogView.findViewById(R.id.spinnerFac);
        TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
        dialog_title.setText("Введите название группы: ");
        spinnerFac.setVisibility(View.VISIBLE);
        CON = new ConnectionClass();
        Connection con = CON.CONN();
        try{

            String query = "SELECT * FROM FACULTETS";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            FacultyName.clear();
            FacultyID.clear();
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

        spinnerFac.setAdapter(adapter);
        // заголовок
        spinnerFac.setPrompt("Title");
        // выделяем элемент
        spinnerFac.setSelection(0);
        // устанавливаем обработчик нажатия

        spinnerFac.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        final EditText et_name = (EditText) dialogView.findViewById(R.id.et_name);
        et_name.setHint("Введите название группы");
        final AlertDialog dialog = builder.create();
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                dialog.cancel();
                String name = et_name.getText().toString();
                Toast.makeText(getApplication(),
                        "Submitted name : " + name, Toast.LENGTH_SHORT).show();
                CON = new ConnectionClass();
                Connection con = CON.CONN();
                try {

                    String query = "INSERT INTO GRUPPS(GRUPPNAME, FACULTETID) " + "VALUES (N'" + name + "','"+ FacultyID.get(spinnerFac.getSelectedItemPosition()) +"');";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);
                    con.close();
                }
                catch (Exception ex) { ex.getMessage(); }
            }
        });
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(getApplication(),
                        "No button clicked", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

}

package com.example.valer.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SeekBar;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.ContextThemeWrapper;
import android.widget.Toast;
import android.view.Gravity;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Statement;
import java.sql.ResultSet;
import android.view.LayoutInflater;
import android.app.AlertDialog;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private ConnectionClass CON;
    int listN = 0;
    int listNMax = 0;
    private MenuItem item;
    private ArrayList<String> askTest = new ArrayList<String>();
    private ArrayList<String> test = new ArrayList<String>();
    private ArrayList<ArrayList<Boolean>> myAsk = new ArrayList<ArrayList<Boolean>>();

    Boolean Variant = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.item = item;
        int id = item.getItemId();
        switch (id) {
            case R.id.aFinish: {
                if(Variant == false){
                    Intent intent = new Intent(MainActivity.this, TeacherActivity.class);
                    startActivity(intent);
                    return false;
                }
                int Right=0;
                String AnswerRight = "";
                String AnswerSelect = "";
                for(int i=0; i<listNMax; i++) {
                    int numright=0;
                    for(int j=0; j < 7; j++) {
                        if(myAsk.get(i).get(j) != null) {
                            AnswerSelect = AnswerSelect + "1";
                            Boolean R=false;
                            for(int x=0; x<askTest.get(i).length(); x++){
                                if(Character.getNumericValue(askTest.get(i).charAt(x)) == j+1){ numright++; R=true; break; }
                            }
                            if(R==false){
                                numright--;
                            }
                        }else {
                            AnswerSelect = AnswerSelect + "0";
                        }
                        if(j==6) AnswerSelect = AnswerSelect + "|";
                    }
                    if(numright == 0) AnswerRight = AnswerRight + "0";
                    else if(numright == askTest.get(i).length()){
                        AnswerRight = AnswerRight + "1";
                        Right++;
                    }else{
                        AnswerRight = AnswerRight + "2";
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alertdialog_finish_test,null);
                builder.setCancelable(false);
                builder.setView(dialogView);
                Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);
                Button btn_negative = (Button) dialogView.findViewById(R.id.dialog_negative_btn);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                CON = new ConnectionClass();
                Connection con = CON.CONN();
                ResultSet rs;
                Bundle arguments = getIntent().getExtras();
                String idName = arguments.get("training").toString();
                try {
                    Statement stmt = con.createStatement();
                    rs = stmt.executeQuery("Select * from TESTSNAMES where TESTSNAMESID = "+idName);
                    rs.next();
                    String TName = rs.getString("TESTNAME");
                    rs = stmt.executeQuery("Select * from LOGIN where LOGINID = "+FirstActivity.LoginID);
                    rs.next();
                    dialog_title.setText("Вы завершили тест.\n\n\n" +
                            "Результат теста: "+TName+"("+idName+")\n\n"+
                            "Фамилия: "+rs.getString("FAM")+"\n"+
                            "Имя: "+rs.getString("NAME")+"\n"+
                            "Отчество: "+rs.getString("OTCH")+"\n"+
                            "№ Зачетки: "+rs.getString("NOMZA")+"\n\n"+
                            "Всего вопросов: "+Integer.toString(listNMax)+"\n"+
                            "Ошибок: "+Integer.toString(listNMax-Right)+"\n"+
                            "Верных ответов: "+Integer.toString(Right));
                    con.close();
                }
                catch (Exception ex) {
                    ex.getMessage();
                }
                final String ARight = AnswerRight;
                final String ASelect = AnswerSelect;
                final AlertDialog dialog = builder.create();
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Dismiss the alert dialog
                        CON = new ConnectionClass();
                        Connection con = CON.CONN();
                        try {
                            Bundle arguments = getIntent().getExtras();
                            String query = "INSERT INTO TESTSRESULTS(TESTSNAMESID, LOGINID, STUDRESULT, STUDOTVETS) " + "VALUES ('"+arguments.get("training").toString()+"','"+FirstActivity.LoginID+"',N'" + ARight + "',N'"+ ASelect +"');";
                            Statement stmt = con.createStatement();
                            stmt.executeUpdate(query);
                            con.close();
                        }
                        catch (Exception ex) {
                            ex.getMessage();
                        }
                        dialog.cancel();
                        Intent intent = new Intent(MainActivity.this, TeacherActivity.class);
                        startActivity(intent);
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
                return true;
            }
            case R.id.aCancel:{
                Intent intent = new Intent(MainActivity.this, TeacherActivity.class);
                startActivity(intent);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void onCreateText() {
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar2);
        seekBar.setProgress(listN);
        String[] split = test.get(listN).split("\\d\\.[\\s ]{1,}");
        LinearLayout mainL = (LinearLayout) findViewById(R.id.lookNew);
        mainL.removeAllViews();
        TextView textCountAsk = (TextView)findViewById(R.id.textCountAsk);
        textCountAsk.setText("Количество ответов: "+askTest.get(listN).length());
        TextView textStatus = (TextView)findViewById(R.id.textStatus);
        String statusEnter="";
        if(FirstActivity.Access == 0) statusEnter="Гость";
        else if(FirstActivity.Access == 1) statusEnter="Администратор";
        else if(FirstActivity.Access == 2) statusEnter="Преподаватель";
        else if(FirstActivity.Access == 3) statusEnter="Студент";
        textStatus.setText("Статус входа: "+statusEnter);
        TextView txtV = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 10);
        txtV.setLayoutParams(layoutParams);
        txtV.setBackgroundResource(R.drawable.question);
        txtV.setTextAppearance(R.style.myQuButton);
        txtV.setPadding(16,20,16,20);
        txtV.setText("Вопрос №" + String.valueOf(listN+1)+".   " + split[0].replaceAll("[\\s]{2,}", " "));
        txtV.setGravity(Gravity.CENTER);
        mainL.addView(txtV);
        for (int i = 1; i < split.length; i++) {
            final int countBut = split.length;
            Button b = new Button(this);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams1.setMargins(100, 10, 100, 10);
            b.setLayoutParams(layoutParams1);
            b.setBackgroundResource(R.drawable.ask);
            if(Variant == false){
                if(myAsk.get(listN).get(i-1) != null) {
                    if(myAsk.get(listN).get(i-1) == true) b.setBackgroundResource(R.drawable.ask1);
                    else b.setBackgroundResource(R.drawable.ask0);
                }
            }else{
                if(myAsk.get(listN).get(i-1) != null) {
                    b.setBackgroundResource(R.drawable.ask3);
                }
            }
            b.setTextAppearance(R.style.myAskButton);
            b.setPadding(16,10,16,10);
            b.setGravity(Gravity.CENTER);
            //b.setId(View.generateViewId());
            b.setTag(i);
            b.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    final Object tag = v.getTag();
                    int aski = Integer.valueOf(String.valueOf(tag))-1;
                    if(myAsk.get(listN).get(aski) != null) {
                        myAsk.get(listN).set(aski, null);
                        onCreateText();
                    } else {
                        for(int x=0; x<askTest.get(listN).length(); x++){
                            if(Character.getNumericValue(askTest.get(listN).charAt(x)) == aski+1) myAsk.get(listN).set(aski, true);
                            else { myAsk.get(listN).set(aski, false); continue; }
                            break;
                        }
                    }
                    int t=0;
                    for(int x=0; x<countBut-1; x++){
                        if(myAsk.get(listN).get(x) != null) {
                            if(myAsk.get(listN).get(x) == false) t--;
                            else t++;
                        }
                    }
                    if(Variant == false) {
                        if(askTest.get(listN).length() == t) listN++;
                        if(listN > listNMax-1) listN = 0;
                    }
                    onCreateText();
                }
            });
            b.setText(split[i].replaceAll("[\\s]{2,}", " "));
            mainL.addView(b);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //if(FirstActivity.Access == 0 || FirstActivity.Access == 3) menu.findItem(R.id.aSaveServer).setVisible(false);
        return true;
    }
    float fromPosition;
    float toPosition;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: fromPosition = event.getX();
            case MotionEvent.ACTION_MOVE: // движение
                break;
            case MotionEvent.ACTION_UP:{
                float toPosition = event.getX();
                if (fromPosition-100 > toPosition) {
                    listN +=1;
                    if(listN > listNMax-1) listN =0;
                    onCreateText();
                }
                else if (fromPosition+100 < toPosition) {
                    listN -=1;
                    if(listN < 0) listN =listNMax-1;
                    onCreateText();
                }
            }
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle arguments = getIntent().getExtras();
        Variant = arguments.getBoolean("Variant");


        View rl;
        rl = (View) findViewById(R.id.lookNew);
        rl.setOnTouchListener(this);

        if(TeacherActivity.InternetView==false) {
            mDBHelper = new DatabaseHelper(this);
            mDb = mDBHelper.getWritableDatabase();
            String idName = arguments.get("training").toString();
            Cursor cursor3 = mDb.rawQuery("SELECT * FROM TESTS WHERE TESTSNAMESID = " + idName, null);
            cursor3.moveToFirst();
            while (!cursor3.isAfterLast()) {
                test.add(cursor3.getString(cursor3.getColumnIndex("TEXTVOPR")));
                askTest.add(cursor3.getString(cursor3.getColumnIndex("NOMOTV")));
                myAsk.add(new ArrayList<Boolean>());
                for (int i = 0; i < 7; i++) myAsk.get(listNMax).add(null);
                cursor3.moveToNext();
                listNMax++;
            }
            cursor3.close();
        } else {
            CON = new ConnectionClass();
            Connection con = CON.CONN();
            String idName = arguments.get("training").toString();
            try {
                String query = "SELECT * FROM TESTS WHERE TESTSNAMESID = " + idName;
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    test.add(rs.getString("TEXTVOPR").toString());
                    askTest.add(rs.getString("NOMOTV").toString());
                    myAsk.add(new ArrayList<Boolean>());
                    for (int i = 0; i < 7; i++) myAsk.get(listNMax).add(null);
                    listNMax++;
                }
                //con.close();
            }
            catch (Exception ex) {
                ex.getMessage();
            }

        }
        onCreateText();
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar2);
        seekBar.setMax(listNMax-1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listN = Integer.valueOf(progress);
                if(listN<0) listN=0;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                onCreateText();
            }
        });
    }
    public void onClickBack(View v) {
        listN -=1;
        if(listN < 0) listN =listNMax-1;
        onCreateText();
    }
    public void onClickNext(View v) {
        listN +=1;
        if(listN > listNMax-1) listN =0;
        onCreateText();
    }
}
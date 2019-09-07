package com.example.valer.myapplication;

import android.content.ClipData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.CountDownTimer;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import java.io.IOException;
import android.content.ContentValues;
import android.content.Context;
import android.view.View.OnTouchListener;
import android.content.Intent;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;
import android.database.SQLException;

public class CreateTestActivity extends AppCompatActivity implements View.OnTouchListener {

    private Menu menu;
    private ArrayList<String> myQuestion = new ArrayList<String>();
    private ArrayList<ArrayList<String>> myAnswer = new ArrayList<ArrayList<String>>();
    private ArrayList<Integer> myAnswerMax = new ArrayList<Integer>();
    private ArrayList<ArrayList<Boolean>> myAnswerBox = new ArrayList<ArrayList<Boolean>>();

    int myQuestionMax = 0;
    Boolean myAdd = false;

    int myQuestionNum = 0;
    String myName = "";
    private MenuItem item;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private ConnectionClass CON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);

        View rl = (LinearLayout) findViewById(R.id.LTouch);

        rl.setOnTouchListener(this);
        Bundle arguments = getIntent().getExtras();
        String editState="0";
        EditText eNameTest = (EditText)findViewById(R.id.eNameTest);
        if(arguments != null)
        {
            setTitle("Редактировать Тест");
            editState = arguments.getString("Edit");
            if(TeacherActivity.InternetView==false)
            {
                if(editState != null)
                {
                    mDBHelper = new DatabaseHelper(this);
                    mDb = mDBHelper.getWritableDatabase();
                    Cursor cursor2 = mDb.rawQuery("SELECT TESTNAME FROM TESTSNAMES WHERE TESTSNAMESID = "+editState, null);
                    cursor2.moveToFirst();
                    eNameTest.setText(cursor2.getString(cursor2.getColumnIndex("TESTNAME")));
                    cursor2.close();
                    Cursor cursor3 = mDb.rawQuery("SELECT * FROM TESTS WHERE TESTSNAMESID = "+editState, null);
                    cursor3.moveToFirst();
                    int i = 0;
                    while (!cursor3.isAfterLast()) {
                        String[] split = cursor3.getString(cursor3.getColumnIndex("TEXTVOPR")).split("\\d\\.[\\s ]{1,}");
                        myQuestion.add(split[0].replaceAll("[\\s]{2,}", " "));
                        myAnswer.add(new ArrayList<String>());
                        myAnswerBox.add(new ArrayList<Boolean>());
                        myAnswerMax.add(split.length-2);
                        for (int j = 1; j < split.length; j++) {
                            myAnswer.get(i).add(split[j].replaceAll("[\\s]{2,}", " "));
                            Boolean AnswerTo=false;
                            for (int s = 0; s < cursor3.getString(cursor3.getColumnIndex("NOMOTV")).length(); s++){
                                if(Character.getNumericValue(cursor3.getString(cursor3.getColumnIndex("NOMOTV")).charAt(s)) == j) {
                                    myAnswerBox.get(i).add(true);
                                    AnswerTo = true;
                                    break;
                                }
                            }
                            if(AnswerTo == false) myAnswerBox.get(i).add(false);

                        }
                        cursor3.moveToNext();
                        i++;
                    }
                    myQuestionMax=i-1;
                    cursor3.close();
                }
            }else{
                CON = new ConnectionClass();
                Connection con = CON.CONN();
                try{
                    String query = "SELECT * FROM TESTSNAMES WHERE TESTSNAMESID = "+ editState;
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    eNameTest.setText(rs.getString("TESTNAME").toString());

                    query = "SELECT * FROM TESTS WHERE TESTSNAMESID = " + editState;
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    int i=0;
                    while (rs.next()) {
                        String[] split = rs.getString("TEXTVOPR").split("\\d\\.[\\s ]{1,}");
                        myQuestion.add(split[0].replaceAll("[\\s]{2,}", " "));
                        myAnswer.add(new ArrayList<String>());
                        myAnswerBox.add(new ArrayList<Boolean>());
                        myAnswerMax.add(split.length-2);
                        for (int j = 1; j < split.length; j++) {
                            myAnswer.get(i).add(split[j].replaceAll("[\\s]{2,}", " "));
                            Boolean AnswerTo=false;
                            for (int s = 0; s < rs.getString("NOMOTV").length(); s++){
                                if(Character.getNumericValue(rs.getString("NOMOTV").charAt(s)) == j) {
                                    myAnswerBox.get(i).add(true);
                                    AnswerTo = true;
                                    break;
                                }
                            }
                            if(AnswerTo == false) myAnswerBox.get(i).add(false);

                        }
                        i++;
                    }
                    myQuestionMax=i-1;
                    con.close();
                }
                catch (Exception ex)
                {
                    ex.getMessage();
                }
            }
        }
        else{
            setTitle("Создать Тест");
            myQuestion.add("");
            myAnswerMax.add(0);
            myAnswer.add(new ArrayList<String>());
            myAnswer.get(myQuestionNum).add("");
            myAnswerBox.add(new ArrayList<Boolean>());
            myAnswerBox.get(myQuestionNum).add(false);
        }
        onCreateQuestion(myQuestionNum);
        SeekBar sQuestionN = ( SeekBar ) findViewById(R.id.seekBarQuestion);
        sQuestionN.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser == true) {
                    onSaveQuestion(myQuestionNum);
                    myQuestionNum = progress;
                }
                onCreateQuestion(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    float fromPosition;
    float toPosition;

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: fromPosition = event.getX();
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:{
                float toPosition = event.getX();
                if (fromPosition-100 > toPosition)
                {
                    onSaveQuestion(myQuestionNum);
                    myQuestionNum -= 1;
                    if (myQuestionNum < 0) myQuestionNum = 0;
                    onCreateQuestion(myQuestionNum);
                }
                else if (fromPosition+100 < toPosition)
                {
                    if(myQuestionNum < myQuestionMax){
                        onSaveQuestion(myQuestionNum);
                        myQuestionNum += 1;
                        if (myQuestionNum >= myQuestion.size()) myQuestionNum = myQuestion.size()-1;
                        onCreateQuestion(myQuestionNum);
                    }
                }
            }
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.item = item;
        int id = item.getItemId();
        switch (id) {
            case R.id.aNext: {
                if (myQuestionNum < myQuestionMax) {
                    onSaveQuestion(myQuestionNum);
                    myQuestionNum += 1;
                    if (myQuestionNum >= myQuestion.size()) myQuestionNum = myQuestion.size() - 1;
                    onCreateQuestion(myQuestionNum);
                }
                return true;
            }
            case R.id.aBack: {
                onSaveQuestion(myQuestionNum);
                myQuestionNum -= 1;
                if (myQuestionNum < 0) myQuestionNum = 0;
                onCreateQuestion(myQuestionNum);
                return true;
            }
            case R.id.aAdd: {
                    onSaveQuestion(myQuestionNum);
                    myQuestionMax += 1;
                    myQuestionNum += 1;
                    SeekBar sQuestion = ( SeekBar ) findViewById(R.id.seekBar);
                    if(myQuestionMax == myQuestionNum) {
                        myQuestion.add("");
                        myAnswer.add(new ArrayList<String>());
                        myAnswerBox.add(new ArrayList<Boolean>());
                        for(int i=0; i<=sQuestion.getMax();i++){
                            myAnswer.get(myQuestionNum).add("");
                            myAnswerBox.get(myQuestionNum).add(false);
                        }
                        myAnswerMax.add(sQuestion.getMax());
                    }
                    else {
                        myQuestion.add(myQuestionNum,"");
                        myAnswer.add(myQuestionNum, new ArrayList<String>());
                        myAnswerBox.add(myQuestionNum, new ArrayList<Boolean>());
                        for(int i=0; i<=sQuestion.getMax();i++) {
                            myAnswer.get(myQuestionNum).add("");
                            myAnswerBox.get(myQuestionNum).add(false);
                        }
                        myAnswerMax.add(myQuestionNum, sQuestion.getMax());
                    }
                    onCreateQuestion(myQuestionNum);
                    myAnswerMax.set(myQuestionNum, 0);
                    onCreateQuestion(myQuestionNum);
                    return true;
            }
            case R.id.aDelete: {
                if(myQuestion.size()>1) {
                    myQuestion.remove(myQuestionNum);
                    myAnswerMax.remove(myQuestionNum);
                    myAnswer.remove(myQuestionNum);
                    myAnswerBox.remove(myQuestionNum);
                    if(myQuestionMax == myQuestionNum) myQuestionNum -=1;
                    myQuestionMax -= 1;
                    onCreateQuestion(myQuestionNum);
                }
                return true;
            }
            case R.id.aSave: {
                onSaveQuestion(myQuestionNum);
                TextView tError = ( TextView ) findViewById(R.id.textError);
                EditText eName = ( EditText ) findViewById(R.id.eNameTest);
                if (eName.length() < 3) {
                    myDownTimer("Введите название теста!",5000);
                    onCreateQuestion(myQuestionNum);
                    return false;
                }
                for (int i = 0; i <= myQuestionMax; i++) {
                    if (myQuestion.get(i) != null) {
                        if (myQuestion.get(i).length() < 3) {
                            myDownTimer("Введите вопрос!",5000);
                            myQuestionNum=i;
                            onCreateQuestion(i);
                            return false;
                        } else {
                            Boolean ChBox=false;
                            for (int j = 0; j <= myAnswerMax.get(i); j++) {
                                if(myAnswerBox.get(i).get(j)==true) { ChBox=true; }
                                if (myAnswer.get(i).get(j).length() < 3) {
                                    myDownTimer("Введите ответ!",5000);
                                    myQuestionNum=i;
                                    onCreateQuestion(i);
                                    return false;
                                }
                            }
                            if(ChBox==false){
                                myDownTimer("Выберите правильный ответ!",5000);
                                myQuestionNum=i;
                                onCreateQuestion(i);
                                return false;
                            }
                        }
                    }else{
                        myDownTimer("Введите ответ!",5000);
                        myQuestionNum=i;
                        onCreateQuestion(i);
                        return false;
                    }
                }
                mDBHelper = new DatabaseHelper(this);
                SQLiteDatabase db = mDBHelper.getWritableDatabase();
                ContentValues cv1 = new ContentValues();
                cv1.put("TESTNAME", eName.getText().toString());
                long countNum = db.insert("TESTSNAMES", null, cv1);
                ContentValues cv = new ContentValues();
                for (int i = 0; i <= myQuestionMax; i++) {
                    cv.put("TESTSNAMESID", countNum);
                    String myQuestions = myQuestion.get(i) + "\n"+ "\n";
                    String qu="";
                    for (int j = 0; j <= myAnswerMax.get(i); j++) {
                        int jj;
                        jj=j+1;
                        myQuestions = myQuestions + jj +". "+ myAnswer.get(i).get(j)+ "\n"+ "\n";
                        if(myAnswerBox.get(i).get(j) == true){
                            qu+=j+1;
                        }
                    }
                    cv.put("NOMOTV", qu);
                    cv.put("TEXTVOPR", myQuestions);
                    cv.put("NOMVOPR", i+1);
                    long rowID = db.insert("TESTS", null, cv);
                }
                onCreateQuestion(myQuestionNum);
                myDownTimer("Тест сохранен как: "+eName.getText().toString()+":"+ String.valueOf(countNum),5000);
                Intent intent = new Intent(CreateTestActivity.this, TeacherActivity.class);
                startActivity(intent);
                return false;
            }
            case R.id.aSaveServer: {
                onSaveQuestion(myQuestionNum);
                TextView tError = ( TextView ) findViewById(R.id.textError);
                EditText eName = ( EditText ) findViewById(R.id.eNameTest);
                if (eName.length() < 3) {
                    myDownTimer("Введите название теста!",5000);
                    onCreateQuestion(myQuestionNum);
                    return false;
                }
                for (int i = 0; i <= myQuestionMax; i++) {
                    if (myQuestion.get(i) != null) {
                        if (myQuestion.get(i).length() < 3) {
                            myDownTimer("Введите вопрос!",5000);
                            myQuestionNum=i;
                            onCreateQuestion(i);
                            return false;
                        } else {
                            Boolean ChBox=false;
                            for (int j = 0; j <= myAnswerMax.get(i); j++) {
                                if(myAnswerBox.get(i).get(j)==true) { ChBox=true; }
                                if (myAnswer.get(i).get(j).length() < 3) {
                                    myDownTimer("Введите ответ!",5000);
                                    myQuestionNum=i;
                                    onCreateQuestion(i);
                                    return false;
                                }
                            }
                            if(ChBox==false){
                                myDownTimer("Выберите правильный ответ!",5000);
                                myQuestionNum=i;
                                onCreateQuestion(i);
                                return false;
                            }
                        }
                    }else{
                        myDownTimer("Введите ответ!",5000);
                        myQuestionNum=i;
                        onCreateQuestion(i);
                        return false;
                    }
                }
                CON = new ConnectionClass();
                Connection con = CON.CONN();
                Statement stmt = null;
                int countNum=0;
                try {
                    con.setAutoCommit(false);
                    stmt = con.createStatement();
                    stmt.executeUpdate("INSERT INTO TESTSNAMES(TESTNAME) " + "VALUES (N'"+eName.getText().toString()+"');");
                    ResultSet rs = stmt.executeQuery("select top 1 * from TESTSNAMES order by TESTSNAMESID desc");
                    rs.next();
                    countNum=rs.getInt("TESTSNAMESID");
                    for (int i = 0; i <= myQuestionMax; i++) {
                        String myQuestions = myQuestion.get(i) + "\n"+ "\n";
                        String qu="";
                        for (int j = 0; j <= myAnswerMax.get(i); j++) {
                            int jj;
                            jj=j+1;
                            myQuestions = myQuestions + jj +". "+ myAnswer.get(i).get(j)+ "\n"+ "\n";
                            if(myAnswerBox.get(i).get(j) == true){
                                qu+=j+1;
                            }
                        }
                        int i1=i+1;
                        try{
                            stmt.executeUpdate("INSERT INTO TESTS(TESTSNAMESID,NOMOTV,TEXTVOPR,NOMVOPR) " + "VALUES ('"+countNum+"','"+qu+"',N'"+myQuestions.toString().replace("\'", "\'\'")+"','"+i1+"');");
                            con.commit();
                        }
                        catch (SQLException e) {
                            con.rollback();
                        }
                    }
                    con.close();
                }
                catch (Exception ex)
                {
                    ex.getMessage();
                }

                onCreateQuestion(myQuestionNum);
                myDownTimer("Тест сохранен как: "+eName.getText().toString()+":"+ Integer.toString(countNum),5000);
                Intent intent = new Intent(CreateTestActivity.this, TeacherActivity.class);
                startActivity(intent);
                return false;
            }
            case R.id.aCancel:{
                Intent intent = new Intent(CreateTestActivity.this, TeacherActivity.class);
                startActivity(intent);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_text, menu);
        if(FirstActivity.Access == 0 || FirstActivity.Access == 3) menu.findItem(R.id.aSaveServer).setVisible(false);
        return true;
    }
    public void myDownTimer(final String TextTimer, final int TimerNum) {
        TextView tError = ( TextView ) findViewById(R.id.textError);
        tError.setText(TextTimer);
        new CountDownTimer(TimerNum, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                TextView tError = ( TextView ) findViewById(R.id.textError);
                tError.setText("");
            }
        }.start();
    }

    public void onCreateQuestion(final int QuestionNum) {
        TextView tQuestion = ( TextView ) findViewById(R.id.tQuestion);
        EditText eQuestion = ( EditText ) findViewById(R.id.editQuestion);
        SeekBar sQuestion = ( SeekBar ) findViewById(R.id.seekBar);

        tQuestion.setText("Вопрос №"+String.valueOf(QuestionNum+1)+" из "+String.valueOf(myQuestionMax+1));
        eQuestion.setHint("Введите вопрос");
        if(myQuestion.get(QuestionNum).length() > 0) eQuestion.setText(myQuestion.get(QuestionNum));
        else eQuestion.setText("");
        sQuestion.setProgress(myAnswerMax.get(QuestionNum));
        sQuestion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser == true) {
                    onCreateAnswer(progress,QuestionNum,fromUser);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        onCreateAnswer(sQuestion.getProgress(),QuestionNum,false);
        SeekBar sQuestionN = ( SeekBar ) findViewById(R.id.seekBarQuestion);
        sQuestionN.setProgress(myQuestionNum);
        sQuestionN.setMax(myQuestionMax);

    }

    public void onCreateAnswer(final int AnswerNum,final int QuestionNum, Boolean fUser) {

        TextView tAnswer = ( TextView ) findViewById(R.id.tAnswer);
        EditText[] eAnswer = new EditText[7];
        eAnswer[0]=( EditText ) findViewById(R.id.editAnswer1);
        eAnswer[1]=( EditText ) findViewById(R.id.editAnswer2);
        eAnswer[2]=( EditText ) findViewById(R.id.editAnswer3);
        eAnswer[3]=( EditText ) findViewById(R.id.editAnswer4);
        eAnswer[4]=( EditText ) findViewById(R.id.editAnswer5);
        eAnswer[5]=( EditText ) findViewById(R.id.editAnswer6);
        eAnswer[6]=( EditText ) findViewById(R.id.editAnswer7);

        CheckBox[] chAnswer = new CheckBox[7];
        chAnswer[0]=( CheckBox ) findViewById(R.id.checkAnswer1);
        chAnswer[1]=( CheckBox ) findViewById(R.id.checkAnswer2);
        chAnswer[2]=( CheckBox ) findViewById(R.id.checkAnswer3);
        chAnswer[3]=( CheckBox ) findViewById(R.id.checkAnswer4);
        chAnswer[4]=( CheckBox ) findViewById(R.id.checkAnswer5);
        chAnswer[5]=( CheckBox ) findViewById(R.id.checkAnswer6);
        chAnswer[6]=( CheckBox ) findViewById(R.id.checkAnswer7);
        tAnswer.setText("Число ответов: "+String.valueOf(AnswerNum+1));
        if (fUser == true) {
            myAnswer.get(QuestionNum).clear();
            myAnswerBox.get(QuestionNum).clear();
            for (int i = 0; i <= AnswerNum; i++) {
                myAnswer.get(QuestionNum).add(eAnswer[i].getText().toString());
                myAnswerBox.get(QuestionNum).add(chAnswer[i].isChecked());
            }
        }
        for (int i = 0; i < eAnswer.length; i++) {
            if(i<=AnswerNum) {
                eAnswer[i].setHint("Введите ответ");
                if(myAnswer.get(QuestionNum).get(i).length() < 1) eAnswer[i].setText("");
                else eAnswer[i].setText(myAnswer.get(QuestionNum).get(i));

                chAnswer[i].setChecked(myAnswerBox.get(QuestionNum).get(i));

                eAnswer[i].setVisibility(View.VISIBLE);
                chAnswer[i].setVisibility(View.VISIBLE);
            }else{
                eAnswer[i].setVisibility(View.INVISIBLE);
                chAnswer[i].setVisibility(View.INVISIBLE);
            }
        }
    }
    public void onSaveQuestion(final int QuestionNum) {

        EditText eQuestion = ( EditText ) findViewById(R.id.editQuestion);
        SeekBar sQuestion = ( SeekBar ) findViewById(R.id.seekBar);

        EditText[] eAnswer = new EditText[7];
        eAnswer[0]=( EditText ) findViewById(R.id.editAnswer1);
        eAnswer[1]=( EditText ) findViewById(R.id.editAnswer2);
        eAnswer[2]=( EditText ) findViewById(R.id.editAnswer3);
        eAnswer[3]=( EditText ) findViewById(R.id.editAnswer4);
        eAnswer[4]=( EditText ) findViewById(R.id.editAnswer5);
        eAnswer[5]=( EditText ) findViewById(R.id.editAnswer6);
        eAnswer[6]=( EditText ) findViewById(R.id.editAnswer7);

        CheckBox[] chAnswer = new CheckBox[7];
        chAnswer[0]=( CheckBox ) findViewById(R.id.checkAnswer1);
        chAnswer[1]=( CheckBox ) findViewById(R.id.checkAnswer2);
        chAnswer[2]=( CheckBox ) findViewById(R.id.checkAnswer3);
        chAnswer[3]=( CheckBox ) findViewById(R.id.checkAnswer4);
        chAnswer[4]=( CheckBox ) findViewById(R.id.checkAnswer5);
        chAnswer[5]=( CheckBox ) findViewById(R.id.checkAnswer6);
        chAnswer[6]=( CheckBox ) findViewById(R.id.checkAnswer7);
        myQuestion.set(QuestionNum,eQuestion.getText().toString());
        myAnswerMax.set(QuestionNum,sQuestion.getProgress());
        myAnswer.get(QuestionNum).clear();
        myAnswerBox.get(QuestionNum).clear();
        for (int i = 0; i <= myAnswerMax.get(QuestionNum); i++) {
            if(eAnswer[i].getText().toString().length() > 0 ) myAnswer.get(QuestionNum).add(eAnswer[i].getText().toString());
            else myAnswer.get(QuestionNum).add("");
            eAnswer[i].setText("");
            myAnswerBox.get(QuestionNum).add(chAnswer[i].isChecked());
            chAnswer[i].setChecked(false);
        }
    }
}
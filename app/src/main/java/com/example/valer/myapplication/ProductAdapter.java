package com.example.valer.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import java.io.IOException;

class ProductAdapter extends ArrayAdapter<Product> {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<Product> productList;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private ConnectionClass CON;

    ProductAdapter(Context context, int resource, ArrayList<Product> products) {
        super(context, resource, products);
        this.productList = products;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Product product = productList.get(position);

        viewHolder.nameView.setText(product.getName());
        viewHolder.nameView.setTag(product.getIdTag());
        viewHolder.textBut1.setText(product.getUnit());
        viewHolder.butImg.setImageResource(product.getBimg());
        viewHolder.textBut2.setText(product.getUnit2());
        viewHolder.butImg2.setImageResource(product.getBimg2());
        viewHolder.textBut3.setText(product.getUnit3());
        viewHolder.butImg3.setImageResource(product.getBimg3());
        viewHolder.textBut4.setText(product.getUnit4());
        viewHolder.butImg4.setImageResource(product.getBimg4());

        if(TeacherActivity.InternetView==false){
            viewHolder.layoutDel.setVisibility(View.VISIBLE);
            viewHolder.layoutEdit.setVisibility(View.VISIBLE);
            viewHolder.layoutTeach.setVisibility(View.VISIBLE);
            viewHolder.layoutControl.setVisibility(View.GONE);
        }else{
            if(FirstActivity.Access == 3){
                viewHolder.layoutDel.setVisibility(View.GONE);
                viewHolder.layoutEdit.setVisibility(View.GONE);
                viewHolder.layoutTeach.setVisibility(View.VISIBLE);
                viewHolder.layoutControl.setVisibility(View.VISIBLE);
            }
        }

        if(TeacherActivity.InternetView==false){
            mDBHelper = new DatabaseHelper(getContext());
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

            viewHolder.butImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), CreateTestActivity.class);
                    intent.putExtra("Edit", viewHolder.nameView.getTag().toString());
                    getContext().startActivity(intent);
                }
            });
            viewHolder.butImg2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDb.delete("TESTSNAMES", "TESTSNAMESID = " + viewHolder.nameView.getTag().toString(), null);
                    mDb.delete("TESTS", "TESTSNAMESID = " + viewHolder.nameView.getTag().toString(), null);
                    Intent intent = new Intent(getContext(), TeacherActivity.class);
                    getContext().startActivity(intent);
                }
            });
            viewHolder.butImg3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("training", viewHolder.nameView.getTag().toString());
                    getContext().startActivity(intent);
                }
            });
            viewHolder.butImg4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }else{


            viewHolder.butImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), CreateTestActivity.class);
                    intent.putExtra("Edit", viewHolder.nameView.getTag().toString());
                    getContext().startActivity(intent);
                }
            });
            viewHolder.butImg2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Connection con;
                    CON = new ConnectionClass();
                    con = CON.CONN();

                    try {
                        Statement stmt = con.createStatement();
                        stmt.executeUpdate("DELETE FROM TESTS WHERE TESTS.TESTSNAMESID="+viewHolder.nameView.getTag().toString());
                        stmt.executeUpdate("DELETE FROM TESTSRESULTS WHERE TESTSRESULTS.TESTSNAMESID="+viewHolder.nameView.getTag().toString());
                        stmt.executeUpdate("DELETE FROM TESTSNAMES WHERE TESTSNAMES.TESTSNAMESID="+viewHolder.nameView.getTag().toString());
                        con.close();
                    }
                    catch (Exception ex)
                    {
                        ex.getMessage();
                    }
                    Intent intent = new Intent(getContext(), TeacherActivity.class);
                    getContext().startActivity(intent);
                }
            });
            viewHolder.butImg3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Connection con;
                    CON = new ConnectionClass();
                    con = CON.CONN();
                    try{
                        Statement stmt = con.createStatement();
                        ResultSet rss = stmt.executeQuery("SELECT * FROM TESTSNAMES WHERE TESTSNAMESID='"+viewHolder.nameView.getTag().toString()+"'");
                        mDBHelper = new DatabaseHelper(getContext());
                        SQLiteDatabase db = mDBHelper.getWritableDatabase();
                        ContentValues cv1 = new ContentValues();
                        rss.next();
                        cv1.put("TESTNAME",  rss.getString("TESTNAME").toString());
                        long countNum = db.insert("TESTSNAMES", null, cv1);
                        stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT * FROM TESTS WHERE TESTSNAMESID="+viewHolder.nameView.getTag().toString());
                        while (rs.next()) {
                            ContentValues cv = new ContentValues();
                            cv.put("TESTSNAMESID",  String.valueOf(countNum));
                            cv.put("NOMOTV", rs.getString("NOMOTV"));
                            cv.put("TEXTVOPR", rs.getString("TEXTVOPR"));
                            cv.put("NOMVOPR", rs.getString("TEXTVOPR"));
                            long rowID = db.insert("TESTS", null, cv);
                        }
                        Toast.makeText(getContext(), "Скачен тест: " + rss.getString("TESTNAME").toString(), Toast.LENGTH_SHORT).show();
                        con.close();
                        db.close();
                    }
                    catch (Exception ex) {
                        ex.getMessage();
                    }
                }
            });
            viewHolder.butImg4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("training", viewHolder.nameView.getTag().toString());
                    intent.putExtra("Variant", true);
                    getContext().startActivity(intent);
                }
            });
        }
        return convertView;
    }

    protected class ViewHolder {
        final ImageButton butImg, butImg2, butImg3, butImg4;
        final TextView nameView, textBut1, textBut2, textBut3, textBut4;
        final LinearLayout layoutDel, layoutEdit, layoutTeach, layoutControl;
        ViewHolder(View view){
            butImg = (ImageButton ) view.findViewById(R.id.butImg);
            textBut1 = (TextView) view.findViewById(R.id.textBut1);
            butImg2 = (ImageButton) view.findViewById(R.id.butImg2);
            textBut2 = (TextView ) view.findViewById(R.id.textBut2);
            butImg3 = (ImageButton) view.findViewById(R.id.butImg3);
            textBut3 = (TextView) view.findViewById(R.id.textBut3);
            butImg4 = (ImageButton ) view.findViewById(R.id.butImg4);
            textBut4 = (TextView) view.findViewById(R.id.textBut4);
            layoutDel = (LinearLayout) view.findViewById(R.id.layoutDel);
            layoutEdit = (LinearLayout) view.findViewById(R.id.layoutEdit);
            layoutTeach = (LinearLayout) view.findViewById(R.id.layoutTeach);
            layoutControl = (LinearLayout ) view.findViewById(R.id.layoutControl);
            nameView = (TextView) view.findViewById(R.id.textview_title);
        }
    }
}
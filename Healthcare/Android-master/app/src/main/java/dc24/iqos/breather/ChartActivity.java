package dc24.iqos.breather;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ChartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    TextView chartday;
    EditText search_day;
    Button btnsearch;
    Button btnsbp;
    Button btndbp;
    Button btnheart;
    Button btnresp;

    LineChart lineChart;

    TempChartDate ChartDate = new TempChartDate();

    public DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date datetitle = new Date();
        System.out.println(datetitle);

        String date_title = format.format(datetitle);
        date_title="날짜 입력 ";
        // System.out.println(time1);
        //System.out.println(time2);

        chartday = findViewById(R.id.chart_day);
        chartday.setText(date_title); //set today date
        ChartDate.setDate(date_title);//현재 확인하고자 하는 날짜를 출력시키기 위함

        btnsearch = findViewById(R.id.btn_search);
        btnsearch.setOnClickListener(this);
        btnsbp = findViewById(R.id.btn_sbp);
        btnsbp.setOnClickListener(this);
        btndbp = findViewById(R.id.btn_dbp);
        btndbp.setOnClickListener(this);
        btnheart = findViewById(R.id.btn_heart);
        btnheart.setOnClickListener(this);
        btnresp = findViewById(R.id.btn_resp);
        btnresp.setOnClickListener(this);

        search_day = (EditText) findViewById(R.id.input_date);
        search_day.setText("");

        ///////////Graph//////////


        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();//DB open

        Cursor iCursor = mDbOpenHelper.sortColumn("date");
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());

        lineChart = (LineChart) findViewById(R.id.chart);

        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Entry> sbp_entries = new ArrayList<>();
        ArrayList<Entry> dbp_entries = new ArrayList<>();
        ArrayList<Entry> heart_entries = new ArrayList<>();
        ArrayList<Entry> resp_entries = new ArrayList<>();
        String tempIndex;
        String tempDate;
        String tempTime;
        String tempSbp;

        int xIndex =0;
        System.out.println("=====================CHART VALUES CHECK============================");
        while(iCursor.moveToNext()) {
            tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            tempDate = iCursor.getString(iCursor.getColumnIndex("DATE"));
            System.out.println("==Date=="+tempDate+"===");
            System.out.println("==Title=="+date_title+"===");
            tempTime = iCursor.getString(iCursor.getColumnIndex("TIME"));
            tempSbp = iCursor.getString(iCursor.getColumnIndex("SBP"));
            if(tempDate.equals(date_title)){//찾고자하는 데이터가 맞으면
                System.out.println("label : "+tempTime.substring(0,3));
                labels.add(tempTime.substring(0,2));
                System.out.println("VALUE : "+tempSbp);
                sbp_entries.add(new Entry(Integer.valueOf(tempSbp),xIndex));
                xIndex++;
            }
        }
        System.out.println("=====================CHART VALUES CHECK============================");

        LineDataSet dataset_sbp = new LineDataSet(sbp_entries, "SBP");


        LineData data_sbp = new LineData(labels, dataset_sbp);
        dataset_sbp.setColors(new int[] { R.color.colorBlack }); //

        /*dataset.setDrawCubic(true); //선 둥글게 만들기
        dataset.setDrawFilled(true); //그래프 밑부분 색칠*/

        lineChart.setData(data_sbp);
        //lineChart.animateY(5000);

        ///////////Graph//////////

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mainfeed) {
            Intent intent=new Intent(ChartActivity.this, FeedActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.status) {
            Intent intent=new Intent(ChartActivity.this, StatusActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.chart) {
            Intent intent=new Intent(ChartActivity.this, ChartActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        }
        else if (id == R.id.like) {
            Intent intent=new Intent(ChartActivity.this, LikeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.more) {
            Intent intent=new Intent(ChartActivity.this, MoreActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Entry> sbp_entries = new ArrayList<>();
        ArrayList<Entry> dbp_entries = new ArrayList<>();
        ArrayList<Entry> heart_entries = new ArrayList<>();
        ArrayList<Entry> resp_entries = new ArrayList<>();
        String tempIndex;
        String tempDate;
        String tempTime;
        String tempSbp;
        String tempDbp;
        String tempHeart;
        String tempResp;
        Cursor iCursor;
        int xIndex =0;
        String Title;

        LineDataSet dataset_sbp;
        LineData data_sbp;
        LineDataSet dataset_dbp;
        LineData data_dbp;
        LineDataSet dataset_heart;
        LineData data_heart;
        LineDataSet dataset_resp;
        LineData data_resp;

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();//DB open

        switch (v.getId()) {
            case R.id.btn_search:
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                Date datetitle = new Date();
                System.out.println(datetitle);

                String date_title = format.format(datetitle);

                Title = search_day.getText().toString();
                chartday.setText(Title); //차트 상단의 시간 세팅
                search_day.setText("");
                ChartDate.setDate(Title);//현재 차트 출력 시간 저장 (temp 역할)

                iCursor = mDbOpenHelper.sortColumn("date");
                Log.d("showDatabase", "DB Size: " + iCursor.getCount());

                lineChart = (LineChart) findViewById(R.id.chart);

                System.out.println("=====================CHART VALUES CHECK============================");
                while(iCursor.moveToNext()) {
                    tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
                    tempDate = iCursor.getString(iCursor.getColumnIndex("DATE"));
                    tempTime = iCursor.getString(iCursor.getColumnIndex("TIME"));
                    tempSbp = iCursor.getString(iCursor.getColumnIndex("SBP"));
                    if(tempDate.equals(Title)){//찾고자하는 데이터가 맞으면
                        System.out.println("label : "+tempTime.substring(0,3));
                        labels.add(tempTime.substring(0,2));
                        System.out.println("VALUE : "+tempSbp);
                        sbp_entries.add(new Entry(Integer.valueOf(tempSbp),xIndex));
                        xIndex++;
                    }
                }
                System.out.println("=====================CHART VALUES CHECK============================");

                dataset_sbp = new LineDataSet(sbp_entries, "SBP");


                data_sbp = new LineData(labels, dataset_sbp);
                dataset_sbp.setColors(new int[] { R.color.colorBlack }); //

                lineChart.setData(data_sbp);
                break;
            case R.id.btn_sbp:
                Title = ChartDate.getDate();
                iCursor = mDbOpenHelper.sortColumn("date");
                Log.d("showDatabase", "DB Size: " + iCursor.getCount());

                lineChart = (LineChart) findViewById(R.id.chart);

                System.out.println("=====================CHART VALUES CHECK============================");
                while(iCursor.moveToNext()) {
                    tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
                    tempDate = iCursor.getString(iCursor.getColumnIndex("DATE"));
                    tempTime = iCursor.getString(iCursor.getColumnIndex("TIME"));
                    tempSbp = iCursor.getString(iCursor.getColumnIndex("SBP"));
                    if(tempDate.equals(Title)){//찾고자하는 데이터가 맞으면
                        System.out.println("label : "+tempTime.substring(0,3));
                        labels.add(tempTime.substring(0,2));
                        System.out.println("VALUE : "+tempSbp);
                        sbp_entries.add(new Entry(Integer.valueOf(tempSbp),xIndex));
                        xIndex++;
                    }
                }
                System.out.println("=====================CHART VALUES CHECK============================");

                dataset_sbp = new LineDataSet(sbp_entries, "SBP");


                data_sbp = new LineData(labels, dataset_sbp);
                dataset_sbp.setColors(new int[] { R.color.colorBlack }); //

                lineChart.setData(data_sbp);
                break;

            case R.id.btn_dbp:
                Title = ChartDate.getDate();
                iCursor = mDbOpenHelper.sortColumn("date");
                Log.d("showDatabase", "DB Size: " + iCursor.getCount());

                lineChart = (LineChart) findViewById(R.id.chart);

                System.out.println("=====================CHART VALUES CHECK============================");
                while(iCursor.moveToNext()) {
                    tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
                    tempDate = iCursor.getString(iCursor.getColumnIndex("DATE"));
                    tempTime = iCursor.getString(iCursor.getColumnIndex("TIME"));
                    tempDbp = iCursor.getString(iCursor.getColumnIndex("DBP"));
                    if(tempDate.equals(Title)){//찾고자하는 데이터가 맞으면
                        System.out.println("label : "+tempTime.substring(0,3));
                        labels.add(tempTime.substring(0,2));
                        System.out.println("VALUE : "+tempDbp);
                        dbp_entries.add(new Entry(Integer.valueOf(tempDbp),xIndex));
                        xIndex++;
                    }
                }
                System.out.println("=====================CHART VALUES CHECK============================");

                dataset_dbp = new LineDataSet(dbp_entries, "DBP");


                data_dbp = new LineData(labels, dataset_dbp);
                dataset_dbp.setColors(new int[] { R.color.colorBlack }); //

                lineChart.setData(data_dbp);
                break;
            case R.id.btn_heart:
                Title = ChartDate.getDate();
                iCursor = mDbOpenHelper.sortColumn("date");
                Log.d("showDatabase", "DB Size: " + iCursor.getCount());

                lineChart = (LineChart) findViewById(R.id.chart);

                System.out.println("=====================CHART VALUES CHECK============================");
                while(iCursor.moveToNext()) {
                    tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
                    tempDate = iCursor.getString(iCursor.getColumnIndex("DATE"));
                    tempTime = iCursor.getString(iCursor.getColumnIndex("TIME"));
                    tempHeart = iCursor.getString(iCursor.getColumnIndex("HEART"));
                    if(tempDate.equals(Title)){//찾고자하는 데이터가 맞으면
                        System.out.println("label : "+tempTime.substring(0,3));
                        labels.add(tempTime.substring(0,2));
                        System.out.println("VALUE : "+tempHeart);
                        heart_entries.add(new Entry(Integer.valueOf(tempHeart),xIndex));
                        xIndex++;
                    }
                }
                System.out.println("=====================CHART VALUES CHECK============================");

                dataset_heart = new LineDataSet(heart_entries, "HEART");


                data_heart = new LineData(labels, dataset_heart);
                dataset_heart.setColors(new int[] { R.color.colorBlack }); //

                lineChart.setData(data_heart);
                break;

            case R.id.btn_resp:
                Title = ChartDate.getDate();
                iCursor = mDbOpenHelper.sortColumn("date");
                Log.d("showDatabase", "DB Size: " + iCursor.getCount());

                lineChart = (LineChart) findViewById(R.id.chart);

                System.out.println("=====================CHART VALUES CHECK============================");
                while(iCursor.moveToNext()) {
                    tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
                    tempDate = iCursor.getString(iCursor.getColumnIndex("DATE"));
                    tempTime = iCursor.getString(iCursor.getColumnIndex("TIME"));
                    tempResp = iCursor.getString(iCursor.getColumnIndex("RESP"));
                    if(tempDate.equals(Title)){//찾고자하는 데이터가 맞으면
                        System.out.println("label : "+tempTime.substring(0,3));
                        labels.add(tempTime.substring(0,2));
                        System.out.println("VALUE : "+tempResp);
                        resp_entries.add(new Entry(Integer.valueOf(tempResp),xIndex));
                        xIndex++;
                    }
                }
                System.out.println("=====================CHART VALUES CHECK============================");

                dataset_resp = new LineDataSet(resp_entries, "RESP");


                data_resp = new LineData(labels, dataset_resp);
                dataset_resp.setColors(new int[] { R.color.colorBlack }); //

                lineChart.setData(data_resp);
                break;
        }
    }
}


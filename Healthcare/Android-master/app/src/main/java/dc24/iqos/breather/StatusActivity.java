package dc24.iqos.breather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;


import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;


import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;


public class StatusActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

//    Button gochart_btn;

    private static final String TAG = "Main";

    Button btn_Update;
    Button btn_Insert;
    Button btn_Select;

    EditText edit_Date;
    EditText edit_Time;
    EditText edit_Sbp;
    EditText edit_Dbp;
    EditText edit_Heart;
    EditText edit_Resp;

    TextView text_Date;
    TextView text_Time;
    TextView text_Sbp;
    TextView text_Dbp;
    TextView text_Heart;
    TextView text_Resp;


    long nowIndex;
    String Date;
    String Time;
    long Sbp;
    long Dbp;
    long Heart;
    long Resp;

    String sort = "DATE";

    ArrayAdapter<String> arrayAdapter;

    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();
    public DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //자세히 보기 (Status => Chart 화면전환)
//        gochart_btn = findViewById(R.id.gochart_btn);
//
//        gochart_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(StatusActivity.this, ChartActivity.class);
//                startActivity(intent);
//                overridePendingTransition(0, 0);//화면전환 효과 제거
//            }
//        });

        //DB Controll
        btn_Insert = (Button) findViewById(R.id.btn_insert);
        btn_Insert.setOnClickListener(this);
        btn_Update = (Button) findViewById(R.id.btn_update);
        btn_Update.setOnClickListener(this);
        btn_Select = (Button) findViewById(R.id.btn_select);
        btn_Select.setOnClickListener(this);
        edit_Date = (EditText) findViewById(R.id.edit_date);
        edit_Time = (EditText) findViewById(R.id.edit_time);
        edit_Sbp = (EditText) findViewById(R.id.edit_sbp);
        edit_Dbp = (EditText) findViewById(R.id.edit_dbp);
        edit_Sbp = (EditText) findViewById(R.id.edit_sbp);
        edit_Heart = (EditText) findViewById(R.id.edit_heart);
        edit_Resp = (EditText) findViewById(R.id.edit_resp);

        text_Date = (TextView) findViewById(R.id.text_date);
        text_Time = (TextView) findViewById(R.id.text_time);
        text_Sbp = (TextView) findViewById(R.id.text_sbp);
        text_Dbp= (TextView) findViewById(R.id.text_dbp);
        text_Heart= (TextView) findViewById(R.id.text_heart);
        text_Resp= (TextView) findViewById(R.id.text_resp);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.db_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(onClickListener);
        listView.setOnItemLongClickListener(longClickListener);

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        showDatabase(sort);


        btn_Insert.setEnabled(true);
        btn_Update.setEnabled(false);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e("On Click", "position = " + position);
            nowIndex = Long.parseLong(arrayIndex.get(position));
            Log.e("On Click", "nowIndex = " + nowIndex);
            Log.e("On Click", "Data: " + arrayData.get(position));
            String[] tempData = arrayData.get(position).split("  ");
            Log.e("On Click", "Split Result = " + tempData);
            edit_Date.setText(tempData[0].trim());
            edit_Time.setText(tempData[1].trim());
            edit_Sbp.setText(tempData[2].trim());
            edit_Dbp.setText(tempData[3].trim());
            edit_Heart.setText(tempData[4].trim());
            edit_Resp.setText(tempData[5].trim());

            btn_Insert.setEnabled(false);
            btn_Update.setEnabled(true);
        }
    };

    private AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("Long Click", "position = " + position);
            nowIndex = Long.parseLong(arrayIndex.get(position));
            String[] nowData = arrayData.get(position).split("  ");
            String viewData = nowData[0] + ", " + nowData[1] + ", " + nowData[2] + ", " + nowData[3];
            AlertDialog.Builder dialog = new AlertDialog.Builder(StatusActivity.this);
            dialog.setTitle("데이터 삭제")
                    .setMessage("해당 데이터를 삭제 하시겠습니까?" + "\n" + viewData)
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(StatusActivity.this, "데이터를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                            mDbOpenHelper.deleteColumn(nowIndex);
                            showDatabase(sort);
                            setInsertMode();
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(StatusActivity.this, "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                            setInsertMode();
                        }
                    })
                    .create()
                    .show();
            return false;
        }
    };

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
            Intent intent = new Intent(StatusActivity.this, FeedActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.status) {
            Intent intent = new Intent(StatusActivity.this, StatusActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.chart) {
            Intent intent = new Intent(StatusActivity.this, ChartActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.like) {
            Intent intent = new Intent(StatusActivity.this, LikeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.more) {
            Intent intent = new Intent(StatusActivity.this, MoreActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void showDatabase(String sort){
        Cursor iCursor = mDbOpenHelper.sortColumn(sort);
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        arrayData.clear();
        arrayIndex.clear();
        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            String tempDate = iCursor.getString(iCursor.getColumnIndex("DATE"));
//            tempDate = setTextLength(tempDate,10);
            String tempTime = iCursor.getString(iCursor.getColumnIndex("TIME"));
//            tempTime = setTextLength(tempTime,10);
            String tempSbp = iCursor.getString(iCursor.getColumnIndex("SBP"));
//            tempSbp = setTextLength(tempSbp,10);
            String tempDbp = iCursor.getString(iCursor.getColumnIndex("DBP"));
//            tempDbp = setTextLength(tempDbp,10);
            String tempHeart = iCursor.getString(iCursor.getColumnIndex("HEART"));
//            tempHeart = setTextLength(tempHeart,10);
            String tempResp = iCursor.getString(iCursor.getColumnIndex("RESP"));
//            tempResp = setTextLength(tempResp,10);

            String Result = tempDate + "  " + tempTime + "  "+ tempSbp +"  "+ tempDbp +"  "+ tempHeart +"  "+ tempResp;
            System.out.println("Show Data : "+Result);
            arrayData.add(Result);
            arrayIndex.add(tempIndex);
        }
        arrayAdapter.clear();
        arrayAdapter.addAll(arrayData);
        arrayAdapter.notifyDataSetChanged();
    }

    public void setInsertMode(){
        edit_Date.setText("");
        edit_Time.setText("");
        edit_Sbp.setText("");
        edit_Dbp.setText("");
        edit_Heart.setText("");
        edit_Resp.setText("");

        btn_Insert.setEnabled(true);
        btn_Update.setEnabled(false);
    }
    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text = text + " ";
            }
        }
        return text;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_insert:
                Date = edit_Date.getText().toString();
                Time = edit_Time.getText().toString();
                Sbp = Long.parseLong(edit_Sbp.getText().toString());
                Dbp = Long.parseLong(edit_Dbp.getText().toString());
                Heart = Long.parseLong(edit_Heart.getText().toString());
                Resp = Long.parseLong(edit_Resp.getText().toString());
                mDbOpenHelper.open();
                mDbOpenHelper.insertColumn(Date,Time, Sbp, Dbp, Heart, Resp);
                showDatabase(sort);
                setInsertMode();
                edit_Date.requestFocus();
                edit_Date.setCursorVisible(true);
                break;

            case R.id.btn_update:
                Date = edit_Date.getText().toString();
                Time = edit_Time.getText().toString();
                Sbp = Long.parseLong(edit_Sbp.getText().toString());
                Dbp = Long.parseLong(edit_Dbp.getText().toString());
                Heart = Long.parseLong(edit_Heart.getText().toString());
                Resp = Long.parseLong(edit_Resp.getText().toString());
                mDbOpenHelper.updateColumn(nowIndex, Date, Time, Sbp, Dbp, Heart, Resp);
                showDatabase(sort);
                setInsertMode();
                edit_Date.requestFocus();
                edit_Date.setCursorVisible(true);
                break;

            case R.id.btn_select:
                showDatabase(sort);
                break;
        }
    }
}

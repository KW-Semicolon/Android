package dc24.iqos.breather;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.IntentCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.jakewharton.processphoenix.ProcessPhoenix;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.database.Cursor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static dc24.iqos.breather.R.drawable.abc_btn_default_mtrl_shape;
import static dc24.iqos.breather.R.drawable.dbp;
import static dc24.iqos.breather.R.drawable.result4;
import static dc24.iqos.breather.R.drawable.user;

public class FeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    final ConnectInfo info = new ConnectInfo();
    Button btn_result;
    Button btn_measure;


    private RecyclerAdapter adapter;//SBP, DBP, Heart, Resp 값 출력하는 파란 결과화면을 사용하기 위한 어댑터
    private DateRecyclerAdapter dateadapter;
    public DbOpenHelper mDbOpenHelper;//SQLite DB 사용 선언
    public TempData userdata; //SQLite DB에 결과값들을 버튼 클릭시 저장하도록

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);



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

    public void connectServerBtn(View view) {
        NetworkThread thread = new NetworkThread();

        mDbOpenHelper = new DbOpenHelper(FeedActivity.this);
        mDbOpenHelper.open();

        thread.start();//쓰레드 발생
    }

    public void storeDBBtn(View view) {
        NetworkThreadforStore thread = new NetworkThreadforStore();

        mDbOpenHelper = new DbOpenHelper(FeedActivity.this);
        mDbOpenHelper.open();

        thread.start();//쓰레드 발생
    }

    //네트워크 처리 담당 쓰래드
    class NetworkThread extends Thread {
        @Override
        public void run() {
            super.run();

            try {
                //클라이언트 객체 생성
                OkHttpClient client = new OkHttpClient();
                //클라이언트 객체에 정보를 셋팅하는 빌더 생성
                Request.Builder builder = new Request.Builder();
                //요청할 페이지의 주소를 셋팅
                builder = builder.url("http://localhost:8080/HttpBasicServer/server.jsp");

                Request request = builder.build();
                //요청한다

                Call call = client.newCall(request);

                NetworkCallback callback = new NetworkCallback();
                call.enqueue(callback);
                call.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //네트워크 처리 담당 쓰래드
    class NetworkThreadforStore extends Thread {
        @Override
        public void run() {
            super.run();

            try {
                //클라이언트 객체 생성
                OkHttpClient client = new OkHttpClient();
                //클라이언트 객체에 정보를 셋팅하는 빌더 생성
                Request.Builder builder = new Request.Builder();
                //요청할 페이지의 주소를 셋팅
                builder = builder.url("http://localhost:8080/HttpBasicServer/server.jsp");

                Request request = builder.build();
                //요청한다

                Call call = client.newCall(request);

                NetworkCallbackforStore callback = new NetworkCallbackforStore();
                call.enqueue(callback);
                call.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //응답결과가 수신되면 반응하는 call-back
    class NetworkCallback implements Callback {
        //네트워크 통신에 오류가 발생되면 호출되는 메서드
        @Override
        public void onFailure(Call call, IOException e) {

        }

        //응답결과가 정상적으로 수신되었을 때 호출되는 메서드
        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            try {
                //응답 결과를 수신

                mDbOpenHelper = new DbOpenHelper(FeedActivity.this);
//                mDbOpenHelper.open();


                final String result = response.body().string();
                //다른 쓰레드일지라도, 같이 사용하도록 final 선언

                //화면 갱신
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String date = "";
                        String time = "";
                        String sbp_txt = "";
                        String dbp_txt = "";
                        String heart_txt = "";
                        String resp_txt = "";

                        System.out.println("RESULT-----" + result);


//                        if(result.equals("\n\n\n\nError")){
//                            text1.setText("NO DATA");
//                        }
//                        else{
                        try {


                            String[] temp = result.split(", "); //SBP, DBP값 파싱

                            date = temp[0];
                            date = date.replace(System.getProperty("line.separator"), "");//개행문자 포함안되도록 처리
                            date = date.trim();//앞뒤 공백문자 포함안되도록 처리
                            time = temp[1];
                            time = time.replace(System.getProperty("line.separator"), "");//개행문자 포함안되도록 처리
                            sbp_txt = temp[2];
                            sbp_txt = sbp_txt.replaceAll("[^0-9]", "");//개행문자 포함안되도록 처리
                            dbp_txt = temp[3];
                            dbp_txt = dbp_txt.replaceAll("[^0-9]", "");//개행문자 포함안되도록 처리 (ex) "88\n"
                            heart_txt = temp[4];
                            heart_txt = heart_txt.replaceAll("[^0-9]", "");
                            resp_txt = temp[5];
                            resp_txt = resp_txt.replaceAll("[^0-9]", "");


                            final TextView home_date = findViewById(R.id.home_date);
                            final TextView home_time = findViewById(R.id.home_time);


                            final ImageView bp_result = findViewById(R.id.bp_home);
                            final ImageView heart_result = findViewById(R.id.heart_home);
                            final ImageView resp_result = findViewById(R.id.resp_home);

                            home_date.setText(date);
                            home_time.setText(time);

                            bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result_default));
                            heart_result.setImageDrawable(getResources().getDrawable(R.drawable.result_default));
                            resp_result.setImageDrawable(getResources().getDrawable(R.drawable.result_default));
                            //결과 상태 기본 세팅


                            //결과값에 따른 상태 판별
                            int sbp_value = 0;
                            int dbp_value = 0;
                            int heart_value = 0;
                            int resp_value = 0;

                            try {
                                sbp_value = Integer.parseInt(sbp_txt);
                                dbp_value = Integer.parseInt(dbp_txt);
                                //dbp_value = Integer.parseInt(dbp_txt.substring(0, 3));
                                System.out.println("sbp_value : " + sbp_value);
                                if (sbp_value < 90) {//저혈압
                                    bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result1));
                                } else if (sbp_value >= 90 && sbp_value < 130) {
                                    //정상
                                    bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result2));
                                } else if (sbp_value >= 130 && sbp_value < 140) {
                                    //고혈압 전단계
                                    bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result3));
                                }else if (sbp_value >= 140 && sbp_value < 160) {
                                    //1기 고혈압
                                    bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result4));
                                }else if (sbp_value >= 160 && sbp_value < 180) {
                                    //2기 고혈압
                                    bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result5));
                                }else {
                                    //고혈압 위기
                                    bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result6));
                                }
                            } catch(NumberFormatException nfe) {
                                System.out.println("Could not parse " + nfe);
                            }
                            try {
                                heart_value = Integer.parseInt(heart_txt);
                                System.out.println("heart_value : " + heart_value);
                                if (heart_value < 50) {//서맥
                                    heart_result.setImageDrawable(getResources().getDrawable(R.drawable.heart_result1));
                                } else if (heart_value >= 50 && heart_value <= 100) {
                                    //정상
                                    heart_result.setImageDrawable(getResources().getDrawable(R.drawable.heart_result2));
                                } else {
                                    //빈맥
                                    heart_result.setImageDrawable(getResources().getDrawable(R.drawable.heart_result3));
                                }
                            } catch(NumberFormatException nfe) {
                                System.out.println("Could not parse " + nfe);
                            }
                            try {
                                resp_value = Integer.parseInt(resp_txt);
                                System.out.println("resp_value : " + resp_value);
                                if (resp_value < 11) {//서맥
                                    resp_result.setImageDrawable(getResources().getDrawable(R.drawable.resp_result1));
                                } else if (resp_value >= 11 && resp_value <= 23) {
                                    //정상
                                    resp_result.setImageDrawable(getResources().getDrawable(R.drawable.resp_result2));
                                } else {
                                    //빈맥
                                    resp_result.setImageDrawable(getResources().getDrawable(R.drawable.resp_result3));
                                }
                            } catch(NumberFormatException nfe) {
                                System.out.println("Could not parse " + nfe);
                            }

                            System.out.println("===============================");
                            System.out.println(date);
                            System.out.println(time);
                            System.out.println(sbp_txt);
                            System.out.println(dbp_txt);
                            System.out.println(heart_txt);
                            System.out.println(resp_txt);
                            System.out.println("===============================");

//                            //DB 저장
                            userdata = new TempData(date, time , sbp_value, dbp_value, heart_value, resp_value);
//
                            init();
//
                            getData(sbp_txt, dbp_txt, heart_txt, resp_txt); //결과값


                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //응답결과가 수신되면 반응하는 call-back
    class NetworkCallbackforStore implements Callback {
        //네트워크 통신에 오류가 발생되면 호출되는 메서드
        @Override
        public void onFailure(Call call, IOException e) {

        }

        //응답결과가 정상적으로 수신되었을 때 호출되는 메서드
        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            try {
                //응답 결과를 수신

                mDbOpenHelper = new DbOpenHelper(FeedActivity.this);
//                mDbOpenHelper.open();


                final String result = response.body().string();
                //다른 쓰레드일지라도, 같이 사용하도록 final 선언

                //화면 갱신
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String date = "";
                        String time = "";
                        String sbp_txt = "";
                        String dbp_txt = "";
                        String heart_txt = "";
                        String resp_txt = "";

                        System.out.println("RESULT-----" + result);


//                        if(result.equals("\n\n\n\nError")){
//                            text1.setText("NO DATA");
//                        }
//                        else{
                        try {


                            String[] temp = result.split(", "); //SBP, DBP값 파싱

                            date = temp[0];
                            date = date.replace(System.getProperty("line.separator"), "");//개행문자 포함안되도록 처리
                            date = date.trim();//앞뒤 공백문자 포함안되도록 처리
                            time = temp[1];
                            time = time.replace(System.getProperty("line.separator"), "");//개행문자 포함안되도록 처리
                            sbp_txt = temp[2];
                            sbp_txt = sbp_txt.replaceAll("[^0-9]", "");//개행문자 포함안되도록 처리
                            dbp_txt = temp[3];
                            dbp_txt = dbp_txt.replaceAll("[^0-9]", "");//개행문자 포함안되도록 처리 (ex) "88\n"
                            heart_txt = temp[4];
                            heart_txt = heart_txt.replaceAll("[^0-9]", "");
                            resp_txt = temp[5];
                            resp_txt = resp_txt.replaceAll("[^0-9]", "");


                            final TextView home_date = findViewById(R.id.home_date);
                            final TextView home_time = findViewById(R.id.home_time);


                            final ImageView bp_result = findViewById(R.id.bp_home);
                            final ImageView heart_result = findViewById(R.id.heart_home);
                            final ImageView resp_result = findViewById(R.id.resp_home);

                            home_date.setText(date);
                            home_time.setText(time);

                            bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result_default));
                            heart_result.setImageDrawable(getResources().getDrawable(R.drawable.result_default));
                            resp_result.setImageDrawable(getResources().getDrawable(R.drawable.result_default));
                            //결과 상태 기본 세팅


                            //결과값에 따른 상태 판별
                            int sbp_value = 0;
                            int dbp_value = 0;
                            int heart_value = 0;
                            int resp_value = 0;

                            try {
                                sbp_value = Integer.parseInt(sbp_txt);
                                dbp_value = Integer.parseInt(dbp_txt);
                                //dbp_value = Integer.parseInt(dbp_txt.substring(0, 3));
                                System.out.println("sbp_value : " + sbp_value);
                                if (sbp_value < 90) {//저혈압
                                    bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result1));
                                } else if (sbp_value >= 90 && sbp_value < 130) {
                                    //정상
                                    bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result2));
                                } else if (sbp_value >= 130 && sbp_value < 140) {
                                    //고혈압 전단계
                                    bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result3));
                                }else if (sbp_value >= 140 && sbp_value < 160) {
                                    //1기 고혈압
                                    bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result4));
                                }else if (sbp_value >= 160 && sbp_value < 180) {
                                    //2기 고혈압
                                    bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result5));
                                }else {
                                    //고혈압 위기
                                    bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result6));
                                }
                            } catch(NumberFormatException nfe) {
                                System.out.println("Could not parse " + nfe);
                                Toast.makeText(FeedActivity.this,"측정된 데이터가 없습니다",Toast.LENGTH_SHORT).show();

                            }
                            try {
                                heart_value = Integer.parseInt(heart_txt);
                                System.out.println("heart_value : " + heart_value);
                                if (heart_value < 50) {//서맥
                                    heart_result.setImageDrawable(getResources().getDrawable(R.drawable.heart_result1));
                                } else if (heart_value >= 50 && heart_value <= 100) {
                                    //정상
                                    heart_result.setImageDrawable(getResources().getDrawable(R.drawable.heart_result2));
                                } else {
                                    //빈맥
                                    heart_result.setImageDrawable(getResources().getDrawable(R.drawable.heart_result3));
                                }
                            } catch(NumberFormatException nfe) {
                                System.out.println("Could not parse " + nfe);
                            }
                            try {
                                resp_value = Integer.parseInt(resp_txt);
                                System.out.println("resp_value : " + resp_value);
                                if (resp_value < 11) {//서맥
                                    resp_result.setImageDrawable(getResources().getDrawable(R.drawable.resp_result1));
                                } else if (resp_value >= 11 && resp_value <= 23) {
                                    //정상
                                    resp_result.setImageDrawable(getResources().getDrawable(R.drawable.resp_result2));
                                } else {
                                    //빈맥
                                    resp_result.setImageDrawable(getResources().getDrawable(R.drawable.resp_result3));
                                }
                            } catch(NumberFormatException nfe) {
                                System.out.println("Could not parse " + nfe);
                            }

                            System.out.println("===============================");
                            System.out.println(date);
                            System.out.println(time);
                            System.out.println(sbp_txt);
                            System.out.println(dbp_txt);
                            System.out.println(heart_txt);
                            System.out.println(resp_txt);
                            System.out.println("===============================");

                            //DB 저장
                            userdata = new TempData(date, time , sbp_value, dbp_value, heart_value, resp_value);

                            init();

                            mDbOpenHelper.insertColumn(date, time, sbp_value, dbp_value, heart_value, resp_value);

                            getData(sbp_txt, dbp_txt, heart_txt, resp_txt); //결과값


                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            Intent intent = new Intent(FeedActivity.this, FeedActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.status) {
            Intent intent = new Intent(FeedActivity.this, StatusActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.chart) {
            Intent intent = new Intent(FeedActivity.this, ChartActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.like) {
            Intent intent = new Intent(FeedActivity.this, LikeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.more) {
            Intent intent = new Intent(FeedActivity.this, MoreActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }


    private void getData(String sbp, String dbp, String heart, String resp) {

        String sbp_result = "수축 " + sbp + " mmHg";
        //sbp_result = sbp_result.concat("mmHg");
        String dbp_result = "확장 " + dbp + " mmHg";
        String heart_result = "심박 " + heart + " bpm";
        String resp_result = "호흡 " + resp + " 회/분";


        List<String> listContent = Arrays.asList(
                sbp_result,
                dbp_result,
                heart_result,
                resp_result
        );

        System.out.println("===============listContent==========");
        for (int i = 0; i < listContent.size(); i++) {
            System.out.println(listContent.get(i));
        }

        List<Integer> listResId = Arrays.asList(
                R.drawable.sbp,
                R.drawable.dbp,
                R.drawable.heartbeat,
                R.drawable.lung
        );
        for (int i = 0; i < listContent.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            Data data = new Data();
            data.setContent(listContent.get(i));
            data.setResId(listResId.get(i));

            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }//함수 getData() 끝

    public void restart() {
        Intent nextIntent = new Intent(this, FeedActivity.class);
        ProcessPhoenix.triggerRebirth(this, nextIntent);
    }

    @Override
    public void onClick(View v) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        switch (v.getId()) {//시간 저장을 위한 기능
            case R.id.btn_measure: //시간 측정 버튼 => 측정 시간 저장 , 시간 카운트다운 시작


                break;
            case R.id.btn_result:

//                //restart();//다시 시작
//
//
//                //건강 결과가 담겨있는 txt파일에서 각각의 결과값을 받아올 String 변수 선언
//                String bp_txt = "";
//
//                String sbp_txt = "";
//                String dbp_txt = "";
//                String heart_txt = "";
//                String resp_txt = "";
//
//                /////////////////// "res/raw" 경로에 있는 Result txt file 읽기에 대한 try - catch 문 ////////////////
//                try {
//                    InputStream in = getResources().openRawResource(R.raw.bp);
//                    byte[] result = new byte[in.available()];
//                    in.read(result);
//                    bp_txt = new String(result);
//                    System.out.println("BP DATA " + bp_txt);
//                } catch (Exception e) {
//
//                }
//                System.out.println("BP DATA " + bp_txt);
//
//                String[] bp_temp = bp_txt.split(", "); //SBP, DBP값 파싱
//
//                sbp_txt = bp_temp[0];
//                sbp_txt = sbp_txt.replaceAll("[^0-9]", "");//개행문자 포함안되도록 처리
//                dbp_txt = bp_temp[1];
//                dbp_txt = dbp_txt.replaceAll("[^0-9]", "");//개행문자 포함안되도록 처리 (ex) "88\n"
//
//                System.out.println("SBP DATA " + sbp_txt);
//                System.out.println("DBP DATA " + dbp_txt);
//                try {
//
//                    InputStream in = getResources().openRawResource(R.raw.heart);
//                    byte[] result = new byte[in.available()];
//                    in.read(result);
//                    heart_txt = new String(result);
//                    System.out.println("HEART DATA " + heart_txt);
//                    heart_txt = heart_txt.replaceAll("[^0-9]", "");
//                } catch (Exception e) {
//
//                }
//                System.out.println("HEART DATA " + heart_txt);
//
//                try {
//
//                    InputStream in = getResources().openRawResource(R.raw.resp);
//                    byte[] result = new byte[in.available()];
//                    in.read(result);
//                    resp_txt = new String(result);
//                    System.out.println("RESP DATA " + resp_txt);
//                    resp_txt = resp_txt.replaceAll("[^0-9]", "");
//                } catch (Exception e) {
//
//                }
//                System.out.println("RESP DATA " + resp_txt);
//                ///////////////////////Result txt file 받아오기 - end /////////////////////////////////
//
//                //Date 객체를 이용한 현재 시간 출력//
//                SimpleDateFormat newformat1 = new SimpleDateFormat("yyyy-MM-dd");
//                SimpleDateFormat newformat2 = new SimpleDateFormat("HH");
//                SimpleDateFormat newformat3 = new SimpleDateFormat("a");
//                String calmin = "";
//
//                Calendar cal = Calendar.getInstance();
//                cal.add(cal.MINUTE, -2);
//                Date newtime = new Date();
//
//                Date newtoday = new Date();
//                System.out.println(newtoday);
//
//
//                String newtime1 = newformat1.format(newtime);
//                String newtime2 = newformat2.format(newtime) + ":" + cal.get(cal.MINUTE) + newformat3.format(newtime);
//
//                // System.out.println(time1);
//                //System.out.println(time2);
//
//                final TextView home_date = findViewById(R.id.home_date);
//                final TextView home_time = findViewById(R.id.home_time);
//
//
//                final ImageView bp_result = findViewById(R.id.bp_home);
//                final ImageView heart_result = findViewById(R.id.heart_home);
//                final ImageView resp_result = findViewById(R.id.resp_home);
//
//
//                home_date.setText(newtime1);
//                home_time.setText(newtime2);
//
//
//                bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result_default));
//                heart_result.setImageDrawable(getResources().getDrawable(R.drawable.result_default));
//                resp_result.setImageDrawable(getResources().getDrawable(R.drawable.result_default));
//
//                int sbp_value = 0;
//                int dbp_value = 0;
//                int heart_value = 0;
//                int resp_value = 0;
//
//                try {
//                    sbp_value = Integer.parseInt(sbp_txt);
//                    dbp_value = Integer.parseInt(dbp_txt);
//                    //dbp_value = Integer.parseInt(dbp_txt.substring(0, 3));
//                    System.out.println("sbp_value : " + sbp_value);
//                    if (sbp_value < 90) {//저혈압
//                        bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result1));
//                    } else if (sbp_value >= 90 && sbp_value < 130) {
//                        //정상
//                        bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result2));
//                    } else if (sbp_value >= 130 && sbp_value < 140) {
//                        //고혈압 전단계
//                        bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result3));
//                    } else if (sbp_value >= 140 && sbp_value < 160) {
//                        //1기 고혈압
//                        bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result4));
//                    } else if (sbp_value >= 160 && sbp_value < 180) {
//                        //2기 고혈압
//                        bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result5));
//                    } else {
//                        //고혈압 위기
//                        bp_result.setImageDrawable(getResources().getDrawable(R.drawable.result6));
//                    }
//                } catch (NumberFormatException nfe) {
//                    System.out.println("Could not parse " + nfe);
//                }
//                try {
//                    heart_value = Integer.parseInt(heart_txt);
//                    System.out.println("heart_value : " + heart_value);
//                    if (heart_value < 50) {//서맥
//                        heart_result.setImageDrawable(getResources().getDrawable(R.drawable.heart_result1));
//                    } else if (heart_value >= 50 && heart_value <= 100) {
//                        //정상
//                        heart_result.setImageDrawable(getResources().getDrawable(R.drawable.heart_result2));
//                    } else {
//                        //빈맥
//                        heart_result.setImageDrawable(getResources().getDrawable(R.drawable.heart_result3));
//                    }
//                } catch (NumberFormatException nfe) {
//                    System.out.println("Could not parse " + nfe);
//                }
//                try {
//                    resp_value = Integer.parseInt(resp_txt);
//                    System.out.println("resp_value : " + resp_value);
//                    if (resp_value < 11) {//서맥
//                        resp_result.setImageDrawable(getResources().getDrawable(R.drawable.resp_result1));
//                    } else if (resp_value >= 11 && resp_value <= 23) {
//                        //정상
//                        resp_result.setImageDrawable(getResources().getDrawable(R.drawable.resp_result2));
//                    } else {
//                        //빈맥
//                        resp_result.setImageDrawable(getResources().getDrawable(R.drawable.resp_result3));
//                    }
//                } catch (NumberFormatException nfe) {
//                    System.out.println("Could not parse " + nfe);
//                }
//
//                //DB 저장
//                String dbdate = userdata.getDate();
//                String dbtime = userdata.getTime();
//
//                mDbOpenHelper.open();
//                mDbOpenHelper.insertColumn(dbdate, dbtime, sbp_value, dbp_value, heart_value, resp_value);
//                init();
//
//                getData(sbp_txt, dbp_txt, heart_txt, resp_txt); //결과값
                break;
        }
    }

}

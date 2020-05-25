package dc24.iqos.breather;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class LikeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ViewFlipper imgBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);

        imgBanner=findViewById(R.id.imgBanner);

        int sliders[]= {
                R.drawable.healthani01, R.drawable.healthani02, R.drawable.healthani03,
                R.drawable.healthani04, R.drawable.healthani05
//                R.layout.v1, R.layout.v2, R.layout.v3
        };
        for(int slide:sliders){
            bannerFliper(slide);
        }

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

    public void bannerFliper(int image){
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(image);
//        TextView imageView = new TextView(this);
//        imageView.setBackgroundResource(image);
        imgBanner.addView(imageView);
        imgBanner.setFlipInterval(4000);
        imgBanner.setAutoStart(true);
        imgBanner.setInAnimation(this, android.R.anim.fade_in);
        imgBanner.setOutAnimation(this,android.R.anim.fade_out);

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
        getMenuInflater().inflate(R.menu.like, menu);
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
            Intent intent=new Intent(LikeActivity.this, FeedActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.status) {
            Intent intent=new Intent(LikeActivity.this, StatusActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.chart) {
            Intent intent=new Intent(LikeActivity.this, ChartActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.like) {
            Intent intent=new Intent(LikeActivity.this, LikeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        } else if (id == R.id.more) {
            Intent intent=new Intent(LikeActivity.this, MoreActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//화면전환 효과 제거
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

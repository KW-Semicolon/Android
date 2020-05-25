package dc24.iqos.breather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            // 4초뒤에 다음화면(MainActivity)으로 넘어가기 Handler 사용
            Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
            startActivity(intent); // 다음화면으로 넘어가기
            overridePendingTransition(0, 0);//화면전환 효과 제거
            finish(); // Activity 화면 제거
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//MainActivity에서만 액션바 없애기
        setContentView(R.layout.activity_main);//xml과 java소스 연결

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 다시 화면에 들어어왔을 때 예약 걸어주기
        handler.postDelayed(r, 3000); // 3초 뒤에 Runnable 객체 수행
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 화면을 벗어나면, handler 에 예약해놓은 작업을 취소하자
        handler.removeCallbacks(r); // 예약 취소
    }

}
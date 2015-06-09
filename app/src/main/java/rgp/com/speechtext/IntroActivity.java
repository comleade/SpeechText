package rgp.com.speechtext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class IntroActivity extends Activity {

    private static final String TAG 			= "IntroActivity";

    private static final int SIGN_OK 			= 1;		// 로그인 화면
    Handler h ;
    Animation animFadein;
    ImageView introScreen ;
    Context mContext;

    boolean signLogin	= false ;
    String signId ;
    String signPass ;

    @SuppressWarnings("null")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);									// 타이틀바 없이
        setContentView(R.layout.introact);

        mContext	= getApplicationContext() ;

        loginOk();
    }


    /**
     * 아이디,비번이 일치하거나 실행시 로그인 화면=false이면 실행
     */
    private void loginOk() {
        //Log.i(TAG, "아이디, 비번 확인 함") ;

        introScreen = (ImageView) findViewById(R.id.introscreen);
        animFadein = AnimationUtils.loadAnimation(mContext, R.anim.blink);
        introScreen.startAnimation(animFadein);

        h = new Handler() ;
        h.postDelayed(irun, 2 * 1000);			// 4초 동안 인트로 화면

    }

    Runnable irun = new Runnable() {
        @Override
        public void run() {
            Intent i = new Intent(IntroActivity.this, MainActivity.class) ;
            startActivity(i);
            finish();
        }
    };

    /**
     * 뒤로가기 버튼을 눌러서 꺼졌을 시 4초 후 메인 페이지가 뜨는 것을 방지
     */
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        h.removeCallbacks(irun);
    }

}
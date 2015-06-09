package rgp.com.speechtext.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private static final String TAG = "Logger";
    private static String filename = "";

    static boolean isExternalStorageAvailable = false;
    static boolean isExternalStorageWriteable = false;
    static String state = Environment.getExternalStorageState();
    static BufferedWriter buf= null ;
    static int LogCnt = 0 ;

    Context context;

    /**
     * 사용자 로그 파일을 생성한다.(toggle_no/logcat_중계폰_년월일.txt)
     * @param tag		: 프로그램 구분
     * @param message	: 화면표시 및 메세지
     *
     * @return			: void
     */
    public void addToLog(Context context, String tag, String message) {
        String phoneNumber	= ConfigInfo.getPhoneNumber() ;

        if("".equals(phoneNumber) || phoneNumber == null) {
            String strMessage = "로그 화일을 저장할 수 없습니다.\n개발자에게 문의하시기 바랍니다." ;
            util.toastmake(context, strMessage);
            Log.e(TAG, strMessage) ;
            return ;
        }
        // 프로그램을 종료하지 않을 수 있기 때문에 푸시를 받을 때 일자별로 로그 파일을 생성한다.
        String currenttime	= DateUtil.getCurrentDate() ;					// yyyyMMdd
        ConfigInfo.setLogFileName("logcat_" + phoneNumber + "_" + currenttime + ".txt");

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            isExternalStorageAvailable = isExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            isExternalStorageAvailable = true;
            isExternalStorageWriteable = false;
        } else {
            isExternalStorageAvailable = isExternalStorageWriteable = false;
        }

        //로그 화일이름
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + ConfigInfo.getDownLoadFolder());
        dir.mkdirs();

        File logFile = new File(dir, ConfigInfo.getLogFileName());
        filename = ConfigInfo.getLogFileName();
        if (!logFile.exists())  {
            try  {
                Log.d(TAG, "File created Again");
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LogCnt += 1;
        try {
            // 버퍼에서 화일로 저장을 하기 위해
            buf = new BufferedWriter(new FileWriter(logFile, true));

            buf.write(String.valueOf(LogCnt)+"\t"+DateUtil.getCurrentDateTimeDot()+"\t"+message+"\t\t"+tag);
            buf.newLine();
            buf.flush();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Log Write Error...") ;
        } finally {
            if( null != buf) {
                try { buf.close(); } catch (Exception e) {}
            }
        }
    }
}

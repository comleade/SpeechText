package rgp.com.speechtext.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ConfigInfo {
    private static final String TAG = "ConfigInfo";

    static Context context ;

    private static String LogFileSend	= "F" ;			// F, T     로그 화일 전송하기
    private String LogFileName			= "" ;
    private boolean startLogin			= false  ;		// 실행시마다 로그인화면

    private  String serverInfo				= "" ;		// 서버와의 연결정보 문자
    private  String regId					= "" ;

    private  String App2svrMain 			= "" ;
    private  String App2svr_jsp				= "" ;

    private  String CommCompany             = "" ;      // 통신사 정보 저장

    private static String PhoneNumber				= "";
    private String deviceModel				= "" ;						// 폰의 모델
    private String deviceVersion			= "" ;						// 폰의 버젼

    private String pref_filename 			= "setting.dat" ;			// 환경설정화일

    /** 다운로드 폴더 (로그저장, 로그보기, apk저장, 백업 등) */
    private static String downLoadFolder			= "/download" ;

    static SharedPreferences pref ;
    static SharedPreferences.Editor editor ;
    /**
     * 메인 & 푸시 실행시 사용되는 변수들을 저장하고 호출한다.
     * @param context
     */
    public ConfigInfo(Context context) {
            this.context				= context;

        pref = context.getSharedPreferences(getPref_filename(), this.context.MODE_PRIVATE) ;
        editor = pref.edit() ;

        this.LogFileSend			= "T" ;
        this.LogFileName			= "" ;
        this.startLogin				= true ;

            // 실제 서버일 경우에는 ===========================================================================
//     		this.App2svrMain 			= "http://svr1.reve.co.kr:18080/app2svr/" ;		// 유경구 PC를 서버로 사용
            this.App2svrMain 			= "http://www.eversms.co.kr/app2svr/" ;			// 실서버일 경우
            //this.App2svrMain 			= "http://svr1.reve.co.kr:8180/app2svr/" ;		// 박래경 PC를 서버로 사용

            this.App2svr_jsp			= "setPhoneInfo.do" ;							// 토큰정보 추가,수정

        if(!App2svrMain.equals(getApp2svrMain())) {
            // 현재 메인과 저장된 것이 다르면 다시 저장한다.

            setApp2svrMain(App2svrMain) ;
            setApp2svr_jsp( App2svr_jsp) ;

            Log.e(TAG, "서버 값 변경하였습니다./" + App2svrMain) ;
        }
        this.PhoneNumber					= "" ;
    }

    /**
     * 저장된 변수 값을 리턴한다.
     * @param getName
     * @return
     */
    @SuppressWarnings("static-access")
    public static String getPref(String getName) {
        String returnPref	= "" ;
        returnPref			= pref.getString(getName, "") ;

        if("LogFileSend".equals(getName) && "".equals(returnPref)) {
            // 로그화일전송 초기값.......
            setLogFileSend(LogFileSend) ;
            returnPref 	= LogFileSend ;
        }

        return returnPref.trim() ;
    }

    public static int getPref(String getName, int iValue) {
        int returnPref	= 0 ;
        returnPref	= pref.getInt(getName, iValue) ;

        return returnPref ;
    }

    public boolean getPref(String getName,  boolean iValue) {
        boolean returnPref	= false ;
        returnPref	= pref.getBoolean(getName, iValue) ;

        return returnPref ;
    }

    public static void setPref(String getName, String iValue) {
        editor.putString(getName, 	iValue) ;
        editor.commit() ;
    }

    public void setPref(String getName, int iValue) {
        editor.putInt(getName, iValue) ;
        editor.commit() ;
    }

    public void setPref(String getName, boolean iValue) {
        editor.putBoolean(getName, iValue) ;
        editor.commit() ;
    }

    // ====================================================================================================

    public static String getDeviceModel() {
        String strreturn = getPref("deviceModel");
        if("".equals(strreturn) || strreturn == null) {
            strreturn = android.os.Build.MODEL + " / " + android.os.Build.PRODUCT ;
            setDeviceModel(strreturn.trim());
        }
        return strreturn ;
    }

    public String getDeviceVersion() {
        String strreturn = getPref("deviceVersion") ;
        if("".equals(strreturn) || strreturn == null) {
            strreturn = android.os.Build.VERSION.RELEASE ;
            setDeviceVersion(strreturn.trim());
        }
        return strreturn ;
    }

    public static void setDeviceModel(String deviceModel) {
        setPref("deviceModel", deviceModel);
    }

    public void setDeviceVersion(String deviceVersion) {
        setPref("deviceVersion", deviceVersion);
    }

    public static String getLogFileSend() {
        return getPref("LogFileSend").trim();
    }

    public static String getLogFileName() {
        return getPref("LogFileName");
    }

    public boolean getStartLogin() {
        return getPref("startLogin", this.startLogin);
    }

    public static String getApp2svrMain() {
        return getPref("App2svrMain");
    }

    public static String getApp2svr_setLogFile() {
        return getPref("App2svr_setLogFile");
    }
    public static String getPhoneNumber() {
        try {
            if("".equals(PhoneNumber)) {
                // 앱 폰번호가 비어 있으면
                PhoneNumber = util.getTelNumber(context) ;			// 폰의 전화번호 가져오기
                setPhoneNumber(PhoneNumber);						// 저장을 한다.
            }
            // PhoneNumber	= util.setTelNumberPrt(PhoneNumber) ;		// -를 넣어서
            // 서버와 통신일 경우에는 -를 빼고 한다. ** 중요 **
        }catch(Exception e){
        }
        return PhoneNumber;
    }

    public String getPref_filename() {
        return pref_filename;			// 기본값이 있기 때문에
    }

    public static void setLogFileSend(String logFileSend) {
        setPref("LogFileSend",logFileSend);
    }

    public static void setLogFileName(String logFileName) {
        setPref("LogFileName",logFileName);
    }

    public void setStartLogin(boolean startLogin) {
        setPref("startLogin",startLogin);
    }

    public void setApp2svrMain(String app2svrMain) {
        setPref("App2svrMain",app2svrMain);
    }

    public void setApp2svr_jsp(String app2svr_jsp) {
        setPref("App2svr_jsp",app2svr_jsp);
    }

    public static void setPhoneNumber(String phoneNumber) {
        setPref("PhoneNumber" ,phoneNumber);
    }

    public void setPref_filename(String pref_filename) {
        setPref("pref_filename",pref_filename);
    }
    /** 다운로드 폴더 (로그화일, apk저장 등) */
    public static String getDownLoadFolder() {
        return downLoadFolder;
    }
    public String getCommCompany() {
        return getPref("CommCompany");
    }

    public void setCommCompany(String commCompany) {
        setPref("CommCompany",commCompany);
    }

}

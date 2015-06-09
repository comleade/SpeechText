package rgp.com.speechtext.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import rgp.com.speechtext.R;

public class util {
    private static final String TAG = "util";
    static Logger logger = new Logger() ;

    Context context;
    static ConfigInfo configinfo ;

    public util(){
    }

    public static String uploadFile(Context context, String sourceFileUri) {
        final String TAG = "uploadFile";

        String fileName = sourceFileUri;
        String serverResponseCode = "False";

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        configinfo = new ConfigInfo(context) ;

        String upLoadServerUri = configinfo.getApp2svrMain()+configinfo.getApp2svr_setLogFile() ;
        if (!sourceFile.isFile()) {
            Log.e(TAG, "Source File not exist :" + sourceFileUri);
            return serverResponseCode ;
        }
        else
        {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name="+sourceFile+";filename='"
                        + fileName + "'" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                int serverResCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                if(serverResCode == 200){
                    serverResponseCode = "True" ;
                }
                Log.v(TAG, "Work->6 Log File Sending: " + serverResponseMessage + "-> " + serverResponseCode);

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e(TAG, "Work->6 Error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Work->6 Exception : "+ e.getMessage(), e);
            }
            return serverResponseCode;

        } // End else block
    }

    public static void logFileStart(Context context, String message) {
        final String TAG = "util.logFileStart";

        configinfo = new ConfigInfo(context) ;

        BufferedWriter buf = null ;
        String currenttime	= String.valueOf(System.currentTimeMillis()) ;
        configinfo.setLogFileName("logcat_"+configinfo.getPhoneNumber()+"_"+currenttime.substring(0,6)+".txt");

        //로그 화일이름
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/toggle_no");
        dir.mkdirs();

        File logFile = new File(dir, configinfo.getLogFileName());
        Log.d(TAG,"로그화일만들기:"+dir.toString()+"/"+configinfo.getLogFileName());
        if (!logFile.exists())  {
            try  {
                Log.d(TAG, "File created Again");
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            buf = new BufferedWriter(new FileWriter(logFile, true));

            buf.write(message + "\r\n");
            //buf.append(message);
            buf.newLine();
            buf.flush();
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "로그 저장 화일을 저장하지 못했습니다.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "로그 저장 화일을 저장하지 못했습니다.");
        } finally {
            if( null != buf) {
                try { buf.close(); } catch (Exception e) {}
            }
        }
        return ;
    }

    public static void logFileView(Context context) {
        final String TAG = "util.logFileView";
        final String SMS_DELIVER_REPORT_ACTION = "com.nyaruka.androidrelay.SMS_DELIVER_REPORT";
        final String EXTRA_FORMAT = "revetec.app.ppms.util.FORMAT";//$NON-NLS-1$
        final String EXTRA_BUFFER = "revetec.app.ppms.util.BUFFER";//$NON-NLS-1$

        configinfo = new ConfigInfo(context) ;

        final int MAX_LOG_MESSAGE_LENGTH = 100000;
        StringBuilder log=new StringBuilder();
        try {
            // Process process = Runtime.getRuntime().exec("logcat -d -v threadtime");
            ArrayList<String> commandLine = new ArrayList<String>();
            commandLine.add("logcat");//$NON-NLS-1$
            commandLine.add("-d");//$NON-NLS-1$

            commandLine.add("-v");
            commandLine.add("threadtime");				// time

            commandLine.add("-t");
            commandLine.add("5000");

			/*
			commandLine.add("*:S");
			*/
            Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[0]));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			/*
			Process process = Runtime.getRuntime().exec("logcat -d -v threadtime");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			*/

            String currenttime	= String.valueOf(System.currentTimeMillis()) ;
            configinfo.setLogFileName("logcat_"+configinfo.getPhoneNumber()+"_"+currenttime.substring(0,6)+".txt");

            //로그 화일이름
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/toggle_no");
            dir.mkdirs();

            File file = new File(dir, configinfo.getLogFileName());
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            Log.d(TAG,"로그화일만들기:"+dir.toString()+"/"+configinfo.getLogFileName());

            pw.println("로그화일:"+dir.toString()+"/"+configinfo.getLogFileName()+"\r\n");
            try {
                String[] logDIE ;
                String gubnSave ;
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    logDIE = line.split(" ") ;
                    // Log.i(TAG,logDIE.toString());					// 디버깅시 화면에 보이기 위해서

                    gubnSave = logDIE[4].trim() ;
                    // Debug, Info, Error, Warring
                    // 위에 옵션 : threadtime
                    if("D".equals(gubnSave) || "E".equals(gubnSave) || "W".equals(gubnSave) ) {
                        if(-1 == logDIE[5].trim().indexOf("dalvikvm")) {
                            // log.append(line+"=\r\n");
                            // pw.println(line.toString()+"=\r\n");
                            // 화일에 저장이 되지 않아서 한줄씩 저장한다.

                            log.append(line+"\r\n");
                            pw.println(line.toString()+"\r\n");
                        }
                    }
                }
                // pw.println(log.toString());
                pw.flush();
                pw.close();
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "로그 저장 화일을 저장하지 못했습니다.");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "로그 저장 화일을 저장하지 못했습니다.");
            } finally {
                if( null != pw) {
                    try { pw.close(); } catch (Exception e) {}
                }
                if( null != f) {
                    try { f.close(); } catch (Exception e) {}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "로그 저장 화일을 생성하지 못했습니다.");
        }
        return ;
    }

    /**
     * 조건에 따라 프로그램 실행여부 확인 한다.
     * @param sPrivillage : 실행여부 문자
     * @return true, flase
     */
    @SuppressWarnings("unused")
    public static Boolean isExcute(Context context, String sPrivillage) {
        Boolean lReturn=false;
        configinfo = new ConfigInfo(context) ;
        String phoneNumber	=  configinfo.getPhoneNumber() ;

        if("sendProgress".equals(sPrivillage) ){
            if(phoneNumber.contains("0555")  || "01025412462".equals(phoneNumber) ) {
                lReturn=true;		// false, true
            }
            lReturn=true;			// ** 중요 ** 지우면 문자보내기 화면이 나오지 않는다.
            // 메세지 전송 확인
        }
        return lReturn ;
    }
    // keyboard 보여주기 위한 함수
    // android:windowSoftInputMode="stateVisible" 				//  manifest
    public static void showVirtualKeyboard(Context context, final View view) {
        if (context != null) {
            final InputMethodManager imm =  (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            view.clearFocus();

            if(view.isShown()) {
                imm.showSoftInput(view, 0);
                view.requestFocus();
            } else {
                view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                view.requestFocus();
                                imm.showSoftInput(view, 0);
                            }
                        });

                        view.removeOnAttachStateChangeListener(this);
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        view.removeOnAttachStateChangeListener(this);
                    }
                });
            }
        }
    }

    /** 첫번째, 10째씩만(10,20,30), 마지막 화면표시 및 로그화일 저장 하도록 한다.( true )
     * @param i 		: 전화번호 ? 번째
     * @param length 	: 전체 갯수
     * @return boolen	: 기록여부 리턴
     */
    public static boolean saveLogComp(int i, int length) {
        boolean lReturn = false ;
        int idivide 	= 10 ;			// 10번째씩 계산해서 저장한다.

        if(i == 0 || i == length-1) {
            // 첫번째, 마지막이고,
            lReturn = true ;
        } else if( i/idivide > 0 &&  (i % idivide) == 0) {
            lReturn = true ;
        }
        return lReturn;
    }
    public static void toastmake(Context mContext, String showtext, long millis) {
        CountDownTimer timer =null;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        // Inflate the Layout
        View layout = inflater.inflate(R.layout.toast_custom, null) ;
        // ViewGroup = (ViewGroup) layout.findViewById(R.id.custom_toast_layout);

        TextView text = (TextView) layout.findViewById(R.id.toasttext);
        text.setText(showtext);					// Set the Text to show in TextView

        final Toast toast = new Toast(mContext.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);		// 가운데
        toast.setView(layout);

        // toast.setGravity(Gravity.BOTTOM, 0, 50);				// 아래쪽

        // 기본으로 할 경우에는
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toastmake(Context mContext, String showtext) {
        toastmake(mContext, showtext, Toast.LENGTH_SHORT) ;
    }

    /**
     * 배열, 찾고자하는 요소
     * @param key 찾고자 하는 요소(String)
     * @return 배열 요소 번째 / -1는 찾지 못함.
     */

    public static int indexOfIntArray(String[] array, String key) {
        int returnvalue = -1;
        if(key != null) {
            for (int i = 0; i < array.length; ++i) {
                if (key.equals(array[i])) {
                    returnvalue = i;
                    break;
                }
            }
        }
        return returnvalue;
    }

    /**
     * ArrayList, 찾고자하는 요소
     * @param arraySearch
     * @param key
     * @return
     */
    public static int indexOfIntArray(ArrayList<String> arraySearch, String key) {
        int returnvalue = -1;
        if(key != null) {
            for (int i = 0; i < arraySearch.size(); ++i) {
                if (key.equals(arraySearch.get(i))) {
                    returnvalue = i;
                    break;
                }
            }
        }
        return returnvalue;
    }

    /**
     * 폴더 생성하기
     * @param dirname : toggle_no
     * @return boolean : true, false
     */
    public static boolean MakeFolder(String dirname) {
        boolean lReturn = true ;

        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/"+dirname);
            dir.mkdirs();
        } catch(Exception e) {
            lReturn = false ;
        }
        return lReturn ;
    }

    /**
     * 해당 파일을 삭제 한다.
     * @param fullfilename
     */
    public static boolean delteFile(String fullfilename) {
        boolean lReturn = false ;
        try {
            if(new File(fullfilename).exists()) {
                // 화일이 존재 할 경우에
                File deleFile = new File(fullfilename) ;
                deleFile.delete() ;
                lReturn = true ;
            }
        } catch (Exception e) {
            Log.e(TAG, "파일 삭제 오류:"+fullfilename) ;
        }
        return lReturn ;
    }

    /**
     * 디렉토리에 있는 모든 파일을 삭제한다.
     * @param path
     * @return
     */
    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
    }

    /**
     * 안드로이드 4.3(JELLY_BEAN_MR2) 버젼 이상이면 true
     * @return
     */
    public static boolean hasJellyBeanMR2() {
        // return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2 ;
        return false ;
    }
    /**
     * 안드로이드 4.4(KitKat) 버젼 이상이면 true
     * @return
     */
    public static boolean hasKitKat() {
        //return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT ;
        return false ;
    }

    /**
     * 폰에 있는 기본 값 문자메세지 : 기본 SMS앱으로 설정되어 있는지 확인
     * @param context
     * @return
     */
    public static boolean isDefaultSmsApp(Context context) {
        if (hasKitKat()) {
            //return context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context));
            // return false ;
        }
        return true;
    }

    /**
     * 폰에 있는 문자 메세지 앱을 변경한다.(설정 값으로 변경)
     * @param context
     */
    public static void setDefaultSmsApp(Context context) {
        if (hasKitKat()) {
            //Intent intent = new Intent(Intents.ACTION_CHANGE_DEFAULT);					// sms에 있는 것
            //intent.putExtra(Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
        }
    }

    /**
     * 전화번호가 유효한지 검사(010->11자리, 나머지 10자리)
     * @param telNumber
     * @return
     */
    public static boolean istelNumber(String telNumber) {
        boolean returnIstelNumber = false ;

        try {
            String [] arrTel	= {"010", "011", "016", "017", "018", "019", "070"} ;
            telNumber			= telNumber.replaceAll(" ", "") ;
            telNumber			= telNumber.replaceAll("-", "") ;

            //telNumber			= telNumber.replaceAll("(", "") ;
            //telNumber			= telNumber.replaceAll(")", "") ;
            // 전화번호가 나오지 않는다... 오류가 나는듯

            String tel_first	= telNumber.substring(0, 3) ;
            String tel_secound	= telNumber.substring(3, 6) ;
            for(int i=0 ; i < arrTel.length; i++) {
                if(arrTel[i].equals(tel_first)) {
                    // 전화번호 앞자리가 같으면
                    returnIstelNumber	= true ;
                    break ;
                }
            }

            if (!returnIstelNumber) {
                // 문자를 받을 수 없는 전화번호는 false
                return false;
            }

            if(tel_secound.contains("0000") ) {
                // 2번째 전화번호가 0000이면
                returnIstelNumber	= false ;
            }

            if("010".equals(tel_first) && telNumber.length() == 11) {
                // 010은 길이가 11자리이고
                returnIstelNumber	= true ;
            } else if(!"010".equals(tel_first) && (telNumber.length() == 10 || telNumber.length() == 11) ) {
                // 나머지는 길이가 10자리, 11자리 이어야 한다.
                // 2015년 5월 12일 수정
                returnIstelNumber	= true ;
            } else {
                returnIstelNumber	= false ;
            }

            if(!returnIstelNumber) {
                String istelNumberMessage = "전화 번호 입력 오류:"+telNumber+"/"+tel_first+"/"+tel_secound ;

                Log.e(TAG, istelNumberMessage) ;
                // logger.addToLog(TAG, istelNumberMessage);
            }
        } catch(Exception e) {
        }

        return returnIstelNumber;
    }

    public static void Prodialog(ProgressDialog prodialog, boolean lShow) {
        if(lShow) {
            if(prodialog != null) {
                prodialog.setTitle("작업중....");
                prodialog.setMessage("잠시만 기다립시오....");
                prodialog.setIndeterminate(false);
                prodialog.setCancelable(false);			// true, false
                prodialog.show();
            }
        } else {
            if(prodialog != null) {
                prodialog.dismiss();
            }
        }
    }

    /**
     * 로그화일 5개만 남겨놓고 삭제한다.
     * @param fileNameIndex:logcat_ - 로그 화일일 경우  (이름에 포함되는 화일 logcat_)
     * @param fileExtIndex:.txt - 확장자 삭제
     * @param deleteCancel:5 - 5개 까지는 남겨놓는다.
     */
    public static boolean doLogfileDelete(String fileNameIndex, final String fileExtIndex, int deleteCancel) {
		/*
		String fileNameIndex		= "logcat_" ;		// 로그 화일일 경우  (이름에 포함되는 화일)
		final String fileExtIndex	= ".txt" ;			// .txt 확장자 삭제
		int deleteCancel			= 5 ;				// 5개 까지는 남겨놓는다.
		*/
        boolean lError = false ;
        try {
            int logfileCount			= 0 ;							// 전체 파일수
            String fileSelect			= "" ;
            ArrayList<String> children = new ArrayList<String> ();		// 로그 화일만 저장

            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/toggle_no");
            if (dir.isDirectory()) {
                // .txt 화일만 리턴한다.
                File[] files = dir.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(fileExtIndex);
                    }
                });

                for(int i=0; i<files.length; i++) {
                    fileSelect = files[i].toString() ;
                    if(fileSelect.contains(fileNameIndex)  ) {
                        // 로그 화일이면
                        children.add(fileSelect) ;
                    }
                }
                //Log.i(TAG, "정렬 전:"+children.toString()) ;

                // 정렬하기
                Comparator<String> compare = new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        return lhs.compareTo(rhs);
                    }
                };
                Collections.sort(children, compare);
                //Log.i(TAG, "정렬 후:"+children.toString()) ;

                // 로그 화일만 삭제 한다.
                logfileCount		=  children.size() ;
                if(deleteCancel < logfileCount) {
                    // 5개 이상이 있으면 삭제 작업 진행
                    for (int i = 0; i < logfileCount-(deleteCancel) ; i++) {
                        File logDelete = new File(children.get(i)) ;
                        logDelete.delete();

                        Log.d(TAG, "화일 삭제:"+logDelete.toString()) ;
                    }
                } else {
                    Log.d(TAG, "화일 삭제 취소("+fileNameIndex+"/"+fileExtIndex+"/"+logfileCount+"개 화일 존재") ;
                }
            }
        } catch (Exception e) {
            lError = true ;
            Log.e(TAG, "화일 삭제 작업중 오류"+e.getMessage()) ;
        }
        return lError ;
    }

    /**
     * 기기전화번호 알아내기
     * @param context
     * @return
     */
    public static String getTelNumber(Context context) {
        String DeviceId 	= "" ;
        String phoneNumber	= "" ;
        String phoneComp	= "" ;
        try {
            TelephonyManager telephonyMgr = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
            DeviceId 	= telephonyMgr.getDeviceId() ;
            phoneNumber	= telephonyMgr.getLine1Number() ;
            phoneComp	= telephonyMgr.getNetworkOperatorName() ;		// 통신사 정보 받아오기
            if( phoneNumber.length() > 10 ) {
                // PhoneNumber = PhoneNumber.replaceAll("+", "");		// + 에러가 난다.
                // 0 + 전화번호 10자리 = 11자리 123-1234-1234
                phoneNumber ='0'+phoneNumber.substring(phoneNumber.length()-10) ;
            } else {
                phoneNumber	="012345678091" ;
            }
        } catch (Exception e) {
            phoneNumber	= "012345678091" ;

            Log.e(TAG, "기기/전화번호 오류:"+DeviceId+"/"+phoneNumber+"/오류:"+e.getMessage()) ;
        }
        configinfo.setPhoneNumber(phoneNumber) ;		// 전화번호
        configinfo.setCommCompany(phoneComp);			// 통신사정보
        return phoneNumber ;
    }

    /**
     * 전화번호 화면표시, 로그화일 -> 끝4자리를 *로 출력한다.
     * @param phoneno : 전화번호
     */
    public static String prtTelNumber(String phoneno) {
        String strReturn = "";
        try {
            strReturn = phoneno.substring(0,phoneno.length()-4)+"****" ;
        } catch(Exception ex) {
            strReturn = "ERROR****" ;
        }
        // Log.i(TAG, "출력시 전화번호 :"+strReturn) ;
        return strReturn ;
    }

    /**
     * 전화번호 화면표시 - 전화번호 010-1234-1234 표시형식으로 변경
     * @param enPhoneno : 전화번호
     */
    public static String setTelNumberPrt(String enPhoneno) {
        String returnStr = "" ;
        if(enPhoneno.contains("-")) return enPhoneno ;			// -가 있으면 그냥

        try {
            if(enPhoneno.length() == 10) {
                returnStr	= enPhoneno.substring(0,3)+"-"+enPhoneno.substring(3, 6)+"-"+enPhoneno.substring(6, 10) ;
            } else if(enPhoneno.length() == 11) {
                returnStr	= enPhoneno.substring(0,3)+"-"+enPhoneno.substring(3, 7)+"-"+enPhoneno.substring(7, 11) ;
            }
        } catch(Exception e) {
            Log.e(TAG, "전화번호 출력 오류:"+enPhoneno) ;
        }
        return returnStr;
    }

    /**
     * EditText에서 포커스 이동시 키보드 숨기기 함수
     * @param context : context
     * @param edtView : editText
     */
    public static void hideKeyboard(Context context, EditText edtView){
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(edtView.getWindowToken(), 0);

/*
        // 사용법
        edtTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                util.hideKeyboard(mJobContext, edtTitle);
            }
        });
*/
    }
}

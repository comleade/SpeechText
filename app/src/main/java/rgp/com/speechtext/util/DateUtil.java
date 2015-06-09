package rgp.com.speechtext.util;

import java.sql.Timestamp;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
    /**
     * 어플리케이션에서 날짜들은 yyyyMMdd 포맷(예: 20020622) 등의 스트링 타입으로 처리되어야 한다
     * 이 클래스 변수의 값은 yyyyMMdd 로 초기화되어 있다
     */
    private static final String DFLT_IN_FORMAT    = "yyyyMMdd";
    private static final String DFLT_OUT_FORMAT   = "yyyyMMdd";

    private static final int BEFORE=-1;
    private static final int AFTER=1;
    private static final int EQUAL=0;

    public DateUtil(){}


    public static String getCurrentDate()
    {
        String dateFormat = "yyyyMMdd";
        Format df = new SimpleDateFormat(dateFormat);
        return df.format(new Date());
    }

    public static String getCurrentDateTime()
    {
        String dateFormat = "yyyyMMddHHmmss";
        Format df = new SimpleDateFormat(dateFormat);
        return df.format(new Date());
    }

    public static String getCurrentDateTime(String dateType)
    {
        Format df = new SimpleDateFormat(dateType);
        return df.format(new Date());
    }

    public static String getCurrentmillisToString(String milliSeconds) {
        long longmilli	= Long.parseLong(milliSeconds) ;
        String dateFormat = "yyyyMMddHHmmss";

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(longmilli);
        String finalDateString = formatter.format(calendar.getTime());

        return finalDateString ;
    }

    public static String getCurrentDateTimeDot()
    {
        String dateFormat = "yyyyMMddHHmmss";
        Format df = new SimpleDateFormat(dateFormat);
        String get_date = df.format(new Date()) ;
        String return_date	= get_date.substring(0,4)+'/'+get_date.substring(4,6)+'/'+get_date.substring(6,8)+"  "
                +get_date.substring(8,10)+':'+get_date.substring(10,12)+':'+get_date.substring(12,14) ;

        return return_date;
    }

    /**
     * 오늘일자 -> 년월일 시 분 초
     * @return
     */
    public static String getCurrentDateTimeWord()
    {
        String dateFormat = "yyyyMMddHHmmss";
        Format df = new SimpleDateFormat(dateFormat);
        String get_date = df.format(new Date()) ;
        String return_date	= get_date.substring(0,4)+'년'+get_date.substring(4,6)+'월'+get_date.substring(6,8)+"일 "
                +get_date.substring(8,10)+':'+get_date.substring(10,12)+':'+get_date.substring(12,14) ;

        return return_date;
    }

    /**
     * date 로 받은 날자가 그레고리안단력(양력 달력)에 존재하는 날자인지를 확인해준다
     *
     * @param date  검증받고 싶은 날자
     * @return  실존하는 날자일 경우 true, 존재하지 않을 경우 false 를 리턴
     * @throws ParseException 입력날자의 포맷이 틀렸을 경우 던져진다<br>
     * 예를 들면 2002/02/04, 2002-05-06 등을 파라미터로 받을 경우 예외가 발생한다
     */
    public static boolean isExist(String date) throws ParseException
    {
        try
        {
            stringToDate(date, DFLT_IN_FORMAT);
        }
        catch(ParseException pEx)
        {
        }
        return true;
    }

    /**
     * date로 받은 날자가 그레고리안단력(양력 달력)에 존재하는 날짜인지를 확인해준다
     * 이 메소드는 yyyyMMdd뿐만 아니라 yyMMdd형태도 지원한다.
     *
     * @param date     검증받고 싶은 날자
     * @param baseYear 기준 year, date가 yyMMdd인 경우에 baseYear보다 크다면
     *                 19를 앞에 부치고 작다면 20를 부친다.
     *
     * @return  실존하는 날자일 경우 true, 존재하지 않을 경우 false 를 리턴
     *
     * @throws ParseException 입력날자의 포맷이 틀렸을 경우 던져진다<br>
     *         예를 들면 2002/02/04, 2002-05-06 등을 파라미터로 받을 경우
     *         예외가 발생한다.
     */
    public static boolean isExist(String date, int baseYear)
            throws ParseException
    {
        if (date == null && date.length() < 6)
            return false;
        if (date != null && date.length() == 6)
        {
            int yy = Integer.parseInt(date.substring(0, 2));
            if (yy > baseYear) {
                date = 19 + date;
            } else {
                date = 20 + date;
            }
        }
        else if (date != null && date.length() == 8)
        {
            int yyyy = Integer.parseInt(date.substring(0, 4));
            if (yyyy > baseYear) {
                date = 19 + date.substring(2);
            } else {
                date = 20 + date.substring(2);
            }
        }
        return isExist(date);
    }

    /**
     * 지정된 일자의 몇일 후 일자를 얻을 수 있다
     * @param date  지정된 날짜
     * @param day 몇일 후로 지정할 지 입력한다
     * @return  몇일 후 일자를 리턴
     * @throws ParseException
     */
    public static String afterSpecDay(String date, int day) throws ParseException {  // 지정된 일자의 몇일후 날짜
        Calendar calendar = stringToCalendar(date);
        calendar.add(Calendar.DATE, day);     // calendar 자체는 변경이 되면 안되니...
        return printCalendar(calendar);
    }

    /**
     * 지정된 일자의 몇년,몇개월,몇일 후 일자를 얻을 수 있다
     * @param date
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String afterSpecDay(String date, int year, int month, int day)throws ParseException
    {
        Calendar calendar = stringToCalendar(date);
        calendar.add(Calendar.YEAR, year);
        calendar.add(Calendar.MONTH, month);
        calendar.add(Calendar.DATE, day);     // calendar 자체는 변경이 되면 안되니...
        return printCalendar(calendar);
    }

    /**
     * 지정된 일자의 몇일 전 일자를 얻을 수 있다
     * @param date
     * @param day
     * @return
     * @throws ParseException
     */
    public static String beforeSpecDay(String date, int day) throws ParseException
    { // 지정된 일자의 몇일 전 날짜
        Calendar calendar = stringToCalendar(date);
        calendar.add(Calendar.DATE, -day);
        return printCalendar(calendar);
    }

    /**
     * 지정된 일자의 몇년,몇개월,몇일 전 일자를 얻을 수 있다
     * @param date 지정된 일자
     * @param year 몇년
     * @param month 몇달
     * @param day 몇일
     * @return 이전 일자를 문자열 형태로 반환한다.
     * @throws ParseException
     */
    public static String beforeSpecDay(String date,int year, int month, int day) throws ParseException
    { // 지정된 일자의 몇일 전 날짜
        Calendar calendar = stringToCalendar(date);
        calendar.add(Calendar.YEAR, -year);
        calendar.add(Calendar.MONTH, -month);
        calendar.add(Calendar.DATE, -day);
        return printCalendar(calendar);
    }

    /**
     * 지정된 일자의 해당년도 총일수를 리턴한다
     *
     * @param date 지정된 일자를 나타내는 문자열
     * @return int 지정된 일자의 해당년도 총 일수를 반환
     * @throws ParseException
     */
    public static int getDayOfYear(String date) throws ParseException
    { // 지정된 일자의 해당년도의 총일 수
        Calendar calendar = stringToCalendar(date);
        if (((GregorianCalendar)calendar).isLeapYear(calendar.get(Calendar.YEAR)))
            return 366 ;
        else
            return 365 ;
    }

    /**
     * 지정된 일자의 몇 개월 전 초일을 리턴한다
     *
     * @param date 지정된 일자
     * @param month 전으로 세팅해야 할 몇달
     * @return 지정된 일자의 몇 개월 전 초일을 리턴한다
     * @throws ParseException
     */
    public static String getFirstDayBeforeMonth(String date, int month) throws ParseException
    {// 지정된 일자의 몇개월 전 초일
        Calendar calendar = stringToCalendar(date);
        calendar.add(Calendar.MONTH, -month);                        // 몇개월 전으로 셋
        int firstDay = 1;
        calendar.set(Calendar.DATE, firstDay);                       // 초일로 셋
        return printCalendar(calendar);
    }

    /**
     * 지정된 일자의 몇 개월 전 말일을 리턴한다
     *
     * @param date
     * @param month
     * @return
     * @throws ParseException
     */
    public static String getLastDayOfBeforeMonth(String date, int month) throws ParseException
    { // 지정된 일자의 몇개월 전 말일
        Calendar calendar = stringToCalendar(date);

        calendar.add(Calendar.MONTH, -month);                        // 몇개월 전으로 셋
        int lastDay = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, lastDay);                       // 말일로 셋
        return printCalendar(calendar);
    }

    /**
     * 지정된 일자의 몇 개월 후 초일을 리턴한다
     *
     * @param date
     * @param month
     * @return
     * @throws ParseException
     */
    public static String getFirstDayAfterMonth(String date, int month) throws ParseException
    {// 지정된 일자의 몇개월 후 초일
        Calendar calendar = stringToCalendar(date);

        calendar.add(Calendar.MONTH, month);                        // 몇개월 후로 셋
        int firstDay = 1;// 초일???
        calendar.set(Calendar.DATE, firstDay);                       // 초일로 셋
        return printCalendar(calendar);
    }

    /**
     * 지정된 일자의 몇 개월 후 말일을 리턴한다
     *
     * @param date
     * @param month
     * @return
     * @throws ParseException
     */
    public static String getLastDayAfterMonth(String date, int month) throws ParseException
    { // 지정된 일자의 몇개월 후 말일
        Calendar calendar = stringToCalendar(date);

        calendar.add(Calendar.MONTH, month);                        // 몇개월 후로 셋
        int lastDay = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, lastDay);                       // 말일로 셋
        return printCalendar(calendar);
    }

    /**
     * 지정된 일자가 말일인지 검증한다
     * @param date
     * @return  말일이면 true, 말일이 아니면 false 를 리턴한다
     * @throws ParseException
     */
    public static boolean isLastOfMonth(String date) throws ParseException
    { // 지정한 일자가 월말인지 체크
        Calendar calendar = stringToCalendar(date);
        return calendar.get(Calendar.DATE) == calendar.getActualMaximum(Calendar.DATE);
    }

    /**
     * 지정된 두 일자간의 차일수
     * @param fromDate  첫 번째 날자
     * @param toDate    두 번째 날자
     * @return 두일자의 차일수를 반환
     * @throws ParseException
     */
    public static int getBetweenDates(String fromDate, String toDate) throws ParseException
    {// 지정된 두 일자간의 차일수
        long betWeenTime = Math.abs(stringToDate(fromDate).getTime() - stringToDate(toDate).getTime());
        return (int) (betWeenTime / (1000*60*60*24));
    }

    /**
     * java.util.Calendar 인스턴스를 지정된 출력 포맷을 통해 스트링 타입으로 변환해준다.
     * 현재 버전은 다양한 입출력 포맷을 지원하지 않으므로 calendarToString() 메소드와 같다<br>
     * calendarToString() 은 입력포맷을 적용하여 스트링 타입으로 변환한다.   *
     * @param cal
     * @return
     */
    private static String printCalendar(Calendar cal)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(DFLT_OUT_FORMAT);
        Date date = cal.getTime();
        return formatter.format(date);
    }

    /**
     * 현재 시간을 yyyyMMdd 타입으로 리턴해준다
     * @return
     */
    public static String getToday()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(new Date());
    }

    public static Calendar getNowCalendar()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal;
    }

    /**
     * java.lang.String 타입을 java.util.Date 타입으로 변환한다
     * @param s
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String s) throws ParseException
    {
        return stringToDate(s, DFLT_IN_FORMAT);
    }

    /**
     * java.lang.String 타입을 java.util.Date 타입으로 변환한다
     * 지원하는 format은 yyMMdd와 yyyyMMddd이다.
     *
     * @param s
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String date, int baseYear) throws ParseException
    {
        if (date != null && date.length() == 6) {
            int yy = Integer.parseInt(date.substring(0, 2));
            if (yy > baseYear) {
                date = 19 + date;
            } else {
                date = 20 + date;
            }
        } else if (date != null && date.length() == 8) {
            int yyyy = Integer.parseInt(date.substring(0, 4));
            if (yyyy > baseYear) {
                date = 19 + date.substring(2);
            } else {
                date = 20 + date.substring(2);
            }
        }
        return stringToDate(date);
    }

    /**
     * java.lang.String 타입을 java.util.Date 타입으로 변환한다.
     * 변환하는 과정에서 DateFormat.parse() API 를 사용하므로 변환하는 중에 파리미터로 입력받은 s, 즉 날자의
     * 포맷과 DateFormat 생성시 사용한 포맷이 동일한 지도 체크된다.
     * 예를 들면 s 가 2000/02/05 일 경우 현재 버전에서는 입력포맷을 yyyyMMdd 만 사용하므로 두 포맷이
     * 다르기 때문에 java.text.ParseException 이 발생한다<br>
     * @param s
     * @param inFormat
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String s, String inFormat) throws ParseException
    {
        if ( s == null )
            throw new ParseException("date string is null", 0);
        if ( inFormat == null )
            throw new ParseException("format string is null", 0);

        SimpleDateFormat formatter = new SimpleDateFormat(inFormat);
        Date date = null;

        try {
            date = formatter.parse(s);
        }
        catch(ParseException e) {
            throw new ParseException(" wrong format or date :\"" + s +
                    "\" with format \"" + inFormat + "\"", 0);
        }
        // ex) s가 2002/02/03 일때, formatter 가 디폴트로 yyyy/MM/dd 로 셋팅되어 있으므로 아래와 같은 로직이 가능하다
        // 항상 s 문자열의 포맷으로 formatter 를 생성해야 한다.
        if ( ! formatter.format(date).equals(s) )
            try {
                throw new Exception(
                        "Out of bound date:\"" + s + "\" with format \"" + inFormat + "\"");
            } catch (Exception e) {
                e.printStackTrace();
            }

        return date;
    }

    /**
     * DB의 DATE타입 String(ex 2009-01-01 01:01:01.0)을  java.lang.String 타입으로 변환한다
     * @param String
     * @return yyyyMMdd
     */
    public static String sqlToString(String sqlDate)
    {
        return sqlToString(sqlDate, "yyyyMMddHHmmss");
    }
    /**
     * DB의 DATE타입 String(ex 2009-01-01 01:01:01.0)을  java.lang.String 타입으로 변환한다
     * @param String
     * @return yyyyMMddHHmmss
     */
    public static String sqlToString(String sqlDate, String inFormat)
    {
        try {
            Date date = Timestamp.valueOf(sqlDate);
            return DateUtil.dateToString(date, inFormat);
        } catch (IllegalArgumentException e) {
            return sqlDate;
        }
    }

    /**
     * java.util.Date 타입을  java.lang.String 타입으로 변환한다
     * @param date
     * @return
     */
    public static String dateToString(Date date)
    {
        return dateToString(date, DFLT_IN_FORMAT);
    }
    /**
     * java.util.Date 타입을  java.lang.String 타입으로 변환한다
     * @param date
     * @return
     */
    public static String dateToString(Date date, String inFormat)
    {
        SimpleDateFormat formatter =  new SimpleDateFormat(inFormat);
        return formatter.format(date);
    }

    /**
     * java.util.Date 타입을 java.util.Calendar 타입으로 변환한다
     * @param date
     * @return
     */
    private static Calendar dateToCalendar(Date date)
    {
        Calendar cal = getNowCalendar();
        cal.setTime(date);
        return cal;
    }


    /**
     * java.util.Calendar 타입을 java.util.Date 타입으로 변환한다
     * @param cal
     * @return
     */
    public static Date calendarToDate(Calendar cal)
    {
        return cal.getTime();
    }

    /**
     * java.lang.String 타입을 java.util.Calendar 타입으로 변환한다
     * @param date
     * @return
     * @throws ParseException
     */
    public static Calendar stringToCalendar(String date) throws ParseException
    {
        return stringToCalendar(date,DFLT_IN_FORMAT);
    }
    /**
     * java.lang.String 타입을 java.util.Calendar 타입으로 변환한다
     * @param date
     * @return
     * @throws ParseException
     */
    public static Calendar stringToCalendar(String date, String inFormat) throws ParseException
    {
        Date transDate = stringToDate(date,inFormat);
        return dateToCalendar(transDate);
    }

    /**
     * java.util.Calendar 타입을 java.lang.String 타입으로 변환한다
     * @param cal
     * @return
     */
    public static String calendarToString(Calendar cal)
    {
        return calendarToString(cal,DFLT_IN_FORMAT);
    }

    /**
     * java.util.Calendar 타입을 java.lang.String 타입으로 변환한다
     * @param cal
     * @return
     */
    public static String calendarToString(Calendar cal, String inFormat)
    {
        return dateToString(cal.getTime(),inFormat);
    }

    /**
     * 두개의 데이타 해당하는 문자열을 받아서 비교한후 나중 날짜를 문자열로 반환해 준다.
     * @param firstData 비교할 첫번째 데이타
     * @Param secondData 비교할 첫번째 데이타
     * @return String 두 날짜 중 이후 날짜를 반환
     */
    public static String getAfterDateWithCompare(String firstData,String secondData) throws ParseException
    {
        if(isExist(firstData) && isExist(secondData))
        {
            Calendar first = stringToCalendar(firstData);
            Calendar second = stringToCalendar(secondData);
            if(first.after(second))
            {
                return calendarToString(first);
            }
            else if(first.before(second))
            {
                return calendarToString(second);
            }
            return firstData;
        }
        else
        {
            try {
                throw new Exception(" wrong format or date: firstDataFormat : " +firstData +" seocondDataFormat : " + secondData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 두개의 데이타 해당하는 문자열을 받아서 비교한후 이전 날짜를 문자열로 반환해 준다.
     * @param firstData 비교할 첫번째 데이타
     * @Param secondData 비교할 첫번째 데이타
     * @return String 두 날짜 중 이전 날짜를 반환
     */
    public static String getBeforeDateWithCompare(String firstData,String secondData) throws ParseException
    {
        if(isExist(firstData) && isExist(secondData))
        {
            Calendar first = stringToCalendar(firstData);
            Calendar second = stringToCalendar(secondData);
            if(first.before(second))
            {
                return calendarToString(first);
            }
            else if(first.after(second))
            {
                return calendarToString(second);
            }
            return firstData;
        }
        else
        {
            try {
                throw new Exception(" wrong format or date: firstDataFormat : " +firstData +" seocondDataFormat : " + secondData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return secondData;
    }
    /**
     * 두개의 데이타 해당하는 문자열을 받아서 비교한후 상수 값 형태로 반환한다.
     * 0은 두 데이터가 동일, 1은 첫번째 데이터가 큰 경우, -1은 두번째 데이터가 큰 경우
     * @param firstData 비교할 첫번째 데이타
     * @Param secondData 비교할 첫번째 데이타
     * @return int
     */
    public static int compareBetweenDates(String firstData,String secondData) throws ParseException
    {
        if(isExist(firstData) && isExist(secondData))
        {
            Calendar first = stringToCalendar(firstData);
            Calendar second = stringToCalendar(secondData);
            if(first.before(second))
            {
                return DateUtil.BEFORE;
            }
            else if(first.after(second))
            {
                return DateUtil.AFTER;
            }
            return DateUtil.EQUAL;
        }
        else
        {
            try {
                throw new Exception(" wrong format or date: firstDataFormat : " +firstData +" seocondDataFormat : " + secondData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    public static String getYear(String src)
    {
        String year = "";
        if(src!=null && src.length()>=8)
        {
            src = StringUtil.replace(src, "-", "");
            year = src.substring(0,4);
        }
        return year;
    }
    public static String getMonth(String src)
    {
        String month = "";

        if (src != null && src.length() >= 8) {
            src = StringUtil.replace(src, "-", "");
            month = src.substring(4, 6);
        }
        return month;
    }

    public static String getDay(String src) {
        String day = "";

        if (src != null && src.length() >= 8) {
            src = StringUtil.replace(src, "-", "");
            day = src.substring(6, 8);
        }
        return day;
    }

    public static String getDate(String src)
    {
        String date = "";
        if (src != null && src.length() >= 8) {
            src = StringUtil.replace(src, "-", "");
            date = src.substring(0,8);
        }
        return date;
    }

    public static String getTime(String src)
    {
        String time = "";
        if (src != null && src.length() >= 14) {
            src = StringUtil.replace(src, "-", "");
            time = src.substring(8,14);
        }
        return time;
    }

    /**
     * src일자에서 다음년도 일자를 구한다.
     * @param src 20070708 ==> 20080708
     * @return String
     */

    public static String getNowFromNextDate(String src) {
        String year = src.substring(0,4);
        String day = src.substring(4);

        return (Integer.parseInt(year) + 1) + day;
    }

    /**
     * 날짜에 - 를 넣는다.
     * @param yyyyMMdd 형식
     * @return yyyy-MM-dd 형식의 날짜가 담긴 날짜
     */

    public static String addDash(String dash){
        //String dash_temp = "";
        if ((dash == null) || (dash.equals(""))) {
            return "";
        } else {
            //dash_temp = replace(dash, "-", "");
            return dash.substring(0,4) + "-" + dash.substring(4,6) + "-" + dash.substring(6,8);
        }

    }

}

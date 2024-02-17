package com.abs.wfs.lvs.util;

import com.abs.wfs.lvs.util.code.UnitCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class CommonDate {
    /** UI용 Date String Format */
    public static final String formattedUIDateString = "yyyy.MM.dd HH:mm:ss";

    /** Milliseconds String Format */
    public static final String formattedMilDateString = "yyyyMMddHHmmssSSS";

    @Autowired
    MessageSource messageSource;

    /**
     * CurrentDateTime을 리턴.
     */
    public static Timestamp getCurrentDateTime() {

        LocalDateTime curDateTime = LocalDateTime.now();
        Timestamp currentTimeStamp = Timestamp.valueOf(curDateTime);

        return currentTimeStamp;
    }

    /**
     * CurrentDateTime을 String Type으로 리턴.
     */
    public static String getCurrentDateTimeToString() {

        LocalDateTime curDateTime = LocalDateTime.now();

        return curDateTime.format(DateTimeFormatter.ofPattern(CommonDate.formattedMilDateString));
    }

    /**
     * 경과 시간 리턴 - Current시간 기준
     *
     * @param endTime : 종료시간
     * @param type : 경과 시간 type (DAY, HOUR, MIN, SEC)
     * @throws Exception
     */
    public static int getElapsedDateTime(Timestamp endTime, String type) {
        return getElapsedDateTime(getCurrentDateTime(), endTime, type);
    }

    /**
     * 경과 시간 리턴
     *
     * @param startTime : 시작시간
     * @param endTime : 종료시간
     * @param type : 경과 시간 type (DAY, HOUR, MIN, SEC)
     * @throws Exception
     */
    public static int getElapsedDateTime(Timestamp startTime, Timestamp endTime, String type) {

        long elapsedTime = 0;

        if (startTime == null || endTime == null)
            return 0;

        if (StringUtils.isEmpty(type))
            return 0;

        elapsedTime = (startTime.getTime() - endTime.getTime()) / 1000;

        int day = (int) (elapsedTime / (60 * 60 * 24));
        int hour = (int) elapsedTime / (60 * 60);
        int minite = (int) elapsedTime / 60;
        int second = (int) elapsedTime;

        if (UnitCode.Day.name().equals(type)) {
            return day;
        } else if (UnitCode.Hour.name().equals(type)) {
            return hour;
        } else if (UnitCode.Min.name().equals(type)) {
            return minite;
        } else if (UnitCode.Sec.name().equals(type)) {
            return second;
        } else {
            return 0;
        }
    }

    /**
     * sumTm 시간 이후 시간 리턴
     * @param type
     * @param sumTm
     * @return
     */
    public static Timestamp getSumDateTime(String type, int sumTm) {
        Timestamp ts = getCurrentDateTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime(ts);

        if (UnitCode.Day.name().equals(type)) {
            cal.add(Calendar.DATE, sumTm);
        } else if (UnitCode.Hour.name().equals(type)) {
            cal.add(Calendar.HOUR, sumTm);
        } else if (UnitCode.Min.name().equals(type)) {
            cal.add(Calendar.MINUTE, sumTm);
        } else if (UnitCode.Sec.name().equals(type)) {
            cal.add(Calendar.SECOND, sumTm);
        } else {
        }

        ts.setTime(cal.getTime().getTime());
        return ts;
    }

    /**
     * 1/1000초 단위시간을 UI 도시형식의 문자열(yyyy.MM.dd HH:mm:ss)로 반환
     *
     * @param timeMillis 1/1000초 단위시간
     * @return UI 도시형식 문자열
     */
    public static String getTimeUI(long timeMillis)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(CommonDate.formattedUIDateString);

        return sdf.format(new Date(timeMillis));
    }

    /**
     * Timestamp로 받은 두 날짜가 같은 날인지 다른날인지 결과를 반환
     * @return true = 같은날, fasle = 다른날
     */
    public static boolean matchingDate(Timestamp day1, Timestamp day2){

        try {
            SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy-MM-dd");
            if(day1 == null || day2 == null){
                return false;
            }

            String str_day1 = simpleDateformat.format(day1);
            String str_day2 = simpleDateformat.format(day2);

            Date dateDay1 = simpleDateformat.parse(str_day1);
            Date dateDay2 = simpleDateformat.parse(str_day2);

            if (dateDay1.equals(dateDay2)) {
                return true;
            }

        }catch (ParseException e) {
            return false;
        }

        return false;
    }

    public static int diffDate(Timestamp day1, Timestamp day2){

        try{
            SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy-MM-dd");

            if(day1 != null && day2 != null) {
                String str_day1 = simpleDateformat.format(day1);
                String str_day2 = simpleDateformat.format(day2);

                Date dateDay1 = simpleDateformat.parse(str_day1);
                Date dateDay2 = simpleDateformat.parse(str_day2);

                long diffDateTemp = dateDay1.getTime() - dateDay2.getTime();
                diffDateTemp = diffDateTemp / (24 * 60 * 60 * 1000);

                int diffDate = Math.toIntExact(diffDateTemp);

                return diffDate;
            }
        }catch(ParseException e) {
        }
        return -1;
    }

    /**
     * Input Value(값/단위) 만큼 경과 시간 리턴
     *
     * @param startTime : 시작시간
     * @param val : 값
     * @param UnitType : 단위
     * @return Timestamp
     */
    public static Timestamp getElapsedDateTime(Timestamp startTime, long val, String UnitType) {

        //long elapsedTime = 0;

        if (startTime == null || val <= 0)
            return null;

        if (StringUtils.isEmpty(UnitType))
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);

        if (UnitCode.Day.name().equals(UnitType)) {
            calendar.add(Calendar.DATE, (int) val);
        } else if (UnitCode.Hour.name().equals(UnitType)) {
            calendar.add(Calendar.HOUR, (int) val);
        } else if (UnitCode.Min.name().equals(UnitType)) {
            calendar.add(Calendar.MINUTE, (int) val);
        } else if (UnitCode.Sec.name().equals(UnitType)) {
            calendar.add(Calendar.SECOND, (int) val);
        }

        Timestamp endTime = new Timestamp(calendar.getTime().getTime());

        return endTime;
    }
}

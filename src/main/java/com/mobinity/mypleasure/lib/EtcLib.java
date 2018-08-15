package com.mobinity.mypleasure.lib;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.style.TtsSpan;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 기타 라이브러리
 * 전화번호를 반환하거나 기기번호를 반환하는 등 기타 메소드 선언
 * TODO : 메소드가 더 많아질 경우 기능에 맞게 별도 클래스 분리 필요
 */
public class EtcLib {
    public final String TAG = EtcLib.class.getSimpleName();
    private volatile static EtcLib instance;

    public static EtcLib getInstance(){
        if(instance == null){
            synchronized (EtcLib.class){
                if(instance == null){
                    instance = new EtcLib();
                }
            }
        }
        return instance;
    }

    public String getPhoneNumber(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String number = null;
        try {
            number = tm.getLine1Number(); //TODO : checkPermission
        } catch (SecurityException se){

        }

        if(number != null && !number.equals("") && number.length() >= 8){
            if(Locale.getDefault().getCountry().equals("KR")){
                if(number.startsWith("82")){
                    number = "+" + number;
                }

                if (number.startsWith("0")) {
                    number="+82" + number.substring(1, number.length());
                }
            }

            MyLog.d(TAG, "number " + number);
        }else{
            number = getDeviceId(context);
        }
        return number;
    }

    /**
     * 전화번호가 없을 경우 기기 아이디 반환
     * TODO : 전화번호가 없을 경우 허용할 것인지 판단
     * @param context 컨텍스트 객체
     * @return 기기 아이디 문자열
     */
    private String getDeviceId(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //TODO:checkPermission
        String tmDevice = null;
        try {
            tmDevice = tm.getDeviceId();
        } catch (SecurityException se){

        }
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        String serial = null;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) serial = Build.SERIAL;

        if(tmDevice != null) return "01" + tmDevice;
        if(androidId != null) return "02" + androidId;
        if(serial != null) return "03" + serial;

        return null;
    }

    /**
     * 전화번호가 유효한 자릿수를 가지고 있는지 체크
     * 일반전화나 휴대폰 전화 모두 등록 가능
     * "-" 문자열 포함 가능
     * @param number 전화번호 문자열
     * @return 유효한 전화번호일 경우 true, 그렇지 않으면 false
     */
    public boolean isValidPhoneNumber(String number){
        if(number == null){
            return false;
        } else {
            if (Pattern.matches("\\d{2}-\\{3}-\\d{4}", number)
                    || Pattern.matches("\\d{3}-\\{3}-\\d{4}", number)
                    || Pattern.matches("\\d{3}-\\{4}-\\d{4}", number)
                    || Pattern.matches("\\d{10}", number)
                    ||Pattern.matches("\\d{11}", number)){
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 전화번호에 '-'를 붙여서 반환
     * @param number 전화번호 문자열
     * @return 변경된 전화번호 문자열
     */
    public String getPhoneNumberText(String number){
        String phoneText = "";

        if (StringLib.getInstance().isBlank(number)){
            return phoneText;
        }

        number = number.replace("-", "");

        int length = number.length();

        if(number.length() >= 10) {
            phoneText = number.substring(0, 3) + "-"
                    + number.substring(3, length - 4) + "-"
                    + number.substring(length - 4, length);
        }

        return phoneText;
    }


}

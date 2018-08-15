package com.mobinity.mypleasure.lib;

import android.content.Context;
import android.provider.Telephony;

import com.mobinity.mypleasure.R;

/**
 * 문자열 관련 라이브러리
 */
public class StringLib {
    public final String TAG = StringLib.class.getSimpleName();
    private volatile static StringLib instance;

    protected StringLib(){
    }

    public static StringLib getInstance(){
        if (instance == null){
            synchronized (StringLib.class){
                if(instance == null){
                    instance = new StringLib();
                }
            }
        }
        return instance;
    }

    /**
     * 문자열이 null이거나 ""인지 파악
     * @param str 문자열 객체
     * @return null 이거나 ""이라면 true, 아니라면 false
     */
    public boolean isBlank(String str){
        if(str == null || str.equals("")){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 문자열을 지정된 길이로 잘라서 반환
     * @param context 컨텍스트
     * @param str 문자열 객체
     * @param max 최대 문자열 길이
     * @return 변경된 문자열 객체
     */
    public String getSubString(Context context,String str, int max){
        if(str != null && str.length() > max){
            return str.substring(0,max) + context.getResources().getString(R.string.skip_string);
        }else{
            return str;
        }
    }
}

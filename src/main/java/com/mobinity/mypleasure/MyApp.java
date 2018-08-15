package com.mobinity.mypleasure;

import android.app.Application;
import android.os.StrictMode;

import com.mobinity.mypleasure.item.MemberInfoItem;
import com.mobinity.mypleasure.item.SenditemInfoItem;

/**
 * 앱 전역에서 사용할 수 있는 클래스
 */
public class MyApp extends Application {
    private MemberInfoItem memberInfoItem;
    private SenditemInfoItem senditemInfoItem;

    @Override
    public void onCreate(){
        super.onCreate();

        //FileUriExposedException 문제를 해결하기 위한 코드
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public MemberInfoItem getMemberInfoItem() {
        if(memberInfoItem == null) memberInfoItem = new MemberInfoItem();

        return memberInfoItem;
    }

    public void setMemberInfoItem(MemberInfoItem item){
        this.memberInfoItem = item;
    }

    public int getMemberSeq(){
        return memberInfoItem.seq;
    }

    public void setSenditemInfoItem(SenditemInfoItem senditemInfoItem){
        this.senditemInfoItem = senditemInfoItem;
    }

    public SenditemInfoItem getSenditemInfoItem() {
        return senditemInfoItem;
    }
}

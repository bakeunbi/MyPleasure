package com.mobinity.mypleasure;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobinity.mypleasure.item.MemberInfoItem;
import com.mobinity.mypleasure.lib.EtcLib;
import com.mobinity.mypleasure.lib.MyLog;
import com.mobinity.mypleasure.lib.MyToast;
import com.mobinity.mypleasure.lib.RemoteLib;
import com.mobinity.mypleasure.lib.StringLib;
import com.mobinity.mypleasure.remote.RemoteService;
import com.mobinity.mypleasure.remote.ServiceGenerator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 시작 액티비티
 * 전화번호 기반, 사용자 정보를 조회하여
 * 메인 액티비티 or 회원가입 액티비티 실행 결정
 */
public class IndexActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    Context context;

    /**
     * 레이아웃을 설정하고 인터넷 연결 확인
     * 인터넷 연결이 안된 경우 showNoService() 메소드 호출
     * @param savedInstanceState 액티비티가 새로 생성된 경우 이전 상태값 저장 객체
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        context = this;

        if(!RemoteLib.getInstance().isConnected(context)) {
            showNoService();
            return;
        }
    }

    /**
     * 일정 시간(1.2초) 이후에 startTask() 호출하여 서버에서 사용자 정보 조회
     */
    @Override
    protected void onStart(){
        super.onStart();

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startTask();
            }
        }, 1200);
    }

    /**
     * 현재 인터넷에 접속할 수 없기 때문에 서비스를 사용할 수 없다는 메세지와 화면 종료 버튼 표시
     */
    private void showNoService(){
        TextView messageText = (TextView) findViewById(R.id.message);
        messageText.setVisibility(View.VISIBLE);

        Button closeButton = (Button) findViewById(R.id.close);
        closeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
        closeButton.setVisibility(View.VISIBLE);
    }

    /**
     * 현재 폰의 전화번호와 동일한 사용자 정보를 조회할 수 있도록
     * selectMemberInfo() 메소드 호출
     * setLastKnownLocation() 메소드를 호출해서 현재 위치 정보 설정
     */
    public void startTask(){
        String phone = EtcLib.getInstance().getPhoneNumber(this);

        selectMemberInfo(phone);
        //GeoLib.getInstance().setLastKnownLocation(this); //TODO:GeoLib 구현
    }

    /**
     * 레트로핏을 활용하여 서버로부터 사용자 정보 조회
     * 사용자 정보 존재하는 경우 setMemberInfoItem() 메소드 호출
     * 없다면 goRegisterActivity() 메소드 호출
     *
     * @param phone 실행 기기(폰) 전화번호
     */
    public void selectMemberInfo(String phone){
        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);

        Call<MemberInfoItem> call = remoteService.selectMemberInfo(phone);
        call.enqueue(new Callback<MemberInfoItem>() {
            @Override
            public void onResponse(Call<MemberInfoItem> call, Response<MemberInfoItem> response) {
                MemberInfoItem item = response.body();

                if(response.isSuccessful() && !StringLib.getInstance().isBlank(item.name)){
                    MyLog.d(TAG, "success " + response.body().toString());
                    setMemberInfoItem(item);
                } else {
                    MyLog.d(TAG, "not success");
                    goRegisterActivity(item);
                }
            }

            @Override
            public void onFailure(Call<MemberInfoItem> call, Throwable t) {
                MyLog.d(TAG, "no internet connectivity");
                MyLog.d(TAG, t.toString());
            }
        });
    }

    /**
     * 전달받은 MemberInfoItem 을 Application 객체에 저장
     * 그리고 startMain() 메소드 호출
     * @param item 사용자 정보
     */
    private void setMemberInfoItem(MemberInfoItem item){
        ((MyApp) getApplicationContext()).setMemberInfoItem(item);

        startMain();
    }

    /**
     * MainActivity를 실행하고 현재 액티비티 종료
     */
    public void startMain(){
        Intent intent = new Intent(IndexActivity.this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    /**
     * 사용자 정보 조회하지 못한 경우, insertMemberPhone() 메소드를 통해
     * 전화번호를 서버에 저장, TODO:?
     * MainActivity를 실행한 후에 ProfileActivity 실행
     * 그리고 현재 액티비티 종료
     *
     * @param item 사용자 정보
     */
    private void goRegisterActivity(MemberInfoItem item){
        if (item == null || item.seq <= 0) {
            insertMemberPhone();
        }

        Intent intent = new Intent(IndexActivity.this, MainActivity.class);
        startActivity(intent);

        Intent intent2 = new Intent(this, ProfileActivity.class);
        startActivity(intent2);

        finish();
    }

    /**
     * 폰 번호 서버 저장
     */
    private void insertMemberPhone(){
        String phone = EtcLib.getInstance().getPhoneNumber(context);
        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);

        Call<String> call = remoteService.insertMemberPhone(phone);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    MyLog.d(TAG, "success insert id " + response.body().toString());
                } else {
                    int statusCode = response.code();

                    ResponseBody errorBody = response.errorBody();

                    //TODO: 제대로 수행하지 않았을 경우 로그만 남기고 있음->다이얼로그 추가함
                    String errorMessage = "fail " + statusCode + errorBody.toString();
                    MyLog.d(TAG, errorMessage);
                    showErrorMessageDialog(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //TODO: 제대로 수행하지 않았을 경우 로그만 남기고 있음 -> 다이얼로그 추가함
                MyLog.d(TAG, "no internet connectivity");
                showNoConnectionDialog();
            }
        });
    }

    /**
     * 인터넷 연결이 안된 경우 다이얼로그를 보여주고 종료한다.
     */
    private void showNoConnectionDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.connection_title);
        dialog.setMessage(R.string.network_not_working);
        dialog.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
                MyToast.s(IndexActivity.this, R.string.connection_restart);
                IndexActivity.this.finish();
            }
        });
        dialog.show();
    }

    /**
     * 에러 발생시 다이얼로그를 보여주고 종료한다.
     */
    private void showErrorMessageDialog(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.error);
        dialog.setMessage(message);
        dialog.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
                MyToast.s(IndexActivity.this, R.string.connection_restart);
                IndexActivity.this.finish();
            }
        });
        dialog.show();
    }

}

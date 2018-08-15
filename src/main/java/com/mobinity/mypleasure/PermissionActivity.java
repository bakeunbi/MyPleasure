package com.mobinity.mypleasure;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.mobinity.mypleasure.lib.MyToast;

import java.util.ArrayList;
import java.util.List;

/**
 * 앱을 실행할 때 필요한 권한을 처리하기 위한 액티비티
 */
public class PermissionActivity extends AppCompatActivity{
    private static final int PERMISSION_MULTI_CODE = 100;

    /**
     * 화면을 구성하고 SDK 버전과 권한에 따른 처리를 한다.
     * @param savedInstanceState 액티비티가 새로 생성되었을 경우, 이전 상태 값을 가지는 객체
     */

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        if(Build.VERSION.SDK_INT < 23){
            goIndexActivity();
        } else {
            if(checkAndRequestPermissions()){
                goIndexActivity();
            }
        }
    }

    /**
     * 권한을 확인하고 권한이 부여되어 있지 않다면 권한을 요청한다.
     * @return 필요한 권한이 모두 부여되었다면 true, 그렇지 않다면 false
     */
    private boolean checkAndRequestPermissions() {
        //요청할 권한 리스트
        String [] permissions = new String []{
                Manifest.permission.CAMERA,             //카메라
                Manifest.permission.READ_PHONE_STATE,   //전화상태
                Manifest.permission.ACCESS_FINE_LOCATION    //위치정보
        };

        List<String> listPermissionsNeeded = new ArrayList<>();

        //권한이 부여되었는지 확인, 그렇지 않다면 list에 추가
        for (String permission:permissions){
            if(ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(permission);
            }
        }

        //권한이 부여되지 않은 항목이 있을 경우 false 반환(앱 종료)
       if(!listPermissionsNeeded.isEmpty()) {
           ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(
                   new String[listPermissionsNeeded.size()]),
                   PERMISSION_MULTI_CODE);
           return false;
       }
       return true;
    }

    /**
     * 권한 요청 결과를 받는 메소드
     * @param requestCode 요청코드
     * @param permissions 권한 종류
     * @param grantResults 권한 결과
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (grantResults.length == 0) return;

        switch(requestCode){
            case PERMISSION_MULTI_CODE:
                checkPermissionResult(permissions, grantResults);

                break;
        }
    }

    /**
     * 권한 처리 결과를 보고 인덱스 액티비티 실행 or 권한설정 다이얼로그 표시 결정
     * 모든 권한이 승인되었을 경우 goIndexActivity() 메소드 호출
     * @param permissions 권한 종류
     * @param grantResults 권한 부여 결과
     */
    private void checkPermissionResult(String[] permissions, int[] grantResults){
        boolean isAllGranted = true;

        for (int i = 0; i < permissions.length; i++){
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                isAllGranted = false;
            }
        }

        //권한이 모두 부여되었다면
        if(isAllGranted){
            goIndexActivity();
        } else { //권한이 모두 부여되지 않았다면
            showPermissionDialog();
        }
    }

    /**
     * 인덱스 액티비티 실행 후 현재 액티비티 종료
     */
    private void goIndexActivity(){
        Intent intent = new Intent(this, IndexActivity.class);
        startActivity(intent);

        finish();
    }

    /**
     * 권한 설정 화면으로 이동할지를 선택하는 다이얼로그를 보여준다.
     */
    private void showPermissionDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.permission_setting_title);
        dialog.setMessage(R.string.permission_setting_message);
        dialog.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
                MyToast.s(PermissionActivity.this, R.string.permission_setting_restart);
                PermissionActivity.this.finish();

                goAppSettingActivity();
            }
        });

        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
                PermissionActivity.this.finish();
            }
        });
        dialog.show();
    }


    /**
     * 권한 설정 액티비티 실행
     */
    private void goAppSettingActivity(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",getPackageName(),null);
        intent.setData(uri);
        startActivity(intent);
    }

}

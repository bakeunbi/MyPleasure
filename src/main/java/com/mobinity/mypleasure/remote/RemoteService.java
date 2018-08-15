package com.mobinity.mypleasure.remote;

import com.mobinity.mypleasure.item.MemberInfoItem;
import com.mobinity.mypleasure.item.SenditemInfoItem;

import java.net.InetAddress;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 노드 서버의 REST API를 호출하기 위한 메소드 선언
 */
public interface RemoteService {

    String BASE_URL = "http://192.168.25.51:3000";//TODO:실제 서버 URL 적용
    String MEMBER_ICON_URL = BASE_URL + "/member/";
    String IMAGE_URL = BASE_URL + "/img";

    //사용자 정보
    @GET("/member/{phone}")
    Call<MemberInfoItem> selectMemberInfo(@Path("phone") String phone);

    @POST("/member/info")
    Call<String> insertMemberInfo(@Body MemberInfoItem memberInfoItem);

    @FormUrlEncoded
    @POST("/member/phone")
    Call<String> insertMemberPhone(@Field("phone") String phone);

    @Multipart
    @POST("/member/icon_upload")
    Call<ResponseBody> uploadMemberIcon(@Part("member_seq") RequestBody memberSeq,
                                        @Part MultipartBody.Part file);

    //배송물품 정보
    @GET("/item/info/{info_seq}")
    Call<SenditemInfoItem> selectItemInfo(@Path("info_seq") int itemInfoSeq,
                                          @Query("member_seq") int memberSeq);

    @POST("/item/info")
    Call<String> insertFoodInfo(@Body SenditemInfoItem infoItem);

    @Multipart
    @POST("/item/info/image")
    Call<ResponseBody> uploadItemImage(@Part("info_seq") RequestBody infoSeq,
                                       @Part("image_memo") RequestBody imageMemo,
                                       @Part MultipartBody.Part file);

    @GET("/item/list")
    Call<ArrayList<SenditemInfoItem>> listItemInfo(@Query("member_seq") int memberSeq,
                                                   @Query("start_latitude") double startLatitude,
                                                   @Query("start_longitude") double startLongitude,
                                                   @Query("end_latitude") double endLatitude,
                                                   @Query("end_longitude") double endLongitude,
                                                   @Query("current_page") int currentPage);

}

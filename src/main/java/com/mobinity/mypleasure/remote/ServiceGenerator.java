package com.mobinity.mypleasure.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobinity.mypleasure.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 서버와 통신하기 위한 레트로핏을 사용하기 위한 클래스
 */
public class ServiceGenerator {
    /**
     * 원격 호출을 정의한 인터페이스 메소드를 호출할 수 있는 서비스 생성
     * @param serviceClass 원격 호출 메소드를 정의한 인터페이스
     * @return 인터페이스 구현체
     */
    public static <S> S createService(Class<S> serviceClass){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if(BuildConfig.DEBUG){
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        //레트로핏이 원격 서버와 통신하기 위해 OkHttpClient 사용
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        //JSON을 JAVA 객체로, JAVA 객체를 JSON으로 변경
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RemoteService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        return retrofit.create(serviceClass);
    }
}

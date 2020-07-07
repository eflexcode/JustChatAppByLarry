package com.example.justchat.cloudmessaging;



import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIClient {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAADTFYbbQ:APA91bFA9jAvzPgb_Xuoa38pVIKoDF3NVPrfyUPbjspGxs9rZ3s9KwRXSUzSuFbu_F95ck7E9V0cTvMJsDBaRpKjyO4ohfl4jd0j0mhPHjCaq_A16oLK3g_bp9AKpeSo-VY2c3Jm1dza"
    })
    @POST("send")
    Call<ResponseBody> sendNotification(@Body MessageBody messageBody);
}

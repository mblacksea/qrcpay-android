package eligateam.etiya.com.qrcpay_android.util;


import java.util.List;

import eligateam.etiya.com.qrcpay_android.model.Account;
import eligateam.etiya.com.qrcpay_android.model.ProvisionRequest;
import eligateam.etiya.com.qrcpay_android.model.ProvisionResponse;
import eligateam.etiya.com.qrcpay_android.model.QRCodeResponse;
import eligateam.etiya.com.qrcpay_android.model.UserAccount;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mustafa.karadeniz on 09-May-18.
 *
 */

public interface RestAPIInterface {
    //TODO service url yazilacak.
    @GET("/Payment/detail?publicKey=e130e5e618f15cee7a")
    Call<QRCodeResponse> resolveQRCode(@Query("token") String token);

    @GET("/Payment/success?publicKey=e130e5e618f15cee7a")
    Call<ResponseBody> callSuccess(@Query("token") String token);

    @GET("/hackathon/v1/cards")
    Call<UserAccount> getUserAccounts();

    @GET("/hackathon/v1/accounts")
    Call<Account> getAccounts();

    @POST("/hackathon/v1/cards/{id}/provisions")
    Call<ProvisionResponse> inquireProvisions(@Path("id") Integer id, @Body ProvisionRequest provisionRequest);

    /*@POST("/api/users")
    Call<User> createUser(@Body User user);

    @GET("/api/users?")
    Call<UserList> doGetUserList(@Query("page") String page);

    @FormUrlEncoded
    @POST("/api/users?")
    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);*/
}

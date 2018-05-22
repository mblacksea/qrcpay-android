package eligateam.etiya.com.qrcpay_android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.zxing.Result;

import java.util.List;

import eligateam.etiya.com.qrcpay_android.R;
import eligateam.etiya.com.qrcpay_android.global.GlobalSession;
import eligateam.etiya.com.qrcpay_android.model.UserAccount;
import eligateam.etiya.com.qrcpay_android.model.Account;
import eligateam.etiya.com.qrcpay_android.util.APIClient;
import eligateam.etiya.com.qrcpay_android.util.QRCPayUtil;
import eligateam.etiya.com.qrcpay_android.util.RestAPIInterface;
import eligateam.etiya.com.qrcpay_android.util.RestApiUrl;
import eligateam.etiya.com.qrcpay_android.util.TokenValue;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private Button btn_signup;
    private ProgressDialog progressDialog;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

            String sonuc = QRCPayUtil.readQRCode(bitmap);
            Log.e("qrimage",sonuc);

            // Do something with the bitmap


            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        btn_signup = findViewById(R.id.btn_signup);


        progressDialog = new ProgressDialog(SplashActivity.this);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO account icin api cagrilacak (Kuveytturk api)
                progressDialog.setMessage("Giriş Bilgileriniz Doğrulanıyor...Lütfen Bekleyiniz!");
                progressDialog.setCancelable(false);
                progressDialog.show();


                RestAPIInterface restAPIInterfacekvt;
                restAPIInterfacekvt = APIClient.getClientKuveytturk(RestApiUrl.KUVEYTTURK_BASE_URL, TokenValue.KUVEYTTURK_TOKEN).create(RestAPIInterface.class);
                Call<UserAccount> call = restAPIInterfacekvt.getUserAccounts();
                Call<Account> callAccount = restAPIInterfacekvt.getAccounts();
                call.enqueue(new Callback<UserAccount>() {
                    @Override
                    public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {


                        Log.d("TAG", response.code() + "");

                        String displayResponse = "";

                        UserAccount resource = response.body();
                        List<UserAccount.Value> datumList = resource.value;

                        ((GlobalSession) getApplicationContext()).setUserAccountInfo(datumList);
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onFailure(Call<UserAccount> call, Throwable t) {
                        call.cancel();
                        progressDialog.dismiss();
                    }
                });
                callAccount.enqueue(new Callback<Account>() {
                    @Override
                    public void onResponse(Call<Account> call, Response<Account> response) {
                        Account resource = response.body();
                        List<Account.value> datumList = resource.value;
                        ((GlobalSession) getApplicationContext()).setAccount(datumList);
                        Log.e("customerAccount", datumList.get(0).customerName);
                    }

                    @Override
                    public void onFailure(Call<Account> call, Throwable t) {
                        call.cancel();
                        progressDialog.dismiss();
                    }
                });

            }
        });
    }

}

package eligateam.etiya.com.qrcpay_android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.zxing.Result;

import java.util.List;

import eligateam.etiya.com.qrcpay_android.R;
import eligateam.etiya.com.qrcpay_android.fragment.AccountListFragment;
import eligateam.etiya.com.qrcpay_android.fragment.HomeFragment;
import eligateam.etiya.com.qrcpay_android.fragment.QCPayFragment;
import eligateam.etiya.com.qrcpay_android.global.GlobalSession;
import eligateam.etiya.com.qrcpay_android.model.ProvisionRequest;
import eligateam.etiya.com.qrcpay_android.model.ProvisionResponse;
import eligateam.etiya.com.qrcpay_android.model.QRCodeResponse;
import eligateam.etiya.com.qrcpay_android.model.UserAccount;
import eligateam.etiya.com.qrcpay_android.util.APIClient;
import eligateam.etiya.com.qrcpay_android.util.CircleTransform;
import eligateam.etiya.com.qrcpay_android.util.GeneralEnumeration;
import eligateam.etiya.com.qrcpay_android.util.QRCPayUtil;
import eligateam.etiya.com.qrcpay_android.util.RestAPIInterface;
import eligateam.etiya.com.qrcpay_android.util.RestApiUrl;
import eligateam.etiya.com.qrcpay_android.util.TokenValue;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, QCPayFragment.OnFragmentInteractionListener, AccountListFragment.OnFragmentInteractionListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private Long cardAvailableLimit;
    private Integer cardId;
    private Integer order;

    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "https://i2.wp.com/eborsahaber.com/wp-content/uploads/2017/08/kuveyt-turk.jpg?resize=640%2C320";
    private static final String urlProfileImg = "https://lh3.googleusercontent.com/6bnYvBKDmsA4Ld13xRvZxRz1GqYnpvI2xpEEeSfS4q-mHgv3mYpDC1qIzhhyE8nL9w=s180";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;
    private PrettyDialog pDialog;
    private PrettyDialog pDialogForConfirmBank;
    private PrettyDialog pDialogForConfirmSuccess;
    private PrettyDialog pDialogInvalid;
    private PrettyDialog pDialogForNotEnoughMoney;
    private ProgressDialog progressDialog;
    private QRCodeResponse qrCodeResponse;
    private String globalToken;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private boolean doubleBackToExitPressedOnce = false;
    private RestAPIInterface restAPIInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.name);
        txtWebsite = navHeader.findViewById(R.id.website);
        imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
        imgProfile = navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);


        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();
        progressDialog = new ProgressDialog(MainActivity.this);

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText("Kuveytturk Mobil");
        txtWebsite.setText("https://www.kuveytturk.com.tr/");

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                HomeFragment photosFragment = new HomeFragment();
                return photosFragment;
            case 2:
                // movies fragment
                HomeFragment moviesFragment = new HomeFragment();
                return moviesFragment;
            case 3:
                // notifications fragment
                AccountListFragment accountListFragment = new AccountListFragment();
                return accountListFragment;

            case 4:
                // settings fragment
                HomeFragment settingsFragment = new HomeFragment();
                return settingsFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

            String token = QRCPayUtil.readQRCode(bitmap);
            Log.e("qrimage",token);

            // Do something with the bitmap


            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
            showMessageDialog(token);



        }
    }

    private void showMessageDialog(final String token ) {
        globalToken = token;
        restAPIInterface = APIClient.getClient(RestApiUrl.BASE_URL).create(RestAPIInterface.class);

        /**
         * Call rest api
         * for QRCode Resolve
         */

        Call<QRCodeResponse> call = restAPIInterface.resolveQRCode(token);
        progressDialog.setMessage("Lütfen Bekleyiniz!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        callQRCodeResolveService(call);



      /*  pDialog = new PrettyDialog(this);
        pDialog
                .setTitle("QRC Pay")
                .setMessage(token)
                .addButton("Ödeme Onay",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_green,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                //TODO service cagrisi yapilacak.

                                restAPIInterface = APIClient.getClient(RestApiUrl.BASE_URL).create(RestAPIInterface.class);

                                /**
                                 * Call rest api
                                 * for QRCode Resolve
                                 */

                   /*             Call<QRCodeResponse> call = restAPIInterface.resolveQRCode(token);
                                progressDialog.setMessage("Lütfen Bekleyiniz!");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                callQRCodeResolveService(call);*/

                    //        }
                   //     })
               // .show();
    }

    private void callQRCodeResolveService(Call<QRCodeResponse> call) {
        call.enqueue(new Callback<QRCodeResponse>() {
            @Override
            public void onResponse(Call<QRCodeResponse> call, Response<QRCodeResponse> response) {
                if (GeneralEnumeration.HttpStatusCode.BAD_REQUEST.getErrorCode() == response.code()) {
                    //QRCPayUtil.showSnackBar(mScannerView, "Geçersiz QRCode!");
                    progressDialog.dismiss();
                    pDialogInvalid = new PrettyDialog(MainActivity.this);
                    pDialogInvalid.setTitle("QRC Pay")
                            .setMessage("Geçersiz QRCode!")
                            .show();


                } else {
                    qrCodeResponse = response.body();
                    final Number totalAmnt = (Number) qrCodeResponse.totalAmnt;
                    List<String> cartItems = qrCodeResponse.cartItems;
                    String ecomOrgName = qrCodeResponse.ecomOrgName;
                    String items = "";
                    for (String i : cartItems) {
                        items = items + " " + i;
                    }
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    order = preferences.getInt("cardId", 1);
                    cardId = preferences.getInt("cardId", -1);
                    cardAvailableLimit = preferences.getLong("cardAvailableLimit", -1);

                    if (cardAvailableLimit >= totalAmnt.longValue()) {
                        pDialogForConfirmBank = new PrettyDialog(MainActivity.this);

                        pDialogForConfirmBank
                                .setTitle("QRC Pay")
                                .setMessage("Ödemeniz gerçekleşecektir. Emin misiniz?\n Alınan Ürünler: " + items + "\n Tutar: " + totalAmnt + "\n Firma: " + ecomOrgName)
                                .addButton("Evet",
                                        R.color.pdlg_color_white,
                                        R.color.pdlg_color_green,
                                        new PrettyDialogCallback() {
                                            @Override
                                            public void onClick() {
                                                RestAPIInterface restAPIInterfaceInquire;
                                                restAPIInterfaceInquire = APIClient.getClientKuveytturk(RestApiUrl.KUVEYTTURK_BASE_URL, TokenValue.KUVEYTTURK_TOKEN).create(RestAPIInterface.class);
                                                Call<ProvisionResponse> call = restAPIInterfaceInquire.inquireProvisions(cardId, new ProvisionRequest(totalAmnt, "test123"));
                                                progressDialog.setMessage("Lütfen Bekleyiniz!");
                                                progressDialog.setCancelable(false);
                                                progressDialog.show();
                                                call.enqueue(new Callback<ProvisionResponse>() {
                                                    @Override
                                                    public void onResponse(Call<ProvisionResponse> call, Response<ProvisionResponse> response) {
                                                        Log.e("kuveyt posttan", response.toString());
                                                        ProvisionResponse provisionResponse = response.body();
                                                        if (provisionResponse.success) {
                                                            progressDialog.dismiss();
                                                            pDialogForConfirmBank.dismiss();
                                                            List<UserAccount.Value> results = ((GlobalSession) getApplicationContext()).getUserAccountInfo();
                                                            Double a = results.get(order - 1).getCardAvailableLimit().doubleValue() - totalAmnt.doubleValue();
                                                            results.get(order - 1).setCardAvailableLimit(a);
                                                            ((GlobalSession) getApplicationContext()).setUserAccountInfo(results);

                                                            /**
                                                             * Success icin qrc_pay e don.
                                                             */

                                                            Call<ResponseBody> callSuccess = restAPIInterface.callSuccess(globalToken);
                                                            progressDialog.setMessage("Lütfen Bekleyiniz!");
                                                            progressDialog.setCancelable(false);
                                                            progressDialog.show();
                                                            callSuccess.enqueue(new Callback<ResponseBody>() {
                                                                @Override
                                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                    Log.e("responsefromsuccessapi", response.toString());
                                                                    progressDialog.dismiss();
                                                                    pDialogForConfirmSuccess = new PrettyDialog(MainActivity.this);
                                                                    pDialogForConfirmSuccess
                                                                            .setTitle("ÖDEME BAŞARILI")
                                                                            .addButton(
                                                                                    "TAMAM",
                                                                                    R.color.pdlg_color_white,
                                                                                    R.color.pdlg_color_green,
                                                                                    new PrettyDialogCallback() {
                                                                                        @Override
                                                                                        public void onClick() {
                                                                                            progressDialog.dismiss();
                                                                                            pDialogForConfirmSuccess.dismiss();
                                                                                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                                                                            startActivity(intent);
                                                                                        }
                                                                                    }
                                                                            )

                                                                            .show();

                                                                }

                                                                @Override
                                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                    Log.e("error response", t.toString());
                                                                    progressDialog.dismiss();
                                                                }
                                                            });
                                                        } else {

                                                            Log.e("provizyon hatası!", response.toString());
                                                        }


                                                    }

                                                    @Override
                                                    public void onFailure(Call<ProvisionResponse> call, Throwable t) {
                                                        progressDialog.dismiss();
                                                        pDialogForConfirmBank.dismiss();
                                                    }
                                                });
                                                //TODO kuveytturk un apisi cagrilacak. Kullanicinin hesabindan para dusecek.
                                                //TODO para dustukten sonra qrcpay apisini success olarak cagir else error olarak cagir.

                                            }
                                        })
                                .addButton(
                                        "Hayır",
                                        R.color.pdlg_color_white,
                                        R.color.pdlg_color_red,
                                        new PrettyDialogCallback() {
                                            @Override
                                            public void onClick() {
                                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                )

                                .show();


                        progressDialog.dismiss();


                        Log.e("response", response.toString());
                        //  Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                    } else {
                        //QRCPayUtil.showSnackBar(mScannerView, "Yetersiz Bakiye!");
                        progressDialog.dismiss();
                        pDialogForNotEnoughMoney = new PrettyDialog(MainActivity.this);
                        pDialogForNotEnoughMoney.setTitle("QRC Pay")
                                .setMessage("Yetersiz Bakiye!")
                                .show();
                    }
                }


            }

            @Override
            public void onFailure(Call<QRCodeResponse> call, Throwable t) {
                progressDialog.dismiss();
              //  QRCPayUtil.showSnackBar(mScannerView, "Ödeme başarısız!!");
            }
        });

    }


    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_photos:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PHOTOS;
                        break;
                    case R.id.nav_movies:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MOVIES;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        //  startActivity(new Intent(MainActivity.this, DenemeActivity.class));
                        //  drawer.closeDrawers();
                        //return true;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Çıkmak İçin Tekrar Çıkışa Basınız!", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

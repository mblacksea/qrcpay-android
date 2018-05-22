package eligateam.etiya.com.qrcpay_android.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import eligateam.etiya.com.qrcpay_android.R;
import eligateam.etiya.com.qrcpay_android.activity.MainActivity;
import eligateam.etiya.com.qrcpay_android.global.GlobalSession;
import eligateam.etiya.com.qrcpay_android.model.ProvisionRequest;
import eligateam.etiya.com.qrcpay_android.model.ProvisionResponse;
import eligateam.etiya.com.qrcpay_android.model.QRCodeResponse;
import eligateam.etiya.com.qrcpay_android.model.UserAccount;
import eligateam.etiya.com.qrcpay_android.util.APIClient;
import eligateam.etiya.com.qrcpay_android.util.GeneralEnumeration;
import eligateam.etiya.com.qrcpay_android.util.QRCPayUtil;
import eligateam.etiya.com.qrcpay_android.util.RestAPIInterface;
import eligateam.etiya.com.qrcpay_android.util.RestApiUrl;
import eligateam.etiya.com.qrcpay_android.util.TokenValue;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

import android.media.Ringtone;
import android.media.RingtoneManager;

import com.google.zxing.Result;

import java.net.HttpRetryException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QCPayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QCPayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QCPayFragment extends Fragment implements
        ZXingScannerView.ResultHandler {


    private ZXingScannerView mScannerView;
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private ArrayList<Integer> mSelectedIndices;
    private boolean mFlash;
    private boolean mAutoFocus;
    private PrettyDialog pDialog;
    private PrettyDialog pDialogForConfirmBank;
    private PrettyDialog pDialogForConfirmSuccess;
    private PrettyDialog pDialogInvalid;
    private PrettyDialog pDialogForNotEnoughMoney;
    private ProgressDialog progressDialog;

    private QRCodeResponse qrCodeResponse;
    private String token;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Long cardAvailableLimit;
    private Integer cardId;
    private Integer order;
    private OnFragmentInteractionListener mListener;
    private RestAPIInterface restAPIInterface;


    public QCPayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QCPayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QCPayFragment newInstance(String param1, String param2) {
        QCPayFragment fragment = new QCPayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        order = preferences.getInt("cardId", 1);
        cardId = preferences.getInt("cardId", -1);
        cardAvailableLimit = preferences.getLong("cardAvailableLimit", -1);

        setHasOptionsMenu(true);
        progressDialog = new ProgressDialog(getActivity());


    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }


    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle state) {
        mScannerView = new ZXingScannerView(getActivity());
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
        }
        return mScannerView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void handleResult(Result rawResult) {

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
        }


        showMessageDialog(rawResult);


    }

    private void showMessageDialog(Result rawResult) {
        token = rawResult.getText();
        restAPIInterface = APIClient.getClient(RestApiUrl.BASE_URL).create(RestAPIInterface.class);
        Call<QRCodeResponse> call = restAPIInterface.resolveQRCode(token);
        progressDialog.setMessage("Lütfen Bekleyiniz!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        callQRCodeResolveService(call);




      /*  pDialog = new PrettyDialog(getActivity());
        pDialog
                .setTitle("QRC Pay")
                .setMessage("Ödemeyi Onaylamak İstiyor Musunuz?")
                .addButton("Ödeme Onayla",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_green,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {

                                restAPIInterface = APIClient.getClient(RestApiUrl.BASE_URL).create(RestAPIInterface.class);

                                /**
                                 * Call rest api
                                 * for QRCode Resolve
                                 */

                             /*   Call<QRCodeResponse> call = restAPIInterface.resolveQRCode(token);
                                progressDialog.setMessage("Lütfen Bekleyiniz!");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                callQRCodeResolveService(call);*/

        //          }
        //       })
        //  .show();
    }

    private void callQRCodeResolveService(Call<QRCodeResponse> call) {
        call.enqueue(new Callback<QRCodeResponse>() {
            @Override
            public void onResponse(Call<QRCodeResponse> call, Response<QRCodeResponse> response) {
                if (GeneralEnumeration.HttpStatusCode.BAD_REQUEST.getErrorCode() == response.code()) {
                    //QRCPayUtil.showSnackBar(mScannerView, "Geçersiz QRCode!");
                    progressDialog.dismiss();
                    pDialogInvalid = new PrettyDialog(getActivity());
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

                    if (cardAvailableLimit >= totalAmnt.longValue()) {
                        pDialogForConfirmBank = new PrettyDialog(getActivity());

                        pDialogForConfirmBank
                                .setTitle("QRC Pay")
                                .setMessage("Ödemeniz gerçekleşecektir. Emin misiniz?\n Alınan Ürünler: " + items + "\n Tutar: " + totalAmnt + "tl \n Firma: " + ecomOrgName)
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
                                                            List<UserAccount.Value> results = ((GlobalSession) getActivity().getApplicationContext()).getUserAccountInfo();
                                                            Double a = results.get(order - 1).getCardAvailableLimit().doubleValue() - totalAmnt.doubleValue();
                                                            results.get(order - 1).setCardAvailableLimit(a);
                                                            ((GlobalSession) getActivity().getApplicationContext()).setUserAccountInfo(results);

                                                            /**
                                                             * Success icin qrc_pay e don.
                                                             */

                                                            Call<ResponseBody> callSuccess = restAPIInterface.callSuccess(token);
                                                            progressDialog.setMessage("Lütfen Bekleyiniz!");
                                                            progressDialog.setCancelable(false);
                                                            progressDialog.show();
                                                            callSuccess.enqueue(new Callback<ResponseBody>() {
                                                                @Override
                                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                    Log.e("responsefromsuccessapi", response.toString());
                                                                    progressDialog.dismiss();
                                                                    pDialogForConfirmSuccess = new PrettyDialog(getActivity());
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
                                                                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                                                                            startActivity(intent);
                                                                                        }
                                                                                    }
                                                                            )

                                                                            .show();

                                                                }

                                                                @Override
                                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                    Log.e("error response", t.toString());
                                                                    QRCPayUtil.showSnackBar(mScannerView, "Geçersiz QRCode!");
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
                                                        QRCPayUtil.showSnackBar(mScannerView, "Provizyon İşlemi Yapılamadı!");
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
                                                pDialogForConfirmBank.dismiss();
                                                Intent intent = new Intent(getActivity(), MainActivity.class);
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
                        pDialogForNotEnoughMoney = new PrettyDialog(getActivity());
                        pDialogForNotEnoughMoney.setTitle("QRC Pay")
                                .setMessage("Yetersiz Bakiye!")
                                .show();
                    }
                }


            }

            @Override
            public void onFailure(Call<QRCodeResponse> call, Throwable t) {
                progressDialog.dismiss();
                QRCPayUtil.showSnackBar(mScannerView, "Ödeme başarısız!!");
            }
        });

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

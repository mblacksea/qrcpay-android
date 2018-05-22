package eligateam.etiya.com.qrcpay_android.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import eligateam.etiya.com.qrcpay_android.R;
import eligateam.etiya.com.qrcpay_android.activity.AccountListAdapter;
import eligateam.etiya.com.qrcpay_android.activity.MainActivity;
import eligateam.etiya.com.qrcpay_android.global.GlobalSession;
import eligateam.etiya.com.qrcpay_android.model.UserAccount;
import eligateam.etiya.com.qrcpay_android.util.APIClient;
import eligateam.etiya.com.qrcpay_android.util.QRCPayUtil;
import eligateam.etiya.com.qrcpay_android.util.RestAPIInterface;
import eligateam.etiya.com.qrcpay_android.util.RestApiUrl;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private PrettyDialog decision;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private OnFragmentInteractionListener mListener;
    private RecyclerView.LayoutManager mLayoutManager;
    private AccountListAdapter mAdapter;
   // SwipeRefreshLayout swipeLayout;
    public AccountListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountListFragment newInstance(String param1, String param2) {
        AccountListFragment fragment = new AccountListFragment();
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

        final PrettyDialog pDialog = new PrettyDialog(getActivity());
        pDialog
                .setTitle("Kamera & Galeri")
                .setMessage("QRCode okutmak için seçim yapınız")
                .setIcon(
                        R.drawable.pdlg_icon_info,     // icon resource
                        R.color.pdlg_color_green,      // icon tint
                        new PrettyDialogCallback() {   // icon OnClick listener
                            @Override
                            public void onClick() {
                            }
                        })
                .addButton(
                        "Kamera",
                        R.color.pdlg_color_white,
                        R.color.colorAccent,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                ((GlobalSession) getActivity().getApplicationContext()).setCameraOrGallery(Boolean.TRUE);
                                pDialog.dismiss();
                                QRCPayUtil.showSnackBar(getView(),"Kamera Seçildi!");
                            }
                        }
                )
                .addButton(
                        "Galeri",
                        R.color.pdlg_color_white,
                        R.color.colorAccent,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                ((GlobalSession) getActivity().getApplicationContext()).setCameraOrGallery(Boolean.FALSE);
                                pDialog.dismiss();
                                QRCPayUtil.showSnackBar(getView(),"Galeri Seçildi!");
                            }
                        }
                )
                .setAnimationEnabled(true)
                .setTitleColor(R.color.pdlg_color_blue)
                .setMessageColor(R.color.pdlg_color_gray)
                .show();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_list, container, false);

        List<UserAccount.Value> results = ((GlobalSession) getActivity().getApplicationContext()).getUserAccountInfo();

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AccountListAdapter(getContext(), results);
        recyclerView.setAdapter(mAdapter);

     /*  swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);*/


        // this is data fro recycler view


        return rootView;
        //return inflater.inflate(R.layout.fragment_account_list, container, false);
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
    public void onRefresh() {
      /*  AccountListFragment accountListFragment = new AccountListFragment();
        Fragment fragment = accountListFragment;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, "notifications");
        fragmentTransaction.commitAllowingStateLoss();*/
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

package eligateam.etiya.com.qrcpay_android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import eligateam.etiya.com.qrcpay_android.R;
import eligateam.etiya.com.qrcpay_android.fragment.QCPayFragment;
import eligateam.etiya.com.qrcpay_android.global.GlobalSession;
import eligateam.etiya.com.qrcpay_android.model.UserAccount;
import eligateam.etiya.com.qrcpay_android.util.CircleTransform;
import eligateam.etiya.com.qrcpay_android.util.QRCPayUtil;

/**
 * Created by MUSTAFA on 12.05.2018.
 */

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.MyViewHolder> {
    private Context mContext;
    private List<UserAccount.Value> fortuneHistoryListList;

    public AccountListAdapter(Context mContext, List<UserAccount.Value> fortuneHistoryListList) {
        this.mContext = mContext;
        this.fortuneHistoryListList = fortuneHistoryListList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cardNumber, cardTotalLimit;
        public TextView cardAvailableLimit;
        ImageView cardImage;

        public MyViewHolder(View view) {
            super(view);

            //TODO thumbnail icin guzel falcı resimleri koy
            cardNumber = view.findViewById(R.id.cardNumber);
            cardTotalLimit = view.findViewById(R.id.cardTotalLimit);
            cardAvailableLimit = view.findViewById(R.id.cardAvailableLimit);
            cardImage = view.findViewById(R.id.cardImage);

        }
    }

    @Override
    public AccountListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_listview_account, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AccountListAdapter.MyViewHolder holder, final int position) {
        final Integer cardId = fortuneHistoryListList.get(position).id;
        String cardNumber = fortuneHistoryListList.get(position).secureCardNumber;
        Number cardTotalLimit = fortuneHistoryListList.get(position).cardTotalLimit;
        Number cardAvailableLimit = fortuneHistoryListList.get(position).cardAvailableLimit;
        holder.cardNumber.setText(cardNumber);
        holder.cardTotalLimit.setText("Toplam Limit: " + QRCPayUtil.formatDecimal(cardTotalLimit));
        holder.cardAvailableLimit.setText("Kullanılabilir Limit: " + QRCPayUtil.formatDecimal(cardAvailableLimit));
        Glide.with(mContext).load("https://www.evolis.com/sites/default/files/thumbnails/image/Kuveyt-Turc-Bank_300X192.jpg")
                .crossFade()
                .thumbnail(0.1f)
                .bitmapTransform(new CircleTransform(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.cardImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("tıklandı", String.valueOf(fortuneHistoryListList.get(position).cardAvailableLimit));

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("order", position);
                editor.putInt("cardId", cardId);
                editor.putLong("cardAvailableLimit", fortuneHistoryListList.get(position).cardAvailableLimit.longValue());
                editor.commit();


                if (((GlobalSession) view.getContext().getApplicationContext()).getCameraOrGallery()) {

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment myFragment = new QCPayFragment();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, myFragment).commit();

                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    ((Activity) mContext).startActivityForResult(photoPickerIntent, 1);

                }


            }
        });

        //    holder.count.setText(String.valueOf(a));
        // Glide.with(mContext).load(FalAppUtils.getThumbnailForPosition(position)).into(holder.thumbnail);


    }



    @Override
    public int getItemCount() {
        return fortuneHistoryListList.size();
    }
}

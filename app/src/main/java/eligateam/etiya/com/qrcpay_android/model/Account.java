package eligateam.etiya.com.qrcpay_android.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by MUSTAFA on 11.05.2018.
 *
 */

public class Account {

    public List<value> value = null;

    public class value {

        @SerializedName("name")
        public String name;
        @SerializedName("suffix")
        public Integer suffix;
        @SerializedName("balance")
        public Number balance;
        @SerializedName("availableBalance")
        public Number availableBalance;
        @SerializedName("fxId")
        public Integer fxId;
        @SerializedName("iban")
        public String iban;
        @SerializedName("type")
        public String type;
        @SerializedName("branchName")
        public String branchName;
        @SerializedName("customerName")
        public String customerName;


    }


}

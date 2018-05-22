package eligateam.etiya.com.qrcpay_android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MUSTAFA on 12.05.2018.
 *
 */

public class ProvisionRequest {

    public Number amount;
    public String description;


    public ProvisionRequest(Number amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    public Number getAmount() {
        return amount;
    }

    public void setAmount(Number amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

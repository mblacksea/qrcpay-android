package eligateam.etiya.com.qrcpay_android.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MUSTAFA on 12.05.2018.
 *
 */

public class UserAccount {
    public List<Value> value = null;

    public class Value {

        @SerializedName("id")
        public Integer id;
        @SerializedName("secureCardNumber")
        public String secureCardNumber;
        @SerializedName("cardHolderName")
        public String cardHolderName;
        @SerializedName("cardTypeName")
        public String cardTypeName;
        @SerializedName("cardProductCode")
        public String cardProductCode;
        @SerializedName("cardAvailableLimit")
        public Number cardAvailableLimit;
        @SerializedName("cardTotalLimit")
        public Number cardTotalLimit;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getSecureCardNumber() {
            return secureCardNumber;
        }

        public void setSecureCardNumber(String secureCardNumber) {
            this.secureCardNumber = secureCardNumber;
        }

        public String getCardHolderName() {
            return cardHolderName;
        }

        public void setCardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
        }

        public String getCardTypeName() {
            return cardTypeName;
        }

        public void setCardTypeName(String cardTypeName) {
            this.cardTypeName = cardTypeName;
        }

        public String getCardProductCode() {
            return cardProductCode;
        }

        public void setCardProductCode(String cardProductCode) {
            this.cardProductCode = cardProductCode;
        }

        public Number getCardAvailableLimit() {
            return cardAvailableLimit;
        }

        public void setCardAvailableLimit(Number cardAvailableLimit) {
            this.cardAvailableLimit = cardAvailableLimit;
        }

        public Number getCardTotalLimit() {
            return cardTotalLimit;
        }

        public void setCardTotalLimit(Number cardTotalLimit) {
            this.cardTotalLimit = cardTotalLimit;
        }
    }

}

package eligateam.etiya.com.qrcpay_android.global;

import android.app.Application;

import java.util.List;

import eligateam.etiya.com.qrcpay_android.model.Account;
import eligateam.etiya.com.qrcpay_android.model.UserAccount;

/**
 * Created by MUSTAFA on 11.05.2018.
 *
 */

public class GlobalSession extends Application {

    private List<UserAccount.Value> userAccountInfo;
    private List<Account.value> account;
    private Boolean isCameraOrGallery = Boolean.TRUE;

    public Boolean getCameraOrGallery() {
        return isCameraOrGallery;
    }

    public void setCameraOrGallery(Boolean cameraOrGallery) {
        isCameraOrGallery = cameraOrGallery;
    }

    public void setUserAccountInfo(List<UserAccount.Value> userAccountInfo) {
        this.userAccountInfo = userAccountInfo;
    }

    public List<UserAccount.Value> getUserAccountInfo() {
        return userAccountInfo;
    }

    public List<Account.value> getAccount() {
        return account;
    }

    public void setAccount(List<Account.value> account) {
        this.account = account;
    }
}

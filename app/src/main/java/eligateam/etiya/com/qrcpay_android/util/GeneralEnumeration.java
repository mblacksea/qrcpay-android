package eligateam.etiya.com.qrcpay_android.util;

/**
 * Created by MUSTAFA on 12.05.2018.
 *
 */

public class GeneralEnumeration {

    public enum HttpStatusCode {
        BAD_REQUEST(400);

        private int code;


        HttpStatusCode(int code) {
            this.code = code;
        }

        public int getErrorCode(){
            return code;
        }

    }
}

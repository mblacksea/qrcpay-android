package  eligateam.etiya.com.qrcpay_android.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by mustafa.karadeniz on 09-May-18.
 *
 */

public class QRCodeResponse {

    //TODO serviceten gelen response a gore sekillendir.
  /*  {
        "ecomOrgName": "Amazon",
            "cdate": "2018-05-12",
            "instCnt": 0,
            "stId": 1,
            "totalAmnt": 38.5,
            "udate": null,
            "cartItems": [
        "Kitap",
                "Kalem",
                "Defter"
    ]
    }*/

    @SerializedName("ecomOrgName")
    public String ecomOrgName;
    @SerializedName("cdate")
    public Date cdate;
    @SerializedName("instCnt")
    public Integer instCnt;
    @SerializedName("stId")
    public Integer stId;
    @SerializedName("totalAmnt")
    public Double totalAmnt;
    @SerializedName("udate")
    public Date udate;




    @SerializedName("cartItems")
    public List<String> cartItems;





}

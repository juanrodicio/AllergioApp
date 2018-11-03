package es.uca.allergioapp.POJOs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Authority {

    @SerializedName("authority")
    @Expose
    private String authority;

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

}

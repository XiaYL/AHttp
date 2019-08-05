package net.luculent.http.https;

/**
 * Created by xiaya on 2017/6/17.
 * https证书设置类
 */

public class SSLCertificate {

    String assetname;
    String password;

    public SSLCertificate(String assetname, String password) {
        this.assetname = assetname;
        this.password = password;
    }

    @Override
    public String toString() {
        return "SSLCertificate{" +
                "assetname='" + assetname + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

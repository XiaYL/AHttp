package net.luculent.http.https;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by xiaya on 2017/6/17.
 * https证书管理类
 */

public class SSLManager {

    public static class SSLParams {
        public SSLSocketFactory sslSocketFactory;
        public X509TrustManager trustManager;
    }

    public static SSLParams getSSLSocketFactory(Context context, List<SSLCertificate> certificates) throws
            Exception {
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        SSLParams sslParams = new SSLParams();
        TrustManager[] trustManagers = prepareTrustManager(context, certificates);
        X509TrustManager trustManager = null;
        if (trustManagers != null) {
            trustManager = new MyTrustManager(chooseTrustManager(trustManagers));
        } else {
            trustManager = new UnSafeTrustManager();
        }
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
        SSLSocketFactory factory = new TLSSocketFactory(sslContext.getSocketFactory());
        sslParams.sslSocketFactory = factory;
        sslParams.trustManager = trustManager;
        return sslParams;
    }

    public static HostnameVerifier getHostnameVerifier(final String[] hosts) {
        HostnameVerifier verifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                if (hosts == null || hosts.length == 0) {
                    return true;
                }
                boolean accept = false;
                for (int i = 0; i < hosts.length; i++) {
                    if (hostname.equalsIgnoreCase(hosts[i])) {
                        accept = true;
                        break;
                    }
                }
                return accept;
            }
        };
        return verifier;
    }

    private static TrustManager[] prepareTrustManager(Context context, List<SSLCertificate> certificates) {
        if (certificates == null || certificates.size() <= 0) return null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            for (SSLCertificate certificate : certificates) {
                InputStream inputStream = context.getAssets().open(certificate.assetname);
                keyStore.setCertificateEntry(String.valueOf(certificate.password), certificateFactory
                        .generateCertificate
                                (inputStream));
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory
                    .getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            return trustManagers;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class UnSafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    private static class MyTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public MyTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var4.init((KeyStore) null);
            defaultTrustManager = chooseTrustManager(var4.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
                localTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                throw ce;
            }
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
                localTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                throw ce;
            }
        }


        @Override
        public X509Certificate[] getAcceptedIssuers() {
            List<X509Certificate> list = new ArrayList<>();
            list.addAll(Arrays.asList(defaultTrustManager.getAcceptedIssuers()));
            list.addAll(Arrays.asList(localTrustManager.getAcceptedIssuers()));
            return list.toArray(new X509Certificate[0]);
        }
    }
}

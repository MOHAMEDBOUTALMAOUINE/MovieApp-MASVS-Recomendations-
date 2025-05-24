package com.example.movieapp.security;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class SSLConfig {
    private static final String TAG = "SSLConfig";

    public static OkHttpClient.Builder configureSSL(OkHttpClient.Builder builder) {
        try {
            // Créer un TrustManager qui vérifie le certificat du serveur
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);

            // Obtenir le TrustManager par défaut
            X509TrustManager trustManager = null;
            for (TrustManager tm : trustManagerFactory.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    trustManager = (X509TrustManager) tm;
                    break;
                }
            }

            if (trustManager == null) {
                throw new IllegalStateException("No X509TrustManager found");
            }

            // Créer un SSLContext avec le TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);

            // Configurer le client OkHttp avec notre SSLContext
            builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);

            // Configurer le hostname verifier pour accepter api.themoviedb.org
            builder.hostnameVerifier((hostname, session) -> {
                try {
                    // Vérifier si le hostname correspond à api.themoviedb.org
                    if ("api.themoviedb.org".equals(hostname)) {
                        Certificate[] certs = session.getPeerCertificates();
                        if (certs != null && certs.length > 0 && certs[0] instanceof X509Certificate) {
                            X509Certificate cert = (X509Certificate) certs[0];
                            cert.checkValidity();
                            return true;
                        }
                    }
                    return false;
                } catch (Exception e) {
                    Log.e(TAG, "SSL Hostname verification failed for: " + hostname, e);
                    return false;
                }
            });

            return builder;
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            Log.e(TAG, "Failed to configure SSL", e);
            throw new RuntimeException("Failed to configure SSL", e);
        }
    }
} 
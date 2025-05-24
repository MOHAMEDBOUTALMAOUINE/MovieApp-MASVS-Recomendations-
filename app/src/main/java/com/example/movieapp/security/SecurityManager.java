package com.example.movieapp.security;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SecurityManager {
    private static final String TAG = "SecurityManager";

    public static boolean isDeviceSecure(Context context) {
        try {
            // Vérifier si l'appareil est rooté
            if (isDeviceRooted()) {
                Log.w(TAG, "Device is rooted");
                return false;
            }

            // Vérifier si c'est un émulateur
            if (isEmulator()) {
                Log.w(TAG, "Device is an emulator");
                return false;
            }

            // Vérifier si le débogage est activé
            if (isDebuggerConnected()) {
                Log.w(TAG, "Debugger is connected");
                return false;
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error checking device security", e);
            // En cas d'erreur, on considère l'appareil comme sécurisé
            // pour ne pas bloquer l'application
            return true;
        }
    }

    private static boolean isDeviceRooted() {
        try {
            // Vérifier les fichiers et dossiers communs de root
            String[] rootPaths = {
                "/system/app/Superuser.apk",
                "/system/xbin/su",
                "/system/bin/su",
                "/sbin/su",
                "/system/su",
                "/system/bin/.ext/su",
                "/system/xbin/mu"
            };

            for (String path : rootPaths) {
                if (new File(path).exists()) {
                    return true;
                }
            }

            // Vérifier si la commande 'su' est disponible
            Process process = Runtime.getRuntime().exec(new String[]{"which", "su"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.readLine() != null;
        } catch (Exception e) {
            Log.e(TAG, "Error checking root", e);
            return false;
        }
    }

    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    private static boolean isDebuggerConnected() {
        return android.os.Debug.isDebuggerConnected();
    }

    public static void checkDeviceSecurity(Context context) {
        if (!isDeviceSecure(context)) {
            Log.w(TAG, "Device security check failed");
            // Ici, vous pouvez ajouter des actions supplémentaires
            // comme limiter certaines fonctionnalités ou afficher un avertissement
            // mais sans bloquer complètement l'application
        }
    }
} 
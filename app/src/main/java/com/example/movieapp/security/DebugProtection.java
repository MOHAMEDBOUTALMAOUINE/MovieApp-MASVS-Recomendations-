package com.example.movieapp.security;

import android.os.Debug;
import android.util.Log;

/**
 * Classe de protection contre le debugging
 * Implémente la recommandation MASVS-CODE-1 pour la protection contre le debugging
 */
public class DebugProtection {
    private static final String TAG = "DebugProtection";

    /**
     * Vérifie si l'application est en cours de debugging
     * @return true si un débogueur est détecté, false sinon
     */
    public static boolean isDebuggerConnected() {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger();
    }

    /**
     * Vérifie et arrête l'application si un débogueur est détecté
     */
    public static void checkDebugging() {
        if (isDebuggerConnected()) {
            Log.e(TAG, "Debugging detected! Application will exit.");
            System.exit(0);
        }
    }

    /**
     * Vérifie si l'application est en mode debug
     * @return true si l'application est en mode debug, false sinon
     */
    public static boolean isDebugMode() {
        return Debug.isDebuggerConnected() || 
               Debug.waitingForDebugger() || 
               Debug.isDebuggerConnected();
    }
} 
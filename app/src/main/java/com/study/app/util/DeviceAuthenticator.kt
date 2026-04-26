package com.study.app.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object DeviceAuthenticator {

    private const val TAG = "DeviceAuthenticator"

    fun isDeviceSecured(context: Context): Boolean {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as android.app.KeyguardManager
        return keyguardManager.isDeviceSecure
    }

    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Logger.d(TAG, "authenticate: start")

        if (!isDeviceSecured(activity)) {
            Logger.d(TAG, "authenticate: device not secured, allowing access")
            onSuccess()
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Logger.d(TAG, "authenticate: success")
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Logger.d(TAG, "authenticate: error $errorCode: $errString")
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Logger.d(TAG, "authenticate: failed")
                onError("认证失败")
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("验证设备")
            .setSubtitle("需要验证设备才能进入家长模式")
            .setAllowedAuthenticators(
                androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL or
                androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
            )
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    fun canAuthenticate(context: Context): Int {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
            androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL or
            androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
        )
    }
}

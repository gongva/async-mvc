package com.vivi.asyncmvc.comm.managers.biometric;

import android.os.CancellationSignal;
import android.support.annotation.NonNull;

interface IBiometricPromptImpl {

    void authenticate(@NonNull CancellationSignal cancel,
                      @NonNull BiometricPromptManager.OnBiometricIdentifyCallback callback);

    void dismiss();

}

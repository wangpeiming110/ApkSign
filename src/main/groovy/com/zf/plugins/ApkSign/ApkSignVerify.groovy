package com.zf.plugins.ApkSign;

import com.android.apksig.ApkVerifier;
import com.android.apksig.apk.ApkFormatException;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ApkSignVerify {

    public static ApkVerifierResult verify(File inputApk) throws ApkFormatException, NoSuchAlgorithmException, IOException {
        ApkVerifier.Builder apkVerifierBuilder = new ApkVerifier.Builder(inputApk);
        ApkVerifier apkVerifier = apkVerifierBuilder.build();
        ApkVerifier.Result verify = apkVerifier.verify();
        return new ApkVerifierResult(verify.isVerified(), verify.isVerifiedUsingV1Scheme(), verify.isVerifiedUsingV2Scheme());
    }
}

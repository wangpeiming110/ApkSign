package com.zf.plugins.ApkSign;

public class ApkVerifierResult {

    public boolean verified;
    public boolean verifiedUsingV1Scheme;
    public boolean verifiedUsingV2Scheme;

    public ApkVerifierResult(boolean mVerified, boolean mVerifiedUsingV1Scheme, boolean mVerifiedUsingV2Scheme) {
        this.verified = mVerified;
        this.verifiedUsingV1Scheme = mVerifiedUsingV1Scheme;
        this.verifiedUsingV2Scheme = mVerifiedUsingV2Scheme;
    }

}

package com.zf.plugins.ApkSign

import org.gradle.api.Action

class SignApkInfo {

    String name
    String inputApkFilePath
    String outputApkDirPath
    String signJarFilePath
    String zipalignExeFilePath
    String buildToolsVersion
    SigningInfo signingInfo

    SignApkInfo(String name) {
        this.name = name
    }

    void inputApkFilePath(String inputApkFilePath) {
        this.inputApkFilePath = inputApkFilePath
    }

    void outputApkDirPath(String outputApkDirPath) {
        this.outputApkDirPath = outputApkDirPath
    }

    void signJarFilePath(String signJarFilePath) {
        this.signJarFilePath = signJarFilePath
    }

    void zipalignExeFilePath(String zipalignExeFilePath) {
        this.zipalignExeFilePath = zipalignExeFilePath
    }

    void buildToolsVersion(String buildToolsVersion) {
        this.buildToolsVersion = buildToolsVersion
    }

    void signingInfo(Action<SigningInfo> action) {
        if (signingInfo == null) {
            signingInfo = new SigningInfo()
        }
        action.execute(signingInfo);
    }

    void signingInfo(Closure c) {
        if (signingInfo == null) {
            signingInfo = new SigningInfo()
        }
        org.gradle.util.ConfigureUtil.configure(c, signingInfo);
    }

}

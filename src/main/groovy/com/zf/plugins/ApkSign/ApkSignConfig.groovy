package com.zf.plugins.ApkSign

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project;

class ApkSignConfig {

    String signJarFilePath
    String zipalignExeFilePath
    String buildToolsVersion
    String signingName
    SigningInfo signingInfo
    NamedDomainObjectContainer<SignApkInfo> items

    ApkSignConfig(Project project) {
        items = project.container(SignApkInfo)
    }

    void signingName(String signingName) {
        this.signingName = signingName
    }

    void signJarFilePath(String signJarFilePath) {
        this.signJarFilePath = signJarFilePath
    }

    void zipalignExeFilePath(String zipalignExeFilePath) {
        this.zipalignExeFilePath = zipalignExeFilePath
    }


    void inputApkFilePath(String inputApkFilePath) {
        this.inputApkFilePath = inputApkFilePath
    }

    void outputApkDirPath(String outputApkDirPath) {
        this.outputApkDirPath = outputApkDirPath
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

    void items(Action<NamedDomainObjectContainer<SignApkInfo>> action) {
        action.execute(items)
    }

    public static ApkSignConfig getConfig(Project project) {
        ApkSignConfig config =
                project.getExtensions().findByType(ApkSignConfig.class);
        if (config == null) {
            config = new ApkSignConfig();
        }
        return config;
    }
}

package com.zf.plugins.ApkSign

import groovy.text.SimpleTemplateEngine
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

public class ApkSignTask extends DefaultTask {

    @Input
    SignApkInfo signApkInfo


    void checkConfig(ApkSignConfig apkSignConfig) {

        if (!apkSignConfig.signingInfo.storeFilePath || apkSignConfig.signingInfo.storeFilePath.trim().length() == 0) {
            throw new GradleException("签名文件 storeFilePath 不能为空 ")
        }

        File storeFile = new File(apkSignConfig.signingInfo.storeFilePath)
        if (!storeFile.exists()) {
            throw new GradleException("签名文件不存在")
        }

        if (!apkSignConfig.signingInfo.storePassword || apkSignConfig.signingInfo.storePassword.trim().length() == 0) {
            throw new GradleException("签名文件 storePassword 不能为空 ")
        }

        if (!apkSignConfig.signingInfo.keyAlias || apkSignConfig.signingInfo.keyAlias.trim().length() == 0) {
            throw new GradleException("签名文件 keyAlias 不能为空 ")
        }

        if (!apkSignConfig.signingInfo.keyPassword || apkSignConfig.signingInfo.keyPassword.trim().length() == 0) {
            throw new GradleException("签名文件 keyPassword 不能为空 ")
        }

        if (apkSignConfig.zipalignExeFilePath == null) {

            def appExtension = getAppPluginExtension()
            def buildToolsVersion = apkSignConfig.buildToolsVersion;
            if (buildToolsVersion == null) {
                buildToolsVersion = appExtension.buildToolsVersion;
            }
            apkSignConfig.zipalignExeFilePath = new File(appExtension.sdkDirectory, "build-tools/${buildToolsVersion}/zipalign.exe")
        }

        if (!apkSignConfig.zipalignExeFilePath) {
            throw new GradleException("对齐命令exe文件路径不能为空")
        }

        File zipalignExeFile = new File(apkSignConfig.zipalignExeFilePath)
        if (!zipalignExeFile.exists()) {
            throw new GradleException("对齐命令exe文件不存在.signJarFile=${apkSignConfig.zipalignExeFilePath}")
        }


        if (apkSignConfig.signJarFilePath == null) {
            def appExtension = getAppPluginExtension()
            def buildToolsVersion = apkSignConfig.buildToolsVersion;
            if (buildToolsVersion == null) {
                buildToolsVersion = appExtension.buildToolsVersion;
            }

            apkSignConfig.signJarFilePath = new File(appExtension.sdkDirectory, "build-tools/${buildToolsVersion}/lib/apksigner.jar")
        }
        if (!apkSignConfig.signJarFilePath) {
            throw new GradleException("待签jar文件路径不能为空")
        }

        File signJarFile = new File(apkSignConfig.signJarFilePath)
        if (!signJarFile.exists()) {
            throw new GradleException("签名jar文件不存在.signJarFile=${apkSignConfig.signJarFilePath}")
        }

    }

    def getAppPluginExtension() {
        def appPlugin = project.plugins.getPlugin("com.android.application")
        return appPlugin.extension
    }

    def checkSignApkInfo(ApkSignConfig apkSignConfig) {
        if (!signApkInfo.inputApkFilePath) {
            throw new GradleException("必须设置待签名apk")
        }

        File inputApkFile = new File(signApkInfo.inputApkFilePath)
        if (!inputApkFile.exists()) {
            throw new GradleException("待签名apk文件不存在。${inputApkFile.absolutePath}")
        }

        if (!signApkInfo.outputApkDirPath) {
            throw new GradleException("签名后的apk输出目录不能为空")
        }

        File outputApkDir = new File(signApkInfo.outputApkDirPath)
        if (!outputApkDir.exists()) {
            outputApkDir.mkdirs()
        }

        if (signApkInfo.signingInfo == null) {
            signApkInfo.signingInfo = apkSignConfig.signingInfo
        } else {
            if (!signApkInfo.signingInfo.storeFilePath || signApkInfo.signingInfo.storeFilePath.trim().length() == 0) {
                throw new GradleException("签名文件 storeFilePath 不能为空 ")
            }

            File storeFile = new File(signApkInfo.signingInfo.storeFilePath)
            if (!storeFile.exists()) {
                throw new GradleException("签名文件不存在")
            }

            if (!signApkInfo.signingInfo.storePassword || signApkInfo.signingInfo.storePassword.trim().length() == 0) {
                throw new GradleException("签名文件 storePassword 不能为空 ")
            }

            if (!signApkInfo.signingInfo.keyAlias || signApkInfo.signingInfo.keyAlias.trim().length() == 0) {
                throw new GradleException("签名文件 keyAlias 不能为空 ")
            }

            if (!signApkInfo.signingInfo.keyPassword || signApkInfo.signingInfo.keyPassword.trim().length() == 0) {
                throw new GradleException("签名文件 keyPassword 不能为空 ")
            }
        }

        if (signApkInfo.zipalignExeFilePath == null) {

            if (signApkInfo.buildToolsVersion == null) {
                signApkInfo.zipalignExeFilePath = apkSignConfig.zipalignExeFilePath
            } else {
                apkSignConfig.zipalignExeFilePath = new File(extension.sdkDirectory, "build-tools/${signApkInfo.buildToolsVersion}/zipalign.exe")
            }
        }

        if (!signApkInfo.zipalignExeFilePath) {
            throw new GradleException("找不到对齐命令exe文件路径")
        }

        File zipalignExeFile = new File(signApkInfo.zipalignExeFilePath)
        if (!zipalignExeFile.exists()) {
            throw new GradleException("对齐命令exe文件不存在")
        }


        if (signApkInfo.signJarFilePath == null) {

            if (signApkInfo.buildToolsVersion == null) {
                signApkInfo.signJarFilePath = apkSignConfig.signJarFilePath
            } else {
                signApkInfo.signJarFilePath = new File(extension.sdkDirectory, "build-tools/${extension.buildToolsVersion}/lib/apksigner.jar")
            }

        }

        if (!signApkInfo.signJarFilePath) {
            throw new GradleException("待签jar文件路径不能为空")
        }

        File signJarFile = new File(signApkInfo.signJarFilePath)
        if (!signJarFile.exists()) {
            throw new GradleException("签名jar文件不存在")
        }

    }


    @TaskAction
    public void run() throws Exception {

        ApkSignConfig jiaGuLeGuConfig = ApkSignConfig.getConfig(project)

        checkConfig(jiaGuLeGuConfig)
        checkSignApkInfo(jiaGuLeGuConfig)

        ApkSign jiaGuLeGu = new ApkSign(new File(signApkInfo.signJarFilePath), new File(signApkInfo.zipalignExeFilePath), project.logger)
        jiaGuLeGu.sign(signApkInfo.signingInfo, new File(signApkInfo.inputApkFilePath), new File(signApkInfo.outputApkDirPath))
    }
}

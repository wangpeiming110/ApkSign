package com.zf.plugins.ApkSign


import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

public class ApkSignTask extends DefaultTask {

    private final static String DEFALUT_SIGNING_CONFIG = "debug"

    @Input
    SignApkInfo signApkInfo

    def getAppPluginExtension() {
        def appPlugin = project.plugins.getPlugin("com.android.application")
        return appPlugin.extension
    }

    SigningInfo getSigningConfig(def appExtension, String name) {
        def signingConfig = appExtension["signingConfigs"].find {
            return it['name'] == name
        }

        if (signingConfig == null) {
            return null
        }

        String storeFilePath = null;
        def storeFile = signingConfig['storeFile'];
        if (storeFile) {
            storeFilePath = storeFile.absolutePath
        }
        def storePassword = signingConfig['storePassword']
        def keyAlias = signingConfig['keyAlias']
        def keyPassword = signingConfig['keyPassword']
        def v1SigningEnabled = signingConfig['v1SigningEnabled']
        def v2SigningEnabled = signingConfig['v2SigningEnabled']
        if (v1SigningEnabled == false) {
            project.logger.warn("The default signature does not support V1 signature")
        }
        if (v2SigningEnabled == false) {
            project.logger.warn("The default signature does not support V2 signature")
        }
        return new SigningInfo(storeFilePath, storePassword, keyAlias, keyPassword)
    }

    File geZipalignFile(def sdkDirectory, String buildToolsVersion) {
        return new File(sdkDirectory, "build-tools/${buildToolsVersion}/zipalign.exe")
    }

    File geApksignerFile(def sdkDirectory, String buildToolsVersion) {
        return new File(sdkDirectory, "build-tools/${buildToolsVersion}/lib/apksigner.jar")
    }

    void checkConfig(ApkSignConfig apkSignConfig) {

        def appExtension = getAppPluginExtension()
        if (apkSignConfig.signingInfo == null) {

            if (apkSignConfig.signingName == null) {
                apkSignConfig.signingName = DEFALUT_SIGNING_CONFIG;
            }
            apkSignConfig.signingInfo = getSigningConfig(appExtension, apkSignConfig.signingName)

            if (apkSignConfig.signingInfo == null) {
                throw new GradleException("No ${apkSignConfig.signingName} signature configuration was found")
            }
        }

        if (!apkSignConfig.signingInfo.storeFilePath || apkSignConfig.signingInfo.storeFilePath.trim().length() == 0) {
            throw new GradleException("Signature file path cannot be empty ")
        }

        File storeFile = new File(apkSignConfig.signingInfo.storeFilePath)
        if (!storeFile.exists()) {
            throw new GradleException("The signature file could not be found.${storeFile.absolutePath}")
        }

        if (!apkSignConfig.signingInfo.storePassword || apkSignConfig.signingInfo.storePassword.trim().length() == 0) {
            throw new GradleException("StorPassword cannot be empty ")
        }

        if (!apkSignConfig.signingInfo.keyAlias || apkSignConfig.signingInfo.keyAlias.trim().length() == 0) {
            throw new GradleException("keyAlias cannot be empty")
        }

        if (!apkSignConfig.signingInfo.keyPassword || apkSignConfig.signingInfo.keyPassword.trim().length() == 0) {
            throw new GradleException("keyPassword cannot be empty ")
        }


        if (apkSignConfig.zipalignExeFilePath == null) {

            def buildToolsVersion = apkSignConfig.buildToolsVersion;
            if (buildToolsVersion == null) {
                buildToolsVersion = appExtension.buildToolsVersion;
            }
            apkSignConfig.zipalignExeFilePath = geZipalignFile(appExtension.sdkDirectory, buildToolsVersion).absolutePath
        }

        File zipalignExeFile = new File(apkSignConfig.zipalignExeFilePath)
        if (!zipalignExeFile.exists()) {
            throw new GradleException("zipalignExeFilePath file could not be found.${apkSignConfig.zipalignExeFilePath}")
        }


        if (apkSignConfig.signJarFilePath == null) {

            def buildToolsVersion = apkSignConfig.buildToolsVersion;
            if (buildToolsVersion == null) {
                buildToolsVersion = appExtension.buildToolsVersion;
            }

            apkSignConfig.signJarFilePath = geApksignerFile(appExtension.sdkDirectory, buildToolsVersion).absolutePath
        }

        if (!apkSignConfig.signJarFilePath) {
            throw new GradleException("signJarFilePath file path cannot be empty")
        }


    }


    def checkSignApkInfo(ApkSignConfig apkSignConfig) {

        if (!signApkInfo.inputApkFilePath) {
            throw new GradleException("The pending signature APK must be set")
        }

        File inputApkFile = new File(signApkInfo.inputApkFilePath)
        if (!inputApkFile.exists()) {
            throw new GradleException("The pending signature APK file does not exist。${inputApkFile.absolutePath}")
        }

        if (signApkInfo.outputApkDirPath) {
            File outputApkDir = new File(signApkInfo.outputApkDirPath)
            if (!outputApkDir.exists()) {
                outputApkDir.mkdirs()
            }
        } else {
            signApkInfo.outputApkDirPath = inputApkFile.parentFile
            if (!signApkInfo.outputApkDirPath) {
                throw new GradleException("Please set the output position of the signed apk")
            }
        }


        if (signApkInfo.signingInfo == null) {

            if (signApkInfo.signingName == null) {
                signApkInfo.signingInfo = apkSignConfig.signingInfo
            } else {

                apkSignConfig.signingInfo = getSigningConfig(appExtension, signApkInfo.signingName)
                if (apkSignConfig.signingInfo == null) {
                    throw new GradleException("No ${apkSignConfig.signingName} signature configuration was found")
                }
            }

        }
        if (!signApkInfo.signingInfo.storeFilePath || signApkInfo.signingInfo.storeFilePath.trim().length() == 0) {
            throw new GradleException("storeFilePath cannot be empty ")
        }

        File storeFile = new File(signApkInfo.signingInfo.storeFilePath)
        if (!storeFile.exists()) {
            throw new GradleException("Signature file does not exist.${storeFile.absolutePath}")
        }

        if (!signApkInfo.signingInfo.storePassword || signApkInfo.signingInfo.storePassword.trim().length() == 0) {
            throw new GradleException("storePassword cannot be empty ")
        }

        if (!signApkInfo.signingInfo.keyAlias || signApkInfo.signingInfo.keyAlias.trim().length() == 0) {
            throw new GradleException("keyAlias cannot be empty")
        }

        if (!signApkInfo.signingInfo.keyPassword || signApkInfo.signingInfo.keyPassword.trim().length() == 0) {
            throw new GradleException("keyPassword cannot be empty ")
        }


        if (signApkInfo.zipalignExeFilePath == null) {

            if (signApkInfo.buildToolsVersion == null) {
                signApkInfo.zipalignExeFilePath = apkSignConfig.zipalignExeFilePath
            } else {
                def appExtension = getAppPluginExtension();
                signApkInfo.zipalignExeFilePath = geZipalignFile(appExtension.sdkDirectory, signApkInfo.buildToolsVersion).absolutePath
            }
        }

        File zipalignExeFile = new File(signApkInfo.zipalignExeFilePath)
        if (!zipalignExeFile.exists()) {
            throw new GradleException("zipalignExeFile file does not exist.${zipalignExeFile.absolutePath}")
        }


        if (signApkInfo.signJarFilePath == null) {

            if (signApkInfo.buildToolsVersion == null) {
                signApkInfo.signJarFilePath = apkSignConfig.signJarFilePath
            } else {
                def appExtension = getAppPluginExtension();
                signApkInfo.signJarFilePath = geApksignerFile(appExtension.sdkDirectory, signApkInfo.buildToolsVersion).absolutePath
            }
        }

        File signJarFile = new File(signApkInfo.signJarFilePath)
        if (!signJarFile.exists()) {
            throw new GradleException("The signature jar file does not exist.${signJarFile.absolutePath}")
        }

        if (signApkInfo.isOpenOutputDir == null) {
            signApkInfo.isOpenOutputDir = apkSignConfig.isOpenOutputDir
        }

    }


    @TaskAction
    public void run() throws Exception {

        ApkSignConfig jiaGuLeGuConfig = ApkSignConfig.getConfig(project)

        checkConfig(jiaGuLeGuConfig)
        checkSignApkInfo(jiaGuLeGuConfig)

        ApkSign jiaGuLeGu = new ApkSign(new File(signApkInfo.signJarFilePath),
                new File(signApkInfo.zipalignExeFilePath),
                signApkInfo.isOpenOutputDir,
                project.logger)
        jiaGuLeGu.sign(signApkInfo.signingInfo, new File(signApkInfo.inputApkFilePath), new File(signApkInfo.outputApkDirPath))
    }
}

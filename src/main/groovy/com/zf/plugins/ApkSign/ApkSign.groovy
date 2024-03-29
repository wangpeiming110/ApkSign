package com.zf.plugins.ApkSign

import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.internal.classloader.VisitableURLClassLoader

public class ApkSign {

    private static final String DOT_APK = ".apk";
    Logger logger
    public File signJarDir
    public String signJarName

    public File zipalignFileExeDir
    public String zipalignFileExeName


    boolean isOpenOutputDir;

    ApkSign(File signJarFile, File zipalignExeFile, boolean isOpenOutputDir, Logger logger) {

        this.signJarDir = signJarFile.getParentFile()
        this.signJarName = signJarFile.getName()

        this.zipalignFileExeDir = zipalignExeFile.getParentFile()
        this.zipalignFileExeName = zipalignExeFile.getName()

        this.isOpenOutputDir = isOpenOutputDir;

        this.logger = logger;
    }

    def getFileName(File file) {
        def name = file.getName();
        return name.substring(0, name.lastIndexOf("."))
    }


    private void zipalign(File inputApkFile, File outputApkDirPath) {
        String cmd = "${this.zipalignFileExeName} -v -p 4 ${inputApkFile.absolutePath} ${outputApkDirPath.absolutePath}"
        exec(cmd, this.zipalignFileExeDir)
    }

    private void signInternal(SigningInfo signingInfo, File inputApkFile, File outputApkDirPath) {
        String cmd = "java -jar ${this.signJarName} sign  --ks ${signingInfo.storeFilePath}  --ks-key-alias ${signingInfo.keyAlias}  --ks-pass pass:${signingInfo.storePassword}  --key-pass pass:${signingInfo.keyPassword}  --out ${outputApkDirPath.absolutePath}  ${inputApkFile.absolutePath}"
        exec(cmd, this.signJarDir, { commandline ->
            logger.quiet(commandline.replaceAll("pass:${signingInfo.storePassword}", "pass:XXXXXX").replaceAll("pass:${signingInfo.keyPassword}", "pass:XXXXXX"))

        })
    }

    private void verify1(File inputApkFile) {
        String cmd = "java -jar ${this.signJarName} verify -v ${inputApkFile.absolutePath}"
        exec(cmd, this.signJarDir, null, { type, line ->
            logger.quiet(line)
        })
    }

    private void verify(File inputApkFile) {
        VisitableURLClassLoader loader = this.getClass().getClassLoader();
        loader.addURL(new File(this.signJarDir, this.signJarName).toURL())

        Class aClass = Class.forName("com.zf.plugins.ApkSign.ApkSignVerify");
        def method = aClass.getMethod("verify", File.class);
        ApkVerifierResult result = method.invoke(null, inputApkFile)

        System.out.println("是否已经签名：" + result.verified);
        System.out.println("是否使用v1签名：" + result.verifiedUsingV1Scheme);
        System.out.println("是否使用v2签名：" + result.verifiedUsingV2Scheme);

        if ((!result.verified) || (!result.verifiedUsingV1Scheme) || (!result.verifiedUsingV2Scheme)) {
            throw new GradleException("没有使用v1,v2签名")
        }
    }

    private void openOutputDir(def dir) {

        if (!isWindowSystem()) {
            return;
        }

        String cmd = "explorer.exe ${dir}"
        exec(cmd, this.signJarDir, {}, { type, line ->
        })

    }


    public void sign(SigningInfo signingInfo, File inputApkFile, File outputApkDirPath) {

        File zipalignFile = new File(outputApkDirPath, "${getFileName(inputApkFile)}_align${DOT_APK}")
        if (zipalignFile.exists()) {
            zipalignFile.delete()
        }

        logger.quiet("开始对齐优化apk")
        zipalign(inputApkFile, zipalignFile)

        File signApkFile = new File(outputApkDirPath, "${getFileName(zipalignFile)}_sign${DOT_APK}")
        if (signApkFile.exists()) {
            signApkFile.delete()
        }

        logger.quiet("开始签名apk")
        signInternal(signingInfo, zipalignFile, signApkFile)

        if (zipalignFile.exists()) {
            zipalignFile.delete()
        }

        verify(signApkFile)

        if (isOpenOutputDir) {
            openOutputDir(outputApkDirPath.absolutePath)
        }

    }


    String[] getCmd(String cmd) {
        String[] cmdArr = ["/bin/sh", "-c", cmd]
        if (isWindowSystem()) {
            cmdArr[0] = "cmd"
            cmdArr[1] = "/C"
        }

        return cmdArr
    }

    void exec(cmd, workDir, doFirstClosure, outputClosure) {

        String[] cmdArr = getCmd(cmd);

        if (doFirstClosure) {
            doFirstClosure(cmdArr[2]);
        } else {
            this.logger.quiet(cmdArr[2].toString())
        }

        Process process = Runtime.runtime.exec(cmdArr, null, workDir)
        StreamConsumer infoStream = new StreamConsumer(process.inputStream, 'output', 'utf-8', outputClosure)
        infoStream.start()
        process.waitFor()

        def errText = process.err.getText();
        if (errText != null && errText.trim().length() > 0) {
            throw new GradleException(errText)
        }
    }

    void exec(cmd, workDir) {
        exec(cmd, workDir, { commandLine ->
            this.logger.quiet(commandLine.toString())
        }, { type, text ->
            if (!((text ==~ /\s?/) || (text ==~ /^#.*#$/))) {
                this.logger.quiet("${type} > ${text}")
            }
        })
    }

    void exec(cmd, workDir, doFirstClosure) {
        exec(cmd, workDir, doFirstClosure, { type, text ->
            if (!((text ==~ /\s?/) || (text ==~ /^#.*#$/))) {
                this.logger.quiet("${type} > ${text}")
            }
        })
    }


    static boolean isWindowSystem() {
        return System.getProperty("file.separator") == "\\";
    }

}

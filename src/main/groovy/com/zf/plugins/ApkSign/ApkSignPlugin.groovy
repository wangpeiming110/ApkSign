package com.zf.plugins.ApkSign

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

class ApkSignPlugin implements Plugin<Project> {

    public static final String sPluginExtensionName = "apkSignConfig";

    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw new ProjectConfigurationException("Plugin requires the 'com.android.application' plugin to be configured.", null);
        }
        project.extensions.create(sPluginExtensionName, ApkSignConfig, project);
        createJiaGuTask(project)
    }


    def createJiaGuTask(Project project) {

        project[sPluginExtensionName].items.all { _item ->

            project.tasks.create("apkSign${_item.name.capitalize()}", ApkSignTask) {
                description "apk 签名"
                group "apk sign"
                signApkInfo _item
            }
        }
    }


}


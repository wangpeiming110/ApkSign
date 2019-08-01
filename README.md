# ApkSign
[ ![Download](https://api.bintray.com/packages/zf/maven/ApkSign/images/download.svg) ](https://github.com/903600017/ApkSign/release)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://raw.githubusercontent.com/903600017/ApkSign/master/LICENSE)




ApkSign：Android 签名插件。

ApkSign 为apk提供快速签名，解放双手，实现自动化流程。解决 "美团渠道打包神器"打包后没签名，“腾讯乐固”加固后没签名等各种需要快速签名的应用场景


### Gradle插件使用方式

#### 配置build.gradle

在位于项目的根目录 `build.gradle` 文件中添加 ApkSign插件的依赖， 如下：

```groovy
buildscript {
    dependencies {
        classpath 'com.zf.plugins:ApkSign:1.0.1'
    }
}
```

并在当前App的 `build.gradle` 文件中apply这个插件

```groovy
apply plugin: 'apksign'
```

#### 配置插件(最简易配置)

```groovy
apkSignConfig {
    items {
        ddebug {
      		//需要签名的APK 路径
            inputApkFilePath file("build/outputs/apk/tap_unsign.apk").absolutePath
        }
		// ...... 可以添加更多选项
    }
}
```

#### 插件全部配置
```groovy
apkSignConfig {

//统一配置 优先级低于自定义配置------------start-----------------------

    //可选，默认为false。签名完成后，是否打签名后的apk所在目录，只支持windows
     isOpenOutputDir true
			

    //可选，自定义签名jar包位置
	signJarFilePath new File("E:\\Android\\sdk\\build-tools\\28.0.3\\lib\\apksigner.jar").absolutePath
	
	//可选，自定义apk包对齐命令位置
	zipalignExeFilePath new File("E:\\Android\\sdk\\build-tools\\28.0.3\\zipalign.exe").absolutePath
	
	//可选，自定签名jar包，apk对齐命令所使用的版本，
	buildToolsVersion '28.0.3'

	//可选，自定义签名文件
	signingInfo {
        storeFilePath "sign.jks"
        storePassword "XXXXX"
        keyAlias "XXXXXX"
        keyPassword "XXXXXX"
    }
	
	//可选，使用android项目里，名称为`debug`的签名配置
	signingName 'debug'

//统一配置-------------end----------------------
    items {
	
        release {
		
		//release 自定义配置优先统一配置------------start-----------------------
		
			//可选，默认为统一配置里的值。签名完成后，是否打签名后的apk所在目录，只支持windows
        	isOpenOutputDir true
			
			//可选，自定签名jar包，apk对齐命令所使用的版本，
			buildToolsVersion '28.0.3'
			
			//可选，自定义签名jar包位置
			signJarFilePath new File("E:\\Android\\sdk\\build-tools\\28.0.3\\lib\\apksigner.jar").absolutePath

			//可选，自定义apk包对齐命令位置
			zipalignExeFilePath new File("E:\\Android\\sdk\\build-tools\\28.0.3\\zipalign.exe").absolutePath
			
				//可选，自定义签名文件
			signingInfo {
				storeFilePath "sign.jks"
				storePassword "XXXXX"
				keyAlias "XXXXXX"
				keyPassword "XXXXXX"
			}
			
			//可选，使用android项目里，名称为`debug`的签名配置
			signingName 'debug'
			
	    //release 自定义配置优先统一配置------------end-----------------------
			
			//必选，需要签名的APK 路径
            inputApkFilePath file("build/outputs/apk/tap_unsign.apk").absolutePath
			
			//可选，指定签名apk文件的输出目录，默认为输入apk所在目录
			outputApkDirPath file("build/outputs/apk").absolutePath
			
        }

		// ...... 可以添加更多选项
    }
}
```


**配置项具体解释：**

* 当`signJarFilePath` ,`buildToolsVersion`都配置时,优化级为 `signJarFilePath `> `buildToolsVersion`;当两个配置项都不配置时，默认使用 android项目里的 `buildToolsVersion`。
*  `signJarFilePath` ,`buildToolsVersion`都配置时,优化级为 `signJarFilePath` > `buildToolsVersion`;当两个配置项都不配置时，默认使用 android项目里的 `buildToolsVersion`。
*  `signingInfo` ,`signingName`都配置时,优化级为 `signingInfo` > `signingName`;当两个配置项都不配置时，默认使用 android项目里的默认debug签名。
*  `signingName='release'` 签名信息配置的名称,
                  
	```groovy
	    android {
			signingConfigs {
				release {
					storeFile signingInfo.storeFile
					storePassword signingInfo.storePassword
					keyAlias signingInfo.keyAlias
					keyPassword signingInfo.keyPassword

					v1SigningEnabled true
					v2SigningEnabled true
				}
			}
		}
	```  
	
	**生成apk签名包：**
	
	`./gradlew apkSign${你的签名配置名称(首页字母大小)}  `
	
	![签名配置名称](https://raw.githubusercontent.com/903600017/ApkSign/master/pic/sign_config_name.png)

	 
	 如上面的配置，生成签名包需要执行如下命令：
	 
	 `./gradlew apkSignRelease `


## Q&A
- [输出乱码](https://github.com/903600017/ApkSign/wiki/Terminal-%E8%BE%93%E5%87%BA%E4%B9%B1%E7%A0%81%EF%BC%9F)？

## 技术支持

* Read The Fucking Source Code
* 通过提交issue来寻求帮助
* 联系我们寻求帮助.(QQ群：366399995)

## 贡献代码
* 欢迎提交issue
* 欢迎提交PR


## License

    Copyright 2017 903600017

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

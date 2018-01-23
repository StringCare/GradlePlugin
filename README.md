<p align="center"><img width="10%" vspace="20" src="https://raw.githubusercontent.com/StringCare/AndroidLibrary/develop/sample/src/main/res/mipmap-xxxhdpi/ic_launcher.png"></p>


# String Care Android Plugin
Source code of plugin used in Android Studio at compilation time for strings encryption.

Gradle implementation
------------

root_project/build.gradle
```groovy
// root_project/build.gradle

buildscript {

    ext {
        stringcare_version = '0.4'
    }

    repositories {
        jcenter()
    }

    dependencies {
        classpath "com.stringcare:plugin:$stringcare_version"
    }

}
 
apply plugin: StringCare
 
stringcare {

    debug true   // prints details

    modules {

        sample {
            stringFiles = ['strings.xml',"other_file.xml"]
            srcFolders = ['src/main', "other_folder"]
        }

        // root_folder/sample/src/main/res/.../strings.xml
        // root_folder/sample/src/main/res/.../other_file.xml
        // root_folder/sample/other_folder/res/.../strings.xml
        // root_folder/sample/other_folder/res/.../other_file.xml

        other_module {
            srcFolders = ['src/moduleB']
        }

        // root_folder/other_module/src/moduleB/res/.../strings.xml

        other_module_ {} //

        // root_folder/other_module_/src/main/res/.../strings.xml

    }

}
```


License
-------
    Copyright 2017 Efraín Espada

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


# String Care Android Plugin

Gradle implementation
------------

root_project/build.gradle
```groovy
// root_project/build.gradle

buildscript {

    ext {
        stringcare_version = '0.3'
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

    debug true   // prints detail build variant info

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
# AndroidObfuscatorPlugin

Gradle implementation
------------

root_project/build.gradle
```groovy
buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        // ...
        classpath files('../AndroidObfuscatorPlugin/build/libs/stringobfuscatorplugin-1.0-SNAPSHOT.jar')
        // ...
    }
}

// bottom
apply plugin: com.efraespada.stringobfuscatorplugin.StringObfuscatorPlugin
```
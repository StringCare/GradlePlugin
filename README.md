repo under construction, sorry

# AndroidObfuscatorPlugin

Gradle implementation
------------

root_project/build.gradle
```groovy
apply plugin: com.efraespada.stringobfuscatorplugin.StringObfuscatorPlugin

buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        // ...
        classpath files('../AndroidLibrary/build/libs/stringobfuscatorplugin-1.0-SNAPSHOT.jar')
        // ...
    }
}

```
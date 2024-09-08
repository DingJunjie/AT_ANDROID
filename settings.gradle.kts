pluginManagement {
    repositories {
//        maven { setUrl("https://maven.aliyun.com/repository/public") }
//        // mavenCentral()
////        maven { url 'https://maven.aliyun.com/repository/central' }
//        maven { setUrl("https://maven.aliyun.com/repository/google") }
//        maven { setUrl("https://maven.aliyun.com/repository/jcenter") }
//        maven { setUrl("https://dl.google.com/dl/android/maven2/") }
//        maven { setUrl("https://repo.huaweicloud.com/repository/maven") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
//        maven { setUrl("https://maven.aliyun.com/repository/public") }
//        // mavenCentral()
////        maven { url 'https://maven.aliyun.com/repository/central' }
//        maven { setUrl("https://maven.aliyun.com/repository/google") }
//        maven { setUrl("https://maven.aliyun.com/repository/jcenter") }
//        maven { setUrl("https://dl.google.com/dl/android/maven2/") }
//        maven { setUrl("https://repo.huaweicloud.com/repository/maven") }
        google()
        mavenCentral()
        mavenLocal()

    }
}

rootProject.name = "bit_android"
include(":app")
 
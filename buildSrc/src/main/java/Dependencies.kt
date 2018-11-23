object Versions {
    val compileSdkVersion = 28
    val minSdkVersion = 15
    val targetSdkVersion = 28

    val kotlin = "1.3.10"
    val androidSupport = "28.0.0"
    val lifecycle = "1.1.1"
}

object Deps {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    val lifecycle = "android.arch.lifecycle:extensions:${Versions.lifecycle}"
    val lifecycle_java8 = "android.arch.lifecycle:common-java8:${Versions.lifecycle}"
}
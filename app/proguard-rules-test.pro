# Proguard/R8 shouldn't mess with androidTest files
-dontskipnonpubliclibraryclassmembers
-dontoptimize
-dontobfuscate
-dontshrink

# Extra rules to keep stuff needed for tests

-dontwarn com.google.errorprone.annotations.MustBeClosed
-dontwarn javax.lang.model.element.Modifier

-keepclassmembers class si.inova.androidarchitectureplayground.MyApplication {
    public getApplicationComponent();
}
-keep class si.inova.kotlinova.core.outcome.CoroutineResourceManager {
    *;
}
-keep class si.inova.kotlinova.core.test.** {
    *;
}

-keep class kotlin.** {
    *;
}
-keep class kotlinx.coroutines.** {
    *;
}
-keepnames class kotlinx.serialization.internal.** {
    *;
}
-keep class androidx.** {
    *;
}
-keep class dispatch.** {
    *;
}
-keep class okhttp3.** {
    *;
}
-keep class okio.** {
    *;
}

-keep class si.inova.androidarchitectureplayground.instrumentation.** {
    *;
}

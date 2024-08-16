# Uncomment this to debug tests. Comment it back to ensure that tests also test whether obfuscation breaks anything
# -dontobfuscate

# Proguard rules needed for instrumented tests to pass
# (since instrumented tests use some code that would otherwise be optimized out)

-dontwarn com.google.errorprone.annotations.MustBeClosed

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

-keep class dagger.** {
    *;
}
-keep class com.deliveryhero.** {
    *;
}
-keep @dagger.** class * {
    *;
}
-keep class * extends dagger.internal.Factory {
    *;
}

-keep class si.inova.androidarchitectureplayground.instrumentation.** {
    *;
}

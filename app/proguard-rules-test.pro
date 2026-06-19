# Uncomment for testing
# -dontobfuscate


# Extra rules to keep stuff needed for tests

-dontwarn com.google.errorprone.annotations.MustBeClosed
-dontwarn javax.lang.model.element.Modifier

-keep class si.inova.androidarchitectureplayground.MyApplication {
    *;
}

-keep class si.inova.androidarchitectureplayground.MainApplicationGraph {
    *;
}

-keep class si.inova.kotlinova.core.outcome.CoroutineResourceManager {
    *;
}
-keep class si.inova.kotlinova.** {
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

-keep class si.inova.androidarchitectureplayground.MyApplication {
    *;
}

-keep class si.inova.androidarchitectureplayground {
    *;
}

-keep class si.inova.androidarchitectureplayground.**$*Metro* {
    *;
}

-keep  @dev.zacsweers.metro.ContributesTo class * {
    *;
}

-keep class dev.zacsweers.metro.** {
    *;
}

# Metro factories
-keep class si.inova.androidarchitectureplayground.**Impl {
    *;
}
-keep class si.inova.androidarchitectureplayground.**Impl$* {
    *;
}

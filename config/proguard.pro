-libraryjars  <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)

-printmapping aegis.promap

-optimizationpasses 3

-keepattributes Signature,Exceptions,InnerClasses,
                PermittedSubclasses,EnclosingMethod,
                Deprecated,SourceFile,LineNumberTable

-dontwarn kotlin.**
-dontwarn org.jetbrains.annotations.**
-dontwarn com.google.gson.**
-dontwarn net.minecraft.**
-dontwarn com.mojang.**
-dontwarn net.fabricmc.**
-dontwarn ca.stellardrift.colonel.**

-keep class com.github.p03w.aegis.internal.AegisInit {
    public void onInitialize();
}

-keep public class com.github.p03w.aegis.internal.types.** {
    public !static *;
}

-keep class com.github.p03w.aegis.AegisKt {
    *;
}

-keep class com.github.p03w.aegis.AegisCommandBuilder {
    public *** get*();
    public *** set*(...);
    public ** build();
    private *;
    public *** requires(...);
}

-keep class ca.stallardrift.colonel.**

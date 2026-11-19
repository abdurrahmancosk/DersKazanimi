# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
#
# This file is used to configure ProGuard when your project is built in release mode.
# By default, it does not get comments in which case the configuration is used as-is.
# If this file is excluded from version control, you can specify a relative path to the
# proguard configuration file, which will be used to get the default ProGuard options.
#
#
# If you want to enable resource shrinking, uncomment the following lines in your
# project.properties file (located in the root of your project directory):
# android.enableResourceShrinking=true
# android.enableShrinkResources=true
#
# and add the following lines here:
# -keep class **.R$* {
#     <fields>;
# }

# Keep line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Keep the line numbers of the original source code
-renamesourcefileattribute SourceFile

# Keep all public classes
-keep public class * {public <methods>;}

# Keep Room database entities
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }

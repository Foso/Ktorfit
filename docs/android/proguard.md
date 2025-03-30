If you use Ktorfit as a dependency in an Android project which uses R8 as a default compiler you don’t have to 
do anything. The specific rules are 
[already bundled](../../ktorfit-lib-core/src/jvmMain/resources/META-INF/proguard/ktorfit.pro) into the JAR 
which can be interpreted by R8 automatically.
open-source Java class file

- shrinker - removes unused classes, fields, and methods from codebase
- optimizer - optimizes the bytecode of application
- obfuscator - renames classes, methods, and fields to obscure original names

enable ProGuard in Gradle

```groovy
proguardFiles getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
```

ensures that `myMethod` will not be obfuscated or removed

```Java
-keep

class com.example.MyClass{
public void method();
        }
```

disable inlining optimizations which can sometimes negatively impact

```Java
-optimizations!method/inlining/
```

do not rename classes that extends MyClass

```Java
-keepnames class extends*com.example.MyClass{*;}
```

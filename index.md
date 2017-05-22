Compilation
-----------
In order to build the API you just need to run this command in your system's terminal. 
```sh
gradle build
```

If you don't have [Gradle] installed in your system then run this command instead:
```sh
# If you are using Linux/Unix
./gradlew build

# If you are using Windows
gradlew build 
```

The compiled JAR file will be place on the `./build/libs` after a successful execution

[Gradle]: https://www.gradle.org/

Example Plugins
------------

### Kotlin
```kotlin
import org.platestack.api.plugin.PlatePlugin
import org.platestack.api.plugin.annotation.*

/**
 * Declare this as a plate plugin and mark it dependent to "my_java_plugin" accepting any version starting from 0.1.0
 */
@Plate("my_kotlin_plugin", "My Kotlin Plugin", Version(0,1,0,"SNAPSHOT"),
        Relation(RelationType.REQUIRED_BEFORE, ID("my_java_plugin"), VersionRange(min = Version(0,1,0))))
object MyKotlinPlugin: PlatePlugin() {

    /**
     * The init block is OPTIONAL, it was placed here just to document
     * that it will be called on construction phase the dependencies
     * and the platform will NOT be ready for operations!
     */
    init { }
}
```

### Java
```java
import org.platestack.api.plugin.PlatePlugin;
import org.platestack.api.plugin.annotation.*;

/**
 * Declare this as a plate plugin and mark it dependent to "my_kotlin_plugin" accepting any version starting from 0.1.0
 */
@Plate(id = "my_java_plugin", name = "My Java Plugin", version = @Version("0.1.0-SNAPSHOT"),
        relations = @Relation(type = RelationType.REQUIRED_AFTER, id = @ID("my_kotlin_plugin"),
                versions = @VersionRange(min = @Version("0.1.0-SNAPSHOT"))))
public class MyJavaPlugin extends PlatePlugin
{
    /**
     * Make sure you have a public constructor. It will be called on construction phase,
     * the dependencies and the platform will NOT be ready for operations!
     */
    public MyJavaPlugin(){}
}
```

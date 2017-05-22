PlateStack API
==============
An extended plugin API for Minecraft servers which aims to 
provide new functionality which are not available yet available
on Minecraft or the platform which the API is implemented.

Check the [changelog](CHANGELOG.md) file to be aware of notable changes.

Creating a Plugin
-----------------
To create a plugin you must add the PlateAPI JAR to your project, that can be done manually by
[compiling the code](#compilation) and configuring your IDE or automatically using gradle or maven.

If you are using Kotlin you must use the version kotlin-gradle-plugin version then PlateAPI.

We are using the version `1.1.2-4` on this commit. **DO NOT** embed the Kotlin STD Lib to your plugin, we are already doing it for you.

### Using gradle
Just add the following code snippet to your `build.gradle` file:
```gradle
repositories {
    jcenter() // Most dependencies are there
    maven { url 'http://dl.bintray.com/kotlin/kotlinx' } // Necessary to interact with immutable collections
    maven { url 'https://jitpack.io' } // Who needs a maven repository when we have jitpack and a github repository? :)
}

dependencies {
    compile 'com.github.PlateStack:PlateAPI:-SNAPSHOT' // All necessary dependencies will be grabbed automatically
}
```

This snippet can be used both by Kotlin and Java projects.

**DO NOT** add `kotlin-stdlib` to your dependencies, the correct version will be added automatically for you.

### Using maven
Add the following repository to your `pom.xml` file:
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

Now add this dependency:
```xml
	<dependency>
	    <groupId>com.github.PlateStack</groupId>
	    <artifactId>PlateAPI</artifactId>
	    <version>master-SNAPSHOT</version>
	</dependency>
```

This snippet can be used both by Kotlin and Java projects.

**DO NOT** add `kotlin-stdlib` to your dependencies, the correct version will be added automatically for you.

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

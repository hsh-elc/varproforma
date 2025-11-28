## Gradle Build

The dependencies are already there in a local repository (see folder `maven-repository`). 

You can build `jar`-file with:
```
bash gradlew clean build -x test
```

The jar file can be found in `build/libs`. It contains a pom.xml with all dependencies.

## Gradle Build

Before build, download and install dependencies for ProFormA java libraries from github:

```bash
./mvnInstallProformaDependenciesFromGithub.sh
```

Then you can build the `jar`-file with:

```
bash gradlew clean build -x test
```

The jar file can be found in `build/libs`. It contains a pom.xml with all dependencies.

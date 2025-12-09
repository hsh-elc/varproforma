# Varproforma #

A java binding library for the XML exchange format for variable programming exercises.

Find more documentation here: [`https://github.com/ProFormA/varproformaxml`](https://github.com/ProFormA/varproformaxml)

## License ##

2025 ZLB-ELC Hochschule Hannover <elc@hs-hannover.de>

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not,
see <http://www.gnu.org/licenses/>.


## Installation ##

You can download the current release and install it in your local maven repository with the command line tool:

```bash
mvnInstallVarproformaDependenciesFromGithub.sh
```

Help for the tool is available:

```bash
mvnInstallVarproformaDependenciesFromGithub.sh -h
```


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

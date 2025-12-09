#!/bin/bash

set -e # exit on first error

MVNOPTS=""
CURLOPTS=""
VERBOSE="1"
VPFLIBVER=1.3.1
TMPDIR=/tmp

unameOut="$(uname -s)"
case "${unameOut}" in
    CYGWIN*)    TMPDIRWIN=`cygpath -w ${TMPDIR}`;;
    *)          TMPDIRWIN=${TMPDIR}
esac


while getopts 'qr:h' opt; do
  case "$opt" in
    q)
      MVNOPTS="-q"
      CURLOPTS="-s"
      VERBOSE="0"
      ;;

    r)
      arg="$OPTARG"
      VPFLIBVER=${OPTARG}
      ;;
   
    ?|h)
      echo "This script downloads a varproforma java library release from github and"
      echo "installs it to your local maven repository."
      echo "All releases can be found here:"
      echo "  https://github.com/hsh-elc/varproforma/releases"
      echo ""
      echo "Usage: $(basename $0) [-q] [-r release]"
      echo "  -h            help"
      echo "  -q            quiet"
      echo "  -r release    select release. Default release is $VPFLIBVER"
      exit 1
      ;;
  esac
done
shift "$(($OPTIND -1))"



declare -a arrayVarproformaDownloads=(\
  varproforma-${VPFLIBVER}.pom \
  varproforma-${VPFLIBVER}.jar  \
)


# Working directory:
WDIR="$TMPDIR/mvnInstallVarproformaDependenciesFromGithub"
mkdir -p "$WDIR"
WDIRWIN="$TMPDIRWIN/mvnInstallVarproformaDependenciesFromGithub"


echoline() {
    local text=$1
    if [ $VERBOSE -ne 0 ]; then
        echo "-------------------------------------------------------------------------------------"
        echo $text
        echo "-------------------------------------------------------------------------------------"
    fi
}

download() {
    local file=$1
    local url=$2
    echoline "   downloading from $url to $WDIR/$file"
    curl $CURLOPTS -L \
        -o "$WDIR/$file" \
        $url
}

downloadVarproforma() {
    local file=$1
    local url="https://github.com/hsh-elc/varproforma/releases/download/v${VPFLIBVER}/$file"
    download "$file" "$url"
}

downloadProforma() {
    local file=$1
    local url="https://github.com/hsh-elc/proforma/releases/download/v${PFLIBVER}/$file"
    download "$file" "$url"
}

deploy() {
    local file=$1
    echoline "   mvn install $file"

    extension="${file##*.}"
    filename="${file%.*}"

    mvn $MVNOPTS install:install-file \
      -Dfile="$WDIRWIN/$file" \
      -DpomFile="$WDIRWIN/$filename.pom" 
}




for i in "${arrayVarproformaDownloads[@]}"
do
    downloadVarproforma "$i"
    deploy "$i"
done


PFLIBVER=`mvn dependency:tree "-Dincludes=proforma:*:*:*" -f $WDIRWIN/varproforma-${VPFLIBVER}.pom | grep "proforma:proformaxml-2-1" | sed -e "s#^.*:\(.*\):compile#\1#g" | tr -d '\r\n'`

echoline "Downloading proforma libs version $PFLIBVER"

declare -a arrayProformaDownloads=(\
  proforma-${PFLIBVER}.pom \
  proformaxml-${PFLIBVER}.pom \
  proformaxml-2-1-${PFLIBVER}.pom  \
  proformautil-${PFLIBVER}.pom  \
  proformautil-2-1-${PFLIBVER}.pom  \
  proformaxml-${PFLIBVER}.jar \
  proformaxml-2-1-${PFLIBVER}.jar  \
  proformautil-${PFLIBVER}.jar  \
  proformautil-2-1-${PFLIBVER}.jar  \
)


for i in "${arrayProformaDownloads[@]}"
do
    downloadProforma "$i"
    deploy "$i"
done


# cleanup
if [ -d "$WDIR" ]; then
  rm -rf $WDIR
fi

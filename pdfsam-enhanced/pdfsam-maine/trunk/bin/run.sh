#!/bin/sh
### ====================================================================== ###
##                                                                          ##
##  pdfsam Bootstrap Script                                                  ##
##                                                                          ##
### ====================================================================== ###

warn() {
    echo "${PROGNAME}: $*"
}

die() {
    warn $*
    exit 1
}

DIRNAME="`dirname $0`"
DIRNAME="${DIRNAME%bin}"
DIRNAME="${DIRNAME%/}"

# I'm already in the pdfsam dir
if [ "x$DIRNAME" = "x" ]; then
	PDFSAMJAR="@PDFSAM_JAR_NAME.jar"
else	
	if [ "x$DIRNAME" = "x." ]; then
		PDFSAMJAR="../@PDFSAM_JAR_NAME.jar"
	else	
		PDFSAMJAR="$DIRNAME/@PDFSAM_JAR_NAME.jar"
	fi
fi

# Setup the classpath
if [ ! -f "$PDFSAMJAR" ]; then
    die "Missing required file: $PDFSAMJAR"
fi

PDFSAM_CLASSPATH="$PDFSAMJAR"

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
	JAVA="$JAVA_HOME/bin/java"
    else
	JAVA="java"
    fi
fi

# Setup pdfsam memory properties
JAVA_OPTS="-Xmx256m"

# Display our environment
echo "========================================================================="
echo ""
echo " pdfsam"
echo ""
echo "  JAVA: $JAVA"
echo ""
echo "  JAVA_OPTS: $JAVA_OPTS"
echo ""
echo "  CLASSPATH: $PDFSAM_CLASSPATH"
echo ""
echo "========================================================================="
echo ""

# Execute the JVM in the foreground
      "$JAVA" $JAVA_OPTS \
         -classpath "$PDFSAM_CLASSPATH" \
         org.pdfsam.guiclient.GuiClient "$@"

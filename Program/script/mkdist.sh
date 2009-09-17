#!/usr/bin/env bash

IFS="
"

PROJECT=JPhotoTagger
THIS_PROJECT_DIR=${PROJECTDIR}/${PROJECT}/Program
ZIPFILE=${INTERNETSITE_LOCAL_DIR}/fotografie/download/JPhotoTagger.zip
SCRIPT_DIR=${THIS_PROJECT_DIR}/script
APP_VERSION_FILE=${THIS_PROJECT_DIR}/src/de/elmar_baumann/jpt/app/AppInfo.java
HTML_PROJECT_FILE=${INTERNETSITE_LOCAL_DIR}/fotografie/tipps/computer/lightroom/imagemetadataviewer.html
TMP_FILE=${HOME}/tmp/$(basename $0)-$$

function make_complete_distribution() {
    cd $THIS_PROJECT_DIR
    rm -f $ZIPFILE
    rm -rf JPhotoTagger/scripts
    mkdir JPhotoTagger/scripts
    cp -a $SCRIPT_DIR/embedxmp.sh JPhotoTagger/scripts
    cp -a $SCRIPT_DIR/thumbnail2stdout.sh JPhotoTagger/scripts
    cp -a $SCRIPT_DIR/thumbnail2stdout_fd.sh JPhotoTagger/scripts
    cp -a $SCRIPT_DIR/rotatejpg.sh JPhotoTagger/scripts
    cp res/user/Manual_de.pdf JPhotoTagger
    zip  $ZIPFILE \
        JPhotoTagger/JPhotoTagger.jar \
        JPhotoTagger/lib/* \
        JPhotoTagger/scripts/embedxmp.sh \
        JPhotoTagger/scripts/rotatejpg.sh \
        JPhotoTagger/scripts/thumbnail2stdout_fd.sh \
        JPhotoTagger/scripts/thumbnail2stdout.sh \
        JPhotoTagger/Manual_de.pdf
        
}

function set_version_to_html_project_file() {
	echo Aktualisiere Versionsinfo in Projektseite ...
    version=$(grep APP_VERSION ${APP_VERSION_FILE} \
        | sed 's%.*"\([0-9].*\.[0-9].*\.[0-9].*\)".*%\1%')
    rfc_date=$(date +"%Y-%m-%dT%H:%M:%S%:z")
    sed -e "s%\(<span class=\"version\">\).*\(</span>\)%\1$version\2%" \
	-e "s%\(<meta name=\"date\" content=\"\)[0-9].*[0-9]\(\" />\)%\1$rfc_date\2%" \
	$HTML_PROJECT_FILE \
     > $TMP_FILE
    mv $TMP_FILE $HTML_PROJECT_FILE
}

umask 0022
cd $THIS_PROJECT_DIR
make_complete_distribution
set_version_to_html_project_file
${INTERNETSITE_LOCAL_DIR}/cgi-bin/skripte/updateprogramrssfeeds.pl

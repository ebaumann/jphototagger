#!/bin/sh

cd $PROJECTDIR/JPhotoTagger/main-repository/DeveloperSupport/script \
	&& /usr/local/opt/netbeans/java/ant/bin/ant -v -buildfile scripts.xml dist-upload

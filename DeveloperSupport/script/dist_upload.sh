#!/bin/sh

cd $PROJECTDIR/JPhotoTagger/Support/script \
	&& /usr/local/opt/netbeans/java/ant/bin/ant -v -buildfile scripts.xml dist-upload

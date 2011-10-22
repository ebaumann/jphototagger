#!/bin/sh

ROOT_DIR=$PROJECTDIR/JPhotoTagger/main-repository

for dir in $(find $ROOT_DIR -type d ! -path '*/test/*')
do
	for bf in $(find $dir -maxdepth 1 -name 'Bundle.properties')
	do
		lines_de=$(cat $dir/Bundle.properties | wc -l)
		lines_en=$(cat $dir/Bundle_en.properties | wc -l)
		if [ $lines_de -ne $lines_en ]
		then
			echo $dir
			echo "        Bundle.properties     $lines_de"
			echo "        Bundle_en.properties  $lines_en"
		fi
	done
done

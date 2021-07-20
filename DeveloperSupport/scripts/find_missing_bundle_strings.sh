#!/bin/sh

ROOT_DIR=$PROJECTDIR/JPhotoTagger/main-repository

for dir in $(find $ROOT_DIR -type d ! -path '*/test/*' ! -path '*/build/*')
do
	for source_file in $(find $dir -maxdepth 1 -name '*\.java')
	do
		grep -q 'Bundle.getString' $source_file
		if [ $? -eq 0 ]
		then
            while read line
            do
                echo $line | grep -q 'NOI18N'
                if [ $? -ne 0 ]
                then
                    echo $line | grep -q 'Bundle.getString'
                    if [ $? -eq 0 ]
                    then
                        key=$(echo $line | sed 's%.*"\(.*\)".*%\1%')
                        if [ -z $key ]
                        then
                            echo -e "Trouble getting key for Bundle.getString in\n\t'$source_file'"
                        else
                            dir=${source_file%/*}
                            package=$(echo $dir | sed -e "s%$ROOT_DIR/.*/src/%%" -e "s%/%.%g")
                            java_file=${source_file##*/}
                            source_path=$(echo $package.$java_file | sed 's%\.java$%%')
                            ls $dir/Bundle*properties 1>&2 > /dev/null
                            if [ $? -ne 0 ]
                            then
                                echo -e "Missing Bundle for\n\t'$source_path'\n\tuses Key '$key'"
                            else
                                for bundle in $dir/Bundle*properties
                                do
                                    grep -q "$key=" $bundle
                                    if [ $? -ne 0 ]
                                    then
                                        bundle_file=${bundle##*/}
                                        echo -e "Missing Key '$key'\n\tin '$bundle_file'\n\tused in '$source_path'"
                                    fi
                                done
                            fi
                        fi
                    fi
                fi
            done < $source_file
		fi
	done
done

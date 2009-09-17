#!/usr/bin/env bash
#
# Author: Elmar Baumann
# Date  : 2009/07/13
#
# Finds unused kyes of a property file. Searches both projects: imv + jsl
#
################################################################################

IFS="
"

JSL_SRC=${PROJECTDIR}/JavaStandardLibrary/src
IMV_SRC=${PROJECTDIR}/JPhotoTagger/Program/src
JSL_PROP_FILE=${JSL_SRC}/de/elmar_baumann/lib/resource/properties/Bundle.properties
IMV_PROP_FILE=${IMV_SRC}/de/elmar_baumann/imv/resource/properties/Bundle.properties


function print_unused_keys() {
    local src=$1
    local prop_file=$2
    echo "Searching ${src}..."
    for line in $(cat ${prop_file} | sort)
    do
        count=0
        key=$(echo $line | cut -d= -f1)
        for filename in $(find ${src} -type f -name '*\.java')
        do
            grep -qw "${key}" ${filename}
            if [ $? -eq 0 ]
            then
                count=$((count + 1))
            fi
        done
        if [ $count -lt 1 ]
        then
            echo "Key ${key} isn't in any file!"
        fi
    done
}

print_unused_keys ${JSL_SRC} ${JSL_PROP_FILE}
print_unused_keys ${IMV_SRC} ${IMV_PROP_FILE}

#!/usr/bin/env bash
#
# Author: Elmar Baumann
# Date  : 2009/09/03
#
# Finds in a property file missing keys. Searches both projects: imv + jsl
#
################################################################################

IFS="
"

JSL_SRC=${PROJECTDIR}/Lib/src
IMV_SRC=${PROJECTDIR}/JPhotoTagger/Program/src
JSL_PROP_FILE=${JSL_SRC}/de/elmar_baumann/lib/resource/properties/Bundle.properties
IMV_PROP_FILE=${IMV_SRC}/de/elmar_baumann/imv/resource/properties/Bundle.properties


function print_unused_keys() {
    local src=$1
    local prop_file=$2
    echo "Searching ${src}..."
    for filename in $(find ${src} -type f -name '*\.java')
    do
        for key in $(cat ${filename} | print_property_keys.pl)
        do
            grep -q "$key" ${prop_file}
            if [ $? -ne 0 ]
            then
                echo "'${key}' is not in the properties file"
            fi
        done
    done
}

print_unused_keys ${JSL_SRC} ${JSL_PROP_FILE}
print_unused_keys ${IMV_SRC} ${IMV_PROP_FILE}

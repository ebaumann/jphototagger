#!/usr/bin/env bash
#
# Author: Elmar Baumann
# Date  : 2009/07/13
#
# Finds lines with strings not commented with "// NOI18N".
# Searches both projects: imv + jsl
#
################################################################################

IFS="
"

JSL_SRC=${PROJECTDIR}/Lib/src
IMV_SRC=${PROJECTDIR}/JPhotoTagger/Program/src
COMMENT_PATTERN='// NOI18N'

function ltrim() {
    echo "$1" | sed -e "s/^ *//";
}

function print_uncommented_keys() {
    local src=$1
    echo "Searching ${src}..."
    for filename in $(find ${src} \
        -type f \
        -name '*\.java' \
        ! -path '*/org/jdesktop/swingx/*' \
        | sort)
    do
        found=0
        lines=$(grep '".*"' $filename)
        for line in ${lines}
        do
            line=$(ltrim "${line}")
            line=$(echo $line \
                | grep -v SuppressWarnings \
                | grep -v '^//' \
                | grep -v '^/\*' \
                | grep -v '^\*')
            if [ ${#line} -gt 0 ]
            then
                echo $line | grep -qv "${COMMENT_PATTERN}"
                if [ $? -eq 0 ]
                then
                    found=$((found + 1))
                fi
            fi
        done
        if [ $found -gt 0 ]
        then
            echo "$(echo ${filename} | sed "s%^${src}/%%") has ${found} uncommented lines"
        fi
    done
}

print_uncommented_keys ${JSL_SRC}
print_uncommented_keys ${IMV_SRC}

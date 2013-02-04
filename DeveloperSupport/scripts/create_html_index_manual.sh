#!/usr/bin/env bash
#
# Author: Elmar Baumann <eb@elmar-baumann.de>
# Date  : 2009-09-24
#
# Creates a language specific HTML index page of the user manual.
#
# Syntax: $0 <title>
# e.g create_html_index_manual.sh en "Photo Tagger User Manual"
#
################################################################################
# Validating input
################################################################################

. $PROJECTDIR/JPhotoTagger/main-repository/DeveloperSupport/script/functions.sh || exit 1

LANG="$1"
TITLE="$2"

if [ -z $LANG ]
then
    echo "Language code is not defined (param 1)!"
    exit 1
fi

if [ -z "${TITLE}" ]
then
    echo "Title is not defined (param 2)!"
    exit 1
fi

################################################################################
# Constants
################################################################################

# Project's root directory. Please define the environment variable PROJECTDIR
# which is the parent directory of "JPhotoTagger" (create a soft link
# "JPhotoTagger" below that directory if You called it different)
PROJECT_DIR="${PROJECTDIR}/JPhotoTagger"

# Directory of the language specific documentation directory
DOC_DIR="${PROJECT_DIR}/main-repository/Program/src/org/jphototagger/program/resource/doc/${LANG}"

# Name of the index file
INDEX_FILE=index.html

# Adding current time to the title
TITLE="$TITLE, $(date +"%Y-%m-%d %H:%M:%S %:z")"

################################################################################
# Validating Constants
################################################################################

check_exists_dir  "${DOC_DIR}"

################################################################################
# Functions
################################################################################

function html_head() {
cat << EOF
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>${TITLE}</title>
</head>
<body>
<h1>${TITLE}</h1>
<p>Diese Seite wurde automatisch erzeugt. Die Seiten sind in der richtigen
Reihenfolge, jedoch nicht durch Überschriften gegliedert wie in der
Programmhilfe.</p>
EOF
}

function create_index_file() {
    html_head
    echo "<ul>"
    anchor=0
    for line in $(cat contents.xml)
    do
        file=
        title=

        echo "$line" | grep -q '<url>'
        if [ $? -eq 0 ]
        then
            file=$(echo "$line" | sed 's%.*<url>\(.*\)</url>.*%\1%')
            anchor=1
        fi

        echo "$line" | grep -q '<title>'
        if [ $? -eq 0 ]
        then
            title=$(echo "$line" | sed 's%.*<title>\(.*\)</title>.*%\1%')
        fi

        if ! [ -z $file ]
        then
            echo -n "    <li><a href=\"$file\">"
        fi
        if ! [ -z $title ] && [ $anchor -eq 1 ]
        then
            echo "$title</a></li>"
            anchor=0
         fi
    done
    echo -e "</ul>\n</body>\n</html>"
}

################################################################################
# Start
################################################################################

IFS="
"
cd ${DOC_DIR}
create_index_file > ${INDEX_FILE}

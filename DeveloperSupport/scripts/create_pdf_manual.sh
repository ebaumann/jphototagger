#!/usr/bin/env bash
#
# Author: Elmar Baumann <eb@elmar-baumann.de>
# Date  : 2009-08-07
#
# Creates the language specific PDF user manual from the language specific
# HTML documentation within the source code used for online help.
#
# Uses html2ps to create a temporary PostScript version and ps2pdf to create
# a PDF version of the PostScript version. Also uses standard Unix programs
# like find and sed. Also uses iconv to convert the UTF-8 HTML help pages into
# ISO-8859-1.
#
# Syntax: $0 <ISO 2 character language code> <title>
# e.g create_pdf_manual.sh en "Photo Tagger User Manual"
#
################################################################################
# Validating input
################################################################################

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

# Target directory of the generate PDF user manual used by mkdist.sh for the ZIP
# archive and InnoSetup script JPhotoTagger.iss
PDF_DOC="${PROJECT_DIR}/main-repository/dist_files/manual/Manual_${LANG}.pdf"

# Language specific configuration file for html2ps
HTML2PS_CONF="${PROJECT_DIR}/main-repository/DeveloperSupport/conf/html2ps_userdoc_${LANG}.conf"

# Temporary directory, if not exists please create it under Your home directory
TMP_DIR="${HOME}/tmp"

# Name of the temporary single HTML doc file
SINGLE_HTML_FILE=doc.html

# Adding current time to the manual
TITLE="$TITLE, $(date +"%Y-%m-%d %H:%M:%S %:z")"

################################################################################
# Validating Constants
################################################################################

check_exists_dir  "${DOC_DIR}"
check_exists_file "${HTML2PS_CONF}"
check_can_write  "${TMP_DIR}"

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
EOF
}

function single_html_file() {
    html_head
    for html_file in $(cat contents.xml \
        | grep '<url>' \
        | sed \
            -e 's%.*<url>%%' \
            -e 's%</url>.*%%')
    do
        start=$(($(grep -n '<body>' ${html_file} | cut -d: -f 1) + 1))
        end=$(($(grep -n '</body>' ${html_file} | cut -d: -f 1) - 1))
        sed -n -e "${start},${end} p" ${html_file}
    done
}

function to_pdf() {
    unique_name_part=$(basename $0).$$
    ps_file=${TMP_DIR}/${unique_name_part}.ps
    iso_8859_1_html_file=doc_iso_8859_1.html

    # html2ps doesn't support UTF-8 yet
    iconv \
        --from-code=UTF-8 \
        --to-code=ISO-8859-1 \
        ${SINGLE_HTML_FILE} \
        > ${iso_8859_1_html_file}

    html2ps \
        -f ${HTML2PS_CONF} \
        -o ${ps_file} \
        ${iso_8859_1_html_file}

    ps2pdf ${ps_file} ${PDF_DOC}

    rm ${ps_file}
    rm ${iso_8859_1_html_file}
    rm ${SINGLE_HTML_FILE}
}

################################################################################
# Start
################################################################################

IFS="
"
cd ${DOC_DIR}
single_html_file > ${SINGLE_HTML_FILE}
to_pdf

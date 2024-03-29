#!/usr/bin/env bash
#
# Datum: 2009/06/07
# Autor: Elmar Baumann <eb@elmar-baumann.de>
#
# Aufruf: embedxmp.sh <Bilddatei>
#
# Bettet in Bilddateien XMP-Metadaten ein, die in XMP-Filialdateien stehen. Eine
# XMP-Filialdatei ist im gleichen Verzeichnis wie die Bilddatei und hat den
# gleichen Basisnamen, die Endung heißt ".xmp". Heißt ein Bild "Rose.jpg", so
# heißt seine Filialdatei "Rose.jpg".
#
# Voraussetzungen: Bash, basename, exiftool-Executable
#                  (http://www.sno.phy.queensu.ca/~phil/exiftool/). Es ist die
#                  Konstante EXIFTOOL anzupassen: Der Pfad zur ausführbaren
#                  Datei "exiftool".
#
################################################################################

IFS="
"

THIS=$(basename $0)
EXIFTOOL=/usr/bin/exiftool
EXIF_TOOL_BACKUP_SUFFIX='_original'
TIMESTAMP_FILE=${HOME}/tmp/${THIS}.$$
EXIT_VALUE=0

if ! [ -x $EXIFTOOL ]
then
    echo "${THIS}: Das Program ${EXIFTOOL} existiert nicht (in diesem Verzeichnis) oder ist nicht ausführbar!" 1>&2
    exit 1
fi

if [ -z $1 ]
then
    echo "${THIS}: Bilddateiname fehlt (Parameter 1)!" 1>&2
    exit 1
fi

IMAGE_FILE=$1

if ! [ -f $IMAGE_FILE ]
then
    echo "${THIS}: Bilddatei '${IMAGE_FILE}' existiert nicht!" 1>&2
    exit 1
fi

touch $TIMESTAMP_FILE
if [ $? -ne 0 ]
then
    echo "${THIS}: Temporäre Datei '${TIMESTAMP_FILE}' kann nicht erzeugt werden!" 1>&2
    exit 1
fi

SIDECAR_FILE=${IMAGE_FILE%*.*}.xmp

if ! [ -f $SIDECAR_FILE ]
then
    echo "${THIS}: XMP-Filialdatei '${SIDECAR_FILE}' existiert nicht!" 1>&2
    exit 1
fi

touch --reference=${IMAGE_FILE} ${TIMESTAMP_FILE}

$EXIFTOOL \
    -tagsFromFile \
    ${SIDECAR_FILE} \
    ${IMAGE_FILE}

if [ $? -eq 0 ]
then
    # Zeile im Anschluss an diesen Kommentar kommentieren, falls nicht das
    # Risiko eingegangen werden soll, dass ExifTool ein beschädigtes Bild
    # erzeugt und dann kein Backup mehr existiert
    rm ${IMAGE_FILE}${EXIF_TOOL_BACKUP_SUFFIX}
    touch --reference=${TIMESTAMP_FILE} ${IMAGE_FILE}
else
    echo "Fehler beim Ausführen von ${EXIFTOOL} -tagsFromFile -preserve ${SIDECAR_FILE} ${IMAGE_FILE}!"
    EXIT_VALUE=1
fi

rm ${TIMESTAMP_FILE}
exit ${EXIT_VALUE}

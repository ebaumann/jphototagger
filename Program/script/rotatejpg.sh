#!/usr/bin/env bash
#
# Autor: Elmar Baumann <eb@elmar-baumann.de>
# Datum: 2009/03/18
#
# Aufruf: rotatejpg.sh <Bilddatei> <Winkel:[90|180|270]>
#
# Dreht ein JPEG-Bild mit jpegtran verlustfrei. Erlaubte Drehwinkel sind 90°,
# 180° und  270°. Das Bild wird im Uhrzeigersinn gedreht.
#
# Das Originalbild wird ersetzt! Vor dem Einsatz ist dieses Skript mit
# Testbildern zu überprüfen, insbesondere, ob die Metadaten nicht gelöscht
# werden. Soll ein Backup des alten Bilds angefertigt werden, die Zeile
# unterhalb "# Backup des alten Bilds" auskommentieren (# entfernen).
#
# Voraussetzungen: Bash, jpegtran im Pfad für ausführbare Dateien,
#                  Beschreibbares Verzeichnis $HOME/tmp,
#                  jpegtran muss die Parameter in diesem Skript unterstützen,
#                  siehe weiter unten, insbesondere "-copy all", sonst werden
#                  z.B. EXIF-Informationen gelöscht (Es gibt jpegtran-Versionen,
#                  die nicht alle Parameter unterstützen).
#
################################################################################

IFS="
"

THIS=$(basename $0)
IMAGE_FILE=$1
ANGLE=$2
TMP_DIR=$HOME/tmp
TMP_FILE=${TMP_DIR}/$(basename $0).$$

if [ -z $IMAGE_FILE ]
then
    echo "${THIS}: Bilddatei (1. Parameter) fehlt!"  1>&2
    exit 1
fi

if [ -z $ANGLE ]
then
    echo "${THIS}: Drehwinkel (2. Parameter) fehlt!"  1>&2
    exit 1
fi

if ! [ -f $IMAGE_FILE ]
then
    echo "${THIS}: Bilddatei '${IMAGE_FILE}' existiert nicht!"  1>&2
    exit 1
fi

if ! [ -d $TMP_DIR ]
then
    echo "${THIS}: Temporärverzeichnis '${TMP_DIR}' existiert nicht!"  1>&2
    exit 1
fi

# Bedeutung der Parameter (unerwünschte löschen):
# -copy all  Extras kopieren (EXIF etc.)
# -perfect   Nur drehen, falls verlustfrei möglich
# -rotate 

jpegtran \
    -rotate $ANGLE \
    -perfect \
    -copy all \
    $IMAGE_FILE \
    > $TMP_FILE

if [ $? -eq 0 ]
then
    # Für ein Backup der alten Bilddatei untere Zeile auskommentieren
    # cp ${IMAGE_FILE} ${$IMAGE_FILE}.bak
    mv $TMP_FILE $IMAGE_FILE
else
    rm $TMP_FILE
    echo "${THIS}: Fehler beim Ausführen!"  1>&2
    exit 1
fi

exit 0

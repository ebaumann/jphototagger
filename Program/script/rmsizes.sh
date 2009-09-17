#!/usr/bin/env bash
#
# Autor: Elmar Baumann <eb@elmar-baumann.de>
# Datum: 2009/06/03
#
# Entfernt aus den User-Einstellungen gespeicherte Breiten und Höhen zum Testen
# modifizierter GUI-Elemente
#
# Vorbedingung: Die Schlüssel enden mit .Width und .Height, was der Fall ist,
# wird die JSL benutzt zum Abspeichern der Fenstergrößen und -Positionen.
#
###############################################################################

PREFS=$HOME/.de.elmar_baumann/ImageMetaDataViewer/Settings.properties
TMPFILE=$HOME/tmp/$(basename $0).$$

sed -e 's%.*\.Width=.*%%' \
    -e 's%.*\.Height=.*%%' \
    -e 's%.*DividerLocation.*%%' \
    -e 's%.*.[Ss]plitPane.*%%' \
    -e 's%.*\.[Tt]able.*=[0-9].*%%' \
    -e 's%.*Location[XY]=.*%%' \
    $PREFS \
    > $TMPFILE
mv $TMPFILE $PREFS
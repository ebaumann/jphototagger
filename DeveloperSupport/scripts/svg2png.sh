#!/bin/sh

# Creates in the current working directory PNG files form SVG files in multiple sizes

IFS="
"

SIZES="16
24
32
48
64
96
128
256
"

SVG=$1

if  [ -z $SVG ]
then
	echo "Usage: $(basename $0) <svg-file>"
	exit 1
fi

if ! [ -f $SVG ]
then
	echo "File '$SVG' does not exist!"
	exit 2
fi

$png

for size in $SIZES
do
	bn=$(basename $(echo $SVG | sed 's%\(.*\)\.[Ss][Vv][Gg]$%\1%'))

	if [ $size -eq "16" ]
	then
		png=$bn.png
	else
		png=$bn-$size.png
	fi

	inkscape -z -e $png -w $size $SVG
done

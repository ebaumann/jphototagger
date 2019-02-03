#!/bin/sh

# If you see this file in an editor you may have to set the executable bit and execute it as script in a terminal.

# in which dir are we running (enclose $0 in " to prevent the shell from truncating at white space)
instDir=`dirname "$0"`

cd "$instDir"

java -Xms64m -Xmx1024M -jar JPhotoTagger.jar &

#!/usr/bin/env bash

. $PROJECTDIR/JPhotoTagger/Support/script/functions.sh || exit 1

IFS="
"

TEST_IMG_SRC=${PROJECTDIR}/JPhotoTagger/Support/res/img
TEST_IMG_TARGET=${HOME}/tmp/img

function copy_test_images() {
    mkdir -p ${TEST_IMG_TARGET} \
    && \
    cp --preserve=timestamps --update \
        ${TEST_IMG_SRC}/*.[Jj][Pp][Gg] \
        ${TEST_IMG_SRC}/*.[Nn][Ee][Ff] \
        ${TEST_IMG_SRC}/*.[Tt][Ii][Ff] \
        ${TEST_IMG_SRC}/*.[Xx][Mm][Pp] \
        ${TEST_IMG_TARGET} \
    && \
    chmod +w ${TEST_IMG_TARGET}/*
}

copy_test_images

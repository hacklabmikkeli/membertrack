#!/bin/sh

ORIGDIR="$(readlink -f $(dirname $0))"
PROJNAME=$1
DESTDIR="$(readlink -f $2)"
cp -r "$ORIGDIR" "$DESTDIR"
rm -rf "$DESTDIR/.git"
find "$DESTDIR" -type f |xargs perl -pi -e "s/membertrack/$PROJNAME/g"
find "$DESTDIR" -type d |xargs rename membertrack $PROJNAME
find "$DESTDIR" -type f |xargs rename membertrack $PROJNAME

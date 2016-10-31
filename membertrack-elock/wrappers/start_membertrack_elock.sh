#!/bin/sh

jsvc \
    -client \
    -cp /var/lib/membertrack-elock/membertrack-elock.jar \
    -outfile /var/log/membertrack-elock/stdout \
    -errfile /var/log/membertrack-elock/stderr \
    -pidfile /var/lib/membertrack-elock/pid \
    fi.ilmoeuro.membertrack.elock.ElockDaemon \
    "$@"

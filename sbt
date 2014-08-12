#!/bin/sh
test -f ~/.sbtconfig && . ~/.sbtconfig
exec java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled ${SBT_OPTS} -jar ./sbt-launch.jar "$@"

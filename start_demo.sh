#!/bin/bash
set -e
S="soonr.jar"
if [ ! -f ${S} ] ; then
   #wget https://us.workplace.datto.com/filelink/6813-79cc5be9-b5c9d2ad2e-2 -O soonr.jar --no-verbose
   curl -o soonr.jar -L https://us.workplace.datto.com/filelink/6813-79cc5be9-b5c9d2ad2e-2
fi

T=`date '+%Y-%m-%d_%H-%M-%S'`
L="log/demo.txt"
BK="log/${T}.txt"
echo log/${T}demo.txt
if [ -f ${L} ] ; then
   mv ${L} ${BK}
fi
java -jar soonr.jar 2>&1 | tee ${L}
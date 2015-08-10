#!/bin/bash -eu

shutup=-1
exit=-1
cat=-1
while getopts ":s:e:c:" opt; do
    case $opt in
    s)
      shutup=$OPTARG
      ;;
    e)
      exit=$OPTARG
      ;;
    c)
      cat=$OPTARG
      ;;
    \?)
      echo "Invalid option: -$OPTARG"
      exit -1
      ;;
  esac
done

cnt=1
while [ $cnt -gt 0 ]; do
    if [ $shutup -eq -1 ] || [ $shutup -ge $cnt ]; then
        echo "Count: $cnt."
    fi
    if [ $cnt -eq $exit ]; then
        exit 0
    elif [ $cnt -eq $cat ] ; then
        cat
    else
        let cnt=$cnt+1
    fi

    sleep 1;
done
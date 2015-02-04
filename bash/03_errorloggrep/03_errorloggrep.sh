#!/bin/bash
find -name "*.log" -type f -print0 | xargs -0 grep -il "error" | tee errorlogs.txt | xargs stat --printf '%n\t%s\n'

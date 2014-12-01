#!/bin/bash
find -name "*.java" -type f -print0 | xargs -0 grep -L "ru\.hh\.deathstar" > almost_harmless.txt

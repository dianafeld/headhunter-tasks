#!/bin/bash
pgrep -f "127\.0\.0\.1" | grep "^[0-9]\{5\}$" | sort --reverse --numeric-sort

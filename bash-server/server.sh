#!/bin/bash

# http://stackoverflow.com/a/24342101

rm -f out
mkfifo out
trap "rm -f out" EXIT

cur_request=none

while true
do
  cat out | nc -l -p 1200 > >( # parse the netcat output, to build the answer redirected to the pipe "out".
    while read line
    do
      
      line=$(echo "$line" | tr -d '[\r\n]')
      
      if [ "x$line" = x ] # empty line / end of request
      then
        cur_request=none
      elif [[ $cur_request = "echo" ]] ; then
        echo "$line" > out
      elif [[ $cur_request = "none" ]] ; then
        if echo "$line" | grep -qE '^GET /' # if line starts with "GET /"   
        then
            REQUEST=$(echo "$line" | cut -d ' ' -f2)
            
            if [[ $REQUEST = "/hello" ]]; then
                cur_request=hello
                ( echo -e "HTTP/1.1 200 OK\r"
                echo "Content-type: text/html"
                echo
                
                echo -e "<html>Hello, world!</html>\r\n\r" ) > out
            elif [[ $REQUEST = "/echo" ]]; then
                cur_request=echo
                echo "$line" > out
            fi
        fi
      fi
    done
  )
done

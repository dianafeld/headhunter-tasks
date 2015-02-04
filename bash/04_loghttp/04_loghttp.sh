#!/bin/bash

taskAfilter() {
  time_hour=12
  regexp_time_starts_from="^$time_hour.*"

  return `[[ "$url" =~ ^/resume(\?|$) ]] && [[ "$response_code" =~ 2[0-9][0-9] ]] &&
     [[ "$time" =~ $regexp_time_starts_from ]]` 
}

taskBfilter() {
  user_date=2013-01-18
  id=43
  regexp_for_id="^/resume\?.*\bid=${id}(&|\$)"

  return `[[ "$url" =~ $regexp_for_id ]] && [[ "$date" = "$user_date" ]]`
}

computeAverage() {
  echo "scale=2; $total_time / $total_hits" | bc -l
}

computeQuantile() {
  percent="$1"
  file="$2"
  lines_in_file=`cat $file | wc -l`
  quantile_line="$(echo "$lines_in_file * $percent / 100" | bc)"
  if [[ "$quantile_line" = 0 ]] ; then
    quantile_line=1
  fi
  sort --numeric-sort "$file" | sed "${quantile_line}q;d"
}


taskA() {

  if [[ -e resume.tmp ]]; then 
    rm resume.tmp
  fi

  total_time=0
  total_hits=0

  while read date time log_level request_type user_id url response_code response_time
  do
    if taskAfilter ; then
      
      response_time=$(echo $response_time | sed -r 's/.{2}$//') # delete ms
      total_time=$(echo $total_time + $response_time | bc) # floating point sum
      (( total_hits += 1 ))
      
      echo $response_time >> resume.tmp
    fi
  done < "$input"

  echo "Total response time of successful hits of URL /resume from $time_hour to $(( $time_hour + 1 )): ${total_time}ms"
  echo "Average response time of successful hits of URL /resume from $time_hour to $(( $time_hour + 1 )): $(computeAverage)ms"

  echo "95 quantile: $(computeQuantile 95 resume.tmp)ms"
  echo "99 quantile: $(computeQuantile 99 resume.tmp)ms"

  rm resume.tmp
}

taskB() {

  if [[ -e resume.tmp ]]; then 
    rm resume.tmp
  fi

  total_time=0
  total_hits=0

  while read date time log_level request_type user_id url response_code response_time
  do
    if taskBfilter ; then
      
      response_time=$(echo $response_time | sed -r 's/.{2}$//') # delete ms
      total_time=$(echo $total_time + $response_time | bc) # floating point sum
      (( total_hits += 1 ))
      
      echo $response_time >> resume.tmp
    fi
  done < "$input"

  echo "Average response time of successful hits of URL /resume of user with id $id during day $user_date: $(computeAverage)ms"

  echo "Median: $(computeQuantile 50 resume.tmp)ms"

  rm resume.tmp
}

gnuplotGraph() {

  gnuplot << EOF
        set terminal png

        set xlabel "time"
        set xrange [0:23]

        set ylabel "95-quantile of response time"
        set yrange [0: ]

        set title "/resume, /vacancy, /user response time per hour"
        set key reverse Left outside
        set grid

        set style data linespoints

        plot "resume.tmp" using 1:2 title "resume", \
             "vacancy.tmp" using 1:2 title "vacancy",\
             "user.tmp" using 1:2 title "user"
             
EOF
}


taskC() {

  rm -f response_time_plot.png

  read -p "Input date you want to plot in format yyyy-mm-dd " user_date

  #user_date=2013-01-18

  while read date time log_level request_type user_id url response_code response_time
  do
    for url_addr in resume vacancy user ; do 
      regexp_for_url="^/${url_addr}(\?|\$)"

      if [[ "$url" =~ $regexp_for_url ]] && [[ "$date" = "$user_date" ]] ; then
        response_time=$(echo $response_time | sed -r 's/.{2}$//') # delete ms

        for time_hour in {00..23}; do
          regexp_time_starts_from="^$time_hour.*"
          if [[ "$time" =~ $regexp_time_starts_from ]] ; then
            echo $response_time >> ${url_addr}${time_hour}.tmp
          fi
        done
      fi
    done
  done < "$input"
  

  for time_hour in {00..23}; do
    if [[ -e resume${time_hour}.tmp ]] ; then
      quantile=$(computeQuantile 95 resume${time_hour}.tmp)
      echo "$time_hour $quantile" >> resume.tmp
    fi
    if [[ -e vacancy${time_hour}.tmp ]] ; then
      quantile=$(computeQuantile 95 vacancy${time_hour}.tmp)
      echo "$time_hour $quantile" >> vacancy.tmp
    fi
    if [[ -e user${time_hour}.tmp ]] ; then
      quantile=$(computeQuantile 95 user${time_hour}.tmp)
      echo "$time_hour $quantile" >> user.tmp
    fi
  done

  gnuplotGraph > response_time_plot.png

  echo "Plot was saved in ./response_time_plot.png"

  for time_hour in {00..23}; do
    rm -f resume${time_hour}.tmp vacancy${time_hour}.tmp user${time_hour}.tmp
  done

  rm -f resume.tmp vacancy.tmp user.tmp

}

input="$1"

# task a

read -n1 -r -p "Press any key to run task A..." key
echo

taskA

# task b
echo
read -n1 -r -p "Press any key to run task B..." key
echo

taskB

echo
read -n1 -r -p "Press any key to run task C..." key
echo 

taskC

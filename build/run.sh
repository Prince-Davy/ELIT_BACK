#!/bin/bash

# Remove downloading log in order to save VM space
# Be aware to put site at the end of the command, exitcode bug.
mvn -ntp checkstyle:check pmd:check clean verify site
export exitcode=$?

#check mvn result and exitcode=1 if failed
if [ "$exitcode" -ne "0" ]; then
  echo "mvn failed"
  export exitcode=1
fi

# Print coverage total for gitlab badge
awk -F"," '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " instructions covered"; print "Total",  int(100*covered/instructions), "%" }' target/site/jacoco/jacoco.csv > target/coverage-output.txt
cat target/coverage-output.txt

export cov=`grep -E "Total.*?([0-9]{1,3}) %" target/coverage-output.txt | cut -d "%" -f1 | awk  '{ printf( "%s ", $2 ); } END { printf( "\n" ); }'`

#check coverage result if failed exitcode=1
if [ "${PIPESTATUS[0]}" -ne "0" ] ; then
  echo "Coverage failed"
  export exitcode=1
#in other case print anybadge
elif [[ $(bc -l <<< "$cov<60.0") -eq 1 ]] ; then
  export color=red
  export exitcode=1
  echo "Coverage under 60%"
  anybadge --value="$cov%" --file=./target/coverage.svg --label=coverage -c $color -o
elif [[ $(bc -l <<< "$cov>=60.0") -eq 1 ]] ; then
  export color=green
  echo "Coverage above 60%"
  anybadge --value="$cov%" --file=./target/coverage.svg --label=coverage -c $color -o
#else unknown fatal error red badge "failed"
else
  export color=red
  echo "QBuild failed, value of \$: $var"
  export exitcode=1
  anybadge --value="Failed" --file=./target/coverage.svg --label=coverage -c $color -o
fi

# Create output file for coverage summary so it can be send to ELK
# echo "Hit Total Coverage" > target/coverage.summary.txt
# cat target/coverage-output.txt | grep -oP "([0-9]+)" | awk  '{ printf( "%s ", $1 ); } END { printf( "\n" ); }' >> target/coverage.summary.txt
# rm target/coverage-output.txt

exec "$@"
exit $exitcode

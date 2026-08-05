#!/usr/bin/env bash

set -uo pipefail

EXIT_CODE=0

COVERAGE_THRESHOLD="${COVERAGE_THRESHOLD:-60}"
JACOCO_CSV="${JACOCO_CSV:-target/site/jacoco/jacoco.csv}"
COVERAGE_OUTPUT="${COVERAGE_OUTPUT:-target/coverage-output.txt}"
COVERAGE_SUMMARY="${COVERAGE_SUMMARY:-target/coverage.summary.txt}"

mkdir -p target


# =============================================================================
# Run Maven quality build
#
# Keep "site" at the end because some Maven reporting plugins can affect the
# final command status.
# =============================================================================

echo "Running Maven quality build..."

if ! mvn \
    -Dspring.profiles.active=test \
    -ntp \
    clean \
    checkstyle:check \
    pmd:check \
    verify \
    site
then
    echo "Maven quality build failed"
    EXIT_CODE=1
else
    echo "Maven quality build completed successfully"
fi


# =============================================================================
# Calculate JaCoCo instruction coverage
# =============================================================================

echo "Calculating JaCoCo instruction coverage..."

if [[ ! -s "${JACOCO_CSV}" ]]; then
    echo "Coverage failed: ${JACOCO_CSV} does not exist or is empty"
    EXIT_CODE=1
else
    if awk -F',' '
        NR > 1 {
            missed += $4
            covered += $5
        }

        END {
            instructions = missed + covered

            if (instructions <= 0) {
                print "No JaCoCo instruction data found" > "/dev/stderr"
                exit 1
            }

            percentage = int((100 * covered) / instructions)

            print covered, "/", instructions, "instructions covered"
            print "Total", percentage, "%"
        }
    ' "${JACOCO_CSV}" > "${COVERAGE_OUTPUT}"
    then
        cat "${COVERAGE_OUTPUT}"

        COVERAGE="$(
            awk '
                /^Total[[:space:]]+[0-9]+[[:space:]]+%$/ {
                    print $2
                    exit
                }
            ' "${COVERAGE_OUTPUT}"
        )"

        if [[ ! "${COVERAGE}" =~ ^[0-9]+$ ]]; then
            echo "Coverage failed: invalid percentage '${COVERAGE:-empty}'"
            EXIT_CODE=1
        elif (( COVERAGE < COVERAGE_THRESHOLD )); then
            echo "Coverage is ${COVERAGE}%"
            echo "Coverage is below the required threshold of ${COVERAGE_THRESHOLD}%"
            EXIT_CODE=1
        else
            echo "Coverage is ${COVERAGE}%"
            echo "Coverage meets the required threshold of ${COVERAGE_THRESHOLD}%"
        fi

        # Create the coverage summary for ELK.
        {
            echo "Hit Total Coverage"
            echo "${COVERAGE}"
        } > "${COVERAGE_SUMMARY}"

        echo "Coverage summary created: ${COVERAGE_SUMMARY}"
    else
        echo "Coverage calculation failed"
        EXIT_CODE=1
    fi
fi


# =============================================================================
# Run an optional command passed to the container
#
# Do not use:
#
#   exec "$@"
#   exit "${EXIT_CODE}"
#
# A successful exec replaces this shell, meaning the final EXIT_CODE would
# never be returned.
# =============================================================================

if (( $# > 0 )); then
    echo "Running additional command: $*"

    if ! "$@"; then
        COMMAND_EXIT_CODE=$?
        echo "Additional command failed with exit code ${COMMAND_EXIT_CODE}"
        EXIT_CODE=1
    fi
fi


# =============================================================================
# Return aggregate quality status
# =============================================================================

echo "Quality build finished with exit code ${EXIT_CODE}"

exit "${EXIT_CODE}"
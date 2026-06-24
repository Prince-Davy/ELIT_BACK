
package com.elit.outildedecision.shared.util;

/** Project analysis state enum. */
public enum ProjectAnalysisState {
    /** Not started. */
    NOT_STARTED("NOT_STARTED"),
    /** In progress. */
    IN_PROGRESS("IN_PROGRESS"),
    /** Done. */
    DONE("DONE");

    private final String stateName;

    /**
     * Constructor for creating a new ProjectConfirmationState instance.
     *
     * @param stateName The name of the state.
     */
    ProjectAnalysisState(final String stateName) {
        this.stateName = stateName;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {

        return stateName;
    }
}

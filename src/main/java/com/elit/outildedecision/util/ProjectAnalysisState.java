
package com.elit.outildedecision.util;

public enum ProjectAnalysisState {
    NOT_STARTED("NOT_STARTED"), IN_PROGRESS("IN_PROGRESS"), DONE("DONE");

    private final String stateName;

    /**
     * Constructor for creating a new ProjectConfirmationState instance.
     *
     * @param stateName The name of the state.
     */
    ProjectAnalysisState(final String stateName) {
        this.stateName = stateName;
    }

    @Override
    public String toString() {

        return stateName;
    }
}

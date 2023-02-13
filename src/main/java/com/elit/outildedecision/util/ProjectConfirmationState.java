package com.elit.outildedecision.util;

public enum ProjectConfirmationState {
    WAITING("WAITING"),
    CONFIRMED("CONFIRMED"),
    REJECTED("REJECTED");

    private final String stateName;

    ProjectConfirmationState(String stateName) {
        this.stateName = stateName;
    }

    @Override
    public String toString() {

        return stateName;
    }
}

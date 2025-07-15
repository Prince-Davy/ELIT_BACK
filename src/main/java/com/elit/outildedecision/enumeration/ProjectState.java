package com.elit.outildedecision.enumeration;

import lombok.Getter;

/**
 * Enum representing the possible states of a project.
 * This is used to filter and manage projects based on their current status.
 */
@Getter
public enum ProjectState {

    /**
     * Represents all projects, regardless of their current state.
     * Typically used to reset filters or show the full list.
     */
    ALL("ALL"),

    /**
     * Represents projects that are currently in progress.
     * These projects are actively being worked on.
     */
    IN_PROGRESS("IN PROGRESS"),

    /**
     * Represents projects that have been completed.
     * These projects are finished and no longer active.
     */
    COMPLETED("COMPLETED"),

    /**
     * Represents projects that are pending.
     * These projects are awaiting validation or have not yet started.
     */
    PENDING("PENDING");

    /**
     * The display label for the project state.
     * -- GETTER --
     *  Gets the display label of the project state.
     */
    private final String label;

    /**
     * Constructor for the enum.
     *
     * @param label the display label for the project state
     */
    ProjectState(final String label) {
        this.label = label;
    }

    /**
     * Returns the string representation of the project state.
     *
     * @return the label of the project state
     */
    @Override
    public String toString() {
        return label;
    }
}

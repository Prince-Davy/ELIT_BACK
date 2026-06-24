package com.elit.outildedecision.audit.entity.comment;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** Agility scale category comment class. */
@Entity
@DiscriminatorValue("SCALED_AGILITY")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
public class AgilityScaleCategoryComment extends CategoryComment {
}

package com.elit.outildedecision.audit.entity.comment;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** V model category comment class. */
@Entity
@DiscriminatorValue("VMODEL")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
public class VModelCategoryComment extends CategoryComment { }

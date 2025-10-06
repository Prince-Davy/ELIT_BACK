package com.elit.outildedecision.audit.entity;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("AgilityScale")
public class AgilityScaleCategoryComment extends CategoryComment {
}

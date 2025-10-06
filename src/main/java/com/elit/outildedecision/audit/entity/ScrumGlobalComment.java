package com.elit.outildedecision.audit.entity;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("SCRUM")
public class ScrumGlobalComment extends GlobalComment { }


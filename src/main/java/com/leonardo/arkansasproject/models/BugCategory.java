package com.leonardo.arkansasproject.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class BugCategory {

    @Id
    @GeneratedValue
    private Long id;
    @Column(length = 16)
    private String label;
}

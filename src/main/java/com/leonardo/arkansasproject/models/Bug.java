package com.leonardo.arkansasproject.models;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Bug {

    @Id
    @Column(length = 16)
    private String id;
    private String description;
    @ElementCollection
    @LazyCollection(value = LazyCollectionOption.FALSE)
    private List<String> roles;

}

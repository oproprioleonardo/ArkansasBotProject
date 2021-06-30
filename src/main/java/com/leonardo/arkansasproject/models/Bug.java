package com.leonardo.arkansasproject.models;

import lombok.Data;

import java.util.List;

@Data
public class Bug {

    private String id;
    private String description;
    private List<String> roles;

}

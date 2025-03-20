package com.datingapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @JsonProperty("idCategory")
    private String id;

    @JsonProperty("strCategory")
    private String name;

    @JsonProperty("strCategoryThumb")
    private String thumbnail;

    @JsonProperty("strCategoryDescription")
    private String description;
}

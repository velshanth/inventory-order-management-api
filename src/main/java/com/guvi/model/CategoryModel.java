package com.guvi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
public class CategoryModel {

    @Id
    private String id;

    @NotBlank(message = "Category name is required")
    private String name;

    @NotNull(message = "Active status is required")
    private Boolean active;

    public CategoryModel() {
    }

    public CategoryModel(String id, String name, Boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
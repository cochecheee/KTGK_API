package com.datingapp.service;

import com.datingapp.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    /**
     * Get all categories
     */
    List<Category> getAllCategories();

    /**
     * Get category by id
     */
    Optional<Category> getCategoryById(String id);

    /**
     * Create a new category
     */
    Category createCategory(Category category);

    /**
     * Update an existing category
     */
    Category updateCategory(String id, Category category);

    /**
     * Delete a category
     */
    void deleteCategory(String id);
}
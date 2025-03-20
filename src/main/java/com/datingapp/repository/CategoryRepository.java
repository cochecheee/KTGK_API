package com.datingapp.repository;

import com.datingapp.entity.Category;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository {

    /**
     * Fetch all categories from the API
     */
    List<Category> findAll();

    /**
     * Find category by id
     */
    Optional<Category> findById(String id);

    /**
     * Save or update a category
     */
    Category save(Category category);

    /**
     * Delete a category
     */
    void deleteById(String id);
}

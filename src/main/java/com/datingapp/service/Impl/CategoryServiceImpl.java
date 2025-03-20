package com.datingapp.service.Impl;

import com.datingapp.entity.Category;
import com.datingapp.repository.CategoryRepository;
import com.datingapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        log.info("Getting all categories");
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> getCategoryById(String id) {
        log.info("Getting category with ID: {}", id);
        return categoryRepository.findById(id);
    }

    @Override
    public Category createCategory(Category category) {
        // Generate ID if not provided
        if (category.getId() == null || category.getId().isEmpty()) {
            category.setId(UUID.randomUUID().toString());
        }

        log.info("Creating new category with ID: {}", category.getId());
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(String id, Category category) {
        // Ensure the ID is set correctly
        category.setId(id);

        // Check if the category exists
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            log.error("Category not found with id: {}", id);
            throw new RuntimeException("Category not found with id: " + id);
        }

        log.info("Updating category with ID: {}", id);
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(String id) {
        // Check if the category exists
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            log.error("Category not found with id: {}", id);
            throw new RuntimeException("Category not found with id: " + id);
        }

        log.info("Deleting category with ID: {}", id);
        categoryRepository.deleteById(id);
    }
}
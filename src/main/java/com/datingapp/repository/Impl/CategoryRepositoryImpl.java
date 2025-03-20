package com.datingapp.repository.Impl;

import com.datingapp.entity.Category;
import com.datingapp.entity.CategoryResponse;
import com.datingapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private static final String API_URL = "https://www.themealdb.com/api/json/v1/1/categories.php";

    private final RestTemplate restTemplate;
    private final Map<String, Category> categoryCache = new ConcurrentHashMap<>();

    @Override
    public List<Category> findAll() {
        try {
            log.info("Fetching all categories from TheMealDB API");
            CategoryResponse response = restTemplate.getForObject(API_URL, CategoryResponse.class);

            if (response != null && response.getCategories() != null) {
                // Update cache
                response.getCategories().forEach(category -> categoryCache.put(category.getId(), category));
                return response.getCategories();
            }
        } catch (Exception e) {
            log.error("Error fetching categories from API", e);
        }

        return new ArrayList<>();
    }

    @Override
    public Optional<Category> findById(String id) {
        // Ensure cache is populated
        if (categoryCache.isEmpty()) {
            findAll();
        }

        log.info("Finding category with ID: {}", id);
        return Optional.ofNullable(categoryCache.get(id));
    }

    @Override
    public Category save(Category category) {
        // This is a mock implementation since the API doesn't support writes
        log.info("Saving category with ID: {}", category.getId());
        categoryCache.put(category.getId(), category);
        return category;
    }

    @Override
    public void deleteById(String id) {
        // This is a mock implementation since the API doesn't support deletes
        log.info("Deleting category with ID: {}", id);
        categoryCache.remove(id);
    }
}

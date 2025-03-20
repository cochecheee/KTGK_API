package com.datingapp.service.Impl;

import com.datingapp.entity.Meal;
import com.datingapp.entity.MealResponse;
import com.datingapp.service.MealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealServiceImpl implements MealService {

    private static final String RANDOM_MEAL_API_URL = "https://www.themealdb.com/api/json/v1/1/random.php";

    private final RestTemplate restTemplate;

    @Override
    public Meal getRandomMeal() {
        log.info("Fetching a random meal from TheMealDB API");
        MealResponse response = restTemplate.getForObject(RANDOM_MEAL_API_URL, MealResponse.class);

        if (response != null && response.getMeals() != null && !response.getMeals().isEmpty()) {
            return response.getMeals().get(0);
        }

        log.warn("No meal found in the response");
        return null;
    }

    @Override
    public List<Meal> getRandomMeals(int count) {
        log.info("Fetching {} random meals from TheMealDB API", count);
        List<Meal> meals = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Meal meal = getRandomMeal();
            if (meal != null) {
                meals.add(meal);
            }
        }

        log.info("Retrieved {} random meals", meals.size());
        return meals;
    }
}

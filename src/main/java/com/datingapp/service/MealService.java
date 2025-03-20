package com.datingapp.service;

import com.datingapp.entity.Meal;

import java.util.List;

public interface MealService {

    /**
     * Get a single random meal
     */
    Meal getRandomMeal();

    /**
     * Get multiple random meals
     */
    List<Meal> getRandomMeals(int count);
}

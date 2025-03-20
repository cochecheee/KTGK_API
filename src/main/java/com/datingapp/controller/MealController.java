package com.datingapp.controller;

import com.datingapp.entity.Meal;
import com.datingapp.service.MealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    @GetMapping("/random")
    public ResponseEntity<Meal> getRandomMeal() {
        log.info("REST request to get a random meal");
        Meal meal = mealService.getRandomMeal();
        return ResponseEntity.ok(meal);
    }

    @GetMapping("/random/multiple")
    public ResponseEntity<List<Meal>> getRandomMeals(
            @RequestParam(defaultValue = "6") int count) {
        log.info("REST request to get {} random meals", count);
        List<Meal> meals = mealService.getRandomMeals(count);
        return ResponseEntity.ok(meals);
    }
}
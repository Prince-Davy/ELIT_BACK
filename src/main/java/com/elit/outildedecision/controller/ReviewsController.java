//package com.elit.outildedecision.controller;
//
//import com.elit.outildedecision.exception.ProjectNotFoundException;
//import com.elit.outildedecision.service.ReviewService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.util.List;
//
//@RestController
//@RequestMapping("/reviews")
//public class ReviewsController {
//    @Autowired
//    ReviewService reviewService;
//
//    @PostMapping
//    public Review addReview(@RequestBody @Valid Review review) throws ProjectNotFoundException {
//        return reviewService.saveReview(review);
//    }
//
//    @GetMapping("/{projectId}")
//    public List<Review> getProjectReviews(@PathVariable Long projectId){
//        return reviewService.getProjectReviews(projectId);
//    }
//}

//package com.elit.outildedecision.service.implementation;
//
//import com.elit.outildedecision.entity.Project;
//import com.elit.outildedecision.exception.ProjectNotFoundException;
//import com.elit.outildedecision.repository.ProjectRepository;
//import com.elit.outildedecision.service.ReviewService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ReviewServiceImpl implements ReviewService {
//    @Autowired
//    ReviewRepositpory reviewRepositpory;
//    @Autowired
//    ProjectRepository projectProperty;
//
//    @Override
//    public Review saveReview(Review review) throws ProjectNotFoundException {
//        Optional<Project> project = projectProperty.findById(review.getProject().getProjectId());
//        if(!project.isPresent()) throw new ProjectNotFoundException("Ce projet n'existe pas");
//        Review reviewToPersist = Review.builder()
//                .project(project.get())
//                .reviewText(review.getReviewText())
//                .starsNumber(review.getStarsNumber())
//                .build();
//        return reviewRepositpory.save(reviewToPersist);
//    }
//
//    @Override
//    public List<Review> getProjectReviews(Long projectId) {
//        return reviewRepositpory.findAllByProjectId(projectId);
//    }
//}

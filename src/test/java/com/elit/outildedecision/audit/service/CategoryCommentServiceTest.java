package com.elit.outildedecision.audit.service;

import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.audit.entity.comment.CategoryComment;
import com.elit.outildedecision.audit.repository.CategoryCommentRepository;
import com.elit.outildedecision.audit.service.impl.CategoryCommentServiceImpl;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CategoryCommentServiceTest {

  private CategoryCommentRepository categoryCommentRepository;
  private CategoryCommentServiceImpl categoryCommentService;

   private MethodType methodType;
   private QuestionCategory questionCategory;
   private LevelScore level;

  @BeforeEach
  void setUp() {
    categoryCommentRepository = mock(CategoryCommentRepository.class);
    categoryCommentService = new CategoryCommentServiceImpl(categoryCommentRepository);

    methodType = MethodType.SCRUM;
    questionCategory = QuestionCategory.ORGANISATIONS;
    level = LevelScore.BON;
  }

  @Test
  void testFindByMethodAndCategoryAndLevel_Found() {


    CategoryComment expectedComment = CategoryComment.builder().category(questionCategory).method(methodType).level(level).build();
    when(categoryCommentRepository.findByMethodAndCategoryAndLevel(methodType, questionCategory, level))
      .thenReturn(Optional.of(expectedComment));

    CategoryComment result = categoryCommentService.findByMethodAndCategoryAndLevel(methodType, questionCategory, level);

    assertNotNull(result);
    assertEquals(expectedComment, result);
  }

  @Test
  void testFindByMethodAndCategoryAndLevel_NotFound() {
    when(categoryCommentRepository.findByMethodAndCategoryAndLevel(methodType, questionCategory, level))
      .thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
      categoryCommentService.findByMethodAndCategoryAndLevel(methodType, questionCategory, level)
    );

    assertTrue(exception.getMessage().contains("Comment not found"));
  }

  @Test
  void testFindAll() {
    CategoryComment comment1 = CategoryComment.builder().category(questionCategory).method(methodType).level(level).build();
    CategoryComment comment2 = CategoryComment.builder().category(QuestionCategory.OUTILS_ET_DOCUMENTATION).method(methodType).level(level).build();

    when(categoryCommentRepository.findAll()).thenReturn(List.of(comment1, comment2));

    List<CategoryComment> result = categoryCommentService.findAll();

    assertEquals(2, result.size());
    assertTrue(result.contains(comment1));
    assertTrue(result.contains(comment2));
  }
}

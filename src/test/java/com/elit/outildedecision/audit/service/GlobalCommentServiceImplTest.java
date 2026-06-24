package com.elit.outildedecision.audit.service;

import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.audit.entity.comment.GlobalComment;
import com.elit.outildedecision.audit.entity.comment.ScrumGlobalComment;
import com.elit.outildedecision.audit.repository.GlobalCommentRepository;
import com.elit.outildedecision.audit.service.implementation.GlobalCommentServiceImpl;
import com.elit.outildedecision.shared.util.MethodType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalCommentServiceImplTest {

  @Mock
  private GlobalCommentRepository globalCommentRepository;

  @InjectMocks
  private GlobalCommentServiceImpl globalCommentService;

  @Test
  void findByMethodAndLevel_returnsCommentWhenSeeded() {
    GlobalComment comment = ScrumGlobalComment.builder().text("Bon").build();
    when(globalCommentRepository.findByMethodAndLevel(MethodType.SCRUM, LevelScore.BON))
        .thenReturn(Optional.of(comment));

    GlobalComment result = globalCommentService.findByMethodAndLevel(MethodType.SCRUM, LevelScore.BON);

    assertThat(result).isSameAs(comment);
  }

  @Test
  void findByMethodAndLevel_returnsNullWhenNotSeeded() {
    when(globalCommentRepository.findByMethodAndLevel(MethodType.SCRUM, LevelScore.BON))
        .thenReturn(Optional.empty());

    GlobalComment result = globalCommentService.findByMethodAndLevel(MethodType.SCRUM, LevelScore.BON);

    assertThat(result).isNull();
  }
}

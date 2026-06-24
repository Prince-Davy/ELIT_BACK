package com.elit.outildedecision.ai.feedback.mapper;

import com.elit.outildedecision.ai.feedback.dto.FeedbackMethodResponse;
import com.elit.outildedecision.ai.feedback.dto.FeedbackRequest;
import com.elit.outildedecision.ai.feedback.dto.FeedbackResponse;
import com.elit.outildedecision.ai.feedback.entity.FeedbackIa;
import com.elit.outildedecision.ai.feedback.entity.FeedbackIaMethod;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FeedbackMapperTest {

  private final FeedbackMapper mapper = Mappers.getMapper(FeedbackMapper.class);

  private Project project(final Long id) {
    Project p = new Project();
    p.setProjectId(id);
    return p;
  }

  private User user(final Long id) {
    User u = new User();
    u.setUserId(id);
    return u;
  }

  @Test
  void toEntity_ShouldCopyAllFields() {
    FeedbackRequest request = FeedbackRequest.builder()
        .id(1L).isLiked(true).methodology("Agile").feedbackAnswer("nice")
        .project(project(3L)).user(user(4L)).build();

    FeedbackIa entity = mapper.toEntity(request);

    assertThat(entity.getId()).isEqualTo(1L);
    assertThat(entity.getIsLiked()).isTrue();
    assertThat(entity.getMethodology()).isEqualTo("Agile");
    assertThat(entity.getFeedbackAnswer()).isEqualTo("nice");
    assertThat(entity.getProject().getProjectId()).isEqualTo(3L);
    assertThat(entity.getUser().getUserId()).isEqualTo(4L);
  }

  @Test
  void toResponse_ShouldFlattenIds() {
    FeedbackIa entity = new FeedbackIa(1L, true, "Agile", "nice", project(3L), user(4L));

    FeedbackResponse response = mapper.toResponse(entity);

    assertThat(response.getIsLiked()).isTrue();
    assertThat(response.getMethodology()).isEqualTo("Agile");
    assertThat(response.getFeedbackAnswer()).isEqualTo("nice");
    assertThat(response.getProjectId()).isEqualTo(3L);
    assertThat(response.getUserId()).isEqualTo(4L);
  }

  @Test
  void toMethodResponseList_ShouldFlattenIdsAndMethod() {
    FeedbackIaMethod entity = new FeedbackIaMethod(7L, false, "meh", project(3L), user(4L), "scrum");

    List<FeedbackMethodResponse> responses = mapper.toMethodResponseList(List.of(entity));

    assertThat(responses).hasSize(1);
    FeedbackMethodResponse response = responses.get(0);
    assertThat(response.getIsLiked()).isFalse();
    assertThat(response.getFeedbackAnswer()).isEqualTo("meh");
    assertThat(response.getProjectId()).isEqualTo(3L);
    assertThat(response.getUserId()).isEqualTo(4L);
    assertThat(response.getMethodName()).isEqualTo("scrum");
  }
}

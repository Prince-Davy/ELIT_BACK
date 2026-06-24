package com.elit.outildedecision.ai.kiviat.calculate;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.kiviat.dto.KiviatMatrixQuestion;
import com.elit.outildedecision.ai.kiviat.dto.KiviatScores;
import com.elit.outildedecision.ai.kiviat.dto.KiviatTheme;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Pure, stateless calculator for the Kiviat (radar) project scores. It aggregates the AI answer notes
 * by {@link KiviatTheme} and turns them into the five radar axes, working only on data already loaded
 * in memory (the matrix questions and the project answers) without any repository access, so the
 * surrounding service keeps the load responsibility and this logic stays unit-testable in isolation.
 */
@Component
public class KiviatScoreCalculator {

  /**
   * Computes the Kiviat project scores: the average note of each theme rounded to two decimals,
   * laid out on the five radar axes. A theme with no matching answer scores {@code 0.0}.
   *
   * @param questions the Kiviat matrix questions (question number, theme and scored responses)
   * @param answers   the project AI answers (question number and selected response id)
   * @return the computed radar scores, with no methodology id (project-answer flow)
   */
  public KiviatScores computeProjectScores(final List<KiviatMatrixQuestion> questions,
                                           final List<AiAnswer> answers) {
    final Map<KiviatTheme, List<Integer>> notesByTheme = collectNotesByTheme(questions, answers);
    final Map<KiviatTheme, Double> averages = averageByTheme(notesByTheme);

    return new KiviatScores(
        null,
        averages.get(KiviatTheme.FlexibiliteGestion),
        averages.get(KiviatTheme.FlexibiliteSolution),
        averages.get(KiviatTheme.ConfianceEtDelegation),
        averages.get(KiviatTheme.MaitrisePerimetre),
        averages.get(KiviatTheme.Coordination));
  }

  /**
   * Matches each answer to its matrix question and selected response, collecting the response notes
   * grouped by theme. Every theme is present in the result, with an empty list when unanswered.
   *
   * @param questions the Kiviat matrix questions
   * @param answers   the project AI answers
   * @return the collected notes indexed by theme
   */
  private Map<KiviatTheme, List<Integer>> collectNotesByTheme(final List<KiviatMatrixQuestion> questions,
                                                              final List<AiAnswer> answers) {
    final Map<KiviatTheme, List<Integer>> notesByTheme = new EnumMap<>(KiviatTheme.class);
    for (final KiviatTheme theme : KiviatTheme.values()) {
      notesByTheme.put(theme, new ArrayList<>());
    }

    questions.forEach(question ->
        answers.stream()
            .filter(answer -> Objects.equals(question.getQuestionNumber(), answer.getQuestion()))
            .forEach(answer ->
                question.getResponses().stream()
                    .filter(response -> Objects.equals(String.valueOf(response.getId()), answer.getAnswer()))
                    .forEach(response -> notesByTheme.get(question.getTheme()).add(response.getNote()))
            )
    );
    return notesByTheme;
  }

  /**
   * Averages the notes of each theme, rounded to two decimals, defaulting to {@code 0.0} when a theme
   * carries no note.
   *
   * @param notesByTheme the collected notes indexed by theme
   * @return the average note indexed by theme
   */
  private Map<KiviatTheme, Double> averageByTheme(final Map<KiviatTheme, List<Integer>> notesByTheme) {
    final Map<KiviatTheme, Double> averages = new EnumMap<>(KiviatTheme.class);
    notesByTheme.forEach((theme, notes) -> {
      final double average = notes.stream().mapToInt(Integer::intValue).average().orElse(0);
      averages.put(theme, Math.round(average * 100.0) / 100.0);
    });
    return averages;
  }
}

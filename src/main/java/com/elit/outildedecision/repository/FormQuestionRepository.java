package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.FormQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormQuestionRepository extends JpaRepository<FormQuestion, Long> {
//
//    @Query("delete from FormQuestion where questionId = ?1 and questionText = ?2")
//    void deleteByFormIdAndQuestionText(Long formId, String questionText);
//
//    @Query("select q from FormQuestion q where q.questionId = ?1 and q.answerText = ?2")
//    FormQuestion getByFormIdAndQuestionText(Long formId, String answerText);
//
//    @Query("select  q from FormQuestion q where q.form.formId = ?1")
//    List<FormQuestion> findAllByFormId(Long formId);
//    @Query("select q from FormQuestion q where q.questionText = ?2 and q.form.formId = ?1")
//    Optional<FormQuestion> findByIdAndQuestionText(Long formId, String questionText);
//    @Query("select q from FormQuestion q where q.questionText like ?2% and q.form.formId = ?1")
//    List<FormQuestion> findAllByFormIdAndQuestionTextContains(Long formId, String step);
}

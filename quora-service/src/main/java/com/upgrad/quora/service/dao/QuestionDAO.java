package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDAO {


    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity question) {
        entityManager.persist(question);
        return question;
    }

    public List<QuestionEntity> getAllQuestion() {
        try {
            return entityManager.createNamedQuery("getAll", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity getQuestionByUuid(String uuid) {
        QuestionEntity entity = null;
        try {
            entity = entityManager.createNamedQuery("questionByUuid", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
        return entity;
    }

    public QuestionEntity updateQuestion(QuestionEntity questionEntity) {
        return entityManager.merge(questionEntity);
    }

    public void deleteQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }

    public List<QuestionEntity> getAllQuestionByUser(String uuid) {
        return entityManager.createNamedQuery("getAllQuestionByUser", QuestionEntity.class).setParameter("user_uuid", uuid).getResultList();
    }
}

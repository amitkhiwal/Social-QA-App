package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDAO;
import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerBusinessService {

    @Autowired
    private AnswerDAO answerDAO;
    @Autowired
    private UserDao userDao;
    @Autowired
    private QuestionDAO questionDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer (AnswerEntity answerEntity, String authorization, String question_id) throws NullPointerException, AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity = questionDAO.getQuestionByUuid(question_id);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }else {
            UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
            if (userAuthTokenEntity == null) {
                throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
            }else {
                if (userAuthTokenEntity.getLogoutAt() != null) {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
                } else {
                    answerEntity.setUser(userAuthTokenEntity.getUser());
                    answerEntity.setQuestion(questionEntity);
                    AnswerEntity createdAnswerEntity = answerDAO.createAnswer(answerEntity);
                    return createdAnswerEntity;
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity getAnswerByUuid(String uuid) throws NullPointerException {
        AnswerEntity answerEntity = answerDAO.getAnswerByUuid(uuid);
        return answerEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity updateAnswer(AnswerEntity answerEntity, String authorization) throws NullPointerException, AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else {
            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
            } else {
                AnswerEntity answerEntity1 = getAnswerByUuid(answerEntity.getUuid());
                if (answerEntity1 != null) {
                    if (answerEntity1.getUser() != null && answerEntity1.getUser().getId() == userAuthTokenEntity.getUser().getId()) {
                        answerEntity1.setAns(answerEntity.getAns());
                        AnswerEntity createdAnswerEntity = answerDAO.updateAnswer(answerEntity1);
                        return createdAnswerEntity;
                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
                    }
                } else {
                    throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(String answerId, String authorization) throws NullPointerException, AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else {
            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
            } else {
                AnswerEntity answerEntity1 = getAnswerByUuid(answerId);
                if (answerEntity1 != null) {
                    if (answerEntity1.getUser() != null && answerEntity1.getUser().getId() == userAuthTokenEntity.getUser().getId()) {
                        answerDAO.deleteAnswer(answerEntity1);
                        return answerEntity1;
                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
                    }
                } else {
                    throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAllAnswerByQuestion(String questionId, String authorization) throws NullPointerException, AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else {
            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
            } else {
                QuestionEntity questionEntity = questionDAO.getQuestionByUuid(questionId);
                if (questionEntity == null) {
                    throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
                }else {
                    List<AnswerEntity> answerEntityList = answerDAO.getAllAnswerByQuestion(questionId);
                    return answerEntityList;
                }
            }
        }
    }
}

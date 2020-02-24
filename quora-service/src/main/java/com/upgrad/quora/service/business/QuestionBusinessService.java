package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDAO questionDAO;
    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity,String authorization) throws NullPointerException, AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else {
            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
            } else {
                questionEntity.setUser(userAuthTokenEntity.getUser());
                QuestionEntity createdQuestionEntity = questionDAO.createQuestion(questionEntity);
                return createdQuestionEntity;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestion(String authorization) throws NullPointerException, AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else {
            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions.");
            } else {
                List<QuestionEntity> questionEntityList = questionDAO.getAllQuestion();
                return questionEntityList;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionbyUser(String userId, String authorization) throws NullPointerException, AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else {
            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
            } else {
                UserEntity userEntity = userDao.getUser(userId);
                if (userEntity == null) {
                    throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
                }else {
                    List<QuestionEntity> questionEntityList = questionDAO.getAllQuestionByUser(userId);
                    return questionEntityList;
                }
            }
        }
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity getQuestionByUuid(String uuid) throws NullPointerException {
        QuestionEntity questionEntity1 = questionDAO.getQuestionByUuid(uuid);
        return questionEntity1;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity updateQuestion(QuestionEntity questionEntity, String authorization) throws NullPointerException, AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else {
            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
            } else {
                QuestionEntity questionEntity1 = getQuestionByUuid(questionEntity.getUuid());
                if (questionEntity1 != null) {
                    if (questionEntity1.getUser() != null && questionEntity1.getUser().getId() == userAuthTokenEntity.getUser().getId()) {
                        questionEntity1.setContent(questionEntity.getContent());
                        QuestionEntity createdQuestionEntity = questionDAO.updateQuestion(questionEntity1);
                        return createdQuestionEntity;
                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
                    }
                } else {
                    throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(String questionId, String authorization) throws NullPointerException, AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else {
            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
            } else {
                QuestionEntity questionEntity1 = getQuestionByUuid(questionId);
                if (questionEntity1 != null) {
                    if (questionEntity1.getUser() != null && questionEntity1.getUser().getId() == userAuthTokenEntity.getUser().getId()) {
                        questionDAO.deleteQuestion(questionEntity1);
                        return questionEntity1;
                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
                    }
                } else {
                    throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
                }
            }
        }
    }
}

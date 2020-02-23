package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;
    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQustion(QuestionRequest questionRequest, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException {

                final QuestionEntity questionEntity = new QuestionEntity();
                questionEntity.setContent(questionRequest.getContent());
                questionEntity.setDate(LocalDateTime.now());
                questionEntity.setUuid(UUID.randomUUID().toString());
                final QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity,authorization);
                QuestionResponse questionResponse = new QuestionResponse();
                questionResponse.setId(createdQuestionEntity.getUuid());
                questionResponse.setStatus("QUESTION CREATED");
                return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> getAllQustion(@RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException {
        List<QuestionDetailsResponse> responseList = new ArrayList<QuestionDetailsResponse>();
                List<QuestionEntity> list = questionBusinessService.getAllQuestion(authorization);
                for (QuestionEntity q : list) {
                    QuestionDetailsResponse response = new QuestionDetailsResponse();
                    response.setId(q.getUuid());
                    response.setContent(q.getContent());
                    responseList.add(response);
                }
        return new ResponseEntity(responseList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> updateQuestion(QuestionEditRequest questionEditRequest, @PathVariable("questionId") String questionId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException {
                QuestionEditResponse questionResponse = new QuestionEditResponse();
                QuestionEntity questionEntity = new QuestionEntity();
                questionEntity.setUuid(questionId);
                questionEntity.setContent(questionEditRequest.getContent());
                final QuestionEntity questionEntity1 = questionBusinessService.updateQuestion(questionEntity,authorization);
                if (questionEntity1 != null) {
                    questionResponse.setId(questionEntity1.getUuid());
                    questionResponse.setStatus("QUESTION EDITED");
                }
                return new ResponseEntity<QuestionEditResponse>(questionResponse, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") String questionId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException {
        QuestionDeleteResponse questionResponse = new QuestionDeleteResponse();
                final QuestionEntity questionEntity = questionBusinessService.deleteQuestion(questionId,authorization);
                if (questionEntity != null) {
                    questionResponse.setId(questionEntity.getUuid());
                    questionResponse.setStatus("QUESTION DELETED");
                }
            return new ResponseEntity<QuestionDeleteResponse>(questionResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> getAllQustion(@PathVariable("userId") String userId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException {
        List<QuestionDetailsResponse> responseList = new ArrayList<QuestionDetailsResponse>();
                List<QuestionEntity> list = questionBusinessService.getAllQuestionbyUser(userId,authorization);
                for (QuestionEntity q : list) {
                    QuestionDetailsResponse response = new QuestionDetailsResponse();
                    response.setId(q.getUuid());
                    response.setContent(q.getContent());
                    responseList.add(response);
                }
        return new ResponseEntity(responseList, HttpStatus.OK);
    }
}

package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
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
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createQustion(AnswerRequest answerRequest, @RequestHeader("authorization") String authorization, @PathVariable("questionId") String questionId) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException {

                final AnswerEntity answerEntity = new AnswerEntity();
                answerEntity.setAns(answerRequest.getAnswer());
                answerEntity.setDate(LocalDateTime.now());
                answerEntity.setUuid(UUID.randomUUID().toString());
                final AnswerEntity createdAnswerEntity = answerBusinessService.createAnswer(answerEntity,authorization,questionId);
                AnswerResponse answerResponse = new AnswerResponse();
                answerResponse.setId(createdAnswerEntity.getUuid());
                answerResponse.setStatus("ANSWER CREATED");
                return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> updateAnswer(AnswerEditRequest answerEditRequest, @PathVariable("answerId") String answerId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException, AnswerNotFoundException {
                AnswerEditResponse answerEditResponse = new AnswerEditResponse();
                AnswerEntity answerEntity = new AnswerEntity();
                answerEntity.setUuid(answerId);
                answerEntity.setAns(answerEditRequest.getContent());
                final AnswerEntity answerEntity1 = answerBusinessService.updateAnswer(answerEntity,authorization);
                if (answerEntity1 != null) {
                    answerEditResponse.setId(answerEntity1.getUuid());
                    answerEditResponse.setStatus("ANSWER EDITED");
                }
                return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") String answerId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException, AnswerNotFoundException {
        AnswerDeleteResponse answerResponse = new AnswerDeleteResponse();
                AnswerEntity answerEntity = answerBusinessService.deleteAnswer(answerId,authorization);
                if (answerEntity != null) {
                    answerResponse.setId(answerEntity.getUuid());
                    answerResponse.setStatus("ANSWER DELETED");
                }
            return new ResponseEntity<AnswerDeleteResponse>(answerResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> getAllAnswer(@PathVariable("questionId") String questionId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException {
        List<AnswerDetailsResponse> responseList = new ArrayList<AnswerDetailsResponse>();
                List<AnswerEntity> list = answerBusinessService.getAllAnswerByQuestion(questionId,authorization);
                for (AnswerEntity q : list) {
                    AnswerDetailsResponse response = new AnswerDetailsResponse();
                    response.setId(q.getUuid());
                    response.setQuestionContent(q.getQuestion().getContent());
                    response.setAnswerContent(q.getAns());
                    responseList.add(response);
                }
        return new ResponseEntity(responseList, HttpStatus.OK);
    }
}

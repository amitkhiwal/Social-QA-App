package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonBusinessService {
    @Autowired
    private UserDao userDao;

    public UserEntity getUser(final String userUuid, final String authorizationToken) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity!=null) {
            if (userAuthTokenEntity.getLogoutAt()==null) {
                UserEntity userEntity = userDao.getUser(userUuid);
                if (userEntity == null) {
                    throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
                }
                return userEntity;
            }else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
}

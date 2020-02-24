package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminBusinessService {
    @Autowired
    private UserDao userDao;

    public UserEntity deleteUser(final String userUuid, final String authorizationToken) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity!=null) {
            if (userAuthTokenEntity.getLogoutAt()==null) {
                String role = userAuthTokenEntity.getUser().getRole();
                if (role.equalsIgnoreCase("nonadmin")){
                    throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin.");
                }else {
                    UserEntity userEntity = userDao.getUser(userUuid);
                    if (userEntity == null) {
                        throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
                    }else {
                        UserEntity user = userDao.deleteUser(userEntity);
                        return user;
                    }
                }
            }else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.");
            }
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
}

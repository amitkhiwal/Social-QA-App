package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        if (userDao.getUserByUserName(userEntity.getUsername()) == null){
            if(userDao.getUserByEmail(userEntity.getEmail()) == null){
                String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
                userEntity.setSalt(encryptedText[0]);
                userEntity.setPassword(encryptedText[1]);
                return userDao.createUser(userEntity);
            }else {
                throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
            }
        }else{
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity loginAuth(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity=userDao.getUserByUserName(username);
        if(userEntity==null){
            throw new AuthenticationFailedException("ATH-001","This username does not exist");
        }
        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        if(encryptedPassword.equals(userEntity.getPassword())){
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
            userAuthToken.setUser(userEntity);
            userAuthToken.setUuid(userEntity.getUuid());
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);
            UserAuthTokenEntity authTokenEntity = userDao.createAuthToken(userAuthToken);
            return authTokenEntity;
        }
        else{
            throw new AuthenticationFailedException("ATH-002", "Password Failed");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signout(String userAuth) throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(userAuth);
        if (userAuthTokenEntity!=null) {
            final ZonedDateTime now = ZonedDateTime.now();
            userAuthTokenEntity.setLogoutAt(now);
            UserAuthTokenEntity authTokenEntity = userDao.updateAuthToken(userAuthTokenEntity);
            UserEntity userEntity = authTokenEntity.getUser();
            return userEntity;
        }
        throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
    }
}

package catan.services.impl;

import catan.dao.UserDao;
import catan.domain.model.user.UserBean;
import catan.exception.UserException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
    public static final String USER_NAME1 = "userName1";
    public static final String PASSWORD1 = "12345";
    public static final String PASSWORD2 = "67890";

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserDao userDao;

    @After
    public void tearDown() {

    }

    @Test
    public void loginSuccessful() throws UserException {
        // GIVEN
        UserBean user = new UserBean(USER_NAME1, PASSWORD1);
        user.setId((int) System.currentTimeMillis());

        when(userDao.getUserByUsername(USER_NAME1)).thenReturn(user);

        // WHEN
        String session = userService.login(USER_NAME1, PASSWORD1);

        // THEN
        assertNotNull(session);
        assertEquals(36, session.length());

        verify(userDao, times(1)).allocateNewTokenToUser(anyString(), any(UserBean.class));
    }

    @Test
    public void loginGeneratesNewTokenSuccessful() throws UserException {
        // GIVEN
        UserBean user = new UserBean(USER_NAME1, PASSWORD1);
        user.setId((int) System.currentTimeMillis());

        when(userDao.getUserByUsername(USER_NAME1)).thenReturn(user);

        // WHEN
        String firstSession = userService.login(USER_NAME1, PASSWORD1);
        String secondSession = userService.login(USER_NAME1, PASSWORD1);

        // THEN
        assertNotNull(firstSession);
        assertNotNull(secondSession);
        assertNotSame(firstSession, secondSession);

        verify(userDao, times(2)).allocateNewTokenToUser(Matchers.anyString(), any(UserBean.class));
    }

    @Test
    public void loginErrorWhenPasswordIncorrect() throws UserException {
        try {
            // WHEN
            userService.login(USER_NAME1, null);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void loginErrorWhenUsernameIncorrect() throws UserException {
        try {
            // WHEN
            userService.login(null, PASSWORD1);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void loginErrorWhenUserNotFound() throws UserException {
        try {
            // GIVEN
            when(userDao.getUserByUsername(USER_NAME1)).thenReturn(null);

            // WHEN
            userService.login(USER_NAME1, PASSWORD1);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_INCORRECT_LOGIN_PASSWORD + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_INCORRECT_LOGIN_PASSWORD, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void loginErrorWhenPasswordDoesNotMatch() throws UserException {
        try {
            // GIVEN
            UserBean user = new UserBean(USER_NAME1, PASSWORD1);
            user.setId((int) System.currentTimeMillis());

            when(userDao.getUserByUsername(USER_NAME1)).thenReturn(user);

            // WHEN
            userService.login(USER_NAME1, PASSWORD2);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_INCORRECT_LOGIN_PASSWORD + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_INCORRECT_LOGIN_PASSWORD, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void logoutSuccessful() {
        try {
            // GIVEN
            String token = "abcde";

            // WHEN
            userService.logout(token);
        } catch (Exception e) {
            fail("No exceptions should be thrown");
        }
    }

    @Test
    public void registerSuccessful() {
        try {
            // GIVEN
            when(userDao.getUserByUsername(USER_NAME1)).thenReturn(null);

            // WHEN
            userService.register(USER_NAME1, PASSWORD1);
        } catch (Exception e) {
            fail("No exceptions should be thrown");
        }

        verify(userDao, times(1)).addNewUser(any(UserBean.class));
    }

    @Test
    public void registerErrorWhenPasswordIncorrect() throws UserException {
        try {
            // WHEN
            userService.register(USER_NAME1, null);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void registerErrorWhenUsernameIncorrect() throws UserException {
        try {
            // WHEN
            userService.register(null, PASSWORD1);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void registerErrorWhenUserWithSuchUsernameAlreadyExists() throws UserException {
        try {
            // GIVEN
            UserBean user = new UserBean(USER_NAME1, PASSWORD1);
            user.setId((int) System.currentTimeMillis());

            when(userDao.getUserByUsername(USER_NAME1)).thenReturn(user);

            // WHEN
            userService.register(USER_NAME1, PASSWORD1);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_USERNAME_ALREADY_EXISTS + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_USERNAME_ALREADY_EXISTS, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }
}
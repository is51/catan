package catan.services.impl;

import catan.dao.UserDao;
import catan.domain.model.user.UserBean;
import catan.domain.model.user.UserSessionBean;
import catan.exception.UserException;
import org.apache.http.util.Args;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
    public void loginUserSuccessful() throws UserException {
        // GIVEN
        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);
        user.setId((int) System.currentTimeMillis());

        when(userDao.getUserByUsername(USER_NAME1)).thenReturn(user);

        // WHEN
        String session = userService.loginUser(USER_NAME1, PASSWORD1);

        // THEN
        assertNotNull(session);
        assertEquals(36, session.length());

        verify(userDao, times(1)).allocateNewTokenToUser(anyString(), any(UserBean.class));
    }

    @Test
    public void loginGuestSuccessful() throws UserException {
        // GIVEN
        UserBean user = new UserBean(USER_NAME1, null, true);
        user.setId((int) System.currentTimeMillis());

        expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(user);
        userDao.removeSessionByUser(anyObject(UserBean.class));
        expectLastCall().atLeastOnce();
        userDao.addNewSession(anyObject(UserSessionBean.class));
        expectLastCall().atLeastOnce();
        replay(userDao);

        // WHEN
        String session = userService.loginGuest(USER_NAME1);

        // THEN
        assertNotNull(session);
        assertEquals(36, session.length());
    }

    @Test
    public void loginGeneratesNewTokenSuccessful() throws UserException {
        // GIVEN
        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);
        user.setId((int) System.currentTimeMillis());

        when(userDao.getUserByUsername(USER_NAME1)).thenReturn(user);

        // WHEN
        String firstSession = userService.loginUser(USER_NAME1, PASSWORD1);
        String secondSession = userService.loginUser(USER_NAME1, PASSWORD1);

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
            userService.loginUser(USER_NAME1, null);
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
            userService.loginUser(null, PASSWORD1);
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
            userService.loginUser(USER_NAME1, PASSWORD1);
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
            UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);
            user.setId((int) System.currentTimeMillis());

            when(userDao.getUserByUsername(USER_NAME1)).thenReturn(user);

            // WHEN
            userService.loginUser(USER_NAME1, PASSWORD2);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_INCORRECT_LOGIN_PASSWORD + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_INCORRECT_LOGIN_PASSWORD, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void loginUserErrorWhenUserIsGuest() throws UserException {
        try {
            // GIVEN
            UserBean user = new UserBean(USER_NAME1, null, true);
            user.setId((int) System.currentTimeMillis());

            expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(user);
            replay(userDao);

            // WHEN
            userService.loginUser(USER_NAME1, PASSWORD2);
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
    public void registerPermanentUserSuccessful() {
        try {
            // GIVEN
            when(userDao.getUserByUsername(USER_NAME1)).thenReturn(null);

            // WHEN
            userService.registerUser(USER_NAME1, PASSWORD1);

            UserBean user = capturedArgs.getValue();
            assertNotNull(user);
            assertFalse(user.isGuest());
        } catch (Exception e) {
            fail("No exceptions should be thrown");
        }

        verify(userDao, times(1)).addNewUser(any(UserBean.class));
    }

    @Test
    public void registerGuestUserSuccessful() throws UserException {
            // GIVEN
            expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(null);
            userDao.addNewUser(anyObject(UserBean.class));

            // Example SECOND of how to assert passed values in mock
            expectLastCall().andAnswer(new IAnswer() {
                public Object answer() { // assert parameter passed to addNewUser method
                    UserBean arg1 = (UserBean) getCurrentArguments()[0];
                    assertTrue(arg1.isGuest());

                    return null;
                }
            });
            replay(userDao);

            // WHEN
            userService.registerGuest(USER_NAME1);

            // THEN
            // no exceptions should be thrown
    }

    @Test
    public void registerErrorWhenPasswordIncorrect() throws UserException {
        try {
            // WHEN
            userService.registerUser(USER_NAME1, null);
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
            userService.registerUser(null, PASSWORD1);
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
            UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);
            user.setId((int) System.currentTimeMillis());

            when(userDao.getUserByUsername(USER_NAME1)).thenReturn(user);

            // WHEN
            userService.registerUser(USER_NAME1, PASSWORD1);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_USERNAME_ALREADY_EXISTS + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_USERNAME_ALREADY_EXISTS, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }
}
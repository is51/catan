package catan.services.impl;

import catan.dao.UserDao;
import catan.domain.model.user.UserBean;
import catan.domain.model.user.UserSessionBean;
import catan.domain.exception.UserException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false, "en", "US");
        user.setId((int) System.currentTimeMillis());

        when(userDao.getUserByUsername(USER_NAME1)).thenReturn(user);

        // WHEN
        String session = userService.loginUser(USER_NAME1, PASSWORD1);

        // THEN
        assertNotNull(session);
        assertEquals(36, session.length());

        verify(userDao, times(1)).removeSessionByUser(any(UserBean.class));
        verify(userDao, times(1)).addNewSession(any(UserSessionBean.class));
    }

    @Test
    public void loginGuestSuccessful() throws UserException {
        // GIVEN
        UserBean user = new UserBean(USER_NAME1, null, true, "en", "US");
        user.setId((int) System.currentTimeMillis());

        when(userDao.getUserByUsername(USER_NAME1)).thenReturn(user);

        // WHEN
        String session = userService.loginGuest(USER_NAME1);

        // THEN
        assertNotNull(session);
        assertEquals(36, session.length());

        verify(userDao, times(1)).removeSessionByUser(any(UserBean.class));
        verify(userDao, times(1)).addNewSession(any(UserSessionBean.class));
    }

    @Test
    public void loginGeneratesNewTokenSuccessful() throws UserException {
        // GIVEN
        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false, "en", "US");
        user.setId((int) System.currentTimeMillis());

        when(userDao.getUserByUsername(USER_NAME1)).thenReturn(user);

        // WHEN
        String firstSession = userService.loginUser(USER_NAME1, PASSWORD1);
        String secondSession = userService.loginUser(USER_NAME1, PASSWORD1);

        // THEN
        assertNotNull(firstSession);
        assertNotNull(secondSession);
        assertNotSame(firstSession, secondSession);

        verify(userDao, times(2)).removeSessionByUser(any(UserBean.class));
        verify(userDao, times(2)).addNewSession(any(UserSessionBean.class));
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
            UserBean user = new UserBean(USER_NAME1, PASSWORD1, false, "en", "US");
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
            UserBean user = new UserBean(USER_NAME1, null, true, "en", "US");
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
            ArgumentCaptor<UserBean> gameUserBeanCaptor = ArgumentCaptor.forClass(UserBean.class);

            // WHEN
            userService.registerUser(USER_NAME1, PASSWORD1);

            // THEN
            verify(userDao, times(1)).addNewUser(gameUserBeanCaptor.capture());
            UserBean user = gameUserBeanCaptor.getValue();
            assertNotNull(user);
            assertFalse(user.isGuest());
        } catch (Exception e) {
            fail("No exceptions should be thrown");
        }
    }

    @Test
    public void registerGuestUserSuccessful() throws UserException {
        try {
            // GIVEN
            when(userDao.getUserByUsername(USER_NAME1)).thenReturn(null);
            ArgumentCaptor<UserBean> gameUserBeanCaptor = ArgumentCaptor.forClass(UserBean.class);

            // WHEN
            userService.registerGuest(USER_NAME1);

            // THEN
            verify(userDao, times(1)).addNewUser(gameUserBeanCaptor.capture());
            UserBean user = gameUserBeanCaptor.getValue();
            assertNotNull(user);
            assertTrue(user.isGuest());
        } catch (Exception e) {
            fail("No exceptions should be thrown");
        }
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
            UserBean user = new UserBean(USER_NAME1, PASSWORD1, false, "en", "US");
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
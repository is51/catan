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
import org.junit.Before;
import org.junit.Test;

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


public class UserServiceImplTest {
    public static final String USER_NAME1 = "userName1";
    public static final String PASSWORD1 = "12345";
    public static final String PASSWORD2 = "67890";

    UserDao userDao;
    UserServiceImpl userService;

    @Before
    public void setUp() {
        userDao = createMock(UserDao.class);

        userService = new UserServiceImpl();
        userService.setUserDao(userDao);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void loginUserSuccessful() throws UserException {
        // GIVEN
        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);
        user.setId((int) System.currentTimeMillis());

        expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(user);
        userDao.removeSessionByUser(anyObject(UserBean.class));
        expectLastCall().atLeastOnce();
        userDao.addNewSession(anyObject(UserSessionBean.class));
        expectLastCall().atLeastOnce();
        replay(userDao);

        // WHEN
        String session = userService.loginUser(USER_NAME1, PASSWORD1);

        // THEN
        assertNotNull(session);
        assertEquals(36, session.length());
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

        expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(user);
        userDao.removeSessionByUser(anyObject(UserBean.class));
        expectLastCall().atLeastOnce();
        userDao.addNewSession(anyObject(UserSessionBean.class));
        expectLastCall().atLeastOnce();
        replay(userDao);

        // WHEN
        String firstSession = userService.loginUser(USER_NAME1, PASSWORD1);
        String secondSession = userService.loginUser(USER_NAME1, PASSWORD1);

        // THEN
        assertNotNull(firstSession);
        assertNotNull(secondSession);
        assertNotSame(firstSession, secondSession);
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
            expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(null);
            replay(userDao);

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
            // Example FIRST of how to assert passed values in mock
            Capture<UserBean> capturedArgs = new Capture<UserBean>();

            expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(null);
            userDao.addNewUser(EasyMock.capture(capturedArgs));
            expectLastCall();
            replay(userDao);

            // WHEN
            userService.registerUser(USER_NAME1, PASSWORD1);

            UserBean user = capturedArgs.getValue();
            assertNotNull(user);
            assertFalse(user.isGuest());
        } catch (Exception e) {
            fail("No exceptions should be thrown");
        }
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

            expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(user);
            replay(userDao);

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
package catan.services.impl;

import catan.dao.UserDao;
import catan.domain.model.user.UserBean;
import catan.domain.model.user.UserSessionBean;
import catan.exception.UserException;
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
    public void loginSuccessful() throws UserException {
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
        String session = userService.login(USER_NAME1, PASSWORD1, false);

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
        String firstSession = userService.login(USER_NAME1, PASSWORD1, false);
        String secondSession = userService.login(USER_NAME1, PASSWORD1, false);

        // THEN
        assertNotNull(firstSession);
        assertNotNull(secondSession);
        assertNotSame(firstSession, secondSession);
    }

    @Test
    public void loginErrorWhenPasswordIncorrect() throws UserException {
        try {
            // WHEN
            userService.login(USER_NAME1, null, false);
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
            userService.login(null, PASSWORD1, false);
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
            userService.login(USER_NAME1, PASSWORD1, false);
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
            userService.login(USER_NAME1, PASSWORD2, false);
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
            expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(null);
            userDao.addNewUser(anyObject(UserBean.class));
            expectLastCall();
            replay(userDao);

            // WHEN
            userService.register(USER_NAME1, PASSWORD1, false);
        } catch (Exception e) {
            fail("No exceptions should be thrown");
        }
    }

    @Test
    public void registerGuestUserSuccessfulWhenPasswordIsEmpty() throws UserException {
            // GIVEN
            expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(null);
            userDao.addNewUser(anyObject(UserBean.class));
            expectLastCall().andAnswer(new IAnswer() {
                public Object answer() { // assert parameter passed to addNewUser method
                    UserBean arg1 = (UserBean) getCurrentArguments()[0];
                    assertTrue(arg1.isGuest());

                    return null;
                }
            });
            replay(userDao);

            // WHEN
            userService.register(USER_NAME1, null, true);

            // THEN
            // no exceptions should be thrown
    }

    @Test
    public void registerErrorWhenPasswordIncorrect() throws UserException {
        try {
            // WHEN
            userService.register(USER_NAME1, null, false);
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
            userService.register(null, PASSWORD1, false);
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
            userService.register(USER_NAME1, PASSWORD1, false);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_USERNAME_ALREADY_EXISTS + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_USERNAME_ALREADY_EXISTS, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }
}
package catan.services;

import catan.dao.UserDao;
import catan.domain.UserBean;
import catan.exception.UserException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;


public class UserServiceImplTest {
    public static final String USER_NAME1 = "userName1";
    public static final String PASSWORD1 = "12345";
    public static final String PASSWORD2 = "67890";
    public static final String FIRST_NAME = "andrey";
    public static final String LAST_NAME = "lastName";
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
        UserBean user = new UserBean(System.currentTimeMillis(), USER_NAME1, PASSWORD1, FIRST_NAME, LAST_NAME);

        expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(user);
        userDao.allocateNewTokenToUser(anyString(), anyObject(UserBean.class));
        expectLastCall();
        replay(userDao);

        // WHEN
        String session = userService.login(USER_NAME1, PASSWORD1);

        // THEN
        assertNotNull(session);
        assertEquals(36, session.length());
    }

    @Test
    public void loginGeneratesNewTokenSuccessful() throws UserException {
        // GIVEN
        UserBean user = new UserBean(System.currentTimeMillis(), USER_NAME1, PASSWORD1, FIRST_NAME, LAST_NAME);

        expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(user);
        userDao.allocateNewTokenToUser(anyString(), anyObject(UserBean.class));
        expectLastCall().atLeastOnce();
        replay(userDao);

        // WHEN
        String firstSession = userService.login(USER_NAME1, PASSWORD1);
        String secondSession = userService.login(USER_NAME1, PASSWORD1);

        // THEN
        assertNotNull(firstSession);
        assertNotNull(secondSession);
        assertNotSame(firstSession, secondSession);
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
        } catch (Exception e){
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
        } catch (Exception e){
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
            userService.login(USER_NAME1, PASSWORD1);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_INCORRECT_LOGIN_PASSWORD + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_INCORRECT_LOGIN_PASSWORD, e.getErrorCode());
        } catch (Exception e){
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void loginErrorWhenPasswordDoesNotMatch() throws UserException {
        try {
            // GIVEN
            UserBean user = new UserBean(System.currentTimeMillis(), USER_NAME1, PASSWORD1, FIRST_NAME, LAST_NAME);

            expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(user);
            replay(userDao);

            // WHEN
            userService.login(USER_NAME1, PASSWORD2);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_INCORRECT_LOGIN_PASSWORD + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_INCORRECT_LOGIN_PASSWORD, e.getErrorCode());
        } catch (Exception e){
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
        } catch (Exception e){
            fail("No exceptions should be thrown");
        }
    }

    @Test
    public void registerSuccessful() {
        try {
            // GIVEN
            expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(null);
            userDao.addNewUser(anyObject(UserBean.class));
            expectLastCall();
            replay(userDao);

            // WHEN
            userService.register(USER_NAME1, PASSWORD1);
        } catch (Exception e){
            fail("No exceptions should be thrown");
        }
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
        } catch (Exception e){
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
        } catch (Exception e){
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void registerErrorWhenUserWithSuchUsernameAlreadyExists() throws UserException {
        try {
            // GIVEN
            UserBean user = new UserBean(System.currentTimeMillis(), USER_NAME1, PASSWORD1, FIRST_NAME, LAST_NAME);

            expect(userDao.getUserByUsername(USER_NAME1)).andStubReturn(user);
            replay(userDao);

            // WHEN
            userService.register(USER_NAME1, PASSWORD1);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_USERNAME_ALREADY_EXISTS + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_USERNAME_ALREADY_EXISTS, e.getErrorCode());
        } catch (Exception e){
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void getUserDetailsByTokenReturnPlayer() {
        try {
            // GIVEN
            String token = "token1";
            UserBean user = new UserBean(System.currentTimeMillis(), USER_NAME1, PASSWORD1, FIRST_NAME, LAST_NAME);

            expect(userDao.getUserByToken(token)).andStubReturn(user);
            replay(userDao);

            // WHEN
            UserBean resultUser = userService.getUserDetailsByToken(token);

            // THEN
            assertEquals(user, resultUser);
        } catch (Exception e){
            fail("No exceptions should be thrown");
        }
    }

    @Test
    public void getUserDetailsByTokenErrorWhenTokenInvalid() {
        try {
            // GIVEN
            String token = "token1";

            expect(userDao.getUserByToken(token)).andStubReturn(null);
            replay(userDao);

            // WHEN
            userService.getUserDetailsByToken(token);
            fail("UserException with error code '" + UserServiceImpl.ERROR_CODE_TOKEN_INVALID + "' should be thrown");
        } catch (UserException e) {
            // THEN
            assertEquals(UserServiceImpl.ERROR_CODE_TOKEN_INVALID, e.getErrorCode());
        } catch (Exception e){
            fail("No other exceptions should be thrown");
        }
    }
}
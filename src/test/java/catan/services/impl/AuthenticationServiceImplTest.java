package catan.services.impl;

import catan.dao.UserDao;
import catan.domain.model.user.UserBean;
import catan.exception.AuthenticationException;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

public class AuthenticationServiceImplTest {
    public static final String USER_NAME1 = "userName1";
    public static final String PASSWORD1 = "12345";

    UserDao userDao;
    AuthenticationServiceImpl authenticationService;

    @Before
    public void setUp() {
        userDao = createMock(UserDao.class);

        authenticationService = new AuthenticationServiceImpl();
        authenticationService.setUserDao(userDao);
    }

    @Test
    public void getUserDetailsByTokenReturnPlayer() {
        try {
            // GIVEN
            String token = "token1";
            UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);
            user.setId((int) System.currentTimeMillis());

            expect(userDao.getUserByToken(token)).andStubReturn(user);
            replay(userDao);

            // WHEN
            UserBean resultUser = authenticationService.authenticateUserByToken(token);

            // THEN
            assertEquals(user, resultUser);
        } catch (Exception e) {
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
            authenticationService.authenticateUserByToken(token);
            fail("AuthenticationException should be thrown");
        } catch (AuthenticationException e) {
            // THEN
            assertNull(e.getMessage());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }
}
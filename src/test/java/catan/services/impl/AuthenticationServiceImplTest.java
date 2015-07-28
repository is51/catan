package catan.services.impl;

import catan.dao.UserDao;
import catan.domain.model.user.UserBean;
import catan.exception.AuthenticationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceImplTest {
    public static final String USER_NAME1 = "userName1";
    public static final String PASSWORD1 = "12345";

    @Mock
    private UserDao userDao;
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    public void getUserDetailsByTokenReturnPlayer() {
        try {
            // GIVEN
            String token = "token1";
            UserBean user = new UserBean(USER_NAME1, PASSWORD1);
            user.setId((int) System.currentTimeMillis());

            when(userDao.getUserByToken(token)).thenReturn(user);

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

            when(userDao.getUserByToken(token)).thenReturn(null);

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
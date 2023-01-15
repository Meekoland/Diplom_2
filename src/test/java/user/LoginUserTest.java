package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.Credentials;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DisplayName("Логин пользователя")
public class LoginUserTest {
    private User user;
    private User userIncorrect;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = UserData.getUser();
        userIncorrect = UserData.getUserIncorrect();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Логин пользователя под существующим пользователем")
    public void loginUser() {
        userClient.createUser(Credentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(user));
        accessToken = responseLogin.extract().path("accessToken");
        assertEquals(SC_OK, responseLogin.extract().statusCode());
        assertTrue(responseLogin.extract().path("success"));
    }

    @Test
    @DisplayName("Логин с неверным логином и/или паролем")
    public void loginUserWithIncorrectCredentials() {
        ValidatableResponse responseCreate = userClient.createUser(Credentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(userIncorrect));
        accessToken = responseCreate.extract().path("accessToken");
        assertEquals(SC_UNAUTHORIZED, responseLogin.extract().statusCode());
        assertEquals("email or password are incorrect", responseLogin.extract().path("message"));
    }

    @Test
    @DisplayName("Логин с пустым полем пароля")
    public void loginUserWithEmptyPasswordField() {
        ValidatableResponse responseCreate = userClient.createUser(Credentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.fromOnlyEmail(user));
        accessToken = responseCreate.extract().path("accessToken");
        assertEquals(SC_UNAUTHORIZED, responseLogin.extract().statusCode());
        assertEquals("email or password are incorrect", responseLogin.extract().path("message"));
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
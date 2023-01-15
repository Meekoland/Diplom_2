package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.Credentials;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DisplayName("Изменение данных пользователя")
public class ChangeUserDataTest {

    private User user;
    private User userUpdate;
    private User userSecond;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = UserData.getUser();
        userUpdate = UserData.getUserUpdate();
        userSecond = UserData.getUserSecond();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Изменение данных пользователя с успешной авторизацией")
    public void changeUserData() {
        userClient.createUser(Credentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(user));
        accessToken = responseLogin.extract().path("accessToken");
        ValidatableResponse responseUpdateUser = userClient.changeUser(accessToken, Credentials.from(userUpdate));
        assertEquals(SC_OK, responseUpdateUser.extract().statusCode());
        assertTrue(responseUpdateUser.extract().path("success"));
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией без логина")
    public void changeUserDataWithoutLogin() {
        ValidatableResponse responseCreate = userClient.createUser(Credentials.from(user));
        accessToken = responseCreate.extract().path("accessToken");
        ValidatableResponse responseUpdateUser = userClient.changeUser("", Credentials.from(userUpdate));
        assertEquals(SC_UNAUTHORIZED, responseUpdateUser.extract().statusCode());
        assertEquals("You should be authorised", responseUpdateUser.extract().path("message"));
    }

    @Test
    @DisplayName("Изменение данных пользователя с существующим email")
    public void changeUserDataExistEmail() {
        ValidatableResponse responseCreate = userClient.createUser(Credentials.from(user));
        ValidatableResponse responseCreateSecondUser = userClient.createUser(Credentials.from(userSecond));
        accessToken = responseCreate.extract().path("accessToken");
        String accessTokenSecond = responseCreateSecondUser.extract().path("accessToken");
        ValidatableResponse responseUpdateUser = userClient.changeUser(accessToken, Credentials.from(userSecond));
        assertEquals(SC_FORBIDDEN, responseUpdateUser.extract().statusCode());
        assertEquals("User with such email already exists", responseUpdateUser.extract().path("message"));
        userClient.deleteUser(accessTokenSecond);
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
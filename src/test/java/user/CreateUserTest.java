package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.Credentials;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;

@DisplayName("Создание пользователя")
public class CreateUserTest {

    private User user;
    private UserClient userClient;
    private String accessToken;
    private String accessTokenError;

    @Before
    public void setUp() {
        user = UserData.getUser();
        userClient = new UserClient();
        accessTokenError = null;
    }

    @Test
    @DisplayName("Создание пользователя")
    public void userCanBeCreated() {
        ValidatableResponse responseCreate = userClient.createUser(Credentials.from(user));
        accessToken = responseCreate.extract().path("accessToken");
        assertEquals(SC_OK, responseCreate.extract().statusCode());
    }

    @Test
    @DisplayName("Пользователь не может быть создан дважды")
    public void userCantBeCreatedTwice() {
        ValidatableResponse responseCreateUniqueUser = userClient.createUser(Credentials.from(user));
        ValidatableResponse responseCreateExistUser = userClient.createUser(Credentials.from(user));
        accessToken = responseCreateUniqueUser.extract().path("accessToken");
        if (responseCreateExistUser.extract().path("accessToken") != null) {
            accessTokenError = responseCreateExistUser.extract().path("accessToken");
        }
        assertEquals(SC_FORBIDDEN, responseCreateExistUser.extract().statusCode());
        assertEquals("User already exists", responseCreateExistUser.extract().path("message"));
    }

    @Test
    @DisplayName("Создание пользователя без логина и пароля")
    public void userCantBeCreatedWithoutNameAndPassword() {
        ValidatableResponse responseCreate = userClient.createUser(Credentials.fromOnlyEmail(user));
        if (responseCreate.extract().path("accessToken") != null) {
            accessTokenError = responseCreate.extract().path("accessToken");
        }
        assertEquals(SC_FORBIDDEN, responseCreate.extract().statusCode());
        assertEquals("Email, password and name are required fields", responseCreate.extract().path("message"));
    }

    @Test
    @DisplayName("Создание пользователя без логина и email")
    public void userCantBeCreatedWithoutNameAndEmail() {
        ValidatableResponse responseCreate = userClient.createUser(Credentials.fromOnlyPassword(user));
        if (responseCreate.extract().path("accessToken") != null) {
            accessTokenError = responseCreate.extract().path("accessToken");
        }
        assertEquals(SC_FORBIDDEN, responseCreate.extract().statusCode());
        assertEquals("Email, password and name are required fields", responseCreate.extract().path("message"));
    }

    @Test
    @DisplayName("Создание пользователя без логина")
    public void userCantBeCreatedWithoutName() {
        ValidatableResponse responseCreate = userClient.createUser(Credentials.fromOnlyEmailAndPassword(user));
        if (responseCreate.extract().path("accessToken") != null) {
            accessTokenError = responseCreate.extract().path("accessToken");
        }
        assertEquals(SC_FORBIDDEN, responseCreate.extract().statusCode());
        assertEquals("Email, password and name are required fields", responseCreate.extract().path("message"));
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
        if (accessTokenError != null) {
            userClient.deleteUser(accessTokenError);
        }
    }
}
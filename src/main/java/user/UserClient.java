package user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import util.ClientConfig;
import util.Credentials;

import static io.restassured.RestAssured.given;

public class UserClient extends ClientConfig {
    private static final String PATH_CREATE = "/api/auth/register";
    private static final String PATH_LOGIN = "/api/auth/login";
    private static final String PATH_CHANGE_USER = "/api/auth/user";
    private static final String PATH_DELETE = "/api/auth/user";

    @Step("Создание пользователя")
    public ValidatableResponse createUser(Credentials credentials) {
        return given()
                .spec(getSpec())
                .body(credentials)
                .when()
                .post(PATH_CREATE)
                .then();
    }

    @Step("Логин пользователя")
    public ValidatableResponse loginUser(Credentials credentials) {
        return given()
                .spec(getSpec())
                .body(credentials)
                .when()
                .post(PATH_LOGIN)
                .then();
    }

    @Step("Изменить данные пользователя с авторизацией")
    public ValidatableResponse changeUser(String accessToken, Credentials credentials) {
        return given()
                .header("Authorization", accessToken)
                .header("Accept", "*/*")
                .spec(getSpec())
                .body(credentials)
                .when()
                .patch(PATH_CHANGE_USER)
                .then();
    }

    @Step("Удалить пользователя")
    public void deleteUser(String accessToken) {
        if (accessToken != null) {
            given()
                    .header("Authorization", accessToken)
                    .header("Accept", "*/*")
                    .spec(getSpecForDelete())
                    .delete(PATH_DELETE)
                    .then();
        }
    }
}
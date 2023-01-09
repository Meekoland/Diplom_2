package order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import util.ClientConfig;

import static io.restassured.RestAssured.given;

public class OrderClient extends ClientConfig {
    private static final String PATH_CREATE = "/api/orders";
    private static final String PATH_GET_ORDERS = "/api/orders";

    @Step("Создать заказ с авторизацией")
    public ValidatableResponse createOrder(Ingredients ingredients, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .header("Accept", "*/*")
                .spec(getSpec())
                .body(ingredients)
                .when()
                .post(PATH_CREATE)
                .then();
    }

    @Step("Создать заказ без авторизации")
    public ValidatableResponse createOrderWithoutAuthorization(Ingredients ingredients) {
        return given()
                .spec(getSpec())
                .body(ingredients)
                .when()
                .post(PATH_CREATE)
                .then();
    }

    @Step("Получить заказ конкретного пользователя с авторизацией")
    public ValidatableResponse getOrdersForUser(String accessToken) {
        if (accessToken != null) {
            return given()
                    .header("Authorization", accessToken)
                    .header("Accept", "*/*")
                    .spec(getSpecForGetOrders())
                    .get(PATH_GET_ORDERS)
                    .then();
        }
        return null;
    }
}
package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserClient;
import user.UserData;
import util.Credentials;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DisplayName("Создание заказа")
public class CreateOrderTest {

    private User user;
    private Ingredients ingredients;
    private Ingredients ingredientsEmpty;
    private Ingredients ingredientsIncorrect;
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = UserData.getUser();
        ingredients = Order.getIngredients();
        ingredientsEmpty = Order.getIngredientsEmpty();
        ingredientsIncorrect = Order.getIngredientsIncorrect();
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами с авторизацией пользователя")
    public void createOrderWithAuth() {
        userClient.createUser(Credentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(user));
        accessToken = responseLogin.extract().path("accessToken");
        ValidatableResponse responseOrderCreate = orderClient.createOrder(ingredients, accessToken);
        assertEquals(SC_OK, responseOrderCreate.extract().statusCode());
        assertTrue(responseOrderCreate.extract().path("success"));
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами без авторизации пользователя")
    public void createOrderWithoutAuth() {
        ValidatableResponse responseUserCreate = userClient.createUser(Credentials.from(user));
        accessToken = responseUserCreate.extract().path("accessToken");
        ValidatableResponse responseOrderCreate = orderClient.createOrderWithoutAuthorization(ingredients);
        assertEquals(SC_OK, responseOrderCreate.extract().statusCode());
        assertTrue(responseOrderCreate.extract().path("success"));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        ValidatableResponse responseUserCreate = userClient.createUser(Credentials.from(user));
        accessToken = responseUserCreate.extract().path("accessToken");
        ValidatableResponse responseOrderCreate = orderClient.createOrderWithoutAuthorization(ingredientsEmpty);
        assertEquals(SC_BAD_REQUEST, responseOrderCreate.extract().statusCode());
        assertEquals("Ingredient ids must be provided", responseOrderCreate.extract().path("message"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithIncorrectHash() {
        ValidatableResponse responseUserCreate = userClient.createUser(Credentials.from(user));
        accessToken = responseUserCreate.extract().path("accessToken");
        ValidatableResponse responseOrderCreate = orderClient.createOrderWithoutAuthorization(ingredientsIncorrect);
        assertEquals(SC_INTERNAL_SERVER_ERROR, responseOrderCreate.extract().statusCode());
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
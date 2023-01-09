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

import java.util.ArrayList;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

@DisplayName("Получение заказов")
public class GetUserOrdersTest {

    private User user;
    private Ingredients ingredients;
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = UserData.getUser();
        ingredients = Order.getIngredients();
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Получение заказов как авторизованный пользователь")
    public void getOrdersUser() {
        int totalOrder = 1;
        userClient.createUser(Credentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(user));
        accessToken = responseLogin.extract().path("accessToken");
        orderClient.createOrder(ingredients, accessToken);
        ValidatableResponse responseGetOrders = orderClient.getOrdersForUser(accessToken);
        ArrayList<Object> orders = responseGetOrders.extract().path("orders");
        assertEquals(SC_OK, responseGetOrders.extract().statusCode());
        assertTrue(responseGetOrders.extract().path("success"));
        assertEquals(totalOrder, orders.size());
    }

    @Test
    @DisplayName("Получение заказов как неавторизованный пользователь")
    public void getOrdersUserWithoutAuth() {
        userClient.createUser(Credentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(user));
        accessToken = responseLogin.extract().path("accessToken");
        orderClient.createOrder(ingredients, accessToken);
        ValidatableResponse responseGetOrders = orderClient.getOrdersForUser("");
        assertEquals(SC_UNAUTHORIZED, responseGetOrders.extract().statusCode());
        assertFalse(responseGetOrders.extract().path("success"));
        assertEquals("You should be authorised", responseGetOrders.extract().path("message"));
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
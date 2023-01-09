package order;

public class Order {

    public static Ingredients getIngredients() {
        return new Ingredients(new String[]{"61c0c5a71d1f82001bdaaa6c", "61c0c5a71d1f82001bdaaa72"});
    }

    public static Ingredients getIngredientsEmpty() {
        return new Ingredients(new String[]{});
    }

    public static Ingredients getIngredientsIncorrect() {
        return new Ingredients(new String[]{"12gfs5323", "45533gre26"});
    }
}
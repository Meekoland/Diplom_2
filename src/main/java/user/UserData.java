package user;

import org.apache.commons.lang3.RandomStringUtils;

public class UserData {
    private static final int count = 10;
    private static final int countIncorrectPassword = 2;

    public static String generateString(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public static User getUser() {
        return new User(generateString(count) + "@mail.ru", generateString(count), generateString(count));
    }

    public static User getUserIncorrect() {
        return new User(generateString(count) + "@mail.ru", generateString(countIncorrectPassword), generateString(count));
    }

    public static User getUserUpdate() {
        return new User(generateString(count) + "@mail.ru", generateString(count), generateString(count));
    }

    public static User getUserSecond() {
        return new User(generateString(count) + "@mail.ru", generateString(count), generateString(count));
    }
}
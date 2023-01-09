package util;

import lombok.Data;
import user.User;

@Data
public class Credentials {

    private String email;
    private String password;
    private String name;

    public Credentials(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static Credentials from(User user) {
        return new Credentials(user.getEmail(), user.getPassword(), user.getName());
    }

    public static Credentials fromOnlyEmailAndPassword(User user) {
        return new Credentials(user.getEmail(), user.getPassword(), "");
    }

    public static Credentials fromOnlyEmail(User user) {
        return new Credentials(user.getEmail(), "", "");
    }

    public static Credentials fromOnlyPassword(User user) {
        return new Credentials("", user.getPassword(), "");
    }
}
package br.com.bruno.users;

import lombok.Data;

@Data
public class User {

    private final String uuid, email;

    public User(String uuid, String email) {
        this.uuid = uuid;
        this.email = email;
    }

}

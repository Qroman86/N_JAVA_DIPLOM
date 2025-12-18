package ru.netology.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {


    @Id
    private String login;

    @Column(nullable = false)
    private String password; // BCrypt

    public String getPassword() {
        return this.password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

}

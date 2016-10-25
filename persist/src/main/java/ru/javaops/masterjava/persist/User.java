package ru.javaops.masterjava.persist;

/**
 * Created by dmitriy_varygin on 25.10.16.
 */
public class User {

    private Integer id;
    private String fullName;
    private String email;

    public User() {

    }

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

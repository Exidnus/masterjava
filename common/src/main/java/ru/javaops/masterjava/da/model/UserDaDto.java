package ru.javaops.masterjava.da.model;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
public class UserDaDto {

    private Integer id;
    private String fullName;
    private String email;
    private String city;

    public UserDaDto() {

    }

    public UserDaDto(String fullName, String email, String city) {
        this.fullName = fullName;
        this.email = email;
        this.city = city;
    }

    public UserDaDto(Integer id, String fullName, String email, String city) {
        this(fullName, email, city);
        this.id = id;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

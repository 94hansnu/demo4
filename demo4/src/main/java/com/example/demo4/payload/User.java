package com.example.demo4.payload;


import jakarta.persistence.*;

/**
 * Detta är en JPA-entitetsklass som representerar användarinformation och mappar till en databastabell.
 * Attribut för användarens unika identifierare.
 * Getters och setters metoder för samtliga attribut
 * toString-metod för att enkelt representera User-objektet som en sträng.
 */
@Entity
@Table (name = "users")
public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

}


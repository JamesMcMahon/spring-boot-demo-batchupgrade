package sh.jfm.springbootdemos.batchupgradeexample;

/**
 * Simple entity class representing a user in the batch processing system.
 * This class is used as a data model for reading user records from CSV files
 * and writing them to the database during batch operations.
 */
public class User {
    private Long id;
    private String firstName;
    private String lastName;

    public User() {
    }

    public User(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
}

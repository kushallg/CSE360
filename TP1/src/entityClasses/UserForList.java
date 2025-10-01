package entityClasses;

import javafx.beans.property.SimpleStringProperty;

/**
 * A data model class designed to hold user information formatted for display in a JavaFX TableView.
 * This class uses SimpleStringProperty to be compatible with JavaFX's property binding mechanisms.
 */
public class UserForList {
    private final SimpleStringProperty username;
    private final SimpleStringProperty name;
    private final SimpleStringProperty email;
    private final SimpleStringProperty roles;

    /**
     * Constructs a new UserForList object.
     * @param username The user's username.
     * @param name The user's formatted full name.
     * @param email The user's email address.
     * @param roles A space-separated string of the user's roles.
     */
    public UserForList(String username, String name, String email, String roles) {
        this.username = new SimpleStringProperty(username);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.roles = new SimpleStringProperty(roles);
    }

    /**
     * Gets the username value.
     * @return The username as a String.
     */
    public String getUsername() {
        return username.get();
    }
    
    /**
     * Gets the full name value.
     * @return The full name as a String.
     */
    public String getName() {
        return name.get();
    }
    
    /**
     * Gets the email address value.
     * @return The email address as a String.
     */
    public String getEmail() {
        return email.get();
    }

    /**
     * Gets the roles value.
     * @return The roles as a String.
     */
    public String getRoles() {
        return roles.get();
    }
}
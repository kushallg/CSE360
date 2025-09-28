package entityClasses;

import javafx.beans.property.SimpleStringProperty;

public class UserForList {
    private final SimpleStringProperty username;
    private final SimpleStringProperty name;
    private final SimpleStringProperty email;
    private final SimpleStringProperty roles;

    public UserForList(String username, String name, String email, String roles) {
        this.username = new SimpleStringProperty(username);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.roles = new SimpleStringProperty(roles);
    }

    public String getUsername() {
        return username.get();
    }

    public String getName() {
        return name.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getRoles() {
        return roles.get();
    }
}
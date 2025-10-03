package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import entityClasses.UserForList;
import entityClasses.User;
import java.time.Instant; //added

public class Database {

    static final String JDBC_DRIVER = "org.h2.Driver";   
    static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

    static final String USER = "sa"; 
    static final String PASS = ""; 

    private Connection connection = null;
    private Statement statement = null;
    
    private String currentUsername;
    private String currentPassword;
    private String currentFirstName;
    private String currentMiddleName;
    private String currentLastName;
    private String currentPreferredFirstName;
    private String currentEmailAddress;
    private boolean currentAdminRole;
    private boolean currentNewStudent;
    private boolean currentNewStaff;

    public Database () {
        
    }
    
    /**
     * Establishes a connection to the H2 database and initializes tables if they don't exist.
     * @throws SQLException if a database access error occurs.
     */
    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement(); 

            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }
    
    /**
     * Creates the necessary tables (userDB, InvitationCodes, otpsTable) if they are not already present.
     * @throws SQLException if a database access error occurs.
     */
    private void createTables() throws SQLException {
        String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "userName VARCHAR(255) UNIQUE, "
                + "password VARCHAR(255), "
                + "firstName VARCHAR(255), "
                + "middleName VARCHAR(255), "
                + "lastName VARCHAR (255), "
                + "preferredFirstName VARCHAR(255), "
                + "emailAddress VARCHAR(255), "
                + "adminRole BOOL DEFAULT FALSE, "
                + "newStudent BOOL DEFAULT FALSE, "
                + "newStaff BOOL DEFAULT FALSE)";
        statement.execute(userTable);
        
        // create Invitation Code table
        String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
                + "code VARCHAR(10) PRIMARY KEY, "
                + "emailAddress VARCHAR(255), "
                + "role VARCHAR(255))";
        statement.execute(invitationCodesTable);
        
        // Added expiry and uses columns to the table
        try { statement.execute("ALTER TABLE InvitationCodes ADD COLUMN expiresAt TIMESTAMP"); } catch (SQLException ignore) {}
        try { statement.execute("ALTER TABLE InvitationCodes ADD COLUMN usesRemaining INT DEFAULT 0"); } catch (SQLException ignore) {}

        //one-time code that lasts a minute
        try (PreparedStatement ps = connection.prepareStatement(
            "MERGE INTO InvitationCodes (code, emailAddress, role, expiresAt, usesRemaining) KEY(code) VALUES (?,?,?,?,?)")) {
            ps.setString(1, "CSE360A1"); 
            ps.setString(2, null);       
            ps.setString(3, "MEMBER");   
            ps.setTimestamp(4, Timestamp.from(Instant.now().plusSeconds(1 * 60))); // +1 minute
            ps.setInt(5, 1);             // one-time use
            ps.executeUpdate();
        }
        
        
        String otpsTable = "CREATE TABLE IF NOT EXISTS otpsTable ("
                + "username VARCHAR(255) PRIMARY KEY, "
                + "otp VARCHAR(10))";
        statement.execute(otpsTable);
    }

    /**
     * Checks if user table empty
     * @return true if no users are in the database and false if there are
     */
    public boolean isDatabaseEmpty() {
        String query = "SELECT COUNT(*) AS count FROM userDB";
        try {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("count") == 0;
            }
        }  catch (SQLException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Gets the total number of users currently in the database.
     * @return The integer count of users.
     */
    public int getNumberOfUsers() {
        String query = "SELECT COUNT(*) AS count FROM userDB";
        try {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            return 0;
        }
        return 0;
    }

    /**
     * Registers a new user by inserting their details into the database.
     * @param user The User object containing all the details for the new user.
     * @throws SQLException if a database access error occurs.
     */
    public void register(User user) throws SQLException {
        String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
                + "lastName, preferredFirstName, emailAddress, adminRole, newStudent, newStaff) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            currentUsername = user.getUserName();
            pstmt.setString(1, currentUsername);
            
            currentPassword = user.getPassword();
            pstmt.setString(2, currentPassword);
            
            currentFirstName = user.getFirstName();
            pstmt.setString(3, currentFirstName);
            
            currentMiddleName = user.getMiddleName();           
            pstmt.setString(4, currentMiddleName);
            
            currentLastName = user.getLastName();
            pstmt.setString(5, currentLastName);
            
            currentPreferredFirstName = user.getPreferredFirstName();
            pstmt.setString(6, currentPreferredFirstName);
            
            currentEmailAddress = user.getEmailAddress();
            pstmt.setString(7, currentEmailAddress);
            
            currentAdminRole = user.getAdminRole();
            pstmt.setBoolean(8, currentAdminRole);
            
            currentNewStudent = user.getNewStudent();
            pstmt.setBoolean(9, currentNewStudent);
            
            currentNewStaff = user.getNewStaff();
            pstmt.setBoolean(10, currentNewStaff);
            
            pstmt.executeUpdate();
        }
        
    }
    
    /**
     * Retrieves a list of all usernames from the database.
     * @return A List of strings, where each string is a username.
     */
    public List<String> getUserList () {
        List<String> userList = new ArrayList<String>();
        userList.add("<Select a User>");
        String query = "SELECT userName FROM userDB";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userList.add(rs.getString("userName"));
            }
        } catch (SQLException e) {
            return null;
        }
        return userList;
    }

    /**
     * Authenticates a user with admin privileges.
     * @param user The User object with username and password to verify.
     * @return true if the credentials are valid and the user is an admin, false otherwise.
     */
    public boolean loginAdmin(User user){
        String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
                + "adminRole = TRUE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch  (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Authenticates a user with student privileges.
     * @param user The User object with username and password to verify.
     * @return true if the credentials are valid and the user is a student, false otherwise.
     */
    public boolean loginStudent(User user) {
        String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
                + "newStudent = TRUE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch  (SQLException e) {
               e.printStackTrace();
        }
        return false;
    }

    /**
     * Authenticates a user with staff privileges.
     * @param user The User object with username and password to verify.
     * @return true if the credentials are valid and the user is staff, false otherwise.
     */
    public boolean loginStaff(User user) {
        String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
                + "newStaff = TRUE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch  (SQLException e) {
               e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Checks if a specific username already exists in the database.
     * @param userName The username to check.
     * @return true if the username exists, false otherwise.
     */
    public boolean doesUserExist(String userName) {
        String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Counts the number of roles (Admin, Student, Staff) assigned to a user.
     * @param user The User object to check.
     * @return The integer count of roles.
     */
    public int getNumberOfRoles (User user) {
        int numberOfRoles = 0;
        if (user.getAdminRole()) numberOfRoles++;
        if (user.getNewStudent()) numberOfRoles++;
        if (user.getNewStaff()) numberOfRoles++;
        return numberOfRoles;
    }   

    /**
     * Generates a new, unique invitation code, stores it in the database, and returns it.
     * The code is valid for one use and expires after a short time.
     * @param emailAddress The email address the invitation is for.
     * @param role The role(s) to be assigned upon registration.
     * @return The generated 6-character invitation code.
     */
    // Generates a one-time invitation that expires in 1 minutes.
    public String generateInvitationCode(String emailAddress, String role) {
        String code = java.util.UUID.randomUUID().toString()
                        .replace("-", "")
                        .substring(0, 6);
              

        String sql = "INSERT INTO InvitationCodes (code, emailAddress, role, expiresAt, usesRemaining) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, emailAddress);
            pstmt.setString(3, role);
            pstmt.setTimestamp(4, Timestamp.from(Instant.now().plusSeconds(1 * 60))); // +1 minute
            pstmt.setInt(5, 1); // one-time
            pstmt.executeUpdate();
            System.out.println("[INVITE] Created code: " + code + " -> " + role);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * Gets the total number of active invitations in the database.
     * @return The integer count of invitations.
     */
    public int getNumberOfInvitations() {
        String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
        try {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch  (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Checks if an email address has already been used for an invitation.
     * @param emailAddress The email address to check.
     * @return true if the email has been used, false otherwise.
     */
    public boolean emailaddressHasBeenUsed(String emailAddress) {
        String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, emailAddress);
            ResultSet rs = pstmt.executeQuery();
            System.out.println(rs);
            if (rs.next()) {
                return rs.getInt("count")>0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Retrieves the role associated with a valid, unexpired invitation code.
     * @param code The invitation code to look up.
     * @return The role as a String if the code is valid, or an empty string otherwise.
     */
    public String getRoleGivenAnInvitationCode(String code) {
        String sql = "SELECT role FROM InvitationCodes " +
                     "WHERE code = ? AND expiresAt > CURRENT_TIMESTAMP AND usesRemaining > 0";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, code == null ? "" : code.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("role");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "";
    }

    /**
     * Retrieves the email address associated with a valid, unexpired invitation code.
     * @param code The invitation code to look up.
     * @return The email address as a String if the code is valid, or an empty string otherwise.
     */
    public String getEmailAddressUsingCode(String code) {
        String sql = "SELECT emailAddress FROM InvitationCodes " +
                     "WHERE code = ? AND expiresAt > CURRENT_TIMESTAMP AND usesRemaining > 0";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, code == null ? "" : code.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("emailAddress");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "";
    }
    
    /**
     * Deletes an invitation code from the database after it has been used.
     * @param code The invitation code to remove.
     */
    public void removeInvitationAfterUse(String code) {
        String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int counter = rs.getInt(1);
                if (counter > 0) {
                    query = "DELETE FROM InvitationCodes WHERE code = ?";
                    try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
                        pstmt2.setString(1, code);
                        pstmt2.executeUpdate();
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }
    
    /**
     * Updates a user's password in the database.
     * @param username The username of the account to update.
     * @param newPassword The new password to set.
     */
    public void updatePassword(String username, String newPassword) {
        String query = "UPDATE userDB SET password = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            currentPassword = newPassword;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    /*******
     * <p> Method: String generateOTPCode(String username) </p>
     * * <p> Description: Given a username, this method establishes a one-time password
     * code and adds a record to the OTPCodes table.  When the OTP code is used, the
     * stored username is used to establish the new password and the record is removed from the
     * table.</p>
     * * @param username specifies the username whose password needs to be updated.
     * * * @return the code of six characters so the new user can use it to securely setup an account.
     * */
    // Generates a new otp code and inserts it into the database.
    public String generateOTPCode(String username) {
        String otp = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
        String query = "INSERT INTO otpsTable (username, otp) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, otp);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.print("OTP: " + otp);
        return otp;
    }
    
    
    /*******
     * <p> Method: boolean otpHasBeenUsed(String username) </p>
     * * <p> Description: Determine if the provided otp matched the one stored for the username.
     * If it does, deletes the one-time password so it can't be used, and returns true.
     * Otherwise, returns false. </p>
     * * @param username is a string that identifies a user in the table
     * * @param otp is the one-time password the user entered to update their password
     * * @return true if the otp is in the table, else return false.
     * */
    // Check to see if an otp is already in the database
    public boolean otpHasBeenUsed(String username, String otp) {
        String query = "SELECT otp FROM otpsTable WHERE username = ?";
        String remove = "DELETE FROM otpsTable WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            System.out.println(rs);
            if (rs.next()) {
                //get the stored otp
                String storedOtp = rs.getString("otp");
                if (storedOtp != null && storedOtp.equals(otp)) {
                    try (PreparedStatement del = connection.prepareStatement(remove)) {
                        del.setString(1, username);
                        //expire after use
                        del.executeUpdate();
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    
    
    /*******
     * <p> Method: String getFirstName(String username) </p>
     * * <p> Description: Get the first name of a user given that user's username.</p>
     * * @param username is the username of the user
     * * @return the first name of a user given that user's username 
     * */
    // Get the First Name
    public String getFirstName(String username) {
        String query = "SELECT firstName FROM userDB WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("firstName");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates a user's first name in the database.
     * @param username The username of the account to update.
     * @param firstName The new first name to set.
     */
    public void updateFirstName(String username, String firstName) {
        String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            currentFirstName = firstName;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves a user's middle name from the database.
     * @param username The username of the user.
     * @return The middle name, or null if not found.
     */
    public String getMiddleName(String username) {
        String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("middleName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates a user's middle name in the database.
     * @param username The username of the account to update.
     * @param middleName The new middle name to set.
     */
    public void updateMiddleName(String username, String middleName) {
        String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, middleName);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            currentMiddleName = middleName;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves a user's last name from the database.
     * @param username The username of the user.
     * @return The last name, or null if not found.
     */
    public String getLastName(String username) {
        String query = "SELECT LastName FROM userDB WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("lastName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates a user's last name in the database.
     * @param username The username of the account to update.
     * @param lastName The new last name to set.
     */
    public void updateLastName(String username, String lastName) {
        String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, lastName);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            currentLastName = lastName;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves a user's preferred first name from the database.
     * @param username The username of the user.
     * @return The preferred first name, or null if not found.
     */
    public String getPreferredFirstName(String username) {
        String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("firstName");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates a user's preferred first name in the database.
     * @param username The username of the account to update.
     * @param preferredFirstName The new preferred first name to set.
     */
    public void updatePreferredFirstName(String username, String preferredFirstName) {
        String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, preferredFirstName);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            currentPreferredFirstName = preferredFirstName;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves a user's email address from the database.
     * @param username The username of the user.
     * @return The email address, or null if not found.
     */
    public String getEmailAddress(String username) {
        String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("emailAddress");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates a user's email address in the database.
     * @param username The username of the account to update.
     * @param emailAddress The new email address to set.
     */
    public void updateEmailAddress(String username, String emailAddress) {
        String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, emailAddress);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            currentEmailAddress = emailAddress;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Fetches all account details for a given user and caches them in the class fields.
     * @param username The username to look up.
     * @return true if the user was found and details were loaded, false otherwise.
     */
    public boolean getUserAccountDetails(String username) {
        String query = "SELECT * FROM userDB WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();            
            rs.next();
            currentUsername = rs.getString(2);
            currentPassword = rs.getString(3);
            currentFirstName = rs.getString(4);
            currentMiddleName = rs.getString(5);
            currentLastName = rs.getString(6);
            currentPreferredFirstName = rs.getString(7);
            currentEmailAddress = rs.getString(8);
            currentAdminRole = rs.getBoolean(9);
            currentNewStudent = rs.getBoolean(10);
            currentNewStaff = rs.getBoolean(11);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Updates a specific role (Admin, Student, or Staff) for a user.
     * @param username The username of the account to update.
     * @param role The role to change ("Admin", "Student", or "Staff").
     * @param value The new boolean value for the role ("true" or "false").
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateUserRole(String username, String role, String value) {
        if (role.compareTo("Admin") == 0) {
            String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, value);
                pstmt.setString(2, username);
                pstmt.executeUpdate();
                if (value.compareTo("true") == 0)
                    currentAdminRole = true;
                else
                    currentAdminRole = false;
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        if (role.compareTo("Student") == 0) {
            String query = "UPDATE userDB SET newStudent = ? WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, value);
                pstmt.setString(2, username);
                pstmt.executeUpdate();
                if (value.compareTo("true") == 0)
                    currentNewStudent = true;
                else
                    currentNewStudent = false;
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        if (role.compareTo("Staff") == 0) {
            String query = "UPDATE userDB SET newStaff = ? WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, value);
                pstmt.setString(2, username);
                pstmt.executeUpdate();
                if (value.compareTo("true") == 0)
                    currentNewStaff = true;
                else
                    currentNewStaff = false;
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }
    
    public String getCurrentUsername() { return currentUsername;};
    public String getCurrentPassword() { return currentPassword;};
    public String getCurrentFirstName() { return currentFirstName;};
    public String getCurrentMiddleName() { return currentMiddleName;};
    public String getCurrentLastName() { return currentLastName;};
    public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};
    public String getCurrentEmailAddress() { return currentEmailAddress;};
    public boolean getCurrentAdminRole() { return currentAdminRole;};
    public boolean getCurrentNewStudent() { return currentNewStudent;};
    public boolean getCurrentNewStaff() { return currentNewStaff;};
    
    /**********
    * <p> Method: public void deleteUser(String username) </p>
    * * <p> Description: This method removes a user's record from the database. </p>
    * * @param username The username of the account to be deleted.
    * */
    public void deleteUser(String username) {
        String query = "DELETE FROM userDB WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }   

    /**
     * Dumps the entire content of the user table to the console for debugging purposes.
     * @throws SQLException if a database access error occurs.
     */
    public void dump() throws SQLException {
        String query = "SELECT * FROM userDB";
        ResultSet resultSet = statement.executeQuery(query);
        ResultSetMetaData meta = resultSet.getMetaData();
        while (resultSet.next()) {
        for (int i = 0; i < meta.getColumnCount(); i++) {
        System.out.println(
        meta.getColumnLabel(i + 1) + ": " +
                resultSet.getString(i + 1));
        }
        System.out.println();
        }
        resultSet.close();
    }

    /**
     * Closes the database statement and connection resources.
     */
    public void closeConnection() {
        try{ 
            if(statement!=null) statement.close(); 
        } catch(SQLException se2) { 
            se2.printStackTrace();
        } 
        try { 
            if(connection!=null) connection.close(); 
        } catch(SQLException se){ 
            se.printStackTrace(); 
        } 
    }

    /**
     * Retrieves all users from the database and formats them for display in a list.
     * @return A List of UserForList objects, each containing formatted user information.
     */
    // Method to get all users for the list
    public List<UserForList> getAllUsersForList() {
        List<UserForList> userList = new ArrayList<>();
        String query = "SELECT userName, firstName, middleName, lastName, emailAddress, adminRole, newStudent, newStaff FROM userDB";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("userName");
                String name = rs.getString("firstName") + " " + rs.getString("middleName") + " " + rs.getString("lastName");
                String email = rs.getString("emailAddress");
                String roles = "";
                if (rs.getBoolean("adminRole")) {
                    roles += "Admin ";
                }
                if (rs.getBoolean("newStudent")) {
                    roles += "Student ";
                }
                if (rs.getBoolean("newStaff")) {
                    roles += "Staff ";
                }
                userList.add(new UserForList(username, name, email, roles));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }
}

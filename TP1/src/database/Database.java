// BOLD CHANGE:
package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import entityClasses.Post; // Import Post
import entityClasses.Reply; // Import Reply
import entityClasses.UserForList;
import entityClasses.User;
import java.time.Instant;
import java.time.LocalDateTime;

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

    public Database() {

    }

    /**
     * Establishes a connection to the H2 database and initializes tables if they
     * don't exist.
     * 
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
     * Creates the necessary tables if they are not already present.
     * 
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

        // This SQL statement defines the structure of the table that stores invitation
        // codes.
        String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
                + "code VARCHAR(10) PRIMARY KEY, "
                + "emailAddress VARCHAR(255), "
                + "role VARCHAR(255))";
        statement.execute(invitationCodesTable);

        // Add expiry and uses columns if they don't exist
        try {
            statement.execute("ALTER TABLE InvitationCodes ADD COLUMN expiresAt TIMESTAMP");
        } catch (SQLException ignore) {
        }
        try {
            statement.execute("ALTER TABLE InvitationCodes ADD COLUMN usesRemaining INT DEFAULT 0");
        } catch (SQLException ignore) {
        }

        // seed a one-time code that expires in 1 minute
        try (PreparedStatement ps = connection.prepareStatement(
                "MERGE INTO InvitationCodes (code, emailAddress, role, expiresAt, usesRemaining) KEY(code) VALUES (?,?,?,?,?)")) {
            ps.setString(1, "CSE360A1");
            ps.setString(2, null);
            ps.setString(3, "MEMBER");
            ps.setTimestamp(4, Timestamp.from(Instant.now().plusSeconds(1 * 60))); // +1 minute
            ps.setInt(5, 1); // one-time use
            ps.executeUpdate();
        }

        String otpsTable = "CREATE TABLE IF NOT EXISTS otpsTable ("
                + "username VARCHAR(255) PRIMARY KEY, "
                + "otp VARCHAR(10))";
        statement.execute(otpsTable);

        // --- NEW TABLES FOR HW2 ---
        String postsTable = "CREATE TABLE IF NOT EXISTS postsDB ("
                + "postID INT AUTO_INCREMENT PRIMARY KEY, "
                + "authorUsername VARCHAR(255), "
                + "title VARCHAR(255), "
                + "content VARCHAR(4096), "
                + "thread VARCHAR(255) DEFAULT 'General', "
                + "visible BOOLEAN DEFAULT TRUE, " // added visibility feature
                + "deleted BOOLEAN DEFAULT FALSE, "
                + "timestamp TIMESTAMP)";
        statement.execute(postsTable);

        try {
            statement.execute("ALTER TABLE postsDB ADD COLUMN visible BOOLEAN DEFAULT TRUE");
        } catch (SQLException ignore) {
        }

        String repliesTable = "CREATE TABLE IF NOT EXISTS repliesDB ("
                + "replyID INT AUTO_INCREMENT PRIMARY KEY, "
                + "postID INT, "
                + "authorUsername VARCHAR(255), "
                + "content VARCHAR(2048), "
                + "visible BOOLEAN DEFAULT TRUE, " // added visibility feature
                + "visibility VARCHAR(20) DEFAULT 'public', "
                + "recipient VARCHAR(255), "
                + "timestamp TIMESTAMP)";
        statement.execute(repliesTable);

        try {
            statement.execute("ALTER TABLE repliesDB ADD COLUMN visible BOOLEAN DEFAULT TRUE");
        } catch (SQLException ignore) {
        }

        try {
            statement.execute("ALTER TABLE repliesDB ADD COLUMN visibility VARCHAR(20) DEFAULT 'public'");
        } catch (SQLException ignore) {
        }

        try {
            statement.execute("ALTER TABLE repliesDB ADD COLUMN recipient VARCHAR(255)");
        } catch (SQLException ignore) {
        }

        String viewedPostsTable = "CREATE TABLE IF NOT EXISTS viewed_posts ("
                + "postID INT, "
                + "username VARCHAR(255), "
                + "PRIMARY KEY (postID, username))";
        statement.execute(viewedPostsTable);

        String viewedRepliesTable = "CREATE TABLE IF NOT EXISTS viewed_replies ("
                + "replyID INT, "
                + "username VARCHAR(255), "
                + "PRIMARY KEY (replyID, username))";
        statement.execute(viewedRepliesTable);

        String moderationLogTable = "CREATE TABLE IF NOT EXISTS moderation_log ("
                + "logID INT AUTO_INCREMENT PRIMARY KEY, "
                + "postID INT, "
                + "username VARCHAR(255), "
                + "action VARCHAR(20), "
                + "reason VARCHAR(1024), "
                + "timestamp TIMESTAMP"
                + ")";
        statement.execute(moderationLogTable);

        // Add this inside the createTables() method in Database.java
        String gradingParamsTable = "CREATE TABLE IF NOT EXISTS grading_parameters ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255), "
                + "description VARCHAR(1024))";
        statement.execute(gradingParamsTable);

    }

    /**
     * Checks if the main user table is empty.
     * 
     * @return true if no users exist in the database, false otherwise.
     */
    public boolean isDatabaseEmpty() {
        String query = "SELECT COUNT(*) AS count FROM userDB";
        try {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("count") == 0;
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * Gets the total number of users currently in the database.
     * 
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
     * 
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
     * 
     * @return A List of strings, where each string is a username.
     */
    public List<String> getUserList() {
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
     * 
     * @param user The User object with username and password to verify.
     * @return true if the credentials are valid and the user is an admin, false
     *         otherwise.
     */
    public boolean loginAdmin(User user) {
        String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
                + "adminRole = TRUE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Authenticates a user with student privileges.
     * 
     * @param user The User object with username and password to verify.
     * @return true if the credentials are valid and the user is a student, false
     *         otherwise.
     */
    public boolean loginStudent(User user) {
        String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
                + "newStudent = TRUE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Authenticates a user with staff privileges.
     * 
     * @param user The User object with username and password to verify.
     * @return true if the credentials are valid and the user is staff, false
     *         otherwise.
     */
    public boolean loginStaff(User user) {
        String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
                + "newStaff = TRUE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a specific username already exists in the database.
     * 
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
     * 
     * @param user The User object to check.
     * @return The integer count of roles.
     */
    public int getNumberOfRoles(User user) {
        int numberOfRoles = 0;
        if (user.getAdminRole())
            numberOfRoles++;
        if (user.getNewStudent())
            numberOfRoles++;
        if (user.getNewStaff())
            numberOfRoles++;
        return numberOfRoles;
    }

    /**
     * Generates a new, unique invitation code, stores it in the database, and
     * returns it.
     * The code is valid for one use and expires after a short time.
     * 
     * @param emailAddress The email address the invitation is for.
     * @param role         The role(s) to be assigned upon registration.
     * @return The generated 6-character invitation code.
     */
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
     * 
     * @return The integer count of invitations.
     */
    public int getNumberOfInvitations() {
        String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
        try {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Checks if an email address has already been used for an invitation.
     * 
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
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves the role associated with a valid, unexpired invitation code.
     * 
     * @param code The invitation code to look up.
     * @return The role as a String if the code is valid, or an empty string
     *         otherwise.
     */
    public String getRoleGivenAnInvitationCode(String code) {
        String sql = "SELECT role FROM InvitationCodes " +
                "WHERE code = ? AND expiresAt > CURRENT_TIMESTAMP AND usesRemaining > 0";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, code == null ? "" : code.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Retrieves the email address associated with a valid, unexpired invitation
     * code.
     * 
     * @param code The invitation code to look up.
     * @return The email address as a String if the code is valid, or an empty
     *         string otherwise.
     */
    public String getEmailAddressUsingCode(String code) {
        String sql = "SELECT emailAddress FROM InvitationCodes " +
                "WHERE code = ? AND expiresAt > CURRENT_TIMESTAMP AND usesRemaining > 0";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, code == null ? "" : code.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getString("emailAddress");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Deletes an invitation code from the database after it has been used.
     * 
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
                    } catch (SQLException e) {
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
     * 
     * @param username    The username of the account to update.
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

    /**
     * Generates a one-time password for a user.
     * 
     * @param username The user for whom to generate the OTP.
     * @return The generated OTP.
     */
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

    /**
     * Checks if the provided OTP is valid for the given user and removes it after
     * use.
     * 
     * @param username The user's username.
     * @param otp      The one-time password to validate.
     * @return true if the OTP is valid, false otherwise.
     */
    public boolean otpHasBeenUsed(String username, String otp) {
        String query = "SELECT otp FROM otpsTable WHERE username = ?";
        String remove = "DELETE FROM otpsTable WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            System.out.println(rs);
            if (rs.next()) {
                String storedOtp = rs.getString("otp");
                if (storedOtp != null && storedOtp.equals(otp)) {
                    try (PreparedStatement del = connection.prepareStatement(remove)) {
                        del.setString(1, username);
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

    /**
     * Retrieves a user's first name.
     * 
     * @param username The user's username.
     * @return The user's first name.
     */
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
     * 
     * @param username  The username of the account to update.
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
     * 
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
     * 
     * @param username   The username of the account to update.
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
     * 
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
     * 
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
     * 
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
     * 
     * @param username           The username of the account to update.
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
     * 
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
     * 
     * @param username     The username of the account to update.
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
     * Fetches all account details for a given user and caches them in the class
     * fields.
     * 
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
     * 
     * @param username The username of the account to update.
     * @param role     The role to change ("Admin", "Student", or "Staff").
     * @param value    The new boolean value for the role ("true" or "false").
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

    public String getCurrentUsername() {
        return currentUsername;
    };

    public String getCurrentPassword() {
        return currentPassword;
    };

    public String getCurrentFirstName() {
        return currentFirstName;
    };

    public String getCurrentMiddleName() {
        return currentMiddleName;
    };

    public String getCurrentLastName() {
        return currentLastName;
    };

    public String getCurrentPreferredFirstName() {
        return currentPreferredFirstName;
    };

    public String getCurrentEmailAddress() {
        return currentEmailAddress;
    };

    public boolean getCurrentAdminRole() {
        return currentAdminRole;
    };

    public boolean getCurrentNewStudent() {
        return currentNewStudent;
    };

    public boolean getCurrentNewStaff() {
        return currentNewStaff;
    };

    /**
     * Deletes a user from the database.
     * 
     * @param username The username of the account to be deleted.
     */
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
     * Dumps the entire content of the user table to the console for debugging
     * purposes.
     * 
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
        try {
            if (statement != null)
                statement.close();
        } catch (SQLException se2) {
            se2.printStackTrace();
        }
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Retrieves all users from the database and formats them for display in a list.
     * 
     * @return A List of UserForList objects, each containing formatted user
     *         information.
     */
    public List<UserForList> getAllUsersForList() {
        List<UserForList> userList = new ArrayList<>();
        String query = "SELECT userName, firstName, middleName, lastName, emailAddress, adminRole, newStudent, newStaff FROM userDB";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("userName");
                String name = rs.getString("firstName") + " " + rs.getString("middleName") + " "
                        + rs.getString("lastName");
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

    // --- NEW DATABASE METHODS FOR POSTS (CRUD) ---

    /**
     * Creates a new post in the database.
     * 
     * @param post The Post object to be created.
     */
    public void create(Post post) {
        String sql = "INSERT INTO postsDB (authorUsername, title, content, thread, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, post.getAuthorUsername());
            pstmt.setString(2, post.getTitle());
            pstmt.setString(3, post.getContent());
            pstmt.setString(4, post.getThread());
            pstmt.setTimestamp(5, Timestamp.from(Instant.now()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new reply in the database.
     * 
     * @param reply The Reply object to be created.
     */
    public void create(Reply reply) { // overload
        String sql = "INSERT INTO repliesDB (postID, authorUsername, content, timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, reply.getPostID());
            pstmt.setString(2, reply.getAuthorUsername());
            pstmt.setString(3, reply.getContent());
            pstmt.setTimestamp(4, Timestamp.from(Instant.now()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all posts from the database that have not been "soft deleted".
     * 
     * @param username The username of the current user, to determine read status.
     * @return A List of Post objects.
     */
    public List<Post> getAllPosts(String username) {
        List<Post> posts = new ArrayList<>();
        boolean viewerIsStaff = false;
        try {
            viewerIsStaff = isUserStaff(username);
        } catch (Exception ignored) {
        }

        // If viewer is staff, counts include all replies. Otherwise restrict to
        // replies.
        String replyCountSubquery;
        String unreadCountSubquery;

        if (viewerIsStaff) {
            replyCountSubquery = "(SELECT COUNT(*) FROM repliesDB r WHERE r.postID = p.postID)";
            unreadCountSubquery = "(SELECT COUNT(*) FROM repliesDB r LEFT JOIN viewed_replies vr ON r.replyID = vr.replyID AND vr.username = ? WHERE r.postID = p.postID AND vr.replyID IS NULL)";
        } else {
            replyCountSubquery = "(SELECT COUNT(*) FROM repliesDB r WHERE r.postID = p.postID AND " +
                    " (r.visibility = 'public' OR (r.visibility = 'private' AND r.recipient = ?) OR r.authorUsername = ?) )";

            unreadCountSubquery = "(SELECT COUNT(*) FROM repliesDB r LEFT JOIN viewed_replies vr ON r.replyID = vr.replyID AND vr.username = ? "
                    +
                    " WHERE r.postID = p.postID AND vr.replyID IS NULL AND " +
                    " (r.visibility = 'public' OR (r.visibility = 'private' AND r.recipient = ?) OR r.authorUsername = ?) )";
        }

        String sql = "SELECT p.*, (v.postID IS NOT NULL) AS viewed, " +
                replyCountSubquery + " AS replyCount, " +
                unreadCountSubquery + " AS unreadReplyCount " +
                "FROM postsDB p " +
                "LEFT JOIN viewed_posts v ON p.postID = v.postID AND v.username = ? " +
                "ORDER BY p.timestamp DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            int idx = 1;
            if (viewerIsStaff) {
                // unreadCount vr.username
                pstmt.setString(idx++, username);
                // join v.username
                pstmt.setString(idx++, username);
            } else {
                // replyCount: recipient, author
                pstmt.setString(idx++, username); // for (r.recipient = ?)
                pstmt.setString(idx++, username); // for r.authorUsername = ?
                // unreadCount: vr.username, recipient, author
                pstmt.setString(idx++, username); // vr.username
                pstmt.setString(idx++, username); // recipient
                pstmt.setString(idx++, username); // author
                // join v.username
                pstmt.setString(idx++, username);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int postID = rs.getInt("postID");
                String author = rs.getString("authorUsername");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String thread = rs.getString("thread");
                boolean deleted = rs.getBoolean("deleted");
                boolean viewed = rs.getBoolean("viewed");
                int replyCount = rs.getInt("replyCount");
                int unreadReplyCount = rs.getInt("unreadReplyCount");

                // read legacy 'visible' flag if present; default true when missing
                boolean visible = true;
                try {
                    visible = rs.getBoolean("visible");
                } catch (SQLException ignore) {
                    // column missing in older DB schema -> assume visible
                }

                // fetch most recent moderation action for this post (if any)
                String actionUser = null;
                String actionReason = null;
                LocalDateTime actionTimestamp = null;
                try {
                    String modSql = "SELECT username, action, reason, timestamp FROM moderation_log WHERE postID = ? ORDER BY timestamp DESC LIMIT 1";
                    try (PreparedStatement modStmt = connection.prepareStatement(modSql)) {
                        modStmt.setInt(1, postID);
                        try (ResultSet mrs = modStmt.executeQuery()) {
                            if (mrs.next()) {
                                actionUser = mrs.getString("username");
                                actionReason = mrs.getString("reason");
                                Timestamp t = mrs.getTimestamp("timestamp");
                                if (t != null)
                                    actionTimestamp = t.toLocalDateTime();
                            }
                        }
                    }
                } catch (SQLException ignore) {
                    // moderation_log might not exist on some DBs - that's fine
                }

                // Construct Post. Use the constructor that includes moderation metadata and
                // visibility.
                Post post = new Post(
                        postID,
                        author,
                        title,
                        content,
                        thread,
                        deleted,
                        viewed,
                        replyCount,
                        unreadReplyCount,
                        visible,
                        actionUser,
                        actionReason,
                        actionTimestamp);

                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    /*****
     * <p>
     * Method: String getPostAuthor(int postID)
     * </p>
     * 
     * <p>
     * Description: This method retrieves the author’s username for a specific post
     * using the
     * post ID. If the post does not exist or an SQL exception occurs, the method
     * returns null.
     * </p>
     * 
     * @param postID The unique identifier for the post whose author's username is
     *               to be retrieved.
     * 
     * @return A String with the author’s username for the given postID
     */
    public String getPostAuthor(int postID) {
        String sql = "SELECT authorUsername FROM postsDB WHERE postID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("authorUsername");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*****
     * <p>
     * Method: boolean isUserStaff(String username)
     * </p>
     * 
     * <p>
     * Description: This method checks whether a specified user has been assigned
     * the staff role.
     * This allows the system to verify a user’s authorization level
     * when performing staff-specific actions.
     * </p>
     * 
     * @param username The username of the user whose staff role status is to be
     *                 checked.
     * 
     * @return A boolean value indicating whether the user has a staff role (TRUE if
     *         staff, FALSE otherwise).
     */
    public boolean isUserStaff(String username) {
        String sql = "SELECT newStaff FROM userDB WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("newStaff");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Searches for posts based on a keyword and an optional thread, excluding "soft
     * deleted" posts.
     * 
     * @param keyword  The keyword to search for in post titles and content.
     * @param thread   The thread to filter by. If "All Threads", no thread filter
     *                 is applied.
     * @param username The username of the current user, to determine read status.
     * @return A List of matching Post objects.
     */
    public List<Post> searchPosts(String keyword, String thread, String username) {
        List<Post> posts = new ArrayList<>();
        boolean viewerIsStaff = false;
        try {
            viewerIsStaff = isUserStaff(username);
        } catch (Exception ignored) {
        }

        String replyCountSubquery;
        String unreadCountSubquery;

        if (viewerIsStaff) {
            replyCountSubquery = "(SELECT COUNT(*) FROM repliesDB r WHERE r.postID = p.postID)";
            unreadCountSubquery = "(SELECT COUNT(*) FROM repliesDB r LEFT JOIN viewed_replies vr " +
                    "ON r.replyID = vr.replyID AND vr.username = ? " +
                    "WHERE r.postID = p.postID AND vr.replyID IS NULL)";
        } else {
            replyCountSubquery = "(SELECT COUNT(*) FROM repliesDB r WHERE r.postID = p.postID AND " +
                    "((r.visible = TRUE) OR r.visibility = 'public' OR (r.visibility = 'private' AND r.recipient = ?) OR r.authorUsername = ?) )";

            unreadCountSubquery = "(SELECT COUNT(*) FROM repliesDB r LEFT JOIN viewed_replies vr " +
                    "ON r.replyID = vr.replyID AND vr.username = ? " +
                    "WHERE r.postID = p.postID AND vr.replyID IS NULL AND " +
                    "((r.visible = TRUE) OR r.visibility = 'public' OR (r.visibility = 'private' AND r.recipient = ?) OR r.authorUsername = ?) )";
        }

        String sql = "SELECT p.*, (v.postID IS NOT NULL) AS viewed, " +
                replyCountSubquery + " AS replyCount, " +
                unreadCountSubquery + " AS unreadReplyCount " +
                "FROM postsDB p " +
                "LEFT JOIN viewed_posts v ON p.postID = v.postID AND v.username = ? " +
                "WHERE (LOWER(p.title) LIKE LOWER(?) OR LOWER(p.content) LIKE LOWER(?))";

        if (!"All Threads".equals(thread)) {
            sql += " AND p.thread = ?";
        }

        sql += " ORDER BY p.timestamp DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchKeyword = "%" + keyword + "%";
            int idx = 1;

            if (viewerIsStaff) {
                // unreadCount vr.username
                pstmt.setString(idx++, username);
                // join v.username
                pstmt.setString(idx++, username);
            } else {
                // replyCount: recipient, author
                pstmt.setString(idx++, username); // for (r.recipient = ?)
                pstmt.setString(idx++, username); // for r.authorUsername = ?
                // unreadCount: vr.username, recipient, author
                pstmt.setString(idx++, username); // vr.username
                pstmt.setString(idx++, username); // recipient
                pstmt.setString(idx++, username); // author
                // join v.username
                pstmt.setString(idx++, username);
            }

            // keyword and optional thread filters
            pstmt.setString(idx++, searchKeyword);
            pstmt.setString(idx++, searchKeyword);
            if (!"All Threads".equals(thread)) {
                pstmt.setString(idx++, thread);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int postID = rs.getInt("postID");
                    String author = rs.getString("authorUsername");
                    String title = rs.getString("title");
                    String content = rs.getString("content");
                    String postThread = rs.getString("thread");
                    boolean deleted = rs.getBoolean("deleted");
                    boolean viewed = rs.getBoolean("viewed");
                    int replyCount = rs.getInt("replyCount");
                    int unreadReplyCount = rs.getInt("unreadReplyCount");

                    // pull visibility like in getAllPosts (safe for older DBs)
                    boolean visible = true;
                    try {
                        visible = rs.getBoolean("visible");
                    } catch (SQLException ignore) {
                        // Column might not exist in an older DB; default to visible
                    }

                    // fetch most recent moderation action for this post (if any)
                    String actionUser = null;
                    String actionReason = null;
                    LocalDateTime actionTimestamp = null;
                    try {
                        String modSql = "SELECT username, action, reason, timestamp FROM moderation_log WHERE postID = ? ORDER BY timestamp DESC LIMIT 1";
                        try (PreparedStatement modStmt = connection.prepareStatement(modSql)) {
                            modStmt.setInt(1, postID);
                            try (ResultSet mrs = modStmt.executeQuery()) {
                                if (mrs.next()) {
                                    actionUser = mrs.getString("username");
                                    actionReason = mrs.getString("reason");
                                    Timestamp t = mrs.getTimestamp("timestamp");
                                    if (t != null)
                                        actionTimestamp = t.toLocalDateTime();
                                }
                            }
                        }
                    } catch (SQLException ignore) {
                        // moderation_log might not exist on some DBs
                    }

                    Post post = new Post(
                            postID, author, title, content, postThread,
                            deleted, viewed, replyCount, unreadReplyCount,
                            visible, actionUser, actionReason, actionTimestamp);
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }

    /**
     * Updates an existing post in the database.
     * 
     * @param post The Post object with updated information.
     */
    public void update(Post post) {
        String sql = "UPDATE postsDB SET title = ?, content = ? WHERE postID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, post.getTitle());
            pstmt.setString(2, post.getContent());
            pstmt.setInt(3, post.getPostID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * "Soft deletes" a post by setting its `deleted` flag to TRUE.
     * 
     * @param postID The ID of the post to delete.
     */
    public void deletePost(int postID) {
        String deletePostSql = "UPDATE postsDB SET title = 'deleted', content = 'deleted', deleted = TRUE WHERE postID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deletePostSql)) {
            pstmt.setInt(1, postID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a post from the database.
     * 
     * @param post The Post object to be deleted.
     */
    public void delete(Post post) { // overload
        deletePost(post.getPostID());
    }

    /**
     * Marks a post as read for a specific user.
     * 
     * @param postID   The ID of the post.
     * @param username The username of the user.
     */
    public void markPostAsRead(int postID, String username) {
        String sql = "MERGE INTO viewed_posts (postID, username) KEY(postID, username) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postID);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Moderation and Visibility Control for Posts

    /**
     * Hides post so that only authorized users can see it.
     * 
     * @param postID   The ID of the post that needs to be fetched.
     * @param username username of who is hiding the post.
     * @param reason   The reason why the post is being hidden.
     */
    public void hidePost(int postID, String username, String reason) {
        String sql = "UPDATE postsDB SET visible = FALSE WHERE postID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postID);
            pstmt.executeUpdate();
            logModerationAction(postID, username, "HIDE_POST", reason);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unhides post so that only authorized users can see it.
     * 
     * @param postID   The ID of the post that needs to be fetched.
     * @param username username of who is hiding the post.
     * @param reason   The reason why the post is being hidden.
     */
    public void unhidePost(int postID, String username, String reason) {
        String sql = "UPDATE postsDB SET visible = TRUE WHERE postID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postID);
            pstmt.executeUpdate();
            logModerationAction(postID, username, "UNHIDE_POST", reason);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Flags post and logs it.
     * 
     * @param postID   The ID of the post that needs to be fetched.
     * @param username username of who is hiding the post.
     * @param reason   The reason why the post is being hidden.
     */
    public void flagPost(int postID, String username, String reason) {
        // This does not change visibility, but just records the issue
        logModerationAction(postID, username, "FLAG_POST", reason);
    }

    // --- NEW DATABASE METHODS FOR REPLIES (CRUD) ---

    /**
     * Creates a new reply in the database.
     * 
     * @param reply The Reply object to be created.
     */
    public boolean createReply(Reply reply) {
        String sql = "INSERT INTO repliesDB (postID, authorUsername, content, visibility, recipient, timestamp) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, reply.getPostID());
            pstmt.setString(2, reply.getAuthorUsername());
            pstmt.setString(3, reply.getContent());
            pstmt.setString(4, reply.getVisibility() == null ? "public" : reply.getVisibility());
            pstmt.setString(5, reply.getPostAuthorUsername()); // recipient (may be null)
            pstmt.setTimestamp(6, Timestamp.from(Instant.now()));
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all replies for a specific post.
     * 
     * @param postID The ID of the post whose replies are to be fetched.
     * @return A List of Reply objects.
     */
    public List<Reply> getRepliesForPost(int postID, String username) {
        List<Reply> replies = new ArrayList<>();
        String sql = "SELECT r.*, vr.replyID IS NOT NULL AS viewed FROM repliesDB r "
                + "LEFT JOIN viewed_replies vr ON r.replyID = vr.replyID AND vr.username = ? "
                + "WHERE r.postID = ? ORDER BY r.timestamp ASC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, postID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int replyID = rs.getInt("replyID");
                String author = rs.getString("authorUsername");
                String content = rs.getString("content");
                boolean viewed = rs.getBoolean("viewed");

                // Legacy 'visible' boolean (may not exist)
                boolean visible = true;
                try {
                    visible = rs.getBoolean("visible");
                } catch (SQLException ignore) {
                    // Older DB: assume visible
                }

                // Newer visibility/recipient model (may not exist)
                String visibility = null;
                String recipient = null;
                try {
                    visibility = rs.getString("visibility");
                } catch (SQLException ignore) {
                }
                try {
                    recipient = rs.getString("recipient");
                } catch (SQLException ignore) {
                }

                // Keep moderation metadata null (same as original A)
                String actionUser = null;
                String actionReason = null;
                LocalDateTime actionTimestamp = null;

                Reply reply;
                if ("private".equalsIgnoreCase(visibility)) {
                    // Use the private-reply constructor from original B
                    reply = new Reply(replyID, postID, author, content, "private", recipient);
                } else {
                    // Use the legacy constructor from original A (visible + null moderation
                    // metadata)
                    reply = new Reply(replyID, postID, author, content,
                            visible, actionUser, actionReason, actionTimestamp);
                }

                reply.setViewed(viewed);
                replies.add(reply);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return replies;
    }

    /**
     * Updates an existing reply in the database.
     * 
     * @param reply The Reply object with updated information.
     */
    /**
     * Updates an existing reply in the database.
     * 
     * @param reply The Reply object with updated information.
     */
    public void update(Reply reply) { // overload
        String sql = "UPDATE repliesDB SET content = ? WHERE replyID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, reply.getContent());
            pstmt.setInt(2, reply.getReplyID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a reply from the database.
     * 
     * @param replyID The ID of the reply to delete.
     */
    public void deleteReply(int replyID) {
        String sql = "DELETE FROM repliesDB WHERE replyID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, replyID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a reply from the database.
     * 
     * @param reply The Reply object to be deleted.
     */
    public void delete(Reply reply) { // overload
        deleteReply(reply.getReplyID());
    }

    /**
     * Marks a reply as read for a specific user.
     * 
     * @param replyID  The ID of the reply.
     * @param username The username of the user.
     */
    public void markReplyAsRead(int replyID, String username) {
        String sql = "MERGE INTO viewed_replies (replyID, username) KEY(replyID, username) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, replyID);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Moderation and Visibility Control for Posts

    /**
     * Hides reply so that only authorized users can see it.
     * 
     * @param replyID  The ID of the reply
     * @param postID   The ID of the post whose replies needs to be fetched.
     * @param username The username of who is hiding the post.
     * @param reason   The reason why the post is being hidden.
     */
    public void hideReply(int replyID, int postID, String username, String reason) {
        String sql = "UPDATE repliesDB SET visible = FALSE WHERE replyID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, replyID);
            pstmt.executeUpdate();
            logModerationAction(postID, username, "HIDE_REPLY", reason);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unhides reply so that only authorized users can see it.
     * 
     * @param replyID  The ID of the reply
     * @param postID   The ID of the post whose replies needs to be fetched.
     * @param username The username of who is hiding the post.
     * @param reason   The reason why the post is being hidden.
     */
    public void unhideReply(int replyID, int postID, String username, String reason) {
        String sql = "UPDATE repliesDB SET visible = TRUE WHERE replyID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, replyID);
            pstmt.executeUpdate();
            logModerationAction(postID, username, "UNHIDE_REPLY", reason);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- ADMIN REQUESTS OPERATIONS ---

    /**
     * Creates a new admin request.
     * 
     * @param requester   The username of the requester.
     * @param description The description of the request.
     */
    public void createAdminRequest(String requester, String description) {
        String sql = "INSERT INTO admin_requests (requester, description, status, adminComments, created_at, updated_at) "
                + "VALUES (?, ?, 'Open', '', ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, requester);
            pstmt.setString(2, description);
            Timestamp now = Timestamp.from(Instant.now());
            pstmt.setTimestamp(3, now);
            pstmt.setTimestamp(4, now);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Flags reply and logs moderation.
     * 
     * @param replyID  The ID of the reply
     * @param postID   The ID of the post whose replies needs to be fetched.
     * @param username The username of who is hiding the post.
     * @param reason   The reason why the post is being hidden.
     */
    public void flagReply(int replyID, int postID, String username, String reason) {
        logModerationAction(postID, username, "FLAG_REPLY", reason);
    }

    // OLD
    /**
     * Logs a moderation action performed on a post.
     *
     * Each log entry stores who did it, what they did, why they did it, and when it
     * happened.
     *
     * @param postID   The ID of the post being modified.
     * @param username The username performing the action.
     * @param action   The action type, e.g. "HIDE", "UNHIDE", "FLAG_POST",
     *                 "FLAG_REPLY".
     * @param reason   The reason for the action (may be null for UNHIDE).
     */
    public void logPostVisibilityAction(int postID, String username, String action, String reason) {
        // Calls to the new method
        logModerationAction(postID, username, action, reason);
    }

    // NEW
    /**
     * <p>
     * Method: void logModerationAction(int postID, String username, String action,
     * String reason)
     * </p>
     *
     * <p>
     * Description: Writes a row to the moderation_log table to track moderation
     * outcomes
     * and prints a concise trace line to the console.
     * </p>
     */
    private void logModerationAction(int postID, String username, String action, String reason) {
        String sql = "INSERT INTO moderation_log (postID, username, action, reason, timestamp) "
                + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postID);
            pstmt.setString(2, username);
            pstmt.setString(3, action);
            pstmt.setString(4, reason);
            int rows = pstmt.executeUpdate();

            // Print to Console
            System.out.println(
                    "[MODERATION] postID=" + postID +
                            " user=" + username +
                            " action=" + action +
                            " reason=" + (reason == null ? "" : reason) +
                            " rowsInserted=" + rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * Method: void printModerationLog()
     * </p>
     *
     * <p>
     * Description: Reads all rows from the moderation_log
     * table and prints them to the console for moderation / testing purposes.
     * </p>
     */
    public void printModerationLog() {
        String sql = "SELECT logID, postID, username, action, reason, timestamp "
                + "FROM moderation_log ORDER BY logID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            System.out.println("========== MODERATION LOG ==========");
            while (rs.next()) {
                int logID = rs.getInt("logID");
                int loggedPostID = rs.getInt("postID");
                String loggedUser = rs.getString("username");
                String loggedAction = rs.getString("action");
                String loggedReason = rs.getString("reason");
                Timestamp ts = rs.getTimestamp("timestamp");

                System.out.println(
                        "#" + logID +
                                " postID=" + loggedPostID +
                                " user=" + loggedUser +
                                " action=" + loggedAction +
                                " reason=" + loggedReason +
                                " time=" + ts);
            }
            System.out.println("=========================================");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all admin requests from the database.
     * 
     * @return A List of AdminRequest objects.
     */
    public List<entityClasses.AdminRequest> getAllAdminRequests() {
        List<entityClasses.AdminRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM admin_requests ORDER BY updated_at DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new entityClasses.AdminRequest(
                        rs.getInt("requestID"),
                        rs.getString("requester"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("adminComments"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Updates an existing admin request.
     * 
     * @param req The AdminRequest object with updated information.
     */
    public void updateAdminRequest(entityClasses.AdminRequest req) {
        String sql = "UPDATE admin_requests SET description = ?, status = ?, adminComments = ?, updated_at = ? WHERE requestID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, req.getDescription());
            pstmt.setString(2, req.getStatus());
            pstmt.setString(3, req.getAdminComments());
            pstmt.setTimestamp(4, Timestamp.from(Instant.now()));
            pstmt.setInt(5, req.getRequestID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // For Testing Purposes Only

    // Declares 3 types of roles
    public enum Role {
        ADMIN, STUDENT, STAFF
    }

    public java.util.EnumSet<Role> getRoles(entityClasses.User user) {
        java.util.EnumSet<Role> roles = java.util.EnumSet.noneOf(Role.class);
        if (loginAdmin(user))
            roles.add(Role.ADMIN);
        if (loginStudent(user))
            roles.add(Role.STUDENT);
        if (loginStaff(user))
            roles.add(Role.STAFF);
        return roles;
    } // --- THREAD MANAGEMENT OPERATIONS ---

    /**
     * Creates a new discussion thread.
     * 
     * @param title The title of the new thread.
     */
    public void createThread(String title) {
        String sql = "INSERT INTO discussion_threads (title, visible, created_at) VALUES (?, TRUE, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setTimestamp(2, Timestamp.from(Instant.now()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all discussion threads.
     * 
     * @return A List of DiscussionThread objects.
     */
    public List<entityClasses.DiscussionThread> getAllThreads() {
        List<entityClasses.DiscussionThread> list = new ArrayList<>();
        String sql = "SELECT * FROM discussion_threads ORDER BY created_at ASC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new entityClasses.DiscussionThread(
                        rs.getString("title"),
                        rs.getBoolean("visible"),
                        rs.getTimestamp("created_at").toLocalDateTime()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // For Students: Only visible threads
    /**
     * Retrieves titles of all visible discussion threads.
     * 
     * @return A List of thread titles.
     */
    public List<String> getVisibleThreadTitles() {
        List<String> list = new ArrayList<>();
        list.add("All Threads");
        String sql = "SELECT title FROM discussion_threads WHERE visible = TRUE ORDER BY created_at ASC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // For Staff/Admin: All threads (including hidden)
    /**
     * Retrieves titles of all discussion threads, including hidden ones.
     * 
     * @return A List of thread titles.
     */
    public List<String> getAllThreadTitles() {
        List<String> list = new ArrayList<>();
        list.add("All Threads");
        String sql = "SELECT title FROM discussion_threads ORDER BY created_at ASC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Updates a discussion thread's title and visibility.
     * 
     * @param oldTitle The current title of the thread.
     * @param newTitle The new title of the thread.
     * @param visible  The new visibility status.
     */
    public void updateThread(String oldTitle, String newTitle, boolean visible) {
        String sqlThread = "UPDATE discussion_threads SET title = ?, visible = ? WHERE title = ?";
        String sqlPosts = "UPDATE postsDB SET thread = ? WHERE thread = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(sqlThread)) {
                pstmt.setString(1, newTitle);
                pstmt.setBoolean(2, visible);
                pstmt.setString(3, oldTitle);
                pstmt.executeUpdate();
            }

            if (!oldTitle.equals(newTitle)) {
                try (PreparedStatement pstmt = connection.prepareStatement(sqlPosts)) {
                    pstmt.setString(1, newTitle);
                    pstmt.setString(2, oldTitle);
                    pstmt.executeUpdate();
                }
            }

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
            }
        }
    }

    /**
     * Deletes a discussion thread.
     * 
     * @param title The title of the thread to delete.
     */
    public void deleteThread(String title) {
        String sql = "DELETE FROM discussion_threads WHERE title = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // --- GRADING PARAMETERS OPERATIONS (Staff Epic) ---

    /**
     * Creates a new grading parameter.
     * 
     * @param name        The name of the parameter (e.g., "Engagement").
     * @param description The description of the parameter.
     */
    public void createGradingParameter(String name, String description) {
        String sql = "INSERT INTO grading_parameters (name, description) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all grading parameters.
     * 
     * @return A list of GradingParameter objects.
     */
    public List<entityClasses.GradingParameter> getAllGradingParameters() {
        List<entityClasses.GradingParameter> list = new ArrayList<>();
        String sql = "SELECT * FROM grading_parameters ORDER BY id ASC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new entityClasses.GradingParameter(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Updates an existing grading parameter.
     * 
     * @param param The GradingParameter object with updated values.
     */
    public void updateGradingParameter(entityClasses.GradingParameter param) {
        String sql = "UPDATE grading_parameters SET name = ?, description = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, param.getName());
            pstmt.setString(2, param.getDescription());
            pstmt.setInt(3, param.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a grading parameter.
     * 
     * @param id The ID of the parameter to delete.
     */
    public void deleteGradingParameter(int id) {
        String sql = "DELETE FROM grading_parameters WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
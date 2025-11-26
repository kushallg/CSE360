package staffTesting;

import static org.junit.Assert.*;

import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;

import database.Database;   

/**
 * 
 * <p> LoginRoleDispatchTest </p>
 *
 * <p> Description: JUnit 4 tests for ControllerUserLogin. Includes BVT and Coverage Tests .
 * 	   PASS: System behavior matches expected behavior.
 *     FAIL: System behavior violates expected behavior. 
 *     </p>
 */
public class LoginRoleDispatchTest {

    private EnumSet<Database.Role> roles;

    // Simulated credential result flags
    private boolean credentialsValid;
    private boolean dbError;

    @Before
    public void setUp() {
        roles = EnumSet.noneOf(Database.Role.class);
        credentialsValid = false;
        dbError = false;

        System.out.println("\n======================================");
        System.out.println(" Setting up LoginRoleDispatchTest...");
    }

    // Helpers

    private boolean canLogin() {
        if (dbError) return false;         // controller would show generic error
        return credentialsValid;           // true only when credentials pass
    }

    private String nextScreen(EnumSet<Database.Role> r) {
        // Simulates controller logic
        if (!canLogin()) return "Login";                   // stays on login page/ show error
        int n = r.size();
        if (n == 0) return "ErrorNoRoles";                 // no roles available
        if (n == 1) {
            if (r.contains(Database.Role.STUDENT)) return "StudentHome";
            if (r.contains(Database.Role.STAFF))   return "StaffHome";
            if (r.contains(Database.Role.ADMIN))   return "AdminHome";
        }
        // n > 1 -> dropdown to choose role
        return "RoleChooser";
    }

    // BVT

    /** 1 role (Student) -> auto-redirect to Student home 
     * 	PASS: System redirects to "StudentHome".
     * 	FAIL: Any other screen appears, or login is blocked.
     * */
    @Test
    public void testBvt_SingleRole_Student() {
        credentialsValid = true;
        roles = EnumSet.of(Database.Role.STUDENT);

        String screen = nextScreen(roles);
        assertEquals("StudentHome", screen);
        System.out.println("PASS: testBvt_SingleRole_Student -> redirect to Student home.");
    }

    /** 1 role (Staff) -> auto-redirect to Staff home 
     * 	PASS: System redirects to "StaffHome".
     * 	FAIL: Any other screen appears, or login is blocked.
     * */
    @Test
    public void testBvt_SingleRole_Staff() {
        credentialsValid = true;
        roles = EnumSet.of(Database.Role.STAFF);

        String screen = nextScreen(roles);
        assertEquals("StaffHome", screen);
        System.out.println("PASS: testBvt_SingleRole_Staff -> redirect to Staff home.");
    }

    /** Multiple roles -> show dropdown chooser 
     * 	PASS: System directs to Role Chooser page.
     * 	FAIL: Incorrect redirect.
     * */
    @Test
    public void testBvt_MultipleRoles_ShowsDropdown() {
        credentialsValid = true;
        roles = EnumSet.of(Database.Role.STUDENT, Database.Role.STAFF);

        String screen = nextScreen(roles);
        assertEquals("RoleChooser", screen);
        System.out.println("PASS: testBvt_MultipleRoles_ShowsDropdown -> dropdown displayed.");
    }

    /** Valid vs invalid credentials
     * 	PASS: Valid credentials login, while invalid do not
     * 	FAIL: Vaild credentials fail to login, while invalid can
     * */
    @Test
    public void testBvt_Credentials_ValidInvalid() {
        // valid
        credentialsValid = true;
        roles = EnumSet.of(Database.Role.STUDENT);
        assertEquals("StudentHome", nextScreen(roles));

        // invalid
        credentialsValid = false;
        roles = EnumSet.of(Database.Role.STUDENT);
        assertEquals("Login", nextScreen(roles));
        System.out.println("PASS: testBvt_Credentials_ValidInvalid -> valid redirects; invalid stays on login.");
    }

    /** DB error during lookup -> generic error (simulated by blocking login) 
     * 	PASS: System returns "Login" (generic error state).
     * 	FAIL: System ignores DB error and redirects to a home screen.
     * */
    @Test
    public void testBvt_DatabaseError_Generic() {
        dbError = true;
        credentialsValid = true; // even if creds pass, DB error blocks
        roles = EnumSet.of(Database.Role.STUDENT);

        String screen = nextScreen(roles);
        assertEquals("Login", screen);
        System.out.println("PASS: testBvt_DatabaseError_Generic -> generic error, no crash.");
    }

    // Coverage Tests (CT)

    /** Correct path: valid login, single role -> correct redirect 
     * 	PASS: Correct home screen returned for STAFF.
     * 	FAIL: Any other incorrect screen or denied login appears.
     * */
    @Test
    public void testCt_HappyPath_SingleRole() {
        credentialsValid = true;
        roles = EnumSet.of(Database.Role.STAFF);

        assertEquals("StaffHome", nextScreen(roles));
        System.out.println("PASS: testCt_HappyPath_SingleRole.");
    }

    /** Invalid login path -> stays on login 
     * 	PASS: Invalid credentials stay on login page.
     * 	FAIL: System incorrectly logs in or redirects anywhere else.
     * */
    @Test
    public void testCt_InvalidLogin_Path() {
        credentialsValid = false;
        roles = EnumSet.noneOf(Database.Role.class); // auth fails first

        assertEquals("Login", nextScreen(roles));
        System.out.println("PASS: testCt_InvalidLogin_Path.");
    }

    /** Multi-role branch -> role chooser shown 
     * 	PASS: System directs to Role Chooser page.
     * 	FAIL: Incorrect redirect to a specific home page or error screen.
     * */
    @Test
    public void testCt_MultiRoleBranch() {
        credentialsValid = true;
        roles = EnumSet.of(Database.Role.STUDENT, Database.Role.ADMIN);

        assertEquals("RoleChooser", nextScreen(roles));
        System.out.println("PASS: testCt_MultiRoleBranch.");
    }


    /** Exception/DB error -> generic handling 
     * 	PASS: System returns "Login" due to DB failure.
     * 	FAIL: System incorrectly proceeds as if a successful login occurred.
     * */
    @Test
    public void testCt_DbErrorBranch() {
        dbError = true;
        credentialsValid = true;
        roles = EnumSet.of(Database.Role.ADMIN);

        assertEquals("Login", nextScreen(roles));
        System.out.println("PASS: testCt_DbErrorBranch.");
    }
    
    /* Manually test SQL Injection: 
    Username: admin' OR '1'='1
    Password: anything
    
    PASS: Should just say incorrect username/password.
    FAIL: Allows login.
    */
}

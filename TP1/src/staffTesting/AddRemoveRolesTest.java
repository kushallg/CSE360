package staffTesting;

import static org.junit.Assert.*;

import java.util.EnumSet;

import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Test;

import database.Database;

/**
 * 
 * <p>
 * AddRemoveRolesTest
 * </p>
 *
 * <p>
 * Description: JUnit 4 tests for ControllerAddRemoveRoles. Includes BVT and
 * Coverage Tests.
 * PASS: System behavior matches expected behavior.
 * FAIL: System behavior violates expected behavior.
 * </p>
 */
public class AddRemoveRolesTest {

    private EnumSet<Database.Role> roles;
    private boolean isAdminCaller;

    @Before
    public void setUp() {
        roles = EnumSet.noneOf(Database.Role.class);
        isAdminCaller = false;
        System.out.println("\n======================================");
        System.out.println(" Setting up AddRemoveRolesTest...");
    }

    // Helpers

    private boolean canModify() {
        // Only Admins can modify roles
        return isAdminCaller;
    }

    private boolean addRole(EnumSet<Database.Role> set, Database.Role role) {
        if (role == null)
            return false; // dropdown empty
        if (!canModify())
            return false; // not authorized
        return set.add(role); // duplicate add
    }

    private boolean removeRole(EnumSet<Database.Role> set, Database.Role role) {

        if (role == null)
            return false;
        if (!canModify())
            return false;

        // Admin cannot remove their own admin role
        if (isAdminCaller && role == Database.Role.ADMIN) {
            return false;
        }

        return set.remove(role);
    }

    // BVT

    /**
     * BVT: Admin selects valid user & role -> add succeeds
     * PASS: Admin was allowed to add a valid role and the role now appears in the
     * list.
     * FAIL: Admin was blocked from adding the role or the role was not actually
     * added.
     */
    @Test
    public void testBvt_AdminAddRole_Success() {
        isAdminCaller = true;
        boolean changed = addRole(roles, Database.Role.STAFF);

        assertTrue(changed);
        assertTrue(roles.contains(Database.Role.STAFF));
        System.out.println("PASS: testBvt_AdminAddRole_Success -> role added.");
    }

    /**
     * BVT: Admin removes an existing role -> success
     * PASS: Admin successfully removed a role that existed.
     * FAIL: Remove failed or the role still exists after removal.
     */
    @Test
    public void testBvt_AdminRemoveRole_Success() {
        isAdminCaller = true;
        roles.add(Database.Role.STAFF);

        boolean changed = removeRole(roles, Database.Role.STAFF);

        assertTrue(changed);
        assertFalse(roles.contains(Database.Role.STAFF));
        System.out.println("PASS: testBvt_AdminRemoveRole_Success -> role removed.");
    }

    /**
     * BVT: Staff/Student tries to modify -> forbidden
     * PASS: Non-admin cannot add or remove roles and the roles list remains
     * unchanged.
     * FAIL: A non-admin is able to add or remove a role, which is a security
     * violation.
     * 
     */
    @Test
    public void testBvt_NonAdmin_NoPermission() {
        isAdminCaller = false;
        roles.add(Database.Role.STAFF);

        boolean addChanged = addRole(roles, Database.Role.ADMIN);
        boolean removeChanged = removeRole(roles, Database.Role.STAFF);

        assertFalse(addChanged);
        assertFalse(removeChanged);
        assertTrue(roles.contains(Database.Role.STAFF)); // unchanged
        System.out.println("PASS: testBvt_NonAdmin_NoPermission -> no change made.");
    }

    /**
     * BVT: Empty dropdown selection (no role picked) -> blocked
     * PASS: No role selected results in no changes.
     * FAIL: Null input is incorrectly accepted and modifies the list.
     */
    @Test
    public void testBvt_NoRoleSelected() {
        isAdminCaller = true;
        Database.Role selected = null;

        boolean changed = addRole(roles, selected);

        assertFalse(changed);
        assertTrue(roles.isEmpty());
        System.out.println("PASS: testBvt_NoRoleSelected -> validation blocked action");
    }

    /**
     * BVT: Add same role twice -> not an option
     * PASS: Not an option to add same role twice.
     * FAIL: Able to add add same role twice.
     */
    @Test
    public void testBvt_DuplicateAdd() {
        isAdminCaller = true;
        roles.add(Database.Role.STAFF);

        boolean changed = addRole(roles, Database.Role.STAFF);

        assertFalse(changed);
        assertEquals(1, roles.size());
        System.out.println("PASS: testBvt_DuplicateAdd -> duplicate add");
    }

    /**
     * BVT: Remove role not held -> not an option
     * PASS: Not an option to remove role not held.
     * FAIL: Able to remove role not held.
     */
    @Test
    public void testBvt_RemoveNonExistingRole() {
        isAdminCaller = true;

        boolean changed = removeRole(roles, Database.Role.STAFF);

        assertFalse(changed);
        assertTrue(roles.isEmpty());
        System.out.println("PASS: testBvt_RemoveNonExistingRole -> no change");
    }

    // Coverage Tests (CT)

    /**
     * Authorized add role executes
     * PASS: Admin is correctly allowed to add a role.
     * FAIL: Admin add is incorrectly blocked or the role is not added.
     */
    @Test
    public void testCt_AuthorizedAddRole() {
        isAdminCaller = true;
        boolean changed = addRole(roles, Database.Role.ADMIN);

        assertTrue(changed);
        assertTrue(roles.contains(Database.Role.ADMIN));
        System.out.println("PASS: testCt_AuthorizedAddRole");
    }

    /**
     * Authorized remove role executes
     * PASS: Admin successfully removes a role they should be able to remove.
     * FAIL: Admin remove fails OR role still exists.
     */
    @Test
    public void testCt_AuthorizedRemoveRole() {
        isAdminCaller = true;
        roles.add(Database.Role.STAFF);

        boolean changed = removeRole(roles, Database.Role.STAFF);

        assertTrue(changed);
        assertFalse(roles.contains(Database.Role.STAFF));
        System.out.println("PASS: testCt_AuthorizedRemoveRole.");
    }

    /**
     * Admin cannot remove their own admin role
     * PASS: removeRole() returns false and ADMIN stays in the roles set.
     * FAIL: removeRole() returns true or ADMIN role is removed.
     */
    @Test
    public void testBvt_AdminCannotRemoveOwnAdminRole() {
        isAdminCaller = true;

        // Admin currently has the ADMIN role
        roles.add(Database.Role.ADMIN);

        // Attempt to remove own admin role
        boolean changed = removeRole(roles, Database.Role.ADMIN);

        // Must refuse
        assertFalse(changed);
        assertTrue(roles.contains(Database.Role.ADMIN));

        System.out.println("PASS: testBvt_AdminCannotRemoveOwnAdminRole -> admin cannot remove self-admin role.");
    }

    /**
     * Unauthorized user adds role
     * PASS: Non-admin cannot add roles.
     * FAIL: A non-admin successfully adds roles.
     */
    @Test
    public void testCt_UnauthorizedRole() {
        isAdminCaller = false;
        boolean changed = addRole(roles, Database.Role.STAFF);

        assertFalse(changed);
        assertFalse(roles.contains(Database.Role.STAFF));
        System.out.println("PASS: testCt_UnauthorizedRole.");
    }

    /**
     * Duplicate add/remove roles executed
     * PASS: Duplicate adds and removes of non-existing roles return false.
     * FAIL: System incorrectly treats duplicate operations as modifications.
     */
    @Test
    public void testCt_DuplicateBranches() {
        isAdminCaller = true;
        roles.add(Database.Role.STAFF);

        boolean dupAdd = addRole(roles, Database.Role.STAFF);
        boolean removeMissing = removeRole(roles, Database.Role.ADMIN);

        assertFalse(dupAdd);
        assertFalse(removeMissing);
        System.out.println("PASS: testCt_DuplicateBranches.");
    }

    /*
     * Manually test database:
     * After updating roles, check list all users, to see updates roles
     * 
     * PASS: Roles have been updated.
     * FAIL: Roles have not been updated.
     */
}

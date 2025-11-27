package adminRequestTesting;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import entityClasses.AdminRequest;

/**
 * AdminRequestTesting — automated JUnit tests for the Admin Ticketing Workflow.
 *
 * <p>
 * TP3 Requirements covered (Admin Requests):
 * </p>
 * <ul>
 * <li>R-TICKET-01: Staff can create requests for admins (default status
 * "Open").</li>
 * <li>R-TICKET-02: Admins can document actions and close requests (status
 * "Closed").</li>
 * <li>R-TICKET-03: Staff can reopen closed requests if the issue persists
 * (status "Reopened").</li>
 * <li>R-TICKET-04: Request history (admin comments) is preserved across status
 * changes.</li>
 * </ul>
 *
 * <p>
 * Notes on interpreting results:
 * </p>
 * <ul>
 * <li>Run this class using JUnit. The test method documents the specific
 * requirement it checks.</li>
 * <li>A green (passing) test indicates correct state transitions and data
 * retention.</li>
 * <li>A red (failing) test indicates a failure in the ticketing lifecycle
 * logic.</li>
 * </ul>
 * *
 * <p>
 * Manual test coverage (UI / integration tests):
 * </p>
 * <ul>
 * <li>MT-TICKET-01: Verify "Ticketing" button appears for Admins and Staff
 * (Admin Home / Staff Home).</li>
 * <li>MT-TICKET-02: Verify Staff can see "Create" and "Reopen" buttons, but not
 * "Resolve".</li>
 * <li>MT-TICKET-03: Verify Admins can see "Resolve" button, but not
 * "Create".</li>
 * </ul>
 */
public class AdminRequestTest {

    private AdminRequest request;
    private LocalDateTime now;

    @Before
    public void setUp() {
        now = LocalDateTime.now();
        // Simulating a request fetched from DB or created by Staff
        request = new AdminRequest(101, "staffUser", "Please merge sections A and B.", "Open", "", now, now);

        System.out.println("\n==============================");
        System.out.println(" Setting up AdminRequestTest environment...");
    }

    /**
     * testCreateRequestDefaultStatus()
     *
     * <p>
     * Section of the Requirement tested:
     * </p>
     * - Staff can create admin request (Default status Open)
     *
     * <p>
     * What this test checks:
     * </p>
     * Ensures that a newly created request has the correct initial status and
     * captures the requester's description.
     *
     * <p>
     * Interpreting results:
     * </p>
     * - PASS: Status is "Open" and description matches input.
     * - FAIL: Status is incorrect or description is lost.
     */
    @Test
    public void testCreateRequestDefaultStatus() {
        assertEquals("Open", request.getStatus());
        assertEquals("Please merge sections A and B.", request.getDescription());
        assertEquals("staffUser", request.getRequesterUsername());

        System.out.println("Creation test passed: Request initialized as 'Open'.\n");
        System.out.println("toString() output: " + request.toString() + "\n");
    }

    /**
     * testAdminResolveAndCloseRequest()
     *
     * <p>
     * Section of the Requirement tested:
     * </p>
     * - Admins can document actions and close requests
     *
     * <p>
     * What this test checks:
     * </p>
     * Simulates an Admin adding comments and changing status to "Closed".
     *
     * <p>
     * Interpreting results:
     * </p>
     * - PASS: Status updates to "Closed" and admin comments are stored.
     * - FAIL: Status does not update or comments are not saved.
     */
    @Test
    public void testAdminResolveAndCloseRequest() {
        // Simulate Admin Action
        String actionTaken = "Merged sections as requested.";
        request.setAdminComments(actionTaken);
        request.setStatus("Closed");
        request.setUpdatedAt(LocalDateTime.now());

        assertEquals("Closed", request.getStatus());
        assertEquals(actionTaken, request.getAdminComments());

        System.out.println("Admin resolution test passed: Request closed with comments.\n");
    }

    /**
     * testStaffReopenRequest()
     *
     * <p>
     * Section of the Requirement tested:
     * </p>
     * - Staff can reopen a closed request
     *
     * <p>
     * What this test checks:
     * </p>
     * Simulates a Staff member reopening a previously closed ticket and updating
     * the description.
     * Crucially, it checks that the previous Admin Comments are NOT lost (history
     * preservation).
     *
     * <p>
     * Interpreting results:
     * </p>
     * - PASS: Status is "Reopened", description is updated, and old admin comments
     * remain.
     * - FAIL: History is lost or status does not update.
     */
    @Test
    public void testStaffReopenRequest() {
        // Pre-condition: Ticket was closed by admin
        request.setAdminComments("Merged sections.");
        request.setStatus("Closed");

        // Action: Staff reopens it
        String newDescription = "Sections merged, but students missing from roster.";
        request.setDescription(newDescription);
        request.setStatus("Reopened");
        request.setUpdatedAt(LocalDateTime.now());

        assertEquals("Reopened", request.getStatus());
        assertEquals(newDescription, request.getDescription());
        assertEquals("Merged sections.", request.getAdminComments()); // History check

        System.out.println("Staff reopen test passed: Status updated, history preserved.\n");
    }
}

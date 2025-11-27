package gradingParametersTesting;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import entityClasses.GradingParameter;
import database.Database;
import java.sql.SQLException;
import java.util.List;

/**
 * <p> Title: GradingParameterTest </p>
 * * <p> Description: JUnit tests for the Grading Parameters CRUD functionality. 
 * Covers creation, reading, updating, and deleting of rubric tags. </p>
 * * <p> Requirements Checked: 
 * R-GRADING-01: Staff can create grading parameters.
 * R-GRADING-02: Staff can list all parameters.
 * R-GRADING-03: Staff can update a parameter.
 * R-GRADING-04: Staff can delete a parameter.
 * </p>
 */
public class GradingParameterTest {

    private Database db;
    
    @Before
    public void setUp() throws Exception {
        db = new Database();
        db.connectToDatabase();
        
        // Clean up table before each test to ensure a known state
        // Note: In a real environment, we might mock the DB, but for this project, 
        // we often use the H2 in-memory logic or just create new items.
        // Since we don't have a clearAll method, we will rely on creating unique items.
    }

    /**
     * Test Case: Create and Read a new Grading Parameter.
     * Checks if the parameter is successfully stored in the database.
     */
    @Test
    public void testCreateAndReadParameter() {
        String name = "Test Param " + System.currentTimeMillis();
        String desc = "Description for testing creation.";
        
        // 1. Create
        db.createGradingParameter(name, desc);
        
        // 2. Read
        List<GradingParameter> list = db.getAllGradingParameters();
        boolean found = false;
        for (GradingParameter gp : list) {
            if (gp.getName().equals(name) && gp.getDescription().equals(desc)) {
                found = true;
                break;
            }
        }
        
        assertTrue("The new parameter should be found in the database list.", found);
        System.out.println("PASS: Create and Read Parameter");
    }

    /**
     * Test Case: Update an existing Grading Parameter.
     * Checks if changes to name/description are persisted.
     */
    @Test
    public void testUpdateParameter() {
        String originalName = "UpdateMe " + System.currentTimeMillis();
        db.createGradingParameter(originalName, "Original Description");
        
        // Fetch the ID of the created item
        List<GradingParameter> list = db.getAllGradingParameters();
        GradingParameter toUpdate = null;
        for (GradingParameter gp : list) {
            if (gp.getName().equals(originalName)) {
                toUpdate = gp;
                break;
            }
        }
        assertNotNull("Setup failed: Could not find parameter to update.", toUpdate);
        
        // 1. Update
        String newName = originalName + " EDITED";
        String newDesc = "Updated Description";
        
        toUpdate.setName(newName);
        toUpdate.setDescription(newDesc);
        db.updateGradingParameter(toUpdate);
        
        // 2. Verify
        List<GradingParameter> newList = db.getAllGradingParameters();
        boolean matched = false;
        for (GradingParameter gp : newList) {
            if (gp.getId() == toUpdate.getId()) {
                assertEquals("Name should be updated", newName, gp.getName());
                assertEquals("Description should be updated", newDesc, gp.getDescription());
                matched = true;
                break;
            }
        }
        assertTrue("Updated parameter should exist in DB.", matched);
        System.out.println("PASS: Update Parameter");
    }

    /**
     * Test Case: Delete a Grading Parameter.
     * Checks if the parameter is removed from the database.
     */
    @Test
    public void testDeleteParameter() {
        String name = "DeleteMe " + System.currentTimeMillis();
        db.createGradingParameter(name, "To be deleted");
        
        // Fetch ID
        List<GradingParameter> list = db.getAllGradingParameters();
        GradingParameter toDelete = null;
        for (GradingParameter gp : list) {
            if (gp.getName().equals(name)) {
                toDelete = gp;
                break;
            }
        }
        assertNotNull("Setup failed: Could not find parameter to delete.", toDelete);
        
        // 1. Delete
        db.deleteGradingParameter(toDelete.getId());
        
        // 2. Verify
        List<GradingParameter> newList = db.getAllGradingParameters();
        boolean found = false;
        for (GradingParameter gp : newList) {
            if (gp.getId() == toDelete.getId()) {
                found = true;
                break;
            }
        }
        assertFalse("Deleted parameter should no longer exist in the list.", found);
        System.out.println("PASS: Delete Parameter");
    }
}
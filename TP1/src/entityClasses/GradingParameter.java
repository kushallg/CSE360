package entityClasses;

/**
 * <p> Title: GradingParameter Class </p>
 * * <p> Description: Represents a specific criterion used for grading student participation.
 * Contains a name (e.g., "Post Quality") and a description of that criterion. </p>
 * * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * * @author [Your Name]
 * @version 1.00
 */
public class GradingParameter {
    private int id;
    private String name;
    private String description;

    public GradingParameter(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return name + ": " + description;
    }
}
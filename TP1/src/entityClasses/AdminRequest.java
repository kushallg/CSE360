package entityClasses;

import java.time.LocalDateTime;

/**
 * Represents a request from a staff member to an admin.
 */
public class AdminRequest {
    private int requestID;
    private String requesterUsername;
    private String description;
    private String status; // "Open", "Closed", "Reopened"
    private String adminComments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AdminRequest(int requestID, String requesterUsername, String description,
            String status, String adminComments, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.requestID = requestID;
        this.requesterUsername = requesterUsername;
        this.description = description;
        this.status = status;
        this.adminComments = adminComments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getRequestID() {
        return requestID;
    }

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getAdminComments() {
        return adminComments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAdminComments(String adminComments) {
        this.adminComments = adminComments;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (by %s)", status, description, requesterUsername);
    }
}

import java.io.Serializable;

/**
 * Staff.java
 * Represents a staff member in the Duty Management System.
 */
public class Staff implements Serializable {

    private static final long serialVersionUID = 1L;

    private String staffId;
    private String name;
    private String department;
    private String contactNumber;
    private String email;
    private String role;

    // Constructor
    public Staff(String staffId, String name, String department,
                 String contactNumber, String email, String role) {
        this.staffId = staffId;
        this.name = name;
        this.department = department;
        this.contactNumber = contactNumber;
        this.email = email;
        this.role = role;
    }

    // Getters
    public String getStaffId()      { return staffId; }
    public String getName()         { return name; }
    public String getDepartment()   { return department; }
    public String getContactNumber(){ return contactNumber; }
    public String getEmail()        { return email; }
    public String getRole()         { return role; }

    // Setters
    public void setStaffId(String staffId)           { this.staffId = staffId; }
    public void setName(String name)                 { this.name = name; }
    public void setDepartment(String department)     { this.department = department; }
    public void setContactNumber(String contactNumber){ this.contactNumber = contactNumber; }
    public void setEmail(String email)               { this.email = email; }
    public void setRole(String role)                 { this.role = role; }

    @Override
    public String toString() {
        return staffId + " - " + name;
    }
}
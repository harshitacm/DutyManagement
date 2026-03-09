import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Duty.java
 * Represents a single duty assignment in the Duty Management System.
 */
public class Duty implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dutyId;
    private String staffId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String shiftType;   // Morning / Evening / Night
    private String location;
    private String status;      // Pending / Completed / Cancelled
    private String notes;

    // Constructor
    public Duty(String dutyId, String staffId, LocalDate date,
                LocalTime startTime, LocalTime endTime,
                String shiftType, String location) {
        this.dutyId    = dutyId;
        this.staffId   = staffId;
        this.date      = date;
        this.startTime = startTime;
        this.endTime   = endTime;
        this.shiftType = shiftType;
        this.location  = location;
        this.status    = "Pending";
        this.notes     = "";
    }

    // Calculate duty duration in hours
    public long getDurationHours() {
        return Duration.between(startTime, endTime).toHours();
    }

    // Check if this duty overlaps with another duty
    public boolean conflictsWith(Duty other) {
        if (!this.date.equals(other.date)) return false;
        return this.startTime.isBefore(other.endTime) &&
               this.endTime.isAfter(other.startTime);
    }

    // Getters
    public String    getDutyId()    { return dutyId; }
    public String    getStaffId()   { return staffId; }
    public LocalDate getDate()      { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime()   { return endTime; }
    public String    getShiftType() { return shiftType; }
    public String    getLocation()  { return location; }
    public String    getStatus()    { return status; }
    public String    getNotes()     { return notes; }

    // Setters
    public void setStatus(String status)       { this.status = status; }
    public void setNotes(String notes)         { this.notes = notes; }
    public void setDate(LocalDate date)        { this.date = date; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime)     { this.endTime = endTime; }
    public void setLocation(String location)   { this.location = location; }
    public void setShiftType(String shiftType) { this.shiftType = shiftType; }
}
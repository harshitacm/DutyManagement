import java.io.*;
import java.util.*;

/**
 * StaffManager.java
 * Manages all staff members – add, delete, search, and file persistence.
 */
public class StaffManager {

    private HashMap<String, Staff> staffMap;
    private static final String DATA_FILE = "staff.dat";

    public StaffManager() {
        staffMap = new HashMap<>();
        loadData();
    }

    /** Add a new staff member. Returns false if the ID already exists. */
    public boolean addStaff(Staff staff) {
        if (staffMap.containsKey(staff.getStaffId())) return false;
        staffMap.put(staff.getStaffId(), staff);
        saveData();
        return true;
    }

    /** Delete staff by ID. Returns true if removed. */
    public boolean deleteStaff(String staffId) {
        if (staffMap.remove(staffId) != null) {
            saveData();
            return true;
        }
        return false;
    }

    /** Get a single staff member by ID. */
    public Staff getStaff(String staffId) {
        return staffMap.get(staffId);
    }

    /** Return all staff as an ArrayList. */
    public ArrayList<Staff> getAllStaff() {
        return new ArrayList<>(staffMap.values());
    }

    /** Search staff by keyword (name, department, or ID). */
    public ArrayList<Staff> searchStaff(String keyword) {
        ArrayList<Staff> results = new ArrayList<>();
        keyword = keyword.toLowerCase();
        for (Staff s : staffMap.values()) {
            if (s.getName().toLowerCase().contains(keyword) ||
                s.getDepartment().toLowerCase().contains(keyword) ||
                s.getStaffId().toLowerCase().contains(keyword)) {
                results.add(s);
            }
        }
        return results;
    }

    public int getTotalStaff() { return staffMap.size(); }

    // ── Persistence ──────────────────────────────────────────────────────────

    private void saveData() {
        try (ObjectOutputStream out =
                new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(staffMap);
        } catch (IOException e) {
            System.err.println("Error saving staff data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream in =
                new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            staffMap = (HashMap<String, Staff>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading staff data: " + e.getMessage());
            staffMap = new HashMap<>();
        }
    }
}
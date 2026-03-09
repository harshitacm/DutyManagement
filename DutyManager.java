import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DutyManager.java
 * Manages all duty assignments – CRUD, conflict detection, roster generation,
 * statistics, and file persistence.
 */
public class DutyManager {

    private HashMap<String, Duty> dutyMap;
    private static final String DATA_FILE = "duties.dat";
    private int dutyCounter = 1;

    public DutyManager() {
        dutyMap = new HashMap<>();
        loadData();
        syncCounter();
    }

    // ── ID generation ────────────────────────────────────────────────────────

    private String nextId() {
        return String.format("D%04d", dutyCounter++);
    }

    /** Ensure counter is always higher than any existing duty number. */
    private void syncCounter() {
        for (String id : dutyMap.keySet()) {
            try {
                int n = Integer.parseInt(id.substring(1));
                if (n >= dutyCounter) dutyCounter = n + 1;
            } catch (NumberFormatException ignored) {}
        }
    }

    // ── Conflict detection ───────────────────────────────────────────────────

    /**
     * Returns true if the given staff member already has a duty on the same
     * date whose time overlaps [startTime, endTime).
     */
    public boolean hasConflict(String staffId, LocalDate date,
                               LocalTime startTime, LocalTime endTime) {
        for (Duty d : dutyMap.values()) {
            if (d.getStaffId().equals(staffId) && d.getDate().equals(date)) {
                if (startTime.isBefore(d.getEndTime()) &&
                    endTime.isAfter(d.getStartTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    /**
     * Add a new duty. The dutyId field in the supplied object is ignored;
     * a new sequential ID is generated automatically.
     *
     * @return the generated Duty ID
     */
    public String addDuty(Duty duty) {
        String id = nextId();
        Duty stored = new Duty(id, duty.getStaffId(), duty.getDate(),
                               duty.getStartTime(), duty.getEndTime(),
                               duty.getShiftType(), duty.getLocation());
        stored.setNotes(duty.getNotes());
        dutyMap.put(id, stored);
        saveData();
        return id;
    }

    /** Delete a duty by ID. Returns true if removed. */
    public boolean deleteDuty(String dutyId) {
        if (dutyMap.remove(dutyId) != null) {
            saveData();
            return true;
        }
        return false;
    }

    /** Retrieve a single duty by ID. */
    public Duty getDuty(String dutyId) {
        return dutyMap.get(dutyId);
    }

    /** Return all duties (unsorted). */
    public ArrayList<Duty> getAllDuties() {
        return new ArrayList<>(dutyMap.values());
    }

    /** Return all duties assigned to a specific staff member. */
    public ArrayList<Duty> getDutiesByStaff(String staffId) {
        return dutyMap.values().stream()
                .filter(d -> d.getStaffId().equals(staffId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Return all duties within [from, to], sorted by date then start time.
     */
    public ArrayList<Duty> getDutiesByDateRange(LocalDate from, LocalDate to) {
        return dutyMap.values().stream()
                .filter(d -> !d.getDate().isBefore(from) && !d.getDate().isAfter(to))
                .sorted(Comparator.comparing(Duty::getDate)
                                  .thenComparing(Duty::getStartTime))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /** Return all duties matching the given status string. */
    public ArrayList<Duty> getDutiesByStatus(String status) {
        return dutyMap.values().stream()
                .filter(d -> d.getStatus().equals(status))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /** Update the status of an existing duty. Returns true on success. */
    public boolean updateDutyStatus(String dutyId, String status) {
        Duty d = dutyMap.get(dutyId);
        if (d == null) return false;
        d.setStatus(status);
        saveData();
        return true;
    }

    public int getTotalDuties() { return dutyMap.size(); }

    // ── Statistics ───────────────────────────────────────────────────────────

    /**
     * Returns a map with counts keyed by:
     * Total, Pending, Completed, Cancelled, Morning, Evening, Night
     */
    public Map<String, Integer> getStatistics() {
        int pending = 0, completed = 0, cancelled = 0;
        int morning = 0, evening = 0, night = 0;

        for (Duty d : dutyMap.values()) {
            switch (d.getStatus()) {
                case "Pending":   pending++;   break;
                case "Completed": completed++; break;
                case "Cancelled": cancelled++; break;
            }
            switch (d.getShiftType()) {
                case "Morning": morning++; break;
                case "Evening": evening++; break;
                case "Night":   night++;   break;
            }
        }

        Map<String, Integer> stats = new HashMap<>();
        stats.put("Total",     dutyMap.size());
        stats.put("Pending",   pending);
        stats.put("Completed", completed);
        stats.put("Cancelled", cancelled);
        stats.put("Morning",   morning);
        stats.put("Evening",   evening);
        stats.put("Night",     night);
        return stats;
    }

    // ── Persistence ──────────────────────────────────────────────────────────

    private void saveData() {
        try (ObjectOutputStream out =
                new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(dutyMap);
        } catch (IOException e) {
            System.err.println("Error saving duty data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream in =
                new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            dutyMap = (HashMap<String, Duty>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading duty data: " + e.getMessage());
            dutyMap = new HashMap<>();
        }
    }
}
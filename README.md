# DutyManagement
A Java Swing desktop application for automating the scheduling, assignment, and tracking of staff duties. Built using Object-Oriented Programming principles and MVC architecture.
# Duty Management System

A Java Swing desktop application for automating the scheduling, assignment, and tracking of staff duties. Built using Object-Oriented Programming principles and MVC architecture.

---

## Project Info

| Field | Details |
|---|---|
| Course | Object Oriented Programming – 20CS21002 |
| Degree | B.Tech in Computer Science and Engineering |
| College | Geethanjali College of Engineering and Technology |
| Academic Year | 2025–26 (Sem-I) |
| Team | Rohan (24R11A6750), Harshita (24R11A6756), Satvika (24R11A6757) |
| Guide | Mr. Pandu Ranga Thota |

---

## Features

- **Dashboard** – Live statistics: total staff, total duties, pending, and completed counts
- **Staff Management** – Add, view, search, and delete staff members
- **Duty Assignment** – Assign duties with date, time, shift type, and location
- **Conflict Detection** – Automatically prevents double-booking of the same staff member
- **Duty Tracking** – Mark duties as Completed or Cancelled
- **Roster Generation** – Generate formatted duty rosters for any date range
- **Data Persistence** – All data is saved to disk automatically using Java serialization

---

## Project Structure

```
DutyManagementSystem/
├── Staff.java           # Data model for a staff member
├── Duty.java            # Data model for a duty assignment
├── StaffManager.java    # Manages staff – CRUD + file persistence
├── DutyManager.java     # Manages duties – CRUD, conflict detection, statistics
├── DutySystemGUI.java   # Main GUI – all screens and navigation
├── staff.dat            # Auto-generated: saved staff data
└── duties.dat           # Auto-generated: saved duty data
```

> `staff.dat` and `duties.dat` are created automatically on first run. Do not delete them while the app is running.

---

## Requirements

- Java JDK 8 or higher
- No external libraries required (uses only the Java standard library)

To check your Java version:
```bash
java -version
```

---

## How to Run

**Step 1 – Compile all files**
```bash
javac *.java
```

**Step 2 – Launch the application**
```bash
java DutySystemGUI
```

Make sure all five `.java` files are in the same directory when compiling.

---

## How to Use

### Adding Staff
1. Go to **Staff Management → Add New Staff** from the menu
2. Fill in Staff ID, Name, Department, Role, and Contact Number
3. Click **Save Staff**

### Assigning a Duty
1. Go to **Duty Management → Assign Duty**
2. Select a staff member from the dropdown
3. Enter date (DD/MM/YYYY), start time, end time (HH:MM), shift type, and location
4. Click **Assign Duty** – the system will automatically check for conflicts

### Viewing & Managing Duties
1. Go to **Duty Management → View All Duties**
2. Select any row and use the buttons to:
   - **Mark Complete** – marks the duty as Completed
   - **Mark Cancelled** – marks the duty as Cancelled
   - **Delete Duty** – permanently removes the duty

### Generating a Roster
1. Go to **Duty Management → Generate Roster**
2. Enter a From and To date (DD/MM/YYYY)
3. Click **Generate Roster** to view a formatted list of all duties in that period

---

## OOP Concepts Used

| Concept | Where Applied |
|---|---|
| Classes & Objects | `Staff`, `Duty` as blueprints; each record is an object |
| Encapsulation | All fields are `private`; accessed via getters/setters |
| Serializable Interface | Enables saving objects to `.dat` files |
| Association | `Duty` stores a `staffId` linking it to a `Staff` object |
| Exception Handling | Try-catch for file I/O and date/time parsing |
| Collections | `HashMap` for O(1) lookup; `ArrayList` for filtered results |
| Stream API | Filtering and sorting duties by date, status, and staff |

---

## Data Formats

| Field | Format | Example |
|---|---|---|
| Date | DD/MM/YYYY | 25/12/2025 |
| Time | HH:MM (24-hour) | 09:00, 17:00, 22:30 |
| Shift Type | Morning / Evening / Night | Morning |
| Status | Pending / Completed / Cancelled | Pending |

---

## Known Limitations

- No login/authentication system
- File-based storage only (no database)
- Single-location support per session
- No export to PDF or Excel

---

## Planned Future Enhancements

- Database integration (MySQL / PostgreSQL)
- User authentication with admin and staff roles
- Email / SMS notifications for upcoming duties
- Automatic duty rotation algorithm
- Export roster to PDF or Excel
- Mobile application
- Leave management and shift-swap requests
- Biometric attendance integration

---

## References

- *Head First Java* – Kathy Sierra & Bert Bates
- *Effective Java* – Joshua Bloch
- Oracle Java Documentation: https://docs.oracle.com/javase/tutorial/
- GeeksforGeeks Java: https://www.geeksforgeeks.org/java/
- W3Schools Java: https://www.w3schools.com/java/

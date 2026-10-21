# Student Attendance System

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-1F6FEB?style=for-the-badge)
![MVC](https://img.shields.io/badge/MVC_Pattern-8250DF?style=for-the-badge)

A desktop application for recording and analysing student absences, built with Java Swing as a team project for a Java programming course. Records can be added, edited, deleted, and searched, and the statistics tab produces per-student, per-group, and per-period reports.

## Screenshots

**Viewing and searching records**

![View records](screenshots/view-records.png)

![Search result](screenshots/search.png)

**Adding and editing records**

![Add and edit form](screenshots/add-edit-form.png)

**Statistics and reports**

![Student statistics](screenshots/student-statistics.png)

![Group statistics](screenshots/group-statistics.png)

## Features

- **Record management:** add, edit, and delete absence records (late, early leave, leave, absent)
- **Search:** case-insensitive search by student name or course name
- **Student report:** absence breakdown by type, with the detailed records of one student
- **Group statistics:** absences per student, organised by student group
- **Period report:** absences within a date range, sorted from the highest frequency to the lowest
- **Input validation:** empty fields, wrong date formats, and non-numeric class numbers are caught with clear error dialogs
- **Custom interface:** hover and pressed states on buttons, striped table rows, and the Nimbus look and feel

## Design

The code follows the Model-View-Controller idea:

- **Model:** `AbsenceRecord` holds the data with private fields and getters and setters, and the `AbsenceType` enum restricts the absence types to a fixed set
- **View:** `AttendanceSystemGUI` builds the interface with a tabbed layout (View Records, Add/Edit Records, Statistics and Reports), using `GridBagLayout` for the forms
- **Controller:** `AbsenceTableModel` (extends `AbstractTableModel`) connects the record list to the `JTable` and notifies it of inserts, updates, and deletions, while `ActionListener`s handle the user's actions

Other implementation details:

- `CustomButton` extends `JButton` and overrides its colours for the default, hover, and pressed states
- Statistics use a `HashMap` to map students to groups, and a custom comparator to sort the period report by absence count
- The application starts through `SwingUtilities.invokeLater()` so that the interface is created on the Event Dispatch Thread

## Run

Requires a JDK (Java 11 or later).

```bash
javac AttendanceSystemGUI.java
java AttendanceSystemGUI
```

The application starts with a few sample records and student groups, so every tab can be tried immediately. Data is kept in memory only and is reset when the program closes.

## Team Project

This was a team project of five students. My part was the interface design and a share of the implementation.

## Skills Demonstrated

Java · Swing GUI development · Layout managers · MVC architecture · Object-oriented design (encapsulation, inheritance, enums) · Collections (`ArrayList`, `HashMap`) · Input validation · Date handling · Team collaboration

## Author

**Mohammed Al-Zubair**
[github.com/Mohammed-Alzubair](https://github.com/Mohammed-Alzubair)

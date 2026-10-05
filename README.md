# Courier and Parcel Tracking Management System

A Java Swing and PostgreSQL based application developed as a micro-project for managing courier and parcel bookings and tracking their delivery status.

## Project Overview

The Courier and Parcel Tracking Management System provides a simple graphical interface to manage customers, book parcels, track parcels, update delivery status, and view parcel records.

## Technologies Used

- Java
- Java Swing
- PostgreSQL
- JDBC
- IntelliJ IDEA
- PostgreSQL JDBC Driver

## Features

- Add Customer
- Book Parcel
- Track Parcel
- Update Parcel Status
- View All Parcels
- Maintain Parcel Tracking History
- PostgreSQL Database Integration

## Parcel Status

The system supports the following statuses:

- Booked
- Picked Up
- In Transit
- Out for Delivery
- Delivered

## Database

The project uses PostgreSQL with the following tables:

- `customers`
- `parcels`
- `tracking`

## Project Files

- `Main.java` – Main GUI and application functionality
- `DatabaseConnection.java` – PostgreSQL database connection
- GUI screenshots – Demonstration of the working application

## GUI Screenshots

### Main GUI

![Main GUI](Screenshot%202026-10-05%20201743.png)

### Book Parcel

![Book Parcel](Screenshot%202026-10-05%20201805.png)

### Track Parcel

![Track Parcel](Screenshot%202026-10-05%20201821.png)

### Update Status

![Update Status](Screenshot%202026-10-05%20201849.png)

### View Parcels

![View Parcels](Screenshot%202026-10-05%20201902.png)

## How to Run

1. Install Java JDK 21 or later.
2. Install PostgreSQL.
3. Create the required database and tables.
4. Add the PostgreSQL JDBC driver to the Java project.
5. Update the database connection details in `DatabaseConnection.java`.
6. Run `Main.java`.

## Project Purpose

This project demonstrates the use of Java GUI programming, JDBC connectivity, and PostgreSQL database management to develop a basic courier and parcel tracking system.

## Developed By

**Rane Om Ajit**  
**Roll No.: ETCB539**  
**Third Year EXTC – B Division**  
**Academic Year: 2026–27**

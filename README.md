# Sunrise Dental Clinic Management System

Dental clinic management web application built with Java Servlets, HTML/CSS/JS, and MySQL.

## GitHub Repository

https://github.com/sumalka/dental-clinic-webapp.git

## Prerequisites

- Java JDK 17
- Apache Tomcat 10
- XAMPP (MySQL)
- Eclipse IDE / IntelliJ IDEA

## Setup Guide

### Step 1: Clone the Repository

```
git clone https://github.com/sumalka/dental-clinic-webapp.git
cd sunrise-dental-clinic
```

### Step 2: Start MySQL

- Open XAMPP Control Panel
- Start MySQL

### Step 3: Set Up Database

The database file is located in the project at: `database/dental_clinic.sql`

1. Go to http://localhost/phpmyadmin
2. If `dental_clinic` doesn't exist:
    - Click "New" in the left sidebar
    - Create database: `dental_clinic` with collation `utf8_general_ci`
    - Click "Create"
3. Select `dental_clinic` from the left sidebar
4. Click the "Import" tab
5. Click "Choose File" and select: `database/dental_clinic.sql`
6. Click "Go" at the bottom

After importing, you should see these tables:

- appointments
- bills
- dentists
- patients
- staff
- treatments
- users

### Step 4: Open in IDE

**Eclipse IDE:**
- File -> Import -> Maven -> Existing Maven Projects
- Select the project folder
- Wait for Maven dependencies to download

**IntelliJ IDEA:**
- File -> Open -> select the project folder
- Wait for Maven dependencies to download

### Step 5: Configure Tomcat

**Eclipse IDE:**
- Right-click project -> Run As -> Run on Server
- Select Apache Tomcat 10.x
- Click Finish

**IntelliJ IDEA:**
- Run -> Edit Configurations
- Add New Configuration -> Tomcat Server -> Local
- Set Tomcat home path
- Deployment tab -> Add -> Artifact -> `dental-clinic-webapp:war exploded`
- Application context: `/dental_clinic_webapp_war_exploded`

### Step 6: Update Database Credentials

Navigate to: `src/main/java/com/dentalclinic/utils/DatabaseConnection.java`

Update these values:

```
URL: jdbc:mysql://localhost:3306/dental_clinic
Username: root
Password: (blank)
```

### Step 7: Run

- Click Run button
- Open: http://localhost:8080/dental_clinic_webapp_war_exploded

## Login Credentials

```
Administrator:
 Username - admin
 Password - admin123
 
Receptionist: 
Username -  recept01
Password -  recept123
```

## Configuration

**DatabaseConnection.java**

```
URL: jdbc:mysql://localhost:3306/dental_clinic
Username: root
Password: (blank)
```

## Common Issues

### Database connection failed

- Check MySQL is running
- Verify database name is `dental_clinic`
- Check username is `root` and password is blank

### Port 8080 in use

**Windows:**
```
netstat -ano | findstr :8080
taskkill /PID [NUMBER] /F
```

**Mac:**
```
sudo lsof -i:8080
kill -9 [PID]
```

### 404 error

- Check URL: http://localhost:8080/dental_clinic_webapp_war_exploded
- Check Tomcat context path
- Verify WAR file is deployed correctly

### Tables missing

- Import `database/dental_clinic.sql` in phpMyAdmin
- Ensure database name is `dental_clinic`

## GitHub

https://github.com/sumalka/dental-clinic-webapp.git


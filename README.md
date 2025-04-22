# BTO Management System

A comprehensive Java application for managing Build-To-Order (BTO) public housing projects, applications, and enquiries.

## Project Overview

The BTO Management System is designed to streamline the process of managing public housing applications in Singapore. It provides different interfaces for applicants, housing officers, and managers, allowing each role to perform specific actions within the system.

## Features

### User Management

- Three user types: Applicants, HDB Officers, and HDB Managers
- Secure login using NRIC validation
- Password management functionality

### Project Management

- Create, edit, and delete BTO housing projects
- Configure project details (name, neighborhood, application dates)
- Manage available flat types (2-room, 3-room)
- Control project visibility

### Application Processing

- Submit applications for BTO projects
- Review and approve/reject applications
- Track application statuses (Pending, Successful, Unsuccessful, Booked, Withdrawn)
- Process withdrawal requests

### Enquiry System

- Submit enquiries about specific projects
- View and respond to enquiries
- Track enquiry history

### Officer Assignment

- Register officers to handle specific projects
- Approve officer registrations
- Set maximum officer count per project

### Reporting

- Generate booking reports
- Filter reports by marital status or flat type
- View project statistics

## User Interfaces

### Applicant Menu

- View available projects
- Apply for projects
- Check application status
- Submit and manage enquiries
- Request application withdrawal

### Officer Menu

- Manage project-specific enquiries
- Book flats for successful applicants
- Generate receipts
- Register to handle projects
- Access to applicant features for personal use

### Manager Menu

- Create and manage BTO projects
- Handle officer registrations
- Review applications and withdrawal requests
- Generate reports
- Manage project visibility

## System Architecture

The system follows object-oriented design principles with:

- Abstract base classes for common functionality
- Interface-based role definitions
- Proper encapsulation and inheritance
- Registry patterns for project management

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Any Java IDE (Eclipse, IntelliJ IDEA, etc.)

### Running the Application

1. Clone the repository
2. Open the project in your IDE
3. Run the `BTOManagementApp.java` file
4. Use the following default credentials:
   - Applicant: NRIC - S1234567A, Password - password
   - Officer: NRIC - T2109876H, Password - password
   - Manager: NRIC - T8765432F, Password - password

## Project Structure

```
src/
└── BTO_Management_System/
    ├── Applicant.java
    ├── Application.java
    ├── ApplicationStatus.java
    ├── BTOManagementApp.java
    ├── BTOProject.java
    ├── Date.java
    ├── Enquiry.java
    ├── FlatType.java
    ├── HDBManager.java
    ├── HDBOfficer.java
    ├── Interfaces.java
    ├── MaritalStatus.java
    ├── ProjectRegistry.java
    ├── RegisterStatus.java
    ├── RegistrationApplication.java
    └── User.java
```

## Future Enhancements

- GUI implementation
- Database integration for persistent storage
- Email notifications
- Statistical reporting and analytics

## Contributors

- Zheng Yijing
- Liu Lingyi
- Toh Fu Tang, Levon
- Tristan Amadeus Surya

## License

- ## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

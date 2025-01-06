# Library Management System 📚

## Overview
This project aims to emulate the management of library resources and enhance user experience by providing a simple way to search for and borrow books. The system emulates basic functions of a library system, utilizing the Google Books API for client requests, which returns results to the user in a readable format. SQLite is used for the database, storing user information and tracking borrowed books. User passwords are encrypted before storage using a simple AES algorithm.

## Technologies Used
- Java
- SQLite
- Google Books API
- Apache HttpClient
- AES Encryption

## Basic Functionality
- **Account Creation**: Users can create an account to manage their borrowed books.
- **Searching Books**: Users can search for books by author, title, or both, making it easy to find desired books.
- **Managing Borrowed Books**: Users can view, borrow, and return books, helping them keep track of their library activity.

## Installation
1. Clone the repository: `git clone https://github.com/mue864/library-cli.git`
2. Navigate to the project directory: `cd Library`
3. Compile the Java files: `javac *.java`
4. Run the application: `java Main`

## Future Enhancements
- User authentication and authorization
- A graphical user interface (GUI)
- Additional search filters (genre, publication date)
- Book review feature

> **Please note, this is a console app 😉**

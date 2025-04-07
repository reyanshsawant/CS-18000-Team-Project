# READMME

## Overview

This project is a command-line-based marketplace application developed as part of the CS18000 Team Project at Purdue University. It allows users to:

-   Create and manage accounts
    
-   List and search for items
    
-   Send and receive messages
    
-   Conduct secure transactions
    

----------

## Instructions to Compile and Run

1.  **Compile the Project**
    
    -   Open a terminal and navigate to the project directory.
        
    -   Run the following command to compile all `.java` files:
        
        bash
        
        CopyEdit
        
        `javac *.java` 
        
2.  **Run the Application**
    
    -   To run the main application:
        
        bash
        
        CopyEdit
        
        `java Main` 
        
    -   To run the test suite:
        
        bash
        
        CopyEdit
        
        `java RunLocalTestCase` 
        

----------

## Submission Details

-   **Reyansh Sawant** – Wrote `Item.java`, `ItemInterface.java`, and `ItemManager.java`
    
-   **Neil Lapsia** – Wrote `Message.java`, `MessageInterface.java`, and `MessageManager.java`
    
-   **Shankh Gupta** – Wrote `User.java`, `UserInterface.java`, and `UserManager.java`
    
-   **Arjun Anilkumar** – Wrote `RunLocalTestCase.java`, `Main.java`, and `MainDatabase.java`; integrated all modules, debugged the application, authored the `README.md`, and submitted the final build
    

----------

## Class and Interface Descriptions

1.  **`Item` Class**  
    Represents an item listed in the marketplace.
    
    -   Attributes: item ID, name, description, price, image path, seller username
        
    -   Provides full getter and setter functionality
        
    -   Implements `ItemInterface`
        
    -   Managed through `ItemManager`
        
2.  **`Message` Class**  
    Models a message exchanged between users.
    
    -   Attributes: sender, receiver, message content
        
    -   Implements `MessageInterface`
        
    -   Managed through `MessageManager`
        
3.  **`User` Class**  
    Represents a registered user.
    
    -   Attributes: username, password, balance
        
    -   Handles account creation, login, and balance updates
        
    -   Implements `UserInterface`
        
    -   Managed via `UserManager`
        
4.  **`ItemInterface` Interface**  
    Defines the required structure and methods for `Item` classes, such as:
    
    -   Accessors and mutators for item attributes
        
    -   Methods for setting and retrieving item data
        
5.  **`MessageInterface` Interface**  
    Outlines methods for handling message operations:
    
    -   Getters and setters for sender, receiver, and content
        
    -   Enables standardization across `Message` implementations
        
6.  **`UserInterface` Interface**  
    Specifies the methods any `User` class must implement:
    
    -   User authentication
        
    -   Balance retrieval and modification
        
7.  **`ItemManager` Class**  
    Handles a collection of `Item` objects.
    
    -   Add, delete, search, and persist items
        
    -   Thread-safe
        
    -   Data stored and retrieved from files
        
8.  **`MessageManager` Class**  
    Manages messaging operations.
    
    -   Send, receive, delete, and persist messages
        
    -   Thread-safe
        
    -   File-based data storage
        
9.  **`UserManager` Class**  
    Manages user-related actions.
    
    -   Create, delete, and retrieve users
        
    -   Update account balances
        
    -   Thread-safe and file-persistent
        
10.  **`RunLocalTestCase` Class**  
    Provides unit testing for core classes.
    
    -   Tests: item creation/deletion, user login and account handling, message functionality
        
    -   Simulates various user actions and interactions
        
11.  **`MainDatabase` Class**  
    Central access point for all data managers.
    
    -   Contains instances of `UserManager`, `ItemManager`, and `MessageManager`
        
    -   Supports centralized data access and coordination
        
12.  **`Main` Class**  
    The entry point for the application.
    
    -   Provides a command-line interface
        
    -   Handles user flow: login, item transactions, messaging, account management
        

----------

## Functional Overview

### User Authentication

-   Log in, create or delete an account
    
-   View received messages on login
    

### Marketplace

-   List, search, buy, and delete items
    
-   Manage account balance
    

### Messaging

-   Send and receive messages between users
    

### Persistence

-   All data is saved and retrieved via the appropriate manager
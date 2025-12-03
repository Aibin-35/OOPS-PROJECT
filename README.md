Inventory Management System — README
Overview
The Inventory Management System is a Java-based desktop application for managing products, categories, users, and stock movement in a store.
It uses Java OOP, Swing/AWT GUI, and MySQL as the backend database.
The system includes login authentication, stock tracking, expiry monitoring, and reporting features.

Key Features
 User Authentication
•	Login system
•	Role-based access: ADMIN and EMPLOYEE
 Category Management
•	Add, update, delete categories
•	Prevent duplicate names (unique constraint)
 Product Management
•	Add new products
•	Update existing items
•	Track quantities
•	Track unit cost
•	Monitor expiry dates
•	View product summary
 Stock Movement Tracking
•	IN (stock added)
•	OUT (stock reduced or sold)
•	Reason tracking (sale, usage, new stock)
•	Auto-calculation of:
o	Stock status (Low, Medium, Sufficient)
o	Expiry status (Good, Expiring Soon, Expired)
 Reports
•	Product summary view
•	Search by category
•	Real-time stock status
Automatic Cleanup
A MySQL Event runs daily to delete expired products:
DELETE FROM products 
WHERE expiry_date IS NOT NULL 
AND expiry_date < CURDATE();

Technologies Used
Component	Technology
Language	Java (OOP)
Database	MySQL / MariaDB
GUI	Java Swing / AWT
Server	XAMPP (phpMyAdmin)
Libraries	MySQL Connector/J
IDE	Eclipse (Runnable JAR exported)

 Database
Database name: inventory_db
 Includes the following tables:
•	users
•	categories
•	products
•	stock_movements
•	product_summary (view)
The SQL file loads:
•	Sample users
•	Sample products
•	Sample stock entries
•	Sample categories
Default Login Credentials
Username	Password	Role
admin	admin123	ADMIN
emp1	emp123	EMPLOYEE
hans	123	EMPLOYEE
stiv	123	EMPLOYEE
anand	123	EMPLOYEE

 How to Import the Database
1.	Open phpMyAdmin
2.	Click Databases
3.	Create a new database:
4.	inventory_db
5.	Select the new database
6.	Go to Import
7.	Choose: inventory_db.sql
8.	Click Go 

How to Run the Application
Method 1: Using the JAR File
1.	Install Java (JRE/JDK)
2.	Double-click:
3.	InventorySystem.jar
4.	Application will launch
Method 2: Using Eclipse
1.	File → Import → Existing Java Project
2.	Add MySQL Connector to Build Path
3.	Locate and run:
4.	Main.java

 Future Enhancements
•	Billing module with GST
•	Barcode scanner support
•	SMS/email alerts for low stock
•	Export reports (Excel/PDF)
•	Cloud database support

 Conclusion
This Inventory Management System demonstrates core Java OOP concepts with real-world application design.
It provides a complete solution for product tracking, stock management, and expiry monitoring, suitable for academic and small business use.


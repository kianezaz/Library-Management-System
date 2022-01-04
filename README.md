# Library-Management-System
This Library Management System is a command line based application in which librarians and members of the library can interact with the library's inventory. The motivation for this project was to strengthen my object oriented design skills and begin to apply database concepts.

## Functionalities

**Librarians**:

- Add books to catalog
- Remove books from catalog
- Delete a regular member's account
- Delete his/her own account

**Members**:

- Search books in catalog
- Check out books
- Renew currently checked out books
- Returned currently checked out books
- View and pay late fees
- Delete his/her own account

## To run locally on your machine
To run this project, you'll need to have Java and MySQL installed on your system. You'll also need to have port 3306 (the default port of the MySQL protocol) open.

Next,
```
git clone https://github.com/kianezaz/Library-Management-System.git
cd Library-Management-System/src
javac librarymanagementsystem/*.java
java -cp .:../lib/mysql-connector-java-8.0.26.jar librarymanagementsystem.Main
```

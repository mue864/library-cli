package org.library;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;

public class Database {

//    ArrayLists to save book data when retrieved
    private final ArrayList<String> bookAuthorList = new ArrayList<>();
    private final ArrayList<String> bookTitleList = new ArrayList<>();
    private final ArrayList<String> bookISBN = new ArrayList<>();
    private final ArrayList<String> bookPublisherList = new ArrayList<>();
    private final ArrayList<String> bookPageList = new ArrayList<>();
    private final ArrayList<String> bookPublishedDate = new ArrayList<>();

    private int userID;
    private Connection connect() {
        String url = "jdbc:sqlite:library.db";
        Connection con = null;

        try {
            con = DriverManager.getConnection(url);
        } catch (SQLException e ) {
            System.out.println(e.getMessage());
        }

        return con;
    }
// retrieves the current userID and passes it to DB connector
    public int getUserID(String userName) {
        return retrieveCurrentUserID(userName);
    }
//    the bones for retrieving the userID
    private int retrieveCurrentUserID(String userName) {
        String retrieveQuery = "SELECT userID FROM users WHERE userName = '" + userName + "'";
        try (Connection connection = this.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(retrieveQuery);
            ResultSet resultSet = preparedStatement.executeQuery();) {
            if (resultSet.next()) {
                userID = resultSet.getInt("userID");

                if (resultSet.wasNull()) {
                    userID = 0;
                }
                return userID;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public void setBookStorage(String bookName, String bookAuthor, String isbn, String publisher,
                               String publishedDate, String pageCount, int userID) {
        addBookInfo(bookName,bookAuthor,publisher,isbn,publishedDate, pageCount,userID);
    }
//    the id must be fetched from the current user db. but how?
    private void addBookInfo (String bookTitle, String bookAuthor,
                              String bookPublisher, String isbn, String publishedDate, String pages, int userID) {
        String insertInfo = "INSERT INTO books(title, author, publisher, isbn, pages, publishedDate, userID) VALUES(?,?,?,?,?,?,?)";

        try (Connection connection = this.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(insertInfo);) {
            preparedStatement.setString(1,bookTitle);
            preparedStatement.setString(2,bookAuthor);
            preparedStatement.setString(3,bookPublisher);
            preparedStatement.setString(4,isbn);
            preparedStatement.setString(5,pages);
            preparedStatement.setString(6,publishedDate);
            preparedStatement.setInt(7,userID);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createTables() {
        String bookTable = "CREATE TABLE IF NOT EXISTS books (\n"
                +   "bookID INTEGER PRIMARY KEY AUTOINCREMENT, \n"
                +   "title TEXT NOT NULL, \n"
                +   "author TEXT NOT NULL, \n"
                +   "publisher TEXT NOT NULL, \n"
                +   "isbn TEXT, \n"
                +   "pages TEXT, \n"
                +   "publishedDate TEXT, \n"
                +   "userID INTEGER NOT NULL, \n"
                +   "FOREIGN KEY (userID) REFERENCES users (userID)"
                +   ");";

        String userTable = "CREATE TABLE IF NOT EXISTS users (\n"
                +   "userID INTEGER PRIMARY KEY AUTOINCREMENT, \n"
                +   "userName TEXT NOT NULL, \n"
                +   "userLastName TEXT NOT NULL, \n"
                +   "password TEXT NOT NULL, \n"
                +   "school TEXT NOT NULL, \n"
                +   "preference TEXT NOT NULL"
                +   ");";

        String sqlCreateKeyTable = "CREATE TABLE IF NOT EXISTS storage (\n"
                +   "value TEXT NOT NULL, \n"
                +   "isCreated INTEGER PRIMARY KEY AUTOINCREMENT"
                +   ");";

        String sqlCreateCurrentUserTable = "CREATE TABLE IF NOT EXISTS currentUser (\n"
                +   "userID INTEGER NOT NULL"
                +   ");";

        try (Connection connection = this.connect();
            PreparedStatement createBookTable = connection.prepareStatement(bookTable);
            PreparedStatement createUserTable = connection.prepareStatement(userTable);
            PreparedStatement createKeyTable = connection.prepareStatement(sqlCreateKeyTable);
            PreparedStatement createCurrentUser = connection.prepareStatement(sqlCreateCurrentUserTable)) {

           createBookTable.execute();
           createUserTable.execute();
           createKeyTable.execute();
           createCurrentUser.execute();

//            System.out.println("Tables created");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addUserInfo(String userName, String userLastName, String password,
                               String preference, String school) {
        createTables();
        String sql = "INSERT INTO users(userName, userLastName, password, school, preference) VALUES (?,?,?,?,?)";
        try (Connection connection = this.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, userLastName);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, school);
            preparedStatement.setString(5, preference);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

//    checks if there is a duplicate userName in the database
    public boolean getCheckDuplicateUserName(String userName) {
        return checkDuplicateUserName(userName);
    }

//supposed to store the key in the database
    private void keyStorage(SecretKey key) {
        String sqlInsert = "INSERT INTO storage (value) VALUES (?)";

        try (Connection connection = this.connect();
            PreparedStatement insertStatement = connection.prepareStatement(sqlInsert);) {


//            converts the key from bytes to string
            String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

            insertStatement.setString(1, encodedKey);
            insertStatement.execute();

        } catch (SQLException e) {
            System.out.println("Here");
            System.out.println(e.getMessage());
        }
    }
    /**
     *
     * @param key the secretKey to be stored
     */
    public void getKeyStorage(SecretKey key) {
        keyStorage(key);
    }

//    retrieve the int value to check if a key has been stored yet
    private int retrieveExistsConfirmation() {
        String sql = "SELECT isCreated FROM storage LIMIT 1";

        try (Connection connection = this.connect();
            PreparedStatement retrieveStatement = connection.prepareStatement(sql);
            ResultSet rs = retrieveStatement.executeQuery()) {

            if (rs.next()) {
                int isCreated = rs.getInt("isCreated");

                if (rs.wasNull()) {
                    return 0;
                } else {
                    return isCreated;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int getExistConfirmation() {
        return retrieveExistsConfirmation();
    }

//    retrieve key from the database
    private SecretKey retrieveKey() {
        String sql = "SELECT value FROM storage LIMIT 1";

        try (Connection connection = this.connect();
             PreparedStatement selectStatement = connection.prepareStatement(sql);
             ResultSet keyResult = selectStatement.executeQuery()) {

            while (keyResult.next()) {
//                Getting the encoded key from db
                String encodedKey = keyResult.getString("value");

//                Decode it back to bytes
                byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

                return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        return null;
    }

    public SecretKey getKey() {
        return retrieveKey();
    }


    private boolean checkDuplicateUserName(String name) {
        createTables();
        String sql = "SELECT userName FROM users";

        try (Connection connection = this.connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                String userName = rs.getString("userName");
//              If name exists in the database return true else return false
                if (userName.equals(name)) {
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

//    returns encrypted password
    public String getSecure() {
        return retrievePassword();
    }

    private String retrievePassword() {
        String sql = "SELECT userName, password FROM users";
        String data = "";
        try (Connection connection = this.connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                data = rs.getString("password");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return data;
    }

//    setter method to add data to the database
    public void setUserInfo(String userName, String userLastName, String password,
                            String preference, String school) {

        addUserInfo(userName, userLastName, password, preference, school);
    }

//  Sets the active user for that particular session, since it's one at a time. It's done through the main class
    public void setCurrentUSerID(int userID) {
        currentUserID(userID);
    }
//   inserts the id of the active user
    private void currentUserID(int userID) {
        String setUserID = "INSERT INTO currentUser(userID) VALUES(?)";
        try (Connection connection = this.connect();
             PreparedStatement statement = connection.prepareStatement(setUserID);) {
            statement.setInt(1,userID);
            statement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

//    This one is supposed to be used when adding books
    public int getActiveUserID() {
        return retrieveActiveUserID();
    }

    private int retrieveActiveUserID() {
        String retrieveSQL = "SELECT userID FROM currentUser LIMIT 1";

        try (Connection connection = this.connect();
             PreparedStatement statement = connection.prepareStatement(retrieveSQL);
             ResultSet rs = statement.executeQuery()) {

            // Check if the ResultSet has at least one row
            if (rs.next()) {
                // Now, it is safe to access the column
                return rs.getInt("userID");
            } else {
                System.out.println("No user found.");  // Debug statement for no results
            }
        } catch (SQLException e) {
            System.out.println("Problem: " + e.getMessage());
        }
        return 0;  // Return 0 if no userID is found
    }

    public void setReplaceOldID(int oldID, int newID) {
        replaceOldID(oldID, newID);
    }

    private void replaceOldID(int oldID, int newID) {
        String deleteSQL = "DELETE FROM currentUser WHERE userID = ?";
        String insertSQL = "INSERT INTO currentUser (userID) VALUES (?)";

        try (Connection connection = this.connect();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL);
             PreparedStatement insertStatement = connection.prepareStatement(insertSQL)) {

            // Delete the row with the oldID
            deleteStatement.setInt(1, oldID);
            deleteStatement.executeUpdate();

            // Insert the new row with the newID
            insertStatement.setInt(1, newID);
            insertStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void getUserBooks(int activeUserID) {
        userBooks(activeUserID);
    }

//    retrieves the current user books
    private void userBooks(int activeUserID) {
        String sqlRetrieveBooks = "SELECT * FROM books WHERE userID = " + activeUserID;

        try (Connection connection = this.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlRetrieveBooks);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {

                //                extracting the book title
                    String bookTitle = rs.getString("title");
                    bookTitleList.add(bookTitle);
//                extracting book author names
                    String bookAuthor = rs.getString("author");
                    bookAuthorList.add(bookAuthor);
//                extracting the publisher
                    String publisher = rs.getString("publisher");
                    bookPublisherList.add(publisher);
//                extracting the isbn
                    String isbn = rs.getString("isbn");
                    bookISBN.add(isbn);
//                extracting the pages
                    String pages = rs.getString("pages");
                    bookPageList.add(pages);
//                extracting the published date
                    String publishedDate = rs.getString("publishedDate");
                    bookPublishedDate.add(publishedDate);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int checkBorrowedBooks(int activeUserID) {
        String retrieveBook = "SELECT * FROM books where userID = " +activeUserID;

        try (Connection connection = this.connect();
            PreparedStatement statement = connection.prepareStatement(retrieveBook);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                bookAuthorList.add(rs.getString("author"));
            }
            return bookAuthorList.size();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return 0;
    }

//   getter methods for getting the retrieved book information
    public ArrayList<String> getBookAuthorList() {
        return bookAuthorList;
    }

    public ArrayList<String> getBookTitleList() {
        return  bookTitleList;
    }

    public ArrayList<String> getBookISBN() {
        return bookISBN;
    }

    public ArrayList<String> getBookPageList() {
        return bookPageList;
    }

    public ArrayList<String> getBookPublishedDate() {
        return bookPublishedDate;
    }

    public ArrayList<String> getBookPublisherList() {
        return bookPublisherList;
    }


    public int getDeleteBook(String isbn) {
        return deleteBook(isbn);
    }
//  deletes book from the table using the isbn linked to the book
//    makes no sense, book ID won't revert when deleted
    private int deleteBook(String isbn) {
        String sqlDelete = "DELETE FROM books WHERE isbn = ?";

        try (Connection connection = this.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlDelete);) {

            preparedStatement.setString(1,isbn);
            preparedStatement.executeUpdate();
//            if successful, return 0
            return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
//            if something bad happens, return 1
            return 1;
        }
    }
}

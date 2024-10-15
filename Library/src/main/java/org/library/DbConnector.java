package org.library;


import java.util.ArrayList;

public class DbConnector {

    ArrayList<String> bookIsbnList;
    //    Adds userdata
    private void addUserData(String userName, String userLastName, String password,
                         String preference, String school) throws Exception {
        Encryption encryption = new Encryption();
//        Encrypting the password
        String data = encryption.doEncrypt(password);

//        Accessing the Database
        Database database = new Database();
        database.setUserInfo(userName, userLastName, data, preference, school);

    }


// Adds userData to the database
    public void setUserData(String userName, String userLastName, String password,
                            String preference, String school) throws Exception{
        addUserData(userName, userLastName, password, preference, school);
    }

    private boolean checkSecure(String userPass) throws Exception{
        Database db = new Database();
        Encryption encryption = new Encryption();
//        stores secure data
        String data = db.getSecure();

//        decrypts secure data
        String url = encryption.doDecrypt(data);

//        returns result if the password matches
        return matchPasswords(userPass, url);
    }

    public boolean getCheckSecure(String userPass) throws Exception{
        return checkSecure(userPass);
    }

    private boolean matchPasswords(String password, String dbPass) {
        return password.equals(dbPass);
    }

//    returns the result if there is a duplicate userName to Main class
    public boolean duplicateUserName(String userName) {
        Database db = new Database();
       return db.getCheckDuplicateUserName(userName);
    }

//    gets userID
    public int getUserID(String userName) {
        Database db = new Database();
        return db.getUserID(userName);
    }

//Sets the active user ID
    public void setCurrentUserID(int userID) {
        Database db = new Database();
        db.setCurrentUSerID(userID);
    }

//    gets the active userID
    public int getActiveUserID() {
        Database db = new Database();
       return db.getActiveUserID();
    }

    public void replaceOldID(int oldID, int newID) {
        Database db = new Database();
        db.setReplaceOldID(oldID, newID);
    }

    public void getBorrowedBooks(int activeUserID) {
        Database database = new Database();
//        retrieving the current user books
        database.getUserBooks(database.getActiveUserID());
        printBorrowedBooks(database);
    }

    public void printBorrowedBooks(Database database) {
//        populating the lists from the database instance
        //    ArrayLists to save book data when retrieved
        ArrayList<String> bookAuthorList = database.getBookAuthorList();
        ArrayList<String> bookTitleList = database.getBookTitleList();
         bookIsbnList = database.getBookISBN();
        ArrayList<String> bookPublisherList = database.getBookPublisherList();
        ArrayList<String> bookPageList = database.getBookPageList();
        ArrayList<String> bookPublishedDate = database.getBookPublishedDate();

        int listSize = Math.min(bookAuthorList.size(),
                                Math.min(bookTitleList.size(),
                                        Math.min(bookIsbnList.size(),
                                                Math.min(bookPublishedDate.size(),
                                                        Math.min(bookPublisherList.size(),
                                                                bookPageList.size()
                                )
                                        )
                                                )
                                                        )
        );

//        printing the books
        for (int i = 0; i < listSize; i++) {
            System.out.println();
            System.out.println(i+1+".\n"
                              + "Title: " + bookTitleList.get(i)
                              + "\nAuthor: " + bookAuthorList.get(i)
                              + "\nPages: " + bookPageList.get(i)
                              + "\nISBN: " + bookIsbnList.get(i)
                              + "\nPublished Date: " + bookPublishedDate.get(i)
                              + "\nPublisher: " + bookPublisherList.get(i));
        }
    }

    private int numberOfBorrowedBooks() {
        Database db = new Database();
        return db.checkBorrowedBooks(db.getActiveUserID());
    }

    public int getNumberOfBorrowedBooks() {
        return numberOfBorrowedBooks();
    }

    public int returnBook(int book) {
        Database database = new Database();

//        gets the isbn of the selected book and then passes it to the database for removal
        String isbn = bookIsbnList.get(book);
        return database.getDeleteBook(isbn);
    }
}

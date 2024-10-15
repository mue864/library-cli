package org.library;

public class BookTransactions extends Book{

    private int borrow(String userName, String bookTitle, String bookAuthor ,String isbn) {

//        Must connect to user account, then check if the user has already borrowed that book, using a different method
         boolean borrowedStatus = checkIfBorrowed(userName, isbn);
         if (borrowedStatus) {
             return -1; // this code is for when the book has already been borrowed by the user
         }

        return 0;
    }

    private boolean checkIfBorrowed (String userName, String isbn) {

//        checks if the book is already borrowed

        return false;
    }

}

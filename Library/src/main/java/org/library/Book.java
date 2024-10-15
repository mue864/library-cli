package org.library;

import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Book {

    /**
     * when this class is invoked, automatically it should communicate with the httpReq class
     *
     */

    private ArrayList<String> bookTitleList;
    private ArrayList<String> bookAuthorList;
    private ArrayList<String> publisherList;
    private ArrayList<String> publishedDate;
    private ArrayList<String> pageCountList;
    private ArrayList<String> bookISBNList;


//  for searching books
    public void insertQuery (String bookAuthor, String bookTitle) {
        getResponse(bookAuthor, bookTitle);
    }

//  for the first attempt when one creates an account
    public void automaticQuery(String bookTitle) {
        autoQuery(bookTitle);
    }

    public void authorQuery(String authorName) {
        searchAuthor(authorName);
    }

    private void searchAuthor(String authorName) {
        HttpReq req = new HttpReq();
        String encodedAuthorName = encode(authorName);
        req.getAuthorData(encodedAuthorName);

        int responseCode = req.getResponseCode();

        if (responseCode == 200) {
            req.setJsonData();
            loadBookInfo(req);

            printBookInfo();
        } else {
            System.out.println("There has been an error: " + responseCode);
        }
    }


    private void autoQuery(String bookTitle) {
        HttpReq req = new HttpReq();

        String encodedBookTitle = encode(bookTitle);
        req.getPreferenceData(encodedBookTitle);

        int responseCode = req.getResponseCode();
        if (responseCode == 200) {
            req.setJsonData();
            loadBookInfo(req);

            printBookInfo();
        } else {
            System.out.println("There has been an error");
        }
    }

    private void getResponse(String author, String bookTitle) {
        HttpReq req = new HttpReq();

//        Encode the data first
        String encodedAuthor =  encode(author);
        String encodedBookTitle = encode(bookTitle);

        req.getSendData(encodedAuthor, encodedBookTitle);
        int responseCode = req.getResponseCode();

        if (responseCode == 200) {
//            continue with extracting the important details form the JSON file
            req.setJsonData();

//            Add the book information to the lists in Book class
            loadBookInfo(req);

//            Then print it
            printBookInfo();
        } else {
            System.out.println("There has been an error. Error code: " + responseCode);
        }
    }

//    Load the book information to the lists from HttpReq
    private void loadBookInfo(HttpReq req) {
//        Copying all elements into the new arrayLists in this class
        bookTitleList = req.getBookTitleList();
        bookAuthorList = req.getBookAuthorList();
        bookISBNList = req.getBookISBNList();
        publisherList = req.getPublisherList();
        publishedDate = req.getPublishedDate();
        pageCountList = req.getPageCountList();

    }

    /**
     * Loads book information from the same HttpReq instance that fetched the data
     */

    public void printBookInfo() {
        Scanner scanner = new Scanner(System.in);
        int minSize = Math.min(
                bookTitleList.size(),
                Math.min(
                        bookAuthorList.size(),
                        Math.min(
                                bookISBNList.size(),
                                Math.min(
                                        publisherList.size(),
                                        Math.min(
                                                publishedDate.size(),
                                                pageCountList.size()
                                        )
                                )
                        )
                )
        );

        for (int i = 0; i < minSize; i++) {
            // Access elements from each list
            processBookSelection(i);
        }
        int bookChoice = scanner.nextInt();
//        lists the selected book
        processBookSelection(bookChoice-1);

//        store the bookChoice for later use
        storeBookChoice(bookChoice-1);
    }


    private void storeBookChoice(int bookChoice) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
                Do you wish to borrow this book?
                1. Yes
                2. No""");
        int choice;
        while (true) {
            try {
                choice = scanner.nextInt();
                if (choice == 1 ) {
//                If choice is 1 then continue with the borrow process
                    saveBooks(bookChoice);
                    System.out.println("Book Successfully borrowed");
//                    then call the main menu method from main to return
                    Main.bookManagementMenu();
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input.");
                scanner.nextLine();
            }
        }
    }

    private void saveBooks(int bookPosition) {
//        Save the books to the db
        Database db = new Database();
        db.setBookStorage(bookTitleList.get(bookPosition), bookAuthorList.get(bookPosition), bookISBNList.get(bookPosition),
                publisherList.get(bookPosition), publishedDate.get(bookPosition), pageCountList.get(bookPosition), db.getActiveUserID());
    }

    /**
     * This method is used twice, to print available books and to print the user choice
     * @param userChoice used for taking either the user selection or loop value
     */
    private void processBookSelection(int userChoice) {
        String title = bookTitleList.get(userChoice);
        String author = bookAuthorList.get(userChoice);
        String isbn = bookISBNList.get(userChoice);
        String publisher = publisherList.get(userChoice);
        String publishedDateStr = publishedDate.get(userChoice);
        String pageCount = pageCountList.get(userChoice);

        System.out.println(userChoice+1 + "\n" + "Title: " + title +"\n" + "Author: " + author + "\n" + "ISBN: " + isbn + "\n" +
                "Publisher: " + publisher + "\n" + "Published Date: " + publishedDateStr + "\n" +
                "Page Count: " + pageCount + "\n\n");


    }

//    Encoding the value, in case it has space
    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

}

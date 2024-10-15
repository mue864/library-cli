package org.library;

import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class HttpReq {

    private final ArrayList<String> bookTitleList = new ArrayList<>();
    private final ArrayList<String> bookAuthorList = new ArrayList<>();
    private final ArrayList<String> publisherList = new ArrayList<>();
    private final ArrayList<String> publishedDate = new ArrayList<>();
    private final ArrayList<String> pageCountList = new ArrayList<>();
    private final ArrayList<String> bookISBNList = new ArrayList<>();


    public String jsonResponse;
    private int responseCode;


    /**
     * @param authorName the name of the author that needs to be sent as a query
     * @param bookTitle  the name of the book to be searched
     */
//    fetching data
    private void sendData(String authorName, String bookTitle) {


//        creating the http client
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

//            build the http req
//            It is going to do both author and title search
           
              var request = ClassicRequestBuilder
                       .get("https://www.googleapis.com/books/v1/volumes?q=inauthor:"+authorName+"+intitle:"+bookTitle)
                       .build();


//            executing the request
            try (CloseableHttpResponse response = httpClient.execute(request)) {

                responseCode = response.getCode();

                System.out.println("Code" + response.getCode());


//                Get the response
                jsonResponse = EntityUtils.toString(response.getEntity());



//                Printing the response as a JSON block
//                System.out.println(jsonResponse);
            } catch (IOException | ParseException exception) {
                System.out.println(exception.getMessage());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public void getAuthorData(String encodedAuthorName) {

        authorData(encodedAuthorName);
    }

    private void authorData(String authorName) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            var request = ClassicRequestBuilder
                    .get("https://www.googleapis.com/books/v1/volumes?q=inauthor:"+authorName)
                    .build();

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                 responseCode = response.getCode();
                jsonResponse = EntityUtils.toString(response.getEntity());
            } catch (IOException | ParseException exception) {
                System.out.println(exception.getMessage());
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void preferenceData(String bookTitle) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            var request = ClassicRequestBuilder
                    .get("https://www.googleapis.com/books/v1/volumes?q=intitle:"+bookTitle)
                    .build();

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                responseCode = response.getCode();
                jsonResponse = EntityUtils.toString(response.getEntity());

            } catch (IOException | ParseException exception) {
                System.out.println(exception.getMessage());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public void getPreferenceData(String bookTitle) {
        preferenceData(bookTitle);
    }

//    Return the stored book info
    public ArrayList<String> getBookAuthorList() {
        return bookAuthorList;
    }

    public  ArrayList<String> getBookISBNList() {
        return bookISBNList;
    }

    public ArrayList<String> getBookTitleList() {
        return bookTitleList;
    }

    public ArrayList<String> getPageCountList() {
        return pageCountList;
    }

    public ArrayList<String> getPublishedDate() {
        return publishedDate;
    }

    public ArrayList<String> getPublisherList() {
        return publisherList;
    }

//    Status code
    public int getResponseCode () {
        return responseCode;
    }

    /**
     *
     * This getter method gets the query that needs to be sent by the user to the API
     * It then calls the sendData method and sends the relevant data to the API
     * @param authorName name of author that is needed to be searched
     * @param bookTitle name of book that is needed to be searched
     */
    public void getSendData(String authorName, String bookTitle) {
         sendData(authorName, bookTitle);
    }

//    This might be the weirdest thing to be done here, but I'm sure it works
//    If status code is 200, it is then called to complete the process of adding the books to relevant lists
    public void setJsonData() {
        jsonData(jsonResponse);
    }

    /**
     * @param jsonResponse takes in the response that is returned by the server in form of JSON string
     */
    private void jsonData (String jsonResponse) {

//        Clear the lists first before inserting again
        bookISBNList.clear();
        bookTitleList.clear();
        bookAuthorList.clear();
        publishedDate.clear();
        publisherList.clear();

        String title = "";
//        Parsing json response
        JSONObject jsonObject = new JSONObject(jsonResponse);

//        Extracting items array from json object
        JSONArray itemsArray = jsonObject.getJSONArray("items");

//        looping through all items to get specific information
        for (int i = 0; i < itemsArray.length(); i++) {

//            Getting each book as a whole
            JSONObject book = itemsArray.getJSONObject(i);
//            Getting volume information
            JSONObject volumeInfo = book.getJSONObject("volumeInfo");
//            title
            title = volumeInfo.optString("title", "No Title Available");
//            Adding the title to the bookTitleList arrayList
            bookTitleList.add(title);

//            publishers
            String publisher = volumeInfo.optString("publisher", "No Publisher Available");
            publisherList.add(publisher);

//            published date
            String publishDate  = volumeInfo.optString("publishedDate", "No Published Date");
            publishedDate.add(publishDate);

//            Page count
            int pageCount = volumeInfo.optInt("pageCount", -1);
            pageCountList.add(pageCount == -1 ? "Pages not listed" : String.valueOf(pageCount));

//            Authors
            JSONArray authorArray = volumeInfo.optJSONArray("authors");

//            Getting ISBN value
            String isbn = "No ISBN provided";
            JSONArray isbnValues = volumeInfo.optJSONArray("industryIdentifiers");

            if (isbnValues != null && !isbnValues.isEmpty()) {
                isbn = isbnValues.getJSONObject(0).optString("identifier", "No ISBN provided");
            }

//            Adding isbn to list
            bookISBNList.add(isbn);

//            Using stringBuilder to add the authors together as 1
            StringBuilder authors = new StringBuilder();

            if (authorArray != null) {
                for (int j = 0; j < authorArray.length(); j++) {
//                Getting the current row of author name
                    authors.append(authorArray.getString(j));
//                    checking if there is still an element ahead and add a comma ahead
                    if (j < authorArray.length() - 1) {
                        authors.append(", ");
                    }
                }

            } else {
                authors.append("No authors available");
            }
//            Adding the authors to the list
            bookAuthorList.add(authors.toString());

// Logging
//            System.out.println("Title: " + title);
//            System.out.println("Authors: " + authors);
//            System.out.println("Publisher: " + publisher);
//            System.out.println("Published: " + publishDate);
//            System.out.println("Page Count: " + (pageCount == -1 ? "Not listed" : pageCount));
//            System.out.println();

        }
        System.out.println(bookAuthorList.size());
//        //              Testing purposes
//        for (int j = 0; j < bookTitleList.size(); j++) {
//            System.out.println("Book Title: " +  bookTitleList.get(j));
//            System.out.println("Book Author: " + bookAuthorList.get(j)); // thats why it was returning an error!
//            System.out.println("Published: " + publishedDate.get(j));
//        }

    }



}

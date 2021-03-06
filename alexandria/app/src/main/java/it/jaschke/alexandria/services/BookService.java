package it.jaschke.alexandria.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.utils.Constants;

/*Copyright (C) 2015  Mohammad Jubair Khan (zub1984.kn@gmail.com) - Alexandria Project of Udacity Nano degree course.

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class BookService extends IntentService {

    private final String LOG_TAG = BookService.class.getSimpleName();

    public BookService() {
        super("Alexandria");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Constants.FETCH_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(Constants.EAN);
                fetchBook(ean);
            } else if (Constants.DELETE_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(Constants.EAN);
                deleteBook(ean);
            }
        }
    }

    /**
     * delete book from database
     *
     * @param ean : ISBN number of the book to be deleted.
     */
    private void deleteBook(String ean) {
        if (ean != null) {
            getContentResolver().delete(AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ean)), null, null);
        }
    }

    /**
     * Handle action fetchBook in the provided background thread with the provided
     * parameters.
     *
     * @param ean number of the book.
     */
    private void fetchBook(String ean) {

        if (ean.length() != 13) {
            return;
        }

        Cursor bookEntry = getContentResolver().query(
                AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ean)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if (null != bookEntry && bookEntry.getCount() > 0) {
            bookEntry.close();
            return;
        }

        callBookApi(ean);
    }


    /**
     * fetch book details using google library API
     *
     * @param ean number of the book.
     */
    private void callBookApi(final String ean) {

        //https://www.googleapis.com/books/v1/volumes?q=isbn%3A9780137903955
        final String FORECAST_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
        final String QUERY_PARAM = "q";
        final String ISBN_PARAM = "isbn:" + ean;
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(Constants.CONNECTION_AND_READ_TIME_OUT, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(Constants.CONNECTION_AND_READ_TIME_OUT, TimeUnit.SECONDS);    // socket timeout
        String builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, ISBN_PARAM)
                .build().toString();

        Log.v(LOG_TAG, "URL:" + builtUri);

        Request request = new Request.Builder()
                .url(builtUri)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.v(LOG_TAG, "onFailure - [IOException] ");
                Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
                messageIntent.putExtra(Constants.MESSAGE_KEY_IO_EXCEPTION, getResources().getString(R.string.io_error_or_timeout));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    if (response.isSuccessful()) {
                        parseJsonData(ean, jsonData);
                    }
                } catch (IOException e) {
                    Log.v(LOG_TAG, "onResponse - [IOException] ");
                    Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
                    messageIntent.putExtra(Constants.MESSAGE_KEY_IO_EXCEPTION, getResources().getString(R.string.io_error_or_timeout));
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
                }
            }
        });
    }

    /**
     * parse json response received from google library API.
     *
     * @param ean            number of the book.
     * @param bookJsonString json response received from google API.
     */

    private void parseJsonData(String ean, String bookJsonString) {

        final String ITEMS = "items";
        final String VOLUME_INFO = "volumeInfo";
        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String DESC = "description";
        final String CATEGORIES = "categories";
        final String IMG_URL_PATH = "imageLinks";
        final String IMG_URL = "thumbnail";
        try {
            JSONObject bookJson = new JSONObject(bookJsonString);
            JSONArray bookArray;
            if (bookJson.has(ITEMS)) {
                bookArray = bookJson.getJSONArray(ITEMS);
            } else {
                Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
                messageIntent.putExtra(Constants.MESSAGE_KEY, getResources().getString(R.string.not_found));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
                return;
            }

            JSONObject bookInfo = ((JSONObject) bookArray.get(0)).getJSONObject(VOLUME_INFO);
            String title = bookInfo.getString(TITLE);
            String subtitle = "";
            if (bookInfo.has(SUBTITLE)) {
                subtitle = bookInfo.getString(SUBTITLE);
            }

            String desc = "";
            if (bookInfo.has(DESC)) {
                desc = bookInfo.getString(DESC);
            }

            String imgUrl = "";
            if (bookInfo.has(IMG_URL_PATH) && bookInfo.getJSONObject(IMG_URL_PATH).has(IMG_URL)) {
                imgUrl = bookInfo.getJSONObject(IMG_URL_PATH).getString(IMG_URL);
            }

            writeBackBook(ean, title, subtitle, desc, imgUrl);

            if (bookInfo.has(AUTHORS)) {
                writeBackAuthors(ean, bookInfo.getJSONArray(AUTHORS));
            }
            if (bookInfo.has(CATEGORIES)) {
                writeBackCategories(ean, bookInfo.getJSONArray(CATEGORIES));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException ", e);
            Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
            messageIntent.putExtra(Constants.MESSAGE_KEY_BAD_RESPONSE, getResources().getString(R.string.bad_response));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
        }
    }


    /**
     * write book details in database.
     *
     * @param ean      number of the book.
     * @param title    of the book.
     * @param subtitle of the book.
     * @param desc     of the book.
     * @param imgUrl   of the book.
     */
    private void writeBackBook(String ean, String title, String subtitle, String desc, String imgUrl) {
        ContentValues values = new ContentValues();
        values.put(AlexandriaContract.BookEntry._ID, ean);
        values.put(AlexandriaContract.BookEntry.TITLE, title);
        values.put(AlexandriaContract.BookEntry.IMAGE_URL, imgUrl);
        values.put(AlexandriaContract.BookEntry.SUBTITLE, subtitle);
        values.put(AlexandriaContract.BookEntry.DESC, desc);
        getContentResolver().insert(AlexandriaContract.BookEntry.CONTENT_URI, values);
    }

    /**
     * write book author details in database.
     *
     * @param ean       number of the book.
     * @param jsonArray containing author's details.
     */
    private void writeBackAuthors(String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values = new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(AlexandriaContract.AuthorEntry._ID, ean);
            values.put(AlexandriaContract.AuthorEntry.AUTHOR, jsonArray.getString(i));
            getContentResolver().insert(AlexandriaContract.AuthorEntry.CONTENT_URI, values);
            values = new ContentValues();
        }
    }

    /**
     * write book categories details in database.
     *
     * @param ean       number of the book.
     * @param jsonArray containing categories details.
     */
    private void writeBackCategories(String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values = new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(AlexandriaContract.CategoryEntry._ID, ean);
            values.put(AlexandriaContract.CategoryEntry.CATEGORY, jsonArray.getString(i));
            getContentResolver().insert(AlexandriaContract.CategoryEntry.CONTENT_URI, values);
            values = new ContentValues();
        }
    }
}
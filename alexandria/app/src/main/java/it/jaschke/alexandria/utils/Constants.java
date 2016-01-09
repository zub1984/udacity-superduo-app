package it.jaschke.alexandria.utils;

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
public class Constants {

    public static final String FETCH_BOOK = "it.jaschke.alexandria.services.action.FETCH_BOOK";
    public static final String DELETE_BOOK = "it.jaschke.alexandria.services.action.DELETE_BOOK";
    public static final String EAN = "it.jaschke.alexandria.services.extra.EAN";

    public final static int ISBN_PREFIX=978;
    public final static int ISBN_LENGTH_10=10;
    public final static int ISBN_LENGTH_13=13;

    public static final String EAN_CONTENT = "eanContent";
    public static final String EAN_KEY = "ean";
    public static final String TITLE_KEY = "title";
    public static final String BOOK_TITLE_KEY = "bookTitle";

    public static final String LIST_POSITION_KEY = "listPosition";

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "BOOK_NOT_FOUND";
    public static final String MESSAGE_KEY_IO_EXCEPTION = "IO_EXCEPTION_OR_TIMEOUT";
    public static final String MESSAGE_KEY_BAD_RESPONSE = "BAD_RESPONSE";

    public static final String ADD_BOOK_FRAGMENT_TAG = "addBook";
    public static final String BOOK_DETAIL_FRAGMENT_TAG = "bookDetail";

    public static final int ADD_BOOK_LOADER_ID = 1;
    public static final int BOOK_LIST_LOADER_ID = 1;

    public static final int CONNECTION_AND_READ_TIME_OUT = 5;

    public static final int DRAWER_BOOK_LIST_POSITION = 0;
    public static final int DRAWER_ADD_BOOK_POSITION = 1;
    public static final int DRAWER_ABOUT_POSITION = 2;


}

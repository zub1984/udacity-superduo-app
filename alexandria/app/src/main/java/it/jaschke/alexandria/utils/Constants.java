package it.jaschke.alexandria.utils;

/**
 * Created by laptop on 12/11/2015.
 */
public class Constants {

    public static final String FETCH_BOOK = "it.jaschke.alexandria.services.action.FETCH_BOOK";
    public static final String DELETE_BOOK = "it.jaschke.alexandria.services.action.DELETE_BOOK";
    public static final String EAN = "it.jaschke.alexandria.services.extra.EAN";

    public final static int ISBN_PREFIX=978;
    public final static int ISBN_LENGTH_10=10;
    public final static int ISBN_LENGTH_13=13;

    public static final String EAN_CONTENT = "eanContent";
    public static final String EAN_KEY = "EAN";

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "BOOK_NOT_FOUND";
    public static final String MESSAGE_KEY_IO_EXCEPTION = "IO_EXCEPTION_OR_TIMEOUT";
    public static final String MESSAGE_KEY_BAD_RESPONSE = "BAD_RESPONSE";

    public static final String ADD_BOOK_FRAGMENT_TAG = "addBook";
    public static final String BOOK_DETAIL_FRAGMENT_TAG = "bookDetail";

    public static final int ADD_BOOK_LOADER_ID = 1;

    public static final int CONNECTION_AND_READ_TIME_OUT = 5;

}

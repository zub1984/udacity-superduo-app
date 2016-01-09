package it.jaschke.alexandria;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.api.BookListAdapter;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.utils.Constants;
import it.jaschke.alexandria.utils.EventUtils;

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
public class ListOfBooks extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private BookListAdapter bookListAdapter;
    private int mListPosition = ListView.INVALID_POSITION;

    @Bind(R.id.searchButton)
    ImageButton searchButton;
    @Bind(R.id.listOfBooks)
    ListView bookList;
    @Bind(R.id.searchText)
    EditText searchText;


    /**
     * Callback interface to be used in the main activity, for selecting a book from the listView
     */
    public interface Callback {
        void onItemSelected(String ean, String title);
    }

    public ListOfBooks() {
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // store the listView scroll position
        if (mListPosition != ListView.INVALID_POSITION) {
            outState.putInt(Constants.LIST_POSITION_KEY, mListPosition);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_of_books, container, false);
        ButterKnife.bind(this, rootView);

        editTextHandler();
        searchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListOfBooks.this.restartLoader();
                    }
                }
        );

        loadSavedBooks();

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = bookListAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    mListPosition = position;
                    ((Callback) getActivity())
                            .onItemSelected(
                                    cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID)),
                                    cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE))
                            );
                }
            }
        });

        getActivity().setTitle(R.string.books);
        searchText.requestFocus();
        if (savedInstanceState != null) {
            // get the listView scroll position
            if (savedInstanceState.containsKey(Constants.LIST_POSITION_KEY)) {
                mListPosition = savedInstanceState.getInt(Constants.LIST_POSITION_KEY);
            }
        }

        return rootView;
    }


    private void editTextHandler() {
        //search the list after typing
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                restartLoader();
            }

        });

        // on enter search book list
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EventUtils.keyEvent(actionId, event)) {
                    // get editText input to search the book
                    String inputText = searchText.getText().toString().trim();
                    if (inputText.length() >= 2) {
                        restartLoader();
                    } else {
                        Toast.makeText(getActivity(), "Enter minimum 2 characters!", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }


    private void loadSavedBooks() {
        Cursor cursor = getActivity().getContentResolver().query(
                AlexandriaContract.BookEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        bookListAdapter = new BookListAdapter(getActivity(), cursor, 0);
        bookList.setAdapter(bookListAdapter);
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(Constants.BOOK_LIST_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String searchString = searchText.getText().toString();
        if (searchString.length() > 0) {
            final String selection = AlexandriaContract.BookEntry.TITLE + " LIKE ? OR " + AlexandriaContract.BookEntry.SUBTITLE + " LIKE ? ";
            searchString = "%" + searchString + "%";
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.CONTENT_URI,
                    null,
                    selection,
                    new String[]{searchString, searchString},
                    null
            );
        } else {
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
        }


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bookListAdapter.swapCursor(data);
        // scroll to the saved list position
        if (mListPosition != ListView.INVALID_POSITION) {
            bookList.smoothScrollToPosition(mListPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookListAdapter.swapCursor(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}

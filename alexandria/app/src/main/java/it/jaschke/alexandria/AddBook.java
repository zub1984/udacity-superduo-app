package it.jaschke.alexandria;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.scanner.CaptureActivityAnyOrientation;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.utils.Constants;
import it.jaschke.alexandria.utils.EventUtils;


public class AddBook extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG_TAG = AddBook.class.getSimpleName();
   // private final int LOADER_ID = 1;

    @Nullable
    @Bind(R.id.ean)
    EditText ean;

    @Nullable
    @Bind(R.id.search_button)
    ImageButton search_button;
    @Nullable
    @Bind(R.id.scan_button)
    Button scan_button;

    @Bind(R.id.bookCover)
    ImageView bookCover;
    @Bind(R.id.bookTitle)
    TextView bookTitle;

    @Bind(R.id.bookSubTitle)
    TextView bookSubTitle;
    @Bind(R.id.authors)
    TextView authors;
    @Bind(R.id.categories)
    TextView categories;

    @Bind(R.id.bookDescription)
    TextView bookDescription;
    @Bind(R.id.cancel_button)
    Button cancel_button;
    @Bind(R.id.save_button)
    Button save_button;

    @Bind(R.id.inc_no_connection)
    View inc_no_connection;

    @Bind(R.id.textErrorHandler)
    TextView textErrorHandler;

    @Bind(R.id.imageErrorHandler)
    ImageView imageErrorHandler;

   /* @Bind(R.id.inc_no_book_found)
    View inc_no_book_found;
*/
    @Bind(R.id.inc_book_preview)
    View inc_book_preview;


    public AddBook() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ean != null) {
            outState.putString(Constants.EAN_CONTENT, ean.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null && ean != null) {
            ean.setText(savedInstanceState.getString(Constants.EAN_CONTENT));
        }

        eanInputHandler();

        search_button.setOnClickListener(this);
        scan_button.setOnClickListener(this);
        save_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);

        getActivity().setTitle(R.string.scan);
        clearViews();
        return rootView;
    }


    @Override
    public void onClick(View v) {
        clearViews();
        switch (v.getId()) {
            case R.id.search_button:
                String eanNumber = validateISBN(ean.getText().toString().trim());
                if (eanNumber.length() == Constants.ISBN_LENGTH_13) {
                    callBookIntent(eanNumber);
                    ean.setHint("");
                    EventUtils.hideKeyboard(getActivity());
                }
                ean.requestFocus();
                break;
            case R.id.scan_button:
                scanBarCode();
                break;
            case R.id.save_button:
                ean.setText("");
                break;
            case R.id.cancel_button:
                if (ean.getText().toString().trim().length() != 13) return;
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(Constants.EAN, ean.getText().toString());
                bookIntent.setAction(Constants.DELETE_BOOK);
                getActivity().startService(bookIntent);
                break;
        }

    }


    private void eanInputHandler() {
        ean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String eanNumber = "";
                if (s.length() >= Constants.ISBN_LENGTH_10 && s.length() == Constants.ISBN_LENGTH_13) {
                    eanNumber = validateISBN(s.toString());
                }

                if (eanNumber.length() == Constants.ISBN_LENGTH_13) {
                    //Once we have an ISBN, start a book intent
                    callBookIntent(eanNumber);
                }
            }
        });

        // on enter fetch book details
        ean.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EventUtils.keyEvent(actionId, event)) {
                    String s = ean.getText().toString().trim();
                    String eanNumber = validateISBN(s);
                    if (eanNumber.length() == Constants.ISBN_LENGTH_13) {
                        //Once we have an ISBN, start a book intent
                        callBookIntent(eanNumber);
                    }
                    ean.requestFocus();
                }
                return true;
            }
        });
    }


    private String validateISBN(String eanStr) {
        //catch isbn10 numbers
        if (eanStr.length() == Constants.ISBN_LENGTH_10 && !eanStr.startsWith(Integer.toString(Constants.ISBN_PREFIX))) {
            eanStr = Integer.toString(Constants.ISBN_PREFIX) + eanStr;
        }
        if (eanStr.length() < Constants.ISBN_LENGTH_13) {
            ean.setText("");
            if(eanStr.startsWith(Integer.toString(Constants.ISBN_PREFIX))&& eanStr.length()>= Constants.ISBN_LENGTH_10)
            ean.setError(getString(R.string.input_isbn_hint));
        }
        return eanStr;
    }


    private void callBookIntent(String ean) {
        if (EventUtils.isNetworkAvailable(getContext())) {
            Intent bookIntent = new Intent(getActivity(), BookService.class);
            bookIntent.putExtra(Constants.EAN, ean);
            bookIntent.setAction(Constants.FETCH_BOOK);
            getActivity().startService(bookIntent);
            AddBook.this.restartLoader();
            EventUtils.hideKeyboard(getActivity());
        } else {
            //display no connection
            inc_book_preview.setVisibility(View.GONE);
            Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            inc_no_connection.setVisibility(View.VISIBLE);
            textErrorHandler.setText(getString(R.string.no_internet_connection));
            EventUtils.hideKeyboard(getActivity());
        }

    }

    private void scanBarCode() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt(getString(R.string.scan_bar_code));
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }


    /**
     * scan book barcode
     *
     * @param requestCode int
     * @param resultCode  int
     * @param data        Intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (null != result) {
            if (null != result.getContents()) {
                String isbn = result.getContents();
                ean.setText(isbn);
            } else {
                Log.v(TAG_TAG, getString(R.string.scan_failed_or_cancel));
            }
        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(Constants.ADD_BOOK_LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (ean.getText().length() == 0) {
            return null;
        }
        String eanStr = ean.getText().toString().trim();
        if (eanStr.length() == Constants.ISBN_LENGTH_10 && !eanStr.startsWith(Integer.toString(Constants.ISBN_PREFIX))) {
            eanStr = Integer.toString(Constants.ISBN_PREFIX) + eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }
        switch (loader.getId()) {
            case Constants.ADD_BOOK_LOADER_ID:
                //DatabaseUtils.dumpCursor(data);
                setBookPreView(data);
        }
    }

    private void setBookPreView(Cursor data) {
        View view = getView();
        if (null != view) {
            inc_book_preview.setVisibility(View.VISIBLE);
            bookTitle.setText(data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE)));
            bookSubTitle.setText(data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE)));

            String authorsName = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
            //check if authors name is available
            if (null != authorsName) {
                String[] authorsArr = authorsName.split(",");
                authors.setLines(authorsArr.length);
                authors.setText(authorsName.replace(",", "\n"));
            }

            String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
            if (Patterns.WEB_URL.matcher(imgUrl).matches()) {

                Picasso.with(getActivity())
                        .load(imgUrl)
                        .placeholder(R.drawable.ic_launcher)
                        .error(R.drawable.ic_launcher)
                        .into(bookCover);
            }
            categories.setText(data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY)));
            bookDescription.setText(data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC)));
        }
        EventUtils.hideKeyboard(getActivity());
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearViews() {
        View view = getView();
        if (view != null) {
            inc_book_preview.setVisibility(View.GONE);
            inc_no_connection.setVisibility(View.GONE);
        }
    }

    public void displayErrorView(Intent intent) {
        View view = getView();
        if (view != null) {
            inc_book_preview.setVisibility(View.GONE);
            inc_no_connection.setVisibility(View.VISIBLE);
            if (intent.getStringExtra(Constants.MESSAGE_KEY) != null) {
                Toast.makeText(getActivity(), intent.getStringExtra(Constants.MESSAGE_KEY), Toast.LENGTH_LONG).show();
                imageErrorHandler.setAdjustViewBounds(true);
                imageErrorHandler.setImageResource(R.drawable.ic_stat_library);
                textErrorHandler.setText(getString(R.string.not_found));
            }
            else if(intent.getStringExtra(Constants.MESSAGE_KEY_IO_EXCEPTION) != null){
                Toast.makeText(getActivity(), intent.getStringExtra(Constants.MESSAGE_KEY_IO_EXCEPTION), Toast.LENGTH_LONG).show();
                imageErrorHandler.setAdjustViewBounds(true);
                imageErrorHandler.setImageResource(R.drawable.ic_cloud_off_dark);
                textErrorHandler.setText(getString(R.string.io_error_or_timeout));
            }
            else if(intent.getStringExtra(Constants.MESSAGE_KEY_BAD_RESPONSE) != null){
                Toast.makeText(getActivity(), intent.getStringExtra(Constants.MESSAGE_KEY_BAD_RESPONSE), Toast.LENGTH_LONG).show();
                imageErrorHandler.setAdjustViewBounds(true);
                imageErrorHandler.setImageResource(R.drawable.ic_cloud_off_dark);
                textErrorHandler.setText(getString(R.string.bad_response));
            }
        }
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

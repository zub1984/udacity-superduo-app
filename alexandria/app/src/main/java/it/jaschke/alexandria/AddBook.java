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
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private final int LOADER_ID = 1;


    /*@Bind(R.id.fragment_add_book_id)
    RelativeLayout fragment_add_book_id;
    @Bind(R.id.eancontainer)
    RelativeLayout ean_container;*/

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

    @Nullable
    @Bind(R.id.inc_horizontal_line)
    View inc_horizontal_line;

    @Bind(R.id.bookDescription)
    TextView bookDescription;
    @Bind(R.id.cancel_button)
    Button cancel_button;
    @Bind(R.id.save_button)
    Button save_button;

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

        if (savedInstanceState != null) {
            ean.setText(savedInstanceState.getString(Constants.EAN_CONTENT));
        }

        eanInputHandler();

        search_button.setOnClickListener(this);
        scan_button.setOnClickListener(this);
        save_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);

        getActivity().setTitle(R.string.scan);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                String eanNumber = validateISBN(ean.getText().toString().trim());
                if (eanNumber.length() == Constants.ISBN_LENGTH_13) {
                    callBookIntent(eanNumber);
                }
                ean.requestFocus();
                break;
            case R.id.scan_button:
                scanBarCode();
                break;
            case R.id.save_button:
                ean.setText("");
                clearFields();
                break;
            case R.id.cancel_button:
                if(ean.getText().toString().trim().length()!=13) return;
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(Constants.EAN, ean.getText().toString());
                bookIntent.setAction(Constants.DELETE_BOOK);
                getActivity().startService(bookIntent);
                clearFields();
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
                if (s.length() == Constants.ISBN_LENGTH_10 || s.length() == Constants.ISBN_LENGTH_13) {
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
            //Toast.makeText(getActivity(), "Enter 13 digit ISBN number!", Toast.LENGTH_SHORT).show();
            clearFields();
        }
        return eanStr;
    }


    private void callBookIntent(String ean) {
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(Constants.EAN, ean);
        bookIntent.setAction(Constants.FETCH_BOOK);
        getActivity().startService(bookIntent);
        AddBook.this.restartLoader();
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
                Log.v(TAG, "scanning is success, isbn:" + isbn);
                ean.setText(isbn);
            } else {
                Log.v(TAG, "scanning failed or cancelled!");
            }
        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (ean.getText().length() == 0) {
            return null;
        }
        String eanStr = ean.getText().toString().trim();
        if (eanStr.length() == Constants.ISBN_LENGTH_10 && !eanStr.startsWith(Integer.toString(Constants.ISBN_LENGTH_13))) {
            eanStr = Integer.toString(Constants.ISBN_LENGTH_13) + eanStr;
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
            case LOADER_ID:
                //DatabaseUtils.dumpCursor(data);
                setBookPreView(data);
        }
    }

    private void setBookPreView(Cursor data) {
        View view = getView();
        if(null!=view){
            bookTitle.setText(data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE)));
            bookSubTitle.setText(data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE)));

            String authorsName = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
            String[] authorsArr = authorsName.split(",");
            authors.setLines(authorsArr.length);
            authors.setText(authorsName.replace(",", "\n"));

            String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
            if (Patterns.WEB_URL.matcher(imgUrl).matches()) {

                Picasso.with(getActivity())
                        .load(imgUrl)
                        .placeholder(R.drawable.ic_launcher)
                        .error(R.drawable.ic_launcher)
                        .into(bookCover);

                bookCover.setVisibility(View.VISIBLE);
            }

            categories.setText(data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY)));
            bookDescription.setText(data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC)));

            save_button.setVisibility(View.VISIBLE);
            cancel_button.setVisibility(View.VISIBLE);
        }
        EventUtils.hideKeyboard(getActivity());
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields() {
        View view = getView();
        if (view != null) {
            bookTitle.setText("");
            bookSubTitle.setText("");
            authors.setText("");
            categories.setText("");
            bookDescription.setText("");
            inc_horizontal_line.setVisibility(View.INVISIBLE);
            bookCover.setVisibility(View.INVISIBLE);
            save_button.setVisibility(View.INVISIBLE);
            cancel_button.setVisibility(View.INVISIBLE);
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

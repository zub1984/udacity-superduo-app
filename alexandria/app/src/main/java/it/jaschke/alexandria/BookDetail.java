package it.jaschke.alexandria;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.utils.Constants;


public class BookDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private final int LOADER_ID = 10;
    private View rootView;
    private String ean;
    private ShareActionProvider shareActionProvider;


    @Bind(R.id.fullBookTitle)
    TextView fullBookTitle;
    @Bind(R.id.fullBookCover)
    ImageView fullBookCover;
    @Bind(R.id.fullBookSubTitle)
    TextView fullBookSubTitle;
    @Bind(R.id.fullBookDesc)
    TextView fullBookDesc;
    @Bind(R.id.categories)
    TextView fullBookCategories;
    @Bind(R.id.authors)
    TextView fullBokAuthors;
    @Bind(R.id.backButton)
    ImageButton backButton;
    @Bind(R.id.delete_button)
    Button delete_button;

    public BookDetail() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v("TAG", "onSaveInstanceState:");
        super.onSaveInstanceState(outState);

        if (ean != null) {
            outState.putString(Constants.EAN_KEY, ean);
            outState.putString(Constants.BOOK_DETAILS_TITLE, getString(R.string.details));
        }
        //outState.putParcelable(Constants.SHARE_ACTION_KEY, shareActionProvider);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (null != savedInstanceState) {
            // set the book title on rotation
            getActivity().setTitle(savedInstanceState.getString(Constants.BOOK_DETAILS_TITLE));
            ean = savedInstanceState.getString(Constants.EAN_KEY);
            //getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (null != getArguments()) {
            Log.v("TAG", "Read the passed argument to fragment:");
            ean = arguments.getString(Constants.EAN_KEY);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
        ButterKnife.bind(this, rootView);

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(Constants.EAN, ean);
                bookIntent.setAction(Constants.DELETE_BOOK);
                getActivity().startService(bookIntent);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
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

        DatabaseUtils.dumpCursor(data);

        showBookDetails(data);

    }

    private void showBookDetails(Cursor data) {
        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        fullBookTitle.setText(bookTitle);

        shareBook(bookTitle);

        fullBookSubTitle.setText(data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE)));
        fullBookDesc.setText(data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC)));

        String authorsName = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authorsName.split(",");
        fullBokAuthors.setLines(authorsArr.length);
        fullBokAuthors.setText(authorsName.replace(",", "\n"));

        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches()) {

            Picasso.with(getActivity())
                    .load(imgUrl)
                    .placeholder(R.drawable.ic_launcher)
                    .error(R.drawable.ic_launcher)
                    .into(fullBookCover);
            fullBookCover.setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        fullBookCategories.setText(categories);

        if (rootView.findViewById(R.id.right_container) != null) {
            backButton.setVisibility(View.INVISIBLE);
        }
    }


    private void shareBook(String bookTitle) {
        // To fix the issue of rotation put null check for shareActionProvider
        if (null != shareActionProvider) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + bookTitle);
            shareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onDestroyView();
        if (MainActivity.IS_TABLET && rootView.findViewById(R.id.right_container) == null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //set the book details title on context creation.
        getActivity().setTitle(R.string.details);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
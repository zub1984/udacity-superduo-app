package it.jaschke.alexandria.api;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;

/**
 * BookListAdapter class for populating the book list items
 */
public class BookListAdapter extends CursorAdapter {

    /**
     * Constructor
     *
     * @param context Context
     * @param cursor  Cursor
     * @param flags   int
     */
    public BookListAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    /**
     * Inflate a new book_list_item view based on the definition in the view holder inner class
     *
     * @param context Context
     * @param cursor  Cursor
     * @param parent  ViewGroup
     * @return View
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /**
     * Populate the view items from cursor data
     *
     * @param view    View
     * @param context Context
     * @param cursor  Cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String imgUrl = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));

        Picasso.with(context)
                .load(imgUrl)
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .into(viewHolder.bookCover);

        viewHolder.bookTitle.setText(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE)));
        viewHolder.bookSubTitle.setText(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE)));
    }

    /**
     * Helper class for references to the view items
     */
    public static class ViewHolder {
        public final ImageView bookCover;
        public final TextView bookTitle;
        public final TextView bookSubTitle;

        public ViewHolder(View view) {
            bookCover = (ImageView) view.findViewById(R.id.fullBookCover);
            bookTitle = (TextView) view.findViewById(R.id.listBookTitle);
            bookSubTitle = (TextView) view.findViewById(R.id.listBookSubTitle);
        }
    }
}
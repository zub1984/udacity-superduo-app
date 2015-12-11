package it.jaschke.alexandria.api;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;

/**
 * Created by saj on 11/01/15.
 */
public class BookListAdapter extends CursorAdapter {

    private final static int BOOK_LIST = 0;

    public static class ViewHolder {
        @Bind(R.id.bookCover)
        ImageView bookCover;
        @Bind(R.id.bookTitle)
        TextView bookTitle;
        @Bind(R.id.bookSubTitle)
        TextView bookSubTitle;


        public ViewHolder(View view) {
           ButterKnife.bind(this, view);
        }
    }

    public BookListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String imgUrl = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));

        Log.v("TAG","imgUrl:"+imgUrl );
        //new DownloadImage(viewHolder.bookCover).execute(imgUrl);

       Picasso.with(context)
                .load(imgUrl)
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .into(viewHolder.bookCover);

        viewHolder.bookTitle.setText(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE)));
        viewHolder.bookSubTitle.setText(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = null;
        int viewType = getItemViewType(cursor.getPosition());
        switch (viewType) {
            case BOOK_LIST:
                int layoutId = R.layout.book_list_item;
                view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                ViewHolder viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
                break;
        }
        return view;
    }
}

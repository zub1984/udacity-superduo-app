package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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
public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, ListOfBooks.Callback, BookDetail.Callback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;

    private CharSequence title;
    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReceiver;
    private TextView mToolbarTitle;

    private String mBookEan;
    private String mBookTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IS_TABLET = isTablet();
        if (IS_TABLET) {
            setContentView(R.layout.activity_main_tablet);
        } else {
            setContentView(R.layout.activity_main);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter(Constants.MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // get the mTitle of the activity
        title = getTitle();
        // set the toolbar title to the name of the app
        mToolbarTitle.setText(R.string.app_name);

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        EventUtils.hideKeyboard(this);

        // get the saved state vars
        if (savedInstanceState != null) {
            getSavedStateInfo(savedInstanceState);
        }
    }


    /**
     * Save the the current state before leaving the activity
     *
     * @param outState Bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.TITLE_KEY, title.toString());
        outState.putString(Constants.EAN_KEY, mBookEan);
        outState.putString(Constants.BOOK_TITLE_KEY, mBookTitle);
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        String tagName = "";
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment;

        switch (position) {
            default:
            case Constants.DRAWER_BOOK_LIST_POSITION:
                nextFragment = new ListOfBooks();
                break;
            case Constants.DRAWER_ADD_BOOK_POSITION:
                nextFragment = new AddBook();
                tagName = Constants.ADD_BOOK_FRAGMENT_TAG;
                break;
            case Constants.DRAWER_ABOUT_POSITION:
                nextFragment = new About();
                break;

        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment, tagName)
                .addToBackStack((String) title)
                .commit();
    }

    public void setTitle(int titleId) {
        title = getString(titleId);
        // update the toolbar title textView
        mToolbarTitle.setText(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            // restore the toolbar title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                mToolbarTitle.setText(title);
            }

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == android.R.id.home) {
            EventUtils.hideKeyboard(this);
            // if we are not on a tablet in landscape mode
            if (findViewById(R.id.right_container) == null) {
                // if we are coming back from the bookDetail fragment, reset the hamburger
                if (title.equals(getString(R.string.details))) {
                    getSupportFragmentManager().popBackStack();
                    toggleToolbarDrawerIcon(false);
                    return true;
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean, String title) {
        mBookEan = ean;
        mBookTitle = title;

        Bundle args = new Bundle();
        args.putString(Constants.EAN_KEY, ean);
        args.putString(Constants.TITLE_KEY, title);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        int id = R.id.container;
        if (findViewById(R.id.right_container) != null) {
            id = R.id.right_container;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment)
                .addToBackStack(Constants.BOOK_DETAIL_FRAGMENT_TAG)  //"Book Detail"
                .commit();
        //toggle the toolbar icon to back
        toggleToolbarDrawerIcon(true);
        EventUtils.hideKeyboard(this);
    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AddBook addBook = (AddBook) getSupportFragmentManager().findFragmentByTag(Constants.ADD_BOOK_FRAGMENT_TAG);
            if (null != addBook) {
                addBook.displayErrorView(intent);
            }
        }
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {
        // close the drawer if it's open
        if (navigationDrawerFragment.isDrawerOpen()) {
            navigationDrawerFragment.closeDrawer();
            return;
        }
        // if we are coming back from the book detail fragment, reset the hamburger icon
        if (title.equals(getString(R.string.details))) {
            toggleToolbarDrawerIcon(false);
        }

        if (getSupportFragmentManager().getBackStackEntryCount() < 2) {
            finish();
        }
        super.onBackPressed();
    }


    /**
     * Toggle the toolbar drawer icon between the hamburger and back icon
     *
     * @param backToHome boolean
     */
    public void toggleToolbarDrawerIcon(boolean backToHome) {
        // if we are not on a tablet landscape mode
        if (findViewById(R.id.right_container) == null) {
            navigationDrawerFragment.toggleToolbarDrawerIcon(backToHome);
        }
    }

    private void getSavedStateInfo(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(Constants.TITLE_KEY))
            title = savedInstanceState.getString(Constants.TITLE_KEY, null);

        if (savedInstanceState.containsKey(Constants.EAN_KEY))
            mBookEan = savedInstanceState.getString(Constants.EAN_KEY, null);

        if (savedInstanceState.containsKey(Constants.BOOK_TITLE_KEY))
            mBookTitle = savedInstanceState.getString(Constants.BOOK_TITLE_KEY, null);

        // when rotating to landscape bookList on a tablet select the previously selected book
        if (IS_TABLET && findViewById(R.id.right_container) != null && title.equals(getString(R.string.books)) && mBookEan != null && mBookTitle != null) {
            onItemSelected(mBookEan, mBookTitle);
            mBookEan = null;
            mBookTitle = null;
        }
    }

}
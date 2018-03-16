/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.asynctaskloader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.asynctaskloader.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    /* A constant to save and restore the URL that is being displayed */
    private static final String SEARCH_QUERY_URL_EXTRA = "query";
    private static final int GITHUB_SEARCH_LOADER = 22;
    private EditText mSearchBoxEditText;
    private TextView mUrlDisplayTextView;
    private TextView mSearchResultsTextView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = findViewById(R.id.et_search_box);

        mUrlDisplayTextView = findViewById(R.id.tv_url_display);
        mSearchResultsTextView = findViewById(R.id.tv_github_search_results_json);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        if (savedInstanceState != null) {

            String queryUrl = savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA);
            mUrlDisplayTextView.setText(queryUrl);

        }

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(GITHUB_SEARCH_LOADER, null, this);

    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally request that an AsyncTaskLoader performs the GET request.
     */
    private void makeGithubSearchQuery() {

        String githubQuery = mSearchBoxEditText.getText().toString();

        if (TextUtils.isEmpty(githubQuery)) {

            mUrlDisplayTextView.setText("No query entered, nothing to search for.");
            return;

        }

        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        mUrlDisplayTextView.setText(githubSearchUrl.toString());

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, githubSearchUrl.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> githubSearchLoader = loaderManager.getLoader(GITHUB_SEARCH_LOADER);

        if (githubSearchLoader == null) {

            loaderManager.initLoader(GITHUB_SEARCH_LOADER, queryBundle, this);

        }
        else {

            loaderManager.restartLoader(GITHUB_SEARCH_LOADER, queryBundle, this);

        }

    }

    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showJsonDataView() {

        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the JSON data is visible */
        mSearchResultsTextView.setVisibility(View.VISIBLE);

    }

    /**
     * This method will make the error message visible and hide the JSON
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {

        /* First, hide the currently visible data */
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);

    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {

        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {

                super.onStartLoading();
                if (args == null) {

                    return;

                }
                mLoadingIndicator.setVisibility(View.VISIBLE);

                forceLoad();

            }

            @Nullable
            @Override
            public String loadInBackground() {

                String searchQuery = args.getString(SEARCH_QUERY_URL_EXTRA);
                if (searchQuery == null || TextUtils.isEmpty(searchQuery)) {

                    return null;

                }
                try {

                    URL githubUrl = new URL(searchQuery);
                    return NetworkUtils.getResponseFromHttpUrl(githubUrl);

                }
                catch (IOException e) {

                    e.printStackTrace();
                    return null;

                }

            }

        };

    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null && !data.equals("")) {

            showJsonDataView();
            mSearchResultsTextView.setText(data);

        }
        else {

            showErrorMessage();

        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search) {

            makeGithubSearchQuery();
            return true;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        String queryUrl = mUrlDisplayTextView.getText().toString();
        outState.putString(SEARCH_QUERY_URL_EXTRA, queryUrl);

    }

}
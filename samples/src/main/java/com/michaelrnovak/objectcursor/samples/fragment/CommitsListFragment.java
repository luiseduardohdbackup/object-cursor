/*
 * Copyright (C) 2013 Michael Novak <michael.novakjr@gmail.com>
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
package com.michaelrnovak.objectcursor.samples.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michaelrnovak.objectcursor.ObjectCursor;
import com.michaelrnovak.objectcursor.ObjectCursorLoader;

import com.michaelrnovak.objectcursor.samples.R;
import com.michaelrnovak.objectcursor.samples.model.Commit;
import com.michaelrnovak.objectcursor.samples.provider.SampleContract;
import com.michaelrnovak.objectcursor.samples.service.CommitsDownloadService;
import com.michaelrnovak.objectcursor.samples.widget.CommitsListAdapter;

public class CommitsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<ObjectCursor<Commit>> {
    public static final String TAG = CommitsListFragment.class.getSimpleName();

    private CommitsListAdapter mListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_commits, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListAdapter = new CommitsListAdapter(getActivity(), null);
        setListAdapter(mListAdapter);

        getLoaderManager().initLoader(0, null, this);

        Intent intent = new Intent(getActivity(), CommitsDownloadService.class);
        getActivity().startService(intent);
    }

    @Override
    public Loader<ObjectCursor<Commit>> onCreateLoader(int id, Bundle bundle) {
        return new ObjectCursorLoader<Commit>(getActivity(), SampleContract.Commits.CONTENT_URI,
                Commit.Query.PROJECTION, SampleContract.Commits.COMMIT_DATE + " DESC", Commit.FACTORY);
    }

    @Override
    public void onLoadFinished(Loader<ObjectCursor<Commit>> objectCursorLoader, ObjectCursor<Commit> cursor) {
        mListAdapter.swapObjectCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<ObjectCursor<Commit>> objectCursorLoader) {
        mListAdapter.swapObjectCursor(null);
    }
}

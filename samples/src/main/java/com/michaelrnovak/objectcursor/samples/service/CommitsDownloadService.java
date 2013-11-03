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
package com.michaelrnovak.objectcursor.samples.service;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.GsonBuilder;

import com.michaelrnovak.objectcursor.samples.provider.SampleContract.Commits;
import com.michaelrnovak.objectcursor.samples.provider.SampleProvider;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CommitsDownloadService extends IntentService {
    private static final String TAG = CommitsDownloadService.class.getSimpleName();

    public CommitsDownloadService() {
        super(TAG);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        List<GithubCommit> commits = downloadCommits();

        if (commits != null) {
            saveCommits(commits);
        }
    }

    private List<GithubCommit> downloadCommits() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer("https://api.github.com")
                .setClient(new OkClient())
                .setConverter(new GsonConverter(new GsonBuilder().create()))
                .build();

        try {
            GithubService github = restAdapter.create(GithubService.class);
            return github.getObjectCursorCommits();
        } catch (RetrofitError e) {
            Log.e("ObjectCursor", "Error downloading commits", e);
            return null;
        }
    }

    private void saveCommits(List<GithubCommit> commits) {
        ContentResolver resolver = getContentResolver();
        ArrayList<String> shas = new ArrayList<String>();

        Cursor cursor = resolver.query(Commits.CONTENT_URI, new String[] { Commits.SHA }, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                shas.add(cursor.getString(0));
            }

            cursor.close();
        }

        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        for (GithubCommit commit : commits) {
            ContentValues values = new ContentValues();

            values.put(Commits.MESSAGE, commit.commit.message);
            values.put(Commits.URL, commit.url);
            values.put(Commits.AUTHOR_AVATAR, commit.author.avatar_url);
            values.put(Commits.AUTHOR_EMAIL, commit.commit.author.email);
            values.put(Commits.AUTHOR_NAME, commit.commit.author.name);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");

            try {
                values.put(Commits.COMMIT_DATE, dateFormat.parse(commit.commit.author.date).getTime());
            } catch (ParseException e) {
                Log.e("ObjectCursor Sample", "Unable to parse date: " + commit.commit.author.date, e);
            }

            if (shas.contains(commit.sha)) {
                operations.add(buildUpdateOperation(commit.sha, values));
            } else {
                operations.add(buildInsertOperation(values));
            }
        }

        try {
            resolver.applyBatch(SampleProvider.AUTHORITY, operations);
        } catch (RemoteException e) {
            Log.e("ObjectCursor", "Remote error saving commits", e);
        } catch (OperationApplicationException e) {
            Log.e("ObjectCursor", "ContentProvider error saving commits", e);
        }
    }

    private ContentProviderOperation buildInsertOperation(ContentValues values) {
        ContentProviderOperation.Builder builder =
                ContentProviderOperation.newInsert(Commits.CONTENT_URI);

        builder.withValues(values);
        builder.withYieldAllowed(true);

        return builder.build();
    }

    private ContentProviderOperation buildUpdateOperation(String sha, ContentValues values) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(Commits.CONTENT_URI);
        builder.withValues(values);
        builder.withSelection(Commits.SHA + "=?", new String[] { sha });
        builder.withYieldAllowed(true);

        return builder.build();
    }

    static class GithubCommit {
        public String url;
        public String sha;

        public Commit commit;
        public Author author;

        static class Commit {
            public CommitAuthor author;
            public String message;
        }

        static class Author {
            public String avatar_url;
        }

        static class CommitAuthor {
            public String name;
            public String email;
            public String date;
        }
    }
}

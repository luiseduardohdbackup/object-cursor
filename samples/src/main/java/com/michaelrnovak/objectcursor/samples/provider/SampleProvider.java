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
package com.michaelrnovak.objectcursor.samples.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.michaelrnovak.objectcursor.samples.provider.SampleContract.Commits;
import com.michaelrnovak.objectcursor.samples.util.SelectionBuilder;

import java.util.ArrayList;

public class SampleProvider extends ContentProvider {
    public static final String AUTHORITY = "com.michaelrnovak.objectcursor.samples";

    private static final int COMMITS = 1;
    private static final int COMMITS_ID = 2;

    private SampleDatabaseHelper mDatabaseHelper;
    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "commits", COMMITS);
        matcher.addURI(AUTHORITY, "commits/*", COMMITS_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new SampleDatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case COMMITS:
                return Commits.CONTENT_TYPE;
            case COMMITS_ID:
                return Commits.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri.toString());
        }
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();

        if (database != null) {
            try {
                database.beginTransaction();

                final int numOperations = operations.size();
                final ContentProviderResult[] results = new ContentProviderResult[numOperations];

                for (int i = 0; i < numOperations; i++) {
                    results[i] = operations.get(i).apply(this, results, i);
                }

                database.setTransactionSuccessful();
                return results;
            } finally {
                database.endTransaction();
            }
        }

        return new ContentProviderResult[0];
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final Context context = getContext();
        final SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();

        if (context != null) {
            Cursor cursor = buildSimpleSelection(uri).where(selection, selectionArgs)
                    .query(database, projection, sortOrder);
            cursor.setNotificationUri(context.getContentResolver(), uri);
            return cursor;
        }

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final Context context = getContext();
        final SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        if (context != null) {
            switch (match) {
                case COMMITS:
                    long id = database.insertOrThrow(SampleDatabaseHelper.Tables.COMMITS, null, values);

                    if (id != -1) {
                        context.getContentResolver().notifyChange(uri, null);
                        return Commits.buildUri(String.valueOf(id));
                    }

                    return null;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri.toString());
            }
        }

        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final Context context = getContext();
        final SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);

        int rows = 0;

        if (context != null) {
            rows = builder.where(selection, selectionArgs).update(database, values);

            if (rows > 0) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return rows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final Context context = getContext();
        final SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);

        int rows = 0;

        if (context != null) {
            rows = builder.where(selection, selectionArgs).delete(database);

            if (rows > 0) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return rows;
    }

    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case COMMITS:
                return builder.table(SampleDatabaseHelper.Tables.COMMITS);
            case COMMITS_ID:
                return builder.table(SampleDatabaseHelper.Tables.COMMITS).where(Commits._ID + " =?",
                        Commits.getId(uri));
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri.toString());
        }
    }
}

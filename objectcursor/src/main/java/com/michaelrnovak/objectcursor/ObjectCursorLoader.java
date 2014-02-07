/*
 * Copyright (C) 2013 Michael Novak <michael.novakjr@gmail.com>
 * Copyright (C) 2013 The Android Open Source Project
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
package com.michaelrnovak.objectcursor;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

public class ObjectCursorLoader<T> extends AsyncTaskLoader<ObjectCursor<T>> {
    final String[] mProjection;
    final String mSelection;
    final String[] mSelectionArgs;
    final String mSortOrder;

    private Uri mUri;
    private final CursorCreator<T> mFactory;
    final ForceLoadContentObserver mObserver;

    ObjectCursor<T> mCursor;

    public ObjectCursorLoader(Context context, Uri uri, String[] projection, CursorCreator<T> factory) {
        this(context, uri, projection, null, factory);
    }

    public ObjectCursorLoader(Context context, Uri uri, String[] projection, String sortOrder,
                              CursorCreator<T> factory) {
        this(context, uri, projection, null, null, sortOrder, factory);
    }

    public ObjectCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs,
                              String sortOrder, CursorCreator<T> factory) {
        super(context);

        if (factory == null) {
            throw new NullPointerException("CursorCreator factory should not be null");
        }

        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
        mFactory = factory;
        mObserver = new ForceLoadContentObserver();

        setUri(uri);
    }

    @Override
    public ObjectCursor<T> loadInBackground() {
        final Cursor inner = getContext().getContentResolver().query(mUri, mProjection, mSelection, mSelectionArgs,
                mSortOrder);

        if (inner != null) {
            inner.getCount();
            inner.registerContentObserver(mObserver);
            return createObjectCursor(inner);
        } else {
            return null;
        }
    }

    @Override
    public void deliverResult(ObjectCursor<T> cursor) {
        if (isReset()) {
            if (cursor != null) {
                cursor.close();
            }

            return;
        }

        final Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }

        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(ObjectCursor<T> cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    public void onReset() {
        super.onReset();
        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }

        mCursor = null;
    }

    protected ObjectCursor<T> createObjectCursor(Cursor cursor) {
        T model = mFactory.createFromCursor(cursor);
        return new ObjectCursor<T>(cursor, model);
    }

    protected final void setUri(Uri uri) {
        if (uri == null) {
            throw new NullPointerException("Uri cannot be blank");
        }

        mUri = uri;
    }
}

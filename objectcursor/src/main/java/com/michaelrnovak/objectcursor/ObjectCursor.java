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

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.SparseArray;

public class ObjectCursor<T> extends CursorWrapper {
    private final SparseArray<T> mCache;
    private final CursorCreator<T> mFactory;

    /* Access to the cursor in a cursor wrapper wasn't added until Honeycomb. */
    private Cursor mCursor;

    public ObjectCursor(Cursor cursor, CursorCreator<T> factory) {
        super(cursor);

        if (AndroidUtils.isHoneycomb()) {
            mCursor = cursor;
        }

        if (cursor != null) {
            mCache = new SparseArray<T>(cursor.getCount());
        } else {
            mCache = null;
        }

        mFactory = factory;
    }

    public final T getModel() {
        final Cursor cursor = getCursor();

        if (cursor == null) {
            return null;
        }

        final int currentPosition = cursor.getPosition();
        final T cachedModel = mCache.get(currentPosition);

        if (cachedModel != null) {
            return cachedModel;
        }

        final T model = mFactory.createFromCursor(cursor);
        mCache.put(currentPosition, model);
        return model;
    }

    final void fillCache() {
        final Cursor cursor = getCursor();

        if (cursor == null) {
            return;
        }

        while (cursor.moveToNext()) {
            getModel();
        }
    }

    public Cursor getCursor() {
        if (AndroidUtils.isHoneycomb()) {
            return getWrappedCursor();
        } else {
            return mCursor;
        }
    }

    @Override
    public void close() {
        super.close();
        mCache.clear();

        if (mCursor != null) {
            mCursor.close();
        }
    }
}

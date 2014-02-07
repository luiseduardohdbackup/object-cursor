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
    /* Access to the cursor in a cursor wrapper wasn't added until Honeycomb. */
    private Cursor mCursor;

    private T mModel;

    public ObjectCursor(Cursor cursor, T model) {
        super(cursor);
        mCursor = cursor;
        mModel = model;
    }

    public final T getModel(int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalArgumentException("Couldn't move to position " + position);
        }
        return mModel;
    }

    public Cursor getCursor() {
        return mCursor;
    }
}

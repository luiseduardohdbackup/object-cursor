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
package com.michaelrnovak.objectcursor.widget;

import android.database.Cursor;
import android.widget.Filter;

import com.michaelrnovak.objectcursor.ObjectCursor;

class ObjectCursorFilter<T> extends Filter {
    ObjectCursorFilterClient<T> mFilterClient;

    interface ObjectCursorFilterClient<T> {
        CharSequence convertToString(Cursor cursor);
        ObjectCursor<T> runQueryOnBackgroundThread(CharSequence constraint);
        ObjectCursor<T> getObjectCursor();

        void changeObjectCursor(ObjectCursor<T> cursor);
    }

    ObjectCursorFilter(ObjectCursorFilterClient<T> client) {
        mFilterClient = client;
    }

    @Override
    public CharSequence convertResultToString(Object resultValue) {
        return mFilterClient.convertToString((Cursor) resultValue);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        Cursor cursor = mFilterClient.runQueryOnBackgroundThread(constraint);
        FilterResults results = new FilterResults();

        if (cursor != null) {
            results.count = cursor.getCount();
            results.values = cursor;
        } else {
            results.count = 0;
            results.values = null;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        ObjectCursor<T> oldCursor = mFilterClient.getObjectCursor();

        if (results.values != null && results.values != oldCursor) {
            mFilterClient.changeObjectCursor((ObjectCursor<T>) results.values);
        }
    }
}

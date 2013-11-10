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

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.michaelrnovak.objectcursor.ObjectCursor;

public abstract class ObjectCursorAdapter<T> extends BaseAdapter implements Filterable,
        ObjectCursorFilter.ObjectCursorFilterClient<T> {
    protected Context mContext;
    protected ObjectCursor<T> mObjectCursor;

    protected boolean mDataValid;
    protected int mRowIdColumn;

    protected ChangeObserver mChangeObserver;
    protected MyDataSetObserver mDataSetObserver;

    protected ObjectFilterQueryProvider<T> mFilterQueryProvider;
    protected ObjectCursorFilter<T> mObjectCursorFilter;

    protected int mDropdownResource;

    public ObjectCursorAdapter(Context context, ObjectCursor<T> cursor) {
        init(context, cursor);
    }

    private void init(Context context, ObjectCursor<T> cursor) {
        boolean cursorPresent = cursor != null;
        mObjectCursor = cursor;
        mDataValid = cursorPresent;

        mContext = context;
        mRowIdColumn = cursorPresent ? mObjectCursor.getColumnIndexOrThrow("_id") : -1;

        mChangeObserver = new ChangeObserver();
        mDataSetObserver = new MyDataSetObserver();

        if (cursorPresent) {
            mObjectCursor.registerContentObserver(mChangeObserver);
            mObjectCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    protected Context getContext() {
        return mContext;
    }

    public ObjectCursor<T> getObjectCursor() {
        return mObjectCursor;
    }

    public int getCount() {
        if (mDataValid && mObjectCursor != null) {
            return mObjectCursor.getCount();
        }

        return 0;
    }

    public T getItem(int position) {
        if (mDataValid && mObjectCursor != null) {
            return mObjectCursor.getModel(position);
        }

        return null;
    }

    public long getItemId(int position) {
        if (mDataValid && mObjectCursor != null) {
            if (mObjectCursor.moveToPosition(position)) {
                return mObjectCursor.getLong(mRowIdColumn);
            }
        }

        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (!mDataValid) {
            throw new IllegalStateException("Adapter should only call getView() when the cursor is valid");
        }

        View view;
        T model = mObjectCursor.getModel(position);

        if (convertView == null) {
            view = newView(mContext, model, parent);
        } else {
            view = convertView;
        }

        bindView(view, mContext, model);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (mDataValid) {
            View view;
            T model = mObjectCursor.getModel(position);

            if (convertView == null) {
                view = newDropDownView(mContext, model, parent);
            } else {
                view = convertView;
            }

            bindView(view, mContext, model);
            return view;
        }

        return null;
    }

    /**
     * Bind an existing view to the data pointed to by cursor.
     *
     * @param view Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param model The object at the current position in the cursor
     */
    public abstract void bindView(View view, Context context, T model);

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param model The object at the current position in the cursor
     * @param parent The parent to which the new view is attached to
     * @return the newly created view.
     */
    public abstract View newView(Context context, T model, ViewGroup parent);

    public View newDropDownView(Context context, T model, ViewGroup parent) {
        return createViewFromResource(parent, mDropdownResource);
    }

    private View createViewFromResource(ViewGroup parent, int resource) {
        return LayoutInflater.from(mContext).inflate(resource, parent, false);
    }

    public void setDropDownViewResource(int resource) {
        mDropdownResource = resource;
    }

    @Override
    public void changeObjectCursor(ObjectCursor<T> objectCursor) {
        ObjectCursor<T> old = swapObjectCursor(objectCursor);

        if (old != null) {
            old.close();
        }
    }

    public ObjectCursor<T> swapObjectCursor(ObjectCursor<T> newObjectCursor) {
        if (newObjectCursor == mObjectCursor) {
            return null;
        }

        ObjectCursor<T> oldObjectCursor = mObjectCursor;

        if (oldObjectCursor != null) {
            oldObjectCursor.unregisterContentObserver(mChangeObserver);
            oldObjectCursor.unregisterDataSetObserver(mDataSetObserver);
        }

        mObjectCursor = newObjectCursor;

        if (newObjectCursor != null) {
            newObjectCursor.registerContentObserver(mChangeObserver);
            newObjectCursor.registerDataSetObserver(mDataSetObserver);

            mRowIdColumn = newObjectCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;

            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;

            notifyDataSetInvalidated();
        }

        return oldObjectCursor;
    }

    @Override
    public CharSequence convertToString(Cursor objectCursor) {
        return objectCursor == null ? "" : objectCursor.toString();
    }

    @Override
    public ObjectCursor<T> runQueryOnBackgroundThread(CharSequence constraint) {
        if (mFilterQueryProvider != null) {
            return mFilterQueryProvider.runQuery(constraint);
        }

        return mObjectCursor;
    }

    @Override
    public Filter getFilter() {
        if (mObjectCursorFilter == null) {
            mObjectCursorFilter = new ObjectCursorFilter<T>(this);
        }

        return mObjectCursorFilter;
    }

    public ObjectFilterQueryProvider<T> getFilterQueryProvider() {
        return mFilterQueryProvider;
    }

    public void setFilterQueryProvider(ObjectFilterQueryProvider<T> filterQueryProvider) {
        mFilterQueryProvider = filterQueryProvider;
    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mDataValid = false;
            notifyDataSetInvalidated();
        }
    }
}

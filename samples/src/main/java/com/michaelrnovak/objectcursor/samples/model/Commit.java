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
package com.michaelrnovak.objectcursor.samples.model;

import android.database.Cursor;

import com.michaelrnovak.objectcursor.CursorCreator;
import com.michaelrnovak.objectcursor.samples.provider.SampleContract;

import java.util.Date;

public class Commit {
    private final Cursor mCursor;

    private Commit(Cursor cursor) {
        if (cursor == null) {
            throw new NullPointerException("cursor is null");
        }
        mCursor = cursor;
    }

    public String getMessage() {
        return mCursor.getString(Query.MESSAGE);
    }

    public String getUrl() {
        return mCursor.getString(Query.URL);
    }

    public Date getDate() {
        return new Date(mCursor.getLong(Query.COMMIT_DATE));
    }

    public String getSha() {
        return mCursor.getString(Query.COMMIT_SHA);
    }

    public String getAuthorAvatar() {
        return mCursor.getString(Query.AUTHOR_AVATAR);
    }

    public String getAuthorName() {
        return mCursor.getString(Query.AUTHOR_NAME);
    }

    public String getAuthorEmail() {
        return mCursor.getString(Query.AUTHOR_EMAIL);
    }

    public static class Query {
        public static final String[] PROJECTION = new String[] {
                SampleContract.Commits._ID,
                SampleContract.Commits.URL,
                SampleContract.Commits.AUTHOR_NAME,
                SampleContract.Commits.AUTHOR_EMAIL,
                SampleContract.Commits.AUTHOR_AVATAR,
                SampleContract.Commits.MESSAGE,
                SampleContract.Commits.COMMIT_DATE,
                SampleContract.Commits.SHA
        };

        public static final int ID = 0;
        public static final int URL = 1;
        public static final int AUTHOR_NAME = 2;
        public static final int AUTHOR_EMAIL = 3;
        public static final int AUTHOR_AVATAR = 4;
        public static final int MESSAGE = 5;
        public static final int COMMIT_DATE = 6;
        public static final int COMMIT_SHA = 7;
    }

    public static final CursorCreator<Commit> FACTORY = new CursorCreator<Commit>() {
        @Override
        public Commit createFromCursor(Cursor cursor) {
            return new Commit(cursor);
        }
    };
}

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

import android.content.ContentValues;
import android.database.Cursor;

import com.michaelrnovak.objectcursor.CursorCreator;
import com.michaelrnovak.objectcursor.samples.provider.SampleContract;

import java.util.Date;

public class Commit implements CursorCreator<Commit> {
    public static final Commit FACTORY = new Commit();

    private String mMessage;
    private String mUrl;
    private String mCommitSha;
    private Date mCommitDate;

    private String mAuthorAvatar;
    private String mAuthorName;
    private String mAuthorEmail;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public Date getDate() {
        return mCommitDate;
    }

    public void setDate(Date date) {
        mCommitDate = date;
    }

    public void setDate(long date) {
        mCommitDate = new Date(date);
    }

    public String getSha() {
        return mCommitSha;
    }

    public void setSha(String sha) {
        mCommitSha = sha;
    }

    public String getAuthorAvatar() {
        return mAuthorAvatar;
    }

    public void setAuthorAvatar(String avatar) {
        mAuthorAvatar = avatar;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String name) {
        mAuthorName = name;
    }

    public String getAuthorEmail() {
        return mAuthorEmail;
    }

    public void setAuthorEmail(String email) {
        mAuthorEmail = email;
    }

    @Override
    public Commit createFromCursor(Cursor cursor) {
        Commit commit = new Commit();

        commit.setMessage(cursor.getString(Query.MESSAGE));
        commit.setUrl(cursor.getString(Query.URL));
        commit.setDate(cursor.getLong(Query.COMMIT_DATE));
        commit.setSha(cursor.getString(Query.COMMIT_SHA));
        commit.setAuthorAvatar(cursor.getString(Query.AUTHOR_AVATAR));
        commit.setAuthorEmail(cursor.getString(Query.AUTHOR_EMAIL));
        commit.setAuthorName(cursor.getString(Query.AUTHOR_NAME));

        return commit;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(SampleContract.Commits.MESSAGE, getMessage());
        values.put(SampleContract.Commits.URL, getUrl());
        values.put(SampleContract.Commits.COMMIT_DATE, getDate().getTime());
        values.put(SampleContract.Commits.SHA, getSha());
        values.put(SampleContract.Commits.AUTHOR_AVATAR, getAuthorAvatar());
        values.put(SampleContract.Commits.AUTHOR_EMAIL, getAuthorEmail());
        values.put(SampleContract.Commits.AUTHOR_NAME, getAuthorName());

        return values;
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
}

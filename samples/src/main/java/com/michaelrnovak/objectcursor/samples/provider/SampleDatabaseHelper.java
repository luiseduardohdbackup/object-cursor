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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.michaelrnovak.objectcursor.samples.provider.SampleContract.Commits;

public class SampleDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sample.db";
    private static final int DATABASE_VERSION = 1;

    public interface Tables {
        String COMMITS = "commits";
    }

    public SampleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        buildCommitsTable(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }

    private void buildCommitsTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + Tables.COMMITS + " ("
                + Commits._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Commits.SHA + " TEXT NOT NULL,"
                + Commits.URL +  " TEXT NOT NULL,"
                + Commits.AUTHOR_NAME + " TEXT NOT NULL,"
                + Commits.AUTHOR_EMAIL + " TEXT NOT NULL,"
                + Commits.AUTHOR_AVATAR + " TEXT,"
                + Commits.MESSAGE + " TEXT,"
                + Commits.COMMIT_DATE + " INTEGER NOT NULL)");
    }
}

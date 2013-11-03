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

import android.net.Uri;
import android.provider.BaseColumns;

public class SampleContract {
    private static final Uri BASE_CONTENT_URI = Uri.parse(String.format("content://%s", SampleProvider.AUTHORITY));
    private static final String PATH_COMMITS = "commits";

    interface CommitColumns {
        String SHA = "sha_hash";
        String URL = "url";
        String AUTHOR_NAME = "author_name";
        String AUTHOR_EMAIL = "author_email";
        String AUTHOR_AVATAR = "author_avatar";
        String MESSAGE = "message";
        String COMMIT_DATE = "commit_date";
    }

    public static class BaseContract {
        public static String getId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    public static class Commits extends BaseContract implements BaseColumns, CommitColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMITS).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.michaelrnovak.objectcursor.commits";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.michaelrnovak.objectcursor.commits";

        public static Uri buildUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }
    }
}

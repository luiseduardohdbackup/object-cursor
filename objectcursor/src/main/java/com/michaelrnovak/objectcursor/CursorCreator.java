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

import android.content.ContentValues;
import android.database.Cursor;

/**
 * An object capable of building an object of type T from a cursor row.
 */
public interface CursorCreator<T> {
    /**
     * Creates a model mapping to the cursor schema. The implementation should be agnostic
     * to the current row.
     *
     * @param cursor The cursor object, typically positioned before the first row.
     * @return A model representing the schema of the cursor, but is not tied to a particular
     *     row.
     */
    T createFromCursor(Cursor cursor);
}

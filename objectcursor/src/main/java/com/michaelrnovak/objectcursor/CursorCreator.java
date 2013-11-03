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

/**
 * An object capable of building an object of type T from a cursor row.
 */
public interface CursorCreator<T> {
    /**
     * Creates an object using the current row of the cursor given here. The implementation should not advance/rewind
     * the cursor, and is only allowed to read the existing row.
     *
     * @param cursor The cursor object pointed at the row the object should be created from.
     * @return A real object built from the current row of the cursor.
     */
    T createFromCursor(Cursor cursor);
}

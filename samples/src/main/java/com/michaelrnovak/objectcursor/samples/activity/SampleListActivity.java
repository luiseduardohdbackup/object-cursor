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
package com.michaelrnovak.objectcursor.samples.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.michaelrnovak.objectcursor.samples.R;
import com.michaelrnovak.objectcursor.samples.fragment.CommitsListFragment;

public class SampleListActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        CommitsListFragment commitsFragment =
                (CommitsListFragment) getSupportFragmentManager().findFragmentByTag(CommitsListFragment.TAG);

        if (commitsFragment == null) {
            commitsFragment = new CommitsListFragment();
            commitsFragment.setRetainInstance(true);
        }

        if (!commitsFragment.isAdded()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_frame, commitsFragment, CommitsListFragment.TAG);
            ft.commit();
        }
    }
}

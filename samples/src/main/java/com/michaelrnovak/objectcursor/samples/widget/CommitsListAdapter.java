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
package com.michaelrnovak.objectcursor.samples.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaelrnovak.objectcursor.ObjectCursor;
import com.michaelrnovak.objectcursor.widget.ObjectCursorAdapter;

import com.squareup.picasso.Picasso;

import com.michaelrnovak.objectcursor.samples.R;
import com.michaelrnovak.objectcursor.samples.model.Commit;

public class CommitsListAdapter extends ObjectCursorAdapter<Commit> {

    public CommitsListAdapter(Context context, ObjectCursor<Commit> commitsCursor) {
        super(context, commitsCursor);
    }

    @Override
    public void bindView(View view, Context context, Commit commit) {
        ViewHolder holder = (ViewHolder) view.getTag();

        Picasso.with(context).load(commit.getAuthorAvatar()).into(holder.avatarView);

        String author = String.format("%s <%s>", commit.getAuthorName(), commit.getAuthorEmail());
        holder.authorView.setText(author);
        holder.messageView.setText(commit.getMessage());
    }

    @Override
    public View newView(Context context, Commit commit, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_commit, parent, false);

        if (view == null) {
            return null;
        }

        ViewHolder holder = new ViewHolder();
        holder.avatarView = (ImageView) view.findViewById(R.id.view_avatar);
        holder.authorView = (TextView) view.findViewById(R.id.view_author);
        holder.messageView = (TextView) view.findViewById(R.id.view_message);

        view.setTag(holder);
        return view;
    }

    static class ViewHolder {
        public ImageView avatarView;
        public TextView authorView;
        public TextView messageView;
    }
}

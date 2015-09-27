/*
 * Copyright 2015 Nicolas Pintos
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

package it.jaschke.alexandria;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putString(BookDetail.EAN_KEY, getIntent().getStringExtra(BookDetail.EAN_KEY));

            BookDetail fragment = new BookDetail();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.book_detail_container, fragment)
                    .commit();
        }
    }
}
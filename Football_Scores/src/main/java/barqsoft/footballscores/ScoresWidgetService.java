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

package barqsoft.footballscores;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoresWidgetService extends RemoteViewsService {
    public final String LOG_TAG = ScoresWidgetService.class.getSimpleName();

    private static final String[] SCORES_COLUMNS = {

            DatabaseContract.scores_table._ID,
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.TIME_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.LEAGUE_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.MATCH_ID,
            DatabaseContract.scores_table.MATCH_DAY
    };

    public double detail_match_id = 0;
    public static final int COL_DATE = 1;
    private static final int COL_MATCHTIME = 2;
    private static final int COL_HOME = 3;
    private static final int COL_AWAY = 4;
    public static final int COL_LEAGUE = 5;
    private static final int COL_HOME_GOALS = 6;
    private static final int COL_AWAY_GOALS = 7;
    private static final int COL_ID = 8;
    public static final int COL_MATCHDAY = 9;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                SimpleDateFormat format = new SimpleDateFormat(
                        getResources().getString(R.string.date_format), Locale.ENGLISH);
                String mDate = format.format(new Date());

                data = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                        SCORES_COLUMNS,
                        null,
                        new String[]{mDate},
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @SuppressLint("NewApi")
            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.scores_widget_item);

                views.setTextViewText(R.id.home_name,data.getString(COL_HOME));
                views.setTextViewText(R.id.away_name,data.getString(COL_AWAY));
                views.setTextViewText(R.id.data_textview, data.getString(COL_MATCHTIME));
                views.setTextViewText(R.id.score_textview, Utilities.getScores(data.getInt(COL_HOME_GOALS), data.getInt(COL_AWAY_GOALS)));
                views.setImageViewResource(R.id.home_crest, Utilities.getTeamCrestByTeamName(
                        data.getString(COL_HOME)));
                views.setImageViewResource(R.id.away_crest, Utilities.getTeamCrestByTeamName(
                        data.getString(COL_AWAY)));

                views.setContentDescription(R.id.home_crest, data.getString(COL_HOME));
                views.setContentDescription(R.id.away_crest, data.getString(COL_AWAY));


                return views;
            }

            /*@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(views.getLayoutId(), description);
            }*/

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.scores_widget_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(COL_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
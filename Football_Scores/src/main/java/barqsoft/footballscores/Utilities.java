package barqsoft.footballscores;

import android.content.res.Resources;

/**
 * Created by yehya khaled on 3/3/2015.
 */
class Utilities {

    private static final int SERIE_A = 357;
    private static final int CHAMPIONS_LEAGUE = 362;
    private static final int PRIMERA_DIVISION = 399;
    private static final int BUNDESLIGA = 351;
    private static final int BUNDESLIGA1 = 394;
    private static final int BUNDESLIGA2 = 395;
    private static final int LIGUE1 = 396;
    private static final int LIGUE2 = 397;
    private static final int PREMIER_LEAGUE = 398;
    private static final int SEGUNDA_DIVISION = 400;
    private static final int PRIMERA_LIGA = 402;
    private static final int Bundesliga3 = 403;
    private static final int EREDIVISIE = 404;

    private static final Resources res = FootballScoresApp.getRes();

    public static String getLeague(int league_num) {

        switch (league_num) {

            case SERIE_A :          return res.getString(R.string.league_serie_a);
            case PREMIER_LEAGUE :   return res.getString(R.string.league_premier);
            case CHAMPIONS_LEAGUE : return res.getString(R.string.league_champions);
            case PRIMERA_DIVISION : return res.getString(R.string.league_primera_division);
            case BUNDESLIGA :       return res.getString(R.string.league_bundesliga);
            case BUNDESLIGA1 :      return res.getString(R.string.league_bundesliga_1);
            case BUNDESLIGA2 :      return res.getString(R.string.league_bundesliga_2);
            case LIGUE1      :      return res.getString(R.string.league_1);
            case LIGUE2      :      return res.getString(R.string.league_2);
            case SEGUNDA_DIVISION : return res.getString(R.string.league_segunda_division);
            case PRIMERA_LIGA     : return res.getString(R.string.league_primera_liga);
            case Bundesliga3      : return res.getString(R.string.league_bundesliga_3);
            case EREDIVISIE       : return res.getString(R.string.league_eredivisie);

            default               : return res.getString(R.string.league_unknown);
        }
    }


    public static String getMatchDay(int match_day,int league_num) {

        if(league_num == CHAMPIONS_LEAGUE) {

            if (match_day <= 6) {
                return res.getString(R.string.group_stage_text, match_day);
            }
            else if(match_day == 7 || match_day == 8) {
                return res.getString(R.string.first_knockout_round);
            }
            else if(match_day == 9 || match_day == 10) {
                return res.getString(R.string.quarter_final);
            }
            else if(match_day == 11 || match_day == 12) {
                return res.getString(R.string.semi_final);
            }
            else {
                return res.getString(R.string.final_text);
            }
        }
        else {
            return res.getString(R.string.matchday_text, String.valueOf(match_day));
        }
    }

    public static String getScores(int home_goals,int awaygoals) {
       if(home_goals < 0 || awaygoals < 0) {
           return res.getString(R.string.score, "", "");
        }
        else {
            return res.getString(R.string.score, String.valueOf(home_goals),String.valueOf(awaygoals));
        }
    }

    public static int getTeamCrestByTeamName (String teamname) {
        if (teamname == null) {
            return R.drawable.ic_shield_grey600_48dp;
        }

        switch (teamname) {
            //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.ic_shield_grey600_48dp;
        }
    }
}

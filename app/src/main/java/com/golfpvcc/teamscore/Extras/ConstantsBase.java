package com.golfpvcc.teamscore.Extras;

/**
 * Created by vinnie on 11/27/2016.
 * DESCRIPTION:
 * Use interface to define '#define' constants' equivalent.
 * This file defines a Java Interface which contains a number
 * of compile time constants.
*/
public interface ConstantsBase  {
    int SCREEN_GAME_SUMMARY = 100, SCREEN_COURSE_LIST = 101, SCREEN_COURSE_SELECTED = 102, SCREEN_COURSE_SETUP = 103,
            SCREEN_PLAYERS_SETUP = 104, SCREEN_LOFT_GAME = 105, SCREEN_GAME_ON = 106;
    String NEXT_SCREEN = "NextScreen", COURSE_NAME = "CourseName";
    byte FRONT_NINE_TOTAL_DISPLAYED = '_', BACK_NINE_TOTAL_DISPLAYED = '-';
    int HOLES_18 = 18, SCREENHOLES = 9, NINETH_HOLE = 9, FIRST_HOLE = 0, NINETH_HOLE_ZB = 8;    // 8 is the nineth Hole Zero Based
    int PAR_3 = 3, PAR_4 = 4, PAR_5 = 5, MIM_COURSE_NAME = 2, PLAYER_TOTAL = 4;
    int NINE_PLAYERS = 3, PLAYER_1_INX = 0, PLAYER_2_INX = 1, PLAYER_3_INX = 2;
    int HOLE_ROW = 0, PAR_ROW = 1, HANDICAP_ROW = 2, PLAYER1_ROW = 3;
    int NAME_COL = 0, TOTAL_COL = 10;
    int DISPLAY_FRONT_NINE = 50, SCREEN_COL = 9;
    int DISPLAY_MODE_GROSS = 60, DISPLAY_MODE_NET = 61, DISPLAY_MODE_POINT_QUOTA = 62, DISPLAY_MODE_9_GAME = 63, DISPLAY_MODE_STABLEFORD = 64;
    int TEAM_GROSS_SCORE = 0x10, TEAM_NET_SCORE = 0x20, DOUBLE_TEAM_SCORE = 0x40, BIT_NOT_USED = 0x80, TEAM_SCORE = 0x70, JUST_RAW_SCORE = 0x0F;
    int PQ_TARGET = 0, PQ_EAGLE = 1, PQ_BIRDEIS = 2, PQ_PAR = 3, PQ_BOGGY = 4, PQ_DOUBLE = 5, PQ_OTHER = 6, PQ_ALBATROSS = 7, PQ_END = 8; // indexes into the point quaota array


    // golf summary - "Score", "Quota", "Sbfd", "Eagle", "Birdies", " Pars", "Bog", "Dbl", "Othr, Pts"
    int SUM_SCORE = 0, SUM_QUOTA = 1, SUM_STABLEFORD = 2, SUM_EAGLE = 3, SUM_BIRDIE = 4, SUM_PAR = 5, SUM__BOGGEY = 6, SUM_DOUBLE = 7, SUM_OTHER = 8, SUM_PTS = 9, SUM_END = 10;  // must be last one in the chain !!
    String POINT_QUOTA_DB = "PointQuota";
    String QUOTA_TARGET = "Target", QUOTA_ALBATROSS = "Albatross", QUOTA_EAGLE = "Eagle", QUOTA_BIRDIE = "Birdie", QUOTA_PAR = "Par", QUOTA_BOGGEY = "Boggey", QUOTA_DOUBLE = "Double", QUOTA_OTHER = "Other";
    String EMAIL_ADDRESS = "EmailAdd";
}

package com.jocv.taroogura.marshallese_dictionary;

/**
 * Created by taro on 2016/08/01.
 */
public class StaticInfo {
    public static int M2E_TABLE_ID = 1;
    public static int E2M_TABLE_ID = 2;
    public static int HISTORY_SHOW_LIMIT = 100;
    public static final String M2E_CON_TABLE_NAME = "mar2engCon";
    public static final String E2M_CON_TABLE_NAME = "eng2marCon";
    public static final String[] M2E_CON_COLUMNS = {"_id", "word", "ex", "exTrans"};
    public static final String[] E2M_CON_COLUMNS = {"_id", "word", "ex", "exTrans"};
    public static final String M2E_TABLE_NAME = "mar2eng";
    public static final String E2M_TABLE_NAME = "eng2mar";
    public static final String[] M2E_COLUMNS = {"_id", "wordE", "word", "phon", "def"};
    public static final String[] E2M_COLUMNS = {"_id", "word", "def"};
    public static String M2E_HIST_TBL_NAME = "m2eHist";
    public static String E2M_HIST_TBL_NAME = "e2mHist";

}

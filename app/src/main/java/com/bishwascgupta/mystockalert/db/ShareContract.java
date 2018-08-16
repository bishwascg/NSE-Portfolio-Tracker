package com.bishwascgupta.mystockalert.db;

import android.net.Uri;

public class ShareContract {

    // ContentProvider information
    public static final String CONTENT_AUTHORITY = "com.bishwascgupta.mystockalert";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MASTER = "master";

    // Database information
    static final String DB_NAME = "mystock_db";
    static final int DB_VERSION = 1;

    public static abstract class Master {
        public static final String NAME = "master";
        public static final String COL_STOCK_NAME = "stockName";
        public static final String COL_NUMSHARES = "noOfShares";
        public static final String COL_CMP = "cmp";
        static final String COL_CHANGE = "change";
        static final String COL_UPPER = "upper";
        static final String COL_LOWER = "lower";
        static final String COL_PORTFOLIO = "portfolio";
        static final String COL_ALERT = "alert";

        // ContentProvider information
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MASTER).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_MASTER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_MASTER;
    }


}
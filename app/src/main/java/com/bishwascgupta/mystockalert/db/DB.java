package com.bishwascgupta.mystockalert.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class DB extends SQLiteOpenHelper {


    // Database information
    //-9 and -9.9f are dummy values

    //Private variables
    private static volatile DB instance;
    private static SQLiteDatabase db;


    public DB(Context context) {
        super(context, ShareContract.DB_NAME, null, ShareContract.DB_VERSION);
        this.db = getWritableDatabase();
    }

    /**
     * Returns the the current DB instance
     *
     * @param c takes the current context as input
     * @return the current instance of the database
     */
    public static DB getInstance(Context c) {
        if (instance == null) {
            synchronized (DB.class) {
                if (instance == null) {
                    instance = new DB(c);
                }
            }
        }
        return instance;
    }



    /**
     * Create the tables
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create the queries to create the tables
        String CREATE_TABLE = "CREATE TABLE " + ShareContract.Master.NAME + "("
                + ShareContract.Master.COL_STOCK_NAME + " TEXT UNIQUE PRIMARY KEY,"
                + ShareContract.Master.COL_CMP + " REAL,"
                + ShareContract.Master.COL_CHANGE + " REAL,"
                + ShareContract.Master.COL_NUMSHARES + " INTEGER,"
                + ShareContract.Master.COL_LOWER + " REAL,"
                + ShareContract.Master.COL_UPPER + " REAL,"
                + ShareContract.Master.COL_PORTFOLIO + " INTEGER,"
                + ShareContract.Master.COL_ALERT + " INTEGER"
                + ")";

        //Execute the queries
        db.execSQL(CREATE_TABLE);

    }

    /**
     * Upgrade DB to newer version
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ShareContract.Master.NAME);

        // Create tables again
        onCreate(db);

    }

    /**
     * Provide access to the database.
     */
    public static SQLiteDatabase getDb() {
        return db;
    }



    /* CRUD Methods follow below */

    //Add Single Stock to Portfolio
    public int addStockPortfolio(Shares stock) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Get Content Values
        ContentValues values = new ContentValues();
        values.put(ShareContract.Master.COL_STOCK_NAME, stock.getStockName());
        values.put(ShareContract.Master.COL_CMP, stock.getCmp());
        values.put(ShareContract.Master.COL_CHANGE, stock.getChange());
        values.put(ShareContract.Master.COL_NUMSHARES, stock.getNoOfShares());
        values.put(ShareContract.Master.COL_PORTFOLIO, 1);

        //Query the DB
        Cursor cursor = db.query(ShareContract.Master.NAME, null,
                ShareContract.Master.COL_STOCK_NAME + "=?",
                new String[]{String.valueOf(stock.getStockName())}, null, null,
                null, null);

        //if not already present in Table, insert
        if (cursor.getCount() == 0) {
            values.put(ShareContract.Master.COL_LOWER, -9.9f);
            values.put(ShareContract.Master.COL_UPPER, -9.9f);
            values.put(ShareContract.Master.COL_ALERT, 0);
            db.insert(ShareContract.Master.NAME, null, values);
            db.close();
            return 1;

        } else {
            //If it is already present in Portfolio list,
            if(cursor != null && cursor.moveToFirst() && cursor.getInt(cursor.getColumnIndex(ShareContract.Master.COL_PORTFOLIO)) == 1)
            {
                db.close();
                return 0;
            }
            //If it is present in the Alert list
            else if(cursor != null && cursor.moveToFirst() && cursor.getInt(cursor.getColumnIndex(ShareContract.Master.COL_ALERT)) == 1)
            {
                values.put(ShareContract.Master.COL_LOWER,
                        cursor.getFloat(cursor.getColumnIndex(ShareContract.Master.COL_LOWER)));
                values.put(ShareContract.Master.COL_UPPER,
                        cursor.getFloat(cursor.getColumnIndex(ShareContract.Master.COL_UPPER)));
                values.put(ShareContract.Master.COL_ALERT,
                        cursor.getInt(cursor.getColumnIndex(ShareContract.Master.COL_ALERT)));
                db.update(ShareContract.Master.NAME, values,
                        ShareContract.Master.COL_STOCK_NAME + " = ?",
                        new String[]{String.valueOf(stock.getStockName())});
                db.close();
                return 1;
            }

            db.close();
            return 0;

        }
    }


    public int addStockAlert(Shares stock) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Get Content Values
        ContentValues values = new ContentValues();
        values.put(ShareContract.Master.COL_STOCK_NAME, stock.getStockName());
        values.put(ShareContract.Master.COL_CMP, stock.getCmp());
        values.put(ShareContract.Master.COL_ALERT, 1);
        values.put(ShareContract.Master.COL_LOWER, stock.getLower());
        values.put(ShareContract.Master.COL_UPPER, stock.getUpper());

        //Query the DB
        Cursor cursor = db.query(ShareContract.Master.NAME, null,
                ShareContract.Master.COL_STOCK_NAME + "=?",
                new String[] {String.valueOf(stock.getStockName())}, null, null,
                null, null);

        //if not already present in Table, insert
        if (cursor.getCount() == 0) {
            values.put(ShareContract.Master.COL_NUMSHARES, -9);
            values.put(ShareContract.Master.COL_PORTFOLIO, 0);
            db.insert(ShareContract.Master.NAME, null, values);
            db.close();
            return 1;

        } else {
            //If it is already present in Alert list,
            if(cursor != null && cursor.moveToFirst() && cursor.getInt(cursor.getColumnIndex(ShareContract.Master.COL_ALERT)) == 1)
            {
                db.close();
                return 0;
            }
            //If it is present in the Alert list
            else if(cursor != null && cursor.moveToFirst() && cursor.getInt(cursor.getColumnIndex(ShareContract.Master.COL_PORTFOLIO)) == 1)
            {

                db.update(ShareContract.Master.NAME, values,
                        ShareContract.Master.COL_STOCK_NAME + " = ?",
                        new String[]{String.valueOf(stock.getStockName())});
                db.close();
                return 1;
            }
            db.close();
            return 0;
        }
    }

    //Get an arraylist of all stocks in the portfolio table
    public ArrayList<String> getAllStocksNames() {

        ArrayList<String> stockList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  "+ShareContract.Master.COL_STOCK_NAME
                +" FROM " + ShareContract.Master.NAME ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                stockList.add(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_STOCK_NAME)));

            } while (cursor.moveToNext());
        }

        return stockList;
    }


    //Get an arraylist of all stocks in the portfolio table
    public ArrayList<Shares> getAllStocksPortfolio() {

        ArrayList<Shares> stockList = new ArrayList<Shares>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ShareContract.Master.NAME + " WHERE " +
                ShareContract.Master.COL_PORTFOLIO +" =?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{"1"} );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Shares stock = new Shares(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_STOCK_NAME)),
                        Float.parseFloat(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_CMP))),
                        Float.parseFloat(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_CHANGE))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_NUMSHARES))));

                stockList.add(stock);
            } while (cursor.moveToNext());
        }

        return stockList;
    }


    //Get an arraylist of all stocks in the alerts table
    public ArrayList<Shares> getAllStocksAlerts() {
        ArrayList<Shares> stockList = new ArrayList<Shares>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ShareContract.Master.NAME + " WHERE " +
                ShareContract.Master.COL_ALERT +"=?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{"1"});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Shares stock = new Shares(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_STOCK_NAME)),
                        Float.parseFloat(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_CMP))),
                        Float.parseFloat(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_LOWER))),
                        Float.parseFloat(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_UPPER))));
                stockList.add(stock);
            } while (cursor.moveToNext());
        }

        return stockList;
    }



    public int getAllStockPortfolioValue()
    {
        SQLiteDatabase db = this.getDb();
        ArrayList<Shares> list = getAllStocksPortfolio();
        int value = 0;
        for(int i =0;i<list.size();i++)
            value = value+list.get(i).getValue();

        return value;
    }

    //update a stock in the portfolio table
    public int updateStockPortfolio(Shares stock) {
        SQLiteDatabase db = this.getWritableDatabase();

        //get content values
        ContentValues values = new ContentValues();
        values.put(ShareContract.Master.COL_STOCK_NAME, stock.getStockName());
        values.put(ShareContract.Master.COL_CMP, stock.getCmp());
        values.put(ShareContract.Master.COL_CHANGE, stock.getChange());
        values.put(ShareContract.Master.COL_NUMSHARES, stock.getNoOfShares());

        // updating row
        return db.update(ShareContract.Master.NAME, values,
                ShareContract.Master.COL_STOCK_NAME + " = ?",
                new String[]{String.valueOf(stock.getStockName())});
    }

    //update a stock in the alert table
    public int updateStockAlert(Shares stock) {
        SQLiteDatabase db = this.getWritableDatabase();

        //get content values
        ContentValues values = new ContentValues();
        values.put(ShareContract.Master.COL_STOCK_NAME, stock.getStockName());
        values.put(ShareContract.Master.COL_LOWER, stock.getLower());
        values.put(ShareContract.Master.COL_UPPER, stock.getUpper());

        // updating row
        return db.update(ShareContract.Master.NAME, values,
                ShareContract.Master.COL_STOCK_NAME + " = ?",
                new String[]{String.valueOf(stock.getStockName())});

    }

    //Delete a stock from the portfolio table
    //todo change to work wit name
    public void deleteStockPortfolio(Shares stock) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(ShareContract.Master.NAME, null,
                ShareContract.Master.COL_STOCK_NAME + "=?",
                new String[] {String.valueOf(stock.getStockName())}, null, null,
                null, null);

        if(cursor != null && cursor.moveToFirst() && cursor.getInt(cursor.getColumnIndex(ShareContract.Master.COL_ALERT)) == 1)
        {
            ContentValues values = new ContentValues();
            values.put(ShareContract.Master.COL_NUMSHARES, -9);
            values.put(ShareContract.Master.COL_PORTFOLIO, 0);

            db.update(ShareContract.Master.NAME, values,
                    ShareContract.Master.COL_STOCK_NAME + " = ?",
                    new String[]{String.valueOf(stock.getStockName())});
            db.close();
        }
        else
        {
            db.delete(ShareContract.Master.NAME, ShareContract.Master.COL_STOCK_NAME +
                    " = ?", new String[]{String.valueOf(stock.getStockName())});
            db.close();
        }

        cursor.close();

    }

    public boolean isPortfolio(String name)
    {

        Cursor cursor = db.query(ShareContract.Master.NAME, null,
                ShareContract.Master.COL_STOCK_NAME + "=?",
                new String[] {name}, null, null,
                null, null);

        if(cursor != null && cursor.moveToFirst() && cursor.getInt(cursor.getColumnIndex(ShareContract.Master.COL_PORTFOLIO)) == 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }




    //Delete a alert from the portfolio table
    public void deleteStockAlert(Shares stock) {

        Cursor cursor = db.query(ShareContract.Master.NAME, null,
                ShareContract.Master.COL_STOCK_NAME + "=?",
                new String[] {String.valueOf(stock.getStockName())}, null, null,
                null, null);

        if(cursor != null && cursor.moveToFirst() && cursor.getInt(cursor.getColumnIndex(ShareContract.Master.COL_PORTFOLIO)) == 1)
        {
            ContentValues values = new ContentValues();
            values.put(ShareContract.Master.COL_UPPER, -9.9f);
            values.put(ShareContract.Master.COL_LOWER, -9.9f);
            values.put(ShareContract.Master.COL_ALERT, 0);

            db.update(ShareContract.Master.NAME, values,
                    ShareContract.Master.COL_STOCK_NAME + " = ?",
                    new String[]{String.valueOf(stock.getStockName())});
            db.close();
        }
        else
        {
            db.delete(ShareContract.Master.NAME, ShareContract.Master.COL_STOCK_NAME +
                    " = ?", new String[]{String.valueOf(stock.getStockName())});
            db.close();
        }

    }


    //return a stock
    public Shares getStock(String sname)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ShareContract.Master.NAME, new String[] {
                        ShareContract.Master.COL_STOCK_NAME, ShareContract.Master.COL_CMP,
                        ShareContract.Master.COL_CHANGE, ShareContract.Master.COL_NUMSHARES,
                        ShareContract.Master.COL_LOWER, ShareContract.Master.COL_UPPER},
                ShareContract.Master.COL_STOCK_NAME + "=?",
                new String[] { String.valueOf(sname) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Shares stock = new Shares(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_STOCK_NAME)),
                Float.parseFloat(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_CMP))),
                Float.parseFloat(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_CHANGE))),
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_NUMSHARES))),
                Float.parseFloat(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_LOWER))),
                Float.parseFloat(cursor.getString(cursor.getColumnIndex(ShareContract.Master.COL_UPPER))));

        return stock;

    }

    public int updateCmp(String sname, float cmp) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ShareContract.Master.NAME, null,
                ShareContract.Master.COL_STOCK_NAME + "=?",
                new String[] {sname}, null, null,
                null, null);

        if(cursor != null && cursor.moveToFirst())
        {
            ContentValues values = new ContentValues();
            values.put(ShareContract.Master.COL_CMP, cmp);
            return db.update(ShareContract.Master.NAME, values,
                    ShareContract.Master.COL_STOCK_NAME + " = ?",
                    new String[]{sname});
        }

        return 0;
    }

    public int updateChange(String sname, float change) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ShareContract.Master.NAME, null,
                ShareContract.Master.COL_STOCK_NAME + "=?",
                new String[] {sname}, null, null,
                null, null);

        if(cursor != null && cursor.moveToFirst() && cursor.getInt(cursor.getColumnIndex(ShareContract.Master.COL_PORTFOLIO)) == 1)
        {
            ContentValues values = new ContentValues();
            values.put(ShareContract.Master.COL_CHANGE, change);
            return db.update(ShareContract.Master.NAME, values,
                    ShareContract.Master.COL_STOCK_NAME + " = ?",
                    new String[]{sname});
        }

        return 0;
    }

}



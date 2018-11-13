package com.resolvebug.app.bahikhata;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_BAHIKHATA = "bahikhatadatabase";
    public static final String TABLE_TRANSACTION_DETAILS = "TRANSACTION_DETAILS";

    Context context;

    public DbHelper(@Nullable Context context) {
        super(context, DB_BAHIKHATA, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor fetchCSVData() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_TRANSACTION_DETAILS, new String[]{});
    }

}

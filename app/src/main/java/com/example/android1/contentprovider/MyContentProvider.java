package com.example.android1.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

public class MyContentProvider extends ContentProvider {
    static final String PROVIDER_NAME="com.example.android1.contentprovider.MyContentProvider";
    static final String URL="content://"+PROVIDER_NAME+"/students";

    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String NAME = "name";
    static final String GRADE = "grade";
    static final String MOBILE="mobile";
    static final String EMAIL="email";

    private static HashMap<String, String> STUDENTS_PROJECTION_MAP;

    static final int STUDENTS = 1;
    static final int STUDENT_ID = 2;

    static UriMatcher uriMatcher;


    static {
        uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"/Students",STUDENTS);
        uriMatcher.addURI(PROVIDER_NAME,"/Students#",STUDENT_ID);

    }

    private SQLiteDatabase db;
    static final String DataBaseName="College";
    static final String Students_TableName="Students";
    static final int DATA_BASE_VERSION=1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + Students_TableName+
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME +" TEXT NOT NULL, " +
                    EMAIL+" TEXT NOT NULL, " +
                    MOBILE+ "TEXT NOT NULL, " +
                    GRADE+ " TEXT NOT NULL);";


    public MyContentProvider() {
    }

    class DataBaseHelper extends SQLiteOpenHelper
    {


        public DataBaseHelper(Context context) {
            super(context,DataBaseName,null,DATA_BASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
          db.execSQL(CREATE_DB_TABLE);
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Students_TableName);
            onCreate(db);

        }


    }



    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long RowId=db.insert(Students_TableName,"",values);
        if (RowId > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI,RowId);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }



        throw new UnsupportedOperationException("Failed to insert data");
    }

    @Override
    public boolean onCreate() {
        Context context=getContext();
        DataBaseHelper dbHelper=new DataBaseHelper(context);
        db=dbHelper.getWritableDatabase();
        return (db==null)?false:true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Students_TableName);

        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                qb.setProjectionMap(STUDENTS_PROJECTION_MAP);
                break;

            case STUDENT_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on student names
             */
            sortOrder = NAME;
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case STUDENTS:
                count = db.delete(Students_TableName, selection, selectionArgs);
                break;

            case STUDENT_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( Students_TableName, _ID +  " = " + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                count = db.update(Students_TableName, values, selection, selectionArgs);
                break;

            case STUDENT_ID:
                count = db.update(Students_TableName, values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


}

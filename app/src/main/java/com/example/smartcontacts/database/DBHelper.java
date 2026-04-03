package com.example.smartcontacts.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartcontacts.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    // ── Schema ────────────────────────────────────────────────────────────────
    private static final String DB_NAME    = "smart_contacts.db";
    private static final int    DB_VERSION = 1;

    public static final String TABLE      = "contacts";
    public static final String COL_ID         = "id";
    public static final String COL_FIRST_NAME = "first_name";
    public static final String COL_LAST_NAME  = "last_name";
    public static final String COL_COMPANY    = "company";
    public static final String COL_PHONE      = "phone";
    public static final String COL_EMAIL      = "email";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE + " ("
            + COL_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_FIRST_NAME + " TEXT NOT NULL, "
            + COL_LAST_NAME  + " TEXT DEFAULT '', "
            + COL_COMPANY    + " TEXT DEFAULT '', "
            + COL_PHONE      + " TEXT NOT NULL, "
            + COL_EMAIL      + " TEXT DEFAULT ''"
            + ")";

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static volatile DBHelper instance;

    public static DBHelper getInstance(Context ctx) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(ctx.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // Insert new contact (create operation)
    public long insertContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.insert(TABLE, null, toContentValues(contact));
        } finally {
            db.close();
        }
    }

    // Read operation
    public List<Contact> getAllContacts() {
        List<Contact> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(
                "SELECT * FROM " + TABLE +
                " ORDER BY LOWER(" + COL_FIRST_NAME + ") ASC, LOWER(" + COL_LAST_NAME + ") ASC",
                null
            );
            while (c.moveToNext()) {
                list.add(fromCursor(c));
            }
        } finally {
            if (c != null) c.close();
            db.close();
        }
        return list;
    }

    // Getting single contact from id
    public Contact getContactById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(TABLE, null,
                    COL_ID + "=?", new String[]{String.valueOf(id)},
                    null, null, null);
            if (c.moveToFirst()) return fromCursor(c);
        } finally {
            if (c != null) c.close();
            db.close();
        }
        return null;
    }

    // search contacts by query
    public List<Contact> searchContacts(String query) {
        List<Contact> list = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return getAllContacts();
        }
        String like = "%" + query.trim() + "%";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(
                "SELECT * FROM " + TABLE +
                " WHERE " + COL_FIRST_NAME + " LIKE ? OR "
                           + COL_LAST_NAME  + " LIKE ? OR "
                           + COL_COMPANY    + " LIKE ? OR "
                           + COL_PHONE      + " LIKE ?" +
                " ORDER BY LOWER(" + COL_FIRST_NAME + ") ASC",
                new String[]{like, like, like, like}
            );
            while (c.moveToNext()) {
                list.add(fromCursor(c));
            }
        } finally {
            if (c != null) c.close();
            db.close();
        }
        return list;
    }

    // update operation
    public int updateContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.update(TABLE, toContentValues(contact),
                    COL_ID + "=?", new String[]{String.valueOf(contact.getId())});
        } finally {
            db.close();
        }
    }

    // Delete single using id
    public int deleteContact(int id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.delete(TABLE, COL_ID + "=?", new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
    }

    private ContentValues toContentValues(Contact c) {
        ContentValues cv = new ContentValues();
        cv.put(COL_FIRST_NAME, c.getFirstName()  != null ? c.getFirstName()  : "");
        cv.put(COL_LAST_NAME,  c.getLastName()   != null ? c.getLastName()   : "");
        cv.put(COL_COMPANY,    c.getCompany()    != null ? c.getCompany()    : "");
        cv.put(COL_PHONE,      c.getPhone()      != null ? c.getPhone()      : "");
        cv.put(COL_EMAIL,      c.getEmail()      != null ? c.getEmail()      : "");
        return cv;
    }

    private Contact fromCursor(Cursor c) {
        return new Contact(
            c.getInt   (c.getColumnIndexOrThrow(COL_ID)),
            c.getString(c.getColumnIndexOrThrow(COL_FIRST_NAME)),
            c.getString(c.getColumnIndexOrThrow(COL_LAST_NAME)),
            c.getString(c.getColumnIndexOrThrow(COL_COMPANY)),
            c.getString(c.getColumnIndexOrThrow(COL_PHONE)),
            c.getString(c.getColumnIndexOrThrow(COL_EMAIL))
        );
    }
}

package com.mibtech.nirmalBakery.helper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.mibtech.nirmalBakery.R;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "ekart.db";
    public static final String TABLE_FAVOURITE_NAME = "tblfavourite";
    public static final String KEY_ID = "pid";

    private final String TABLE_ORDER_NAME = "tblorder";
    private final String PID = "pid";
    private final String VID = "vid";
    private final String QTY = "qty";
    private final String PRODUCT_NAME = "name";
    private final String TOTAL_PRICE = "totalprice";
    public static DecimalFormat decimalformatData = new DecimalFormat("0.00");
    private String FavouriteTableInfo = TABLE_FAVOURITE_NAME + "(" + KEY_ID + " TEXT" + ")";
    private String OrderTableInfo = TABLE_ORDER_NAME + "(" + VID + " TEXT ," + PID + " TEXT ," + QTY + " TEXT ," + TOTAL_PRICE + " DOUBLE ," + PRODUCT_NAME + " TEXT" + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + FavouriteTableInfo);
        db.execSQL("CREATE TABLE " + OrderTableInfo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        replaceDataToNewTable(db, TABLE_FAVOURITE_NAME, FavouriteTableInfo);
        replaceDataToNewTable(db, TABLE_ORDER_NAME, OrderTableInfo);
        onCreate(db);
    }

    private void replaceDataToNewTable(SQLiteDatabase db, String tableName, String tableString) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableString);

        List<String> columns = getColumns(db, tableName);
        db.execSQL("ALTER TABLE " + tableName + " RENAME TO temp_" + tableName);
        db.execSQL("CREATE TABLE " + tableString);

        columns.retainAll(getColumns(db, tableName));
        String cols = join(columns, ",");
        db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s from temp_%s",
                tableName, cols, cols, tableName));
        db.execSQL("DROP TABLE temp_" + tableName);
    }

    private List<String> getColumns(SQLiteDatabase db, String tableName) {
        List<String> ar = null;
        Cursor c = null;
        try {
            c = db.rawQuery("select * from " + tableName + " limit 1", null);
            if (c != null) {
                ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return ar;
    }

    private String join(List<String> list, String divider) {
        StringBuilder buf = new StringBuilder();
        int num = list.size();
        for (int i = 0; i < num; i++) {
            if (i != 0)
                buf.append(divider);
            buf.append(list.get(i));
        }
        return buf.toString();
    }

    public boolean getFavouriteById(String story_id) {
        boolean count = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{story_id};
        Cursor cursor = db.rawQuery("SELECT " + KEY_ID + " FROM " + TABLE_FAVOURITE_NAME + " WHERE " + KEY_ID + "=? ", args);
        if (cursor.moveToFirst()) {
            count = true;
        }
        cursor.close();
        db.close();
        return count;
    }

    public void removeFavouriteById(String _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM  " + TABLE_FAVOURITE_NAME + " WHERE " + KEY_ID + " = " + _id);
        db.close();
    }

    public long addFavourite(String id) {
        ContentValues fav = new ContentValues();
        fav.put(DatabaseHelper.KEY_ID, id);
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TABLE_FAVOURITE_NAME, null, fav);
    }

    public ArrayList<String> getFavourite() {
        final ArrayList<String> ids = new ArrayList<>();
        String selectQuery = "SELECT *  FROM " + TABLE_FAVOURITE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return ids;
    }

    public ArrayList<String> getCartList() {
        final ArrayList<String> ids = new ArrayList<>();
        String selectQuery = "SELECT *  FROM " + TABLE_ORDER_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String count = cursor.getString(cursor.getColumnIndex(QTY));
                if (count.equals("0")) {
                    db.execSQL("DELETE FROM " + TABLE_ORDER_NAME + " WHERE " + VID + " = ? AND " + PID + " = ?", new String[]{cursor.getString(cursor.getColumnIndexOrThrow(VID)), cursor.getString(cursor.getColumnIndexOrThrow(PID))});

                } else
                    ids.add(cursor.getString(cursor.getColumnIndexOrThrow(PID)) + "=" + cursor.getString(cursor.getColumnIndexOrThrow(VID)) + "=" + cursor.getString(cursor.getColumnIndexOrThrow(QTY)) + "=" + cursor.getDouble(cursor.getColumnIndexOrThrow(TOTAL_PRICE)));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return ids;
    }

    public double getTotalCartAmt(Session session) {

        JSONArray vidlist = new JSONArray();
        JSONArray qtylist = new JSONArray();
        JSONArray namelist = new JSONArray();


        double total = 0.0;
        String selectQuery = "SELECT *  FROM " + TABLE_ORDER_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String count = cursor.getString(cursor.getColumnIndex(QTY));
                if (count.equals("0")) {
                    db.execSQL("DELETE FROM " + TABLE_ORDER_NAME + " WHERE " + VID + " = ? AND " + PID + " = ?", new String[]{cursor.getString(cursor.getColumnIndexOrThrow(VID)), cursor.getString(cursor.getColumnIndexOrThrow(PID))});

                } else {
                    total = total + cursor.getDouble(cursor.getColumnIndexOrThrow(TOTAL_PRICE));
                    vidlist.put(cursor.getString(cursor.getColumnIndexOrThrow(VID)));
                    qtylist.put(cursor.getString(cursor.getColumnIndexOrThrow(QTY)));
                    namelist.put(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_NAME)) + "==" + decimalformatData.format(cursor.getDouble(cursor.getColumnIndexOrThrow(TOTAL_PRICE))));
                }

            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();

        session.setData(Session.KEY_Ordervid, vidlist.toString());
        session.setData(Session.KEY_Orderqty, qtylist.toString());
        session.setData(Session.KEY_Ordername, namelist.toString());

        return total;
    }

    public int getTotalItemOfCart() {
        String countQuery = "SELECT  * FROM " + TABLE_ORDER_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void AddOrderData(String vid, String pid, String qty, double total, String name) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(VID, vid);
            values.put(PID, pid);
            values.put(QTY, qty);
            values.put(TOTAL_PRICE, total);
            values.put(PRODUCT_NAME, name);

            db.insert(TABLE_ORDER_NAME, null, values);
            db.close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void UpdateOrderData(String vid, String pid, String qty, double total, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(QTY, qty);
        values.put(TOTAL_PRICE, total);
        values.put(PRODUCT_NAME, name);

        db.update(TABLE_ORDER_NAME, values, VID + " = ? AND " + PID + " = ?", new String[]{vid, pid});
        db.close();
    }

    public void UpdateProductTotal(String vid, String pid, double total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TOTAL_PRICE, total);
        db.update(TABLE_ORDER_NAME, values, VID + " = ? AND " + PID + " = ?", new String[]{vid, pid});
        db.close();
    }

    public void DeleteOrderData(String vid, String pid) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_ORDER_NAME + " WHERE " + VID + " = ? AND " + PID + " = ?", new String[]{vid, pid});
        database.close();
    }

    public void DeleteAllOrderData() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_ORDER_NAME);
        database.close();

    }

    public String AddUpdateOrder(String vid, String pid, boolean isadd, Activity activity, boolean fromcart, double price, String name) {

        int qty = Integer.parseInt(CheckOrderExists(vid, pid));
        int newqty = qty;
        boolean ischange = true;


        if (isadd && (qty + 1) > Constant.MAX_PRODUCT_LIMIT) {
            ischange = false;
            Toast.makeText(activity, activity.getResources().getString(R.string.limit_alert), Toast.LENGTH_SHORT).show();
        } else if (isadd)
            newqty = qty + 1;
        else if (fromcart && qty > 1)
            newqty = qty - 1;
        else if (!fromcart && qty > 0)
            newqty = qty - 1;
        else
            ischange = false;

        double total = price * newqty;

        if (ischange && qty == 0) {
            AddOrderData(vid, pid, newqty + "", total, name);
        } else if (ischange && newqty == 0) {
            DeleteOrderData(vid, pid);
        } else if (ischange) {
            UpdateOrderData(vid, pid, newqty + "", total, name);
        }

        return newqty + "=" + decimalformatData.format(total);
    }

    public String CheckOrderExists(String vid, String pid) {

        String count = "0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDER_NAME + " WHERE " + VID + " = ? AND " + PID + " = ?", new String[]{vid, pid});
        if (cursor.moveToFirst()) {
            count = cursor.getString(cursor.getColumnIndex(QTY));
            if (count.equals("0")) {
                db.execSQL("DELETE FROM " + TABLE_ORDER_NAME + " WHERE " + VID + " = ? AND " + PID + " = ?", new String[]{vid, pid});

            }
        }
        cursor.close();
        db.close();

        return count;
    }

    public double getTotalKG(String pid) {
        double quantity = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ORDER_NAME + " WHERE " + PID + " = ?", new String[]{pid});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                int totalGram, qty;
                qty = c.getInt(c.getColumnIndex(QTY));
                String measurement = c.getString(c.getColumnIndex(PRODUCT_NAME)).split("==")[0];

                if (measurement.contains("kg") || measurement.contains("ltr")) {
                    String strGram = measurement.replaceAll("[a-zA-Z]", "").trim();
                    totalGram = (qty * Integer.parseInt(strGram) * 1000);
                } else {
                    String strGram = measurement.replaceAll("[a-zA-Z]", "").trim();
                    totalGram = (qty * Integer.parseInt(strGram));
                }
                quantity += totalGram;

            } while (c.moveToNext());
        }
        return quantity;
    }
}

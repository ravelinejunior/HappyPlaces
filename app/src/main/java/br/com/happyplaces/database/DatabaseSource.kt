package br.com.happyplaces.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import br.com.happyplaces.model.HappyPlaceModel

class DatabaseSource(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        //create table
        val createHappyPlaceTable =
            ("CREATE TABLE IF NOT EXISTS $HAPPYPLACE_TABLE" +
                    "($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$KEY_TITLE TEXT," +
                    "$KEY_IMAGE TEXT, " +
                    "$KEY_DESCRIPTION TEXT, " +
                    "$KEY_DATE TEXT, " +
                    "$KEY_LOCATION TEXT, " +
                    "$KEY_LATITUDE TEXT, " +
                    "$KEY_LONGITUDE )")

        db?.execSQL(createHappyPlaceTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTable = "DROP TABLE IF EXISTS $HAPPYPLACE_TABLE"
        db?.execSQL(dropTable)
    }

    fun addHappyPlace(happyPlace: HappyPlaceModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title)
        contentValues.put(KEY_IMAGE, happyPlace.image)
        contentValues.put(KEY_DESCRIPTION, happyPlace.description)
        contentValues.put(KEY_DATE, happyPlace.date)
        contentValues.put(KEY_LOCATION, happyPlace.location)
        contentValues.put(KEY_LATITUDE, happyPlace.latitude)
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude)

        val result = db.insert(HAPPYPLACE_TABLE, null, contentValues)

        db.close()
        return result

    }

    fun updateHappyPlace(happyPlace: HappyPlaceModel): Int {
        val db = this.writableDatabase
        val updateSQL = "$KEY_ID = ${happyPlace.id}"
        try {
            val contentValues = ContentValues()
            contentValues.put(KEY_TITLE, happyPlace.title)
            contentValues.put(KEY_IMAGE, happyPlace.image)
            contentValues.put(KEY_DESCRIPTION, happyPlace.description)
            contentValues.put(KEY_DATE, happyPlace.date)
            contentValues.put(KEY_LOCATION, happyPlace.location)
            contentValues.put(KEY_LATITUDE, happyPlace.latitude)
            contentValues.put(KEY_LONGITUDE, happyPlace.longitude)

            val result = db.update(HAPPYPLACE_TABLE, contentValues, updateSQL, null)
            db.close()
            return result
        } catch (e: SQLiteException) {
            e.printStackTrace()
            db.close()
            return -1
        }
    }

    fun deleteHappyPlace(happyPlace: HappyPlaceModel): Int {

        val db = this.writableDatabase
        val deleteSQL = "$KEY_ID = ${happyPlace.id}"

        try {
            val deletedResult = db.delete(HAPPYPLACE_TABLE, deleteSQL, null)
            db.close()
            return deletedResult
        } catch (e: SQLiteException) {
            e.printStackTrace()
            db.close()
            return -1
        }
    }

    fun getHappyPlacesList(): ArrayList<HappyPlaceModel> {
        val happyPlacesList = ArrayList<HappyPlaceModel>()
        val selectQuery = "SELECT * FROM $HAPPYPLACE_TABLE"
        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val placeModel = HappyPlaceModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
                    )

                    happyPlacesList.add(placeModel)

                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            e.printStackTrace()

            return ArrayList()
        }

        db.close()

        return happyPlacesList
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "HappyPlace_Db"
        private const val HAPPYPLACE_TABLE = "HappyPlace_tbl"

        //COLUMN NAME
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

}
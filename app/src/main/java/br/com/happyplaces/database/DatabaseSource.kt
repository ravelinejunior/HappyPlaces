package br.com.happyplaces.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
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
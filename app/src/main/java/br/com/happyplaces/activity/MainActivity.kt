package br.com.happyplaces.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.happyplaces.R
import br.com.happyplaces.adapter.HappyPlaceAdapter
import br.com.happyplaces.database.DatabaseSource
import br.com.happyplaces.model.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: HappyPlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabAddHappyPlace.setOnClickListener {
            val intent = Intent(this, AddHappyPlace::class.java)
            startActivityForResult(intent, ADD_PLACE_REQUEST_CODE)
        }

    }

    private fun getHappyPlacesFromLocal() {
        val db = DatabaseSource(this@MainActivity)
        val getHappyPlaces: ArrayList<HappyPlaceModel> = db.getHappyPlacesList()

        if (getHappyPlaces.size > 0) {
            recyclerView_Main.visibility = View.VISIBLE
            noHappyPlaces_textView_id.visibility = View.GONE
            setupRecyclerView(getHappyPlaces)
        } else {
            recyclerView_Main.visibility = View.GONE
            noHappyPlaces_textView_id.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView(listHappyPlace: ArrayList<HappyPlaceModel>) {
        adapter = HappyPlaceAdapter(applicationContext, listHappyPlace)
        recyclerView_Main.layoutManager = LinearLayoutManager(this)
        recyclerView_Main.setHasFixedSize(true)
        recyclerView_Main.adapter = adapter


    }

    override fun onStart() {
        super.onStart()
        getHappyPlacesFromLocal()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PLACE_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getHappyPlacesFromLocal()
            }
        }
    }

    companion object{
        const val ADD_PLACE_REQUEST_CODE = 100
    }
}
package br.com.happyplaces.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.happyplaces.R
import br.com.happyplaces.activity.MainActivity.Companion.HAPPYPLACE_KEY
import br.com.happyplaces.model.HappyPlaceModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mHappyPlace: HappyPlaceModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        getModelExtra()

        if (mHappyPlace != null) {
            setSupportActionBar(toolbar_maps_id)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = mHappyPlace?.title

            toolbar_maps_id.setNavigationOnClickListener { onBackPressed() }

            val supportMapFragment: SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.mapFragment_id) as SupportMapFragment
            supportMapFragment.getMapAsync(this)
        }


    }

    private fun getModelExtra() {
        if (intent.hasExtra(HAPPYPLACE_KEY)) {
            mHappyPlace = intent.getSerializableExtra(HAPPYPLACE_KEY) as HappyPlaceModel
        }
    }

    override fun onMapReady(map: GoogleMap?) {

        try{
            val position = LatLng(mHappyPlace!!.latitude,mHappyPlace!!.longitude)
            map?.addMarker(MarkerOptions().position(position).title(mHappyPlace!!.location))
            val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position,12f)
            map?.animateCamera(newLatLngZoom)
            
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}
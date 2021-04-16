package br.com.happyplaces.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.happyplaces.R
import br.com.happyplaces.activity.MainActivity.Companion.HAPPYPLACE_KEY
import br.com.happyplaces.model.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_happy_place_detail.*

class HappyPlaceDetail : AppCompatActivity(), View.OnClickListener {
    var model: HappyPlaceModel? = null

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_detail)


        var imageUri: Uri? = null

        if (intent.hasExtra(HAPPYPLACE_KEY)) {
            model = intent.getSerializableExtra(HAPPYPLACE_KEY) as HappyPlaceModel
        }

        if (model != null) {
            tv_nameDetail_id.text = model?.title
            tv_descriptionDetail_id.text = model?.description
            tv_locationDetail_id.text = model?.location

            imageUri = Uri.parse(model?.image)
            iv_detail_id.setImageURI(imageUri)

            toolbar_detail_id.title = model?.title

            toolbar_detail_id.setOnClickListener {
                onBackPressed()
            }

            btn_openMap_detail_id.setOnClickListener(this)

        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_openMap_detail_id -> {
                val intent = Intent(this@HappyPlaceDetail, MapsActivity::class.java)
                intent.putExtra(HAPPYPLACE_KEY, model)
                startActivity(intent)

            }
        }
    }
}
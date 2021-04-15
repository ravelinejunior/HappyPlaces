package br.com.happyplaces.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import br.com.happyplaces.R
import br.com.happyplaces.activity.MainActivity.Companion.HAPPYPLACE_KEY
import br.com.happyplaces.database.DatabaseSource
import br.com.happyplaces.model.HappyPlaceModel
import br.com.happyplaces.utils.ApiKey.GOOGLE_MAPS_API_KEY
import br.com.happyplaces.utils.ApiKey.GOOGLE_MAPS_API_KEY_AMBEV
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlace : AppCompatActivity(), View.OnClickListener {

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double? = 0.0
    private var mLongitude: Double? = 0.0

    private var mHappyPlaceDetail: HappyPlaceModel? = null


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        setSupportActionBar(toolbar_add_place)
        toolbar_add_place.textAlignment = View.TEXT_ALIGNMENT_CENTER
        toolbar_add_place.setOnClickListener {
            onBackPressed()
        }

        //initializing the map
        if (!Places.isInitialized()) {
            Places.initialize(this@AddHappyPlace, GOOGLE_MAPS_API_KEY)
        }

        if (intent.hasExtra(HAPPYPLACE_KEY)) {
            mHappyPlaceDetail = intent.getSerializableExtra(HAPPYPLACE_KEY) as HappyPlaceModel
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->

            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)
            updateDateView()

        }

        if (mHappyPlaceDetail != null) {
            supportActionBar?.title = mHappyPlaceDetail?.title
            fillDataIfEdit(mHappyPlaceDetail)

        }

        et_date.setOnClickListener(this)

        btn_save.setOnClickListener(this)

        tv_add_image.setOnClickListener(this)

        et_location.setOnClickListener(this)

        btn_selectCurrentPosition_id.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlace,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            R.id.tv_add_image -> {
                val pickerDialog = AlertDialog.Builder(this)
                pickerDialog.setTitle("Select an Action")

                val pictureDialogItems =
                    arrayOf("Select photo from gallery", "Capture photo from camera")

                pickerDialog.setItems(pictureDialogItems) { dialogInterface, item ->
                    when (item) {
                        0 -> {
                            choosePhotoFromGallery()
                        }
                        1 -> {
                            choosePhotoFromCamera()
                        }
                    }
                }

                val dialog = pickerDialog.create()
                dialog.show()

            }

            R.id.btn_save -> {

                if (mHappyPlaceDetail != null) {
                    updateHappyPlace()
                } else {
                    addHappyPlace()
                }

            }

            R.id.et_location -> {
                try {
                    val fields = listOf(
                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS
                    )

                    val intent =
                        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(applicationContext)

                    startActivityForResult(intent, PLACE_MAPS_KEY)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun fillDataIfEdit(happyPlace: HappyPlaceModel?) {
        if (happyPlace != null) {

            et_title.setText(happyPlace.title)
            et_description.setText(happyPlace.description)
            et_date.setText(happyPlace.date)
            et_location.setText(happyPlace.location)

            saveImageToInternalStorage = Uri.parse(happyPlace.image)
            iv_place_image.setImageURI(saveImageToInternalStorage)

            mLatitude = happyPlace.latitude
            mLongitude = happyPlace.longitude

            btn_save.text = "UPDATE"
        }
    }

    private fun updateDateView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        et_date.setText(sdf.format(cal.time).toString())
    }

    private fun checkIfFieldsAreEmpty(): Boolean {
        var check = false
        when {
            et_title.text.isNullOrEmpty() -> {
                Toast.makeText(applicationContext, "Title can´t be empty!", Toast.LENGTH_SHORT)
                    .show()
            }
            et_description.text.isNullOrEmpty() -> {
                Toast.makeText(
                    applicationContext,
                    "Description can´t be empty!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            et_date.text.isNullOrEmpty() -> {
                Toast.makeText(applicationContext, "Date can´t be empty!", Toast.LENGTH_SHORT)
                    .show()
            }
            et_location.text.isNullOrEmpty() -> {
                Toast.makeText(applicationContext, "Location can´t be empty!", Toast.LENGTH_SHORT)
                    .show()
            }
            saveImageToInternalStorage == null -> {
                Toast.makeText(applicationContext, "Image can´t be empty!", Toast.LENGTH_SHORT)
                    .show()
            }

            else -> {
                check = true
            }
        }
        return check
    }

    private fun addHappyPlace() {
        val db = DatabaseSource(applicationContext)

        val title = et_title.text.toString()
        val date = et_date.text.toString()
        val location = et_location.text.toString()
        val description = et_description.text.toString()

        if (checkIfFieldsAreEmpty()) {
            val happyPlace = HappyPlaceModel(
                0,
                title,
                saveImageToInternalStorage.toString(),
                description,
                date,
                location,
                mLatitude!!,
                mLongitude!!
            )

            val addHappyPlace = db.addHappyPlace(happyPlace)

            if (addHappyPlace > 0) {
                Toast.makeText(applicationContext, "Inserted with success!", Toast.LENGTH_SHORT)
                    .show()
                setResult(Activity.RESULT_OK)
                finish()
                // startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun updateHappyPlace() {
        val db = DatabaseSource(this)

        val title = et_title.text.toString()
        val date = et_date.text.toString()
        val location = et_location.text.toString()
        val description = et_description.text.toString()

        if (checkIfFieldsAreEmpty()) {

            val happyPlace = HappyPlaceModel(
                mHappyPlaceDetail!!.id,
                title,
                saveImageToInternalStorage.toString(),
                description,
                date,
                location,
                mLatitude!!,
                mLongitude!!
            )

            val updateHappyPlace = db.updateHappyPlace(happyPlace)
            if (updateHappyPlace > 0) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }


    private fun choosePhotoFromGallery() {
        Dexter.withContext(applicationContext).withPermissions(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(
                        galleryIntent,
                        GALLERY_KEY
                    )
                } else {
                    Toast.makeText(
                        this@AddHappyPlace,
                        "Check if your phone has permissions!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showDialogForPermissions()
            }

        }).onSameThread()
            .check()
    }

    private fun choosePhotoFromCamera() {
        Dexter.withContext(applicationContext).withPermissions(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val galleryIntent =
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(
                        galleryIntent,
                        CAMERA_KEY
                    )
                } else {
                    Toast.makeText(
                        this@AddHappyPlace,
                        "Check if your phone has permissions!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showDialogForPermissions()
            }

        }).onSameThread()
            .check()
    }

    private fun showDialogForPermissions() {
        val builder = AlertDialog.Builder(this@AddHappyPlace)
        builder.setMessage("Go to application settings to get permission!")
        builder.setPositiveButton("Settings") { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)

                intent.data = uri
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
        builder.setNegativeButton("Cancel") { dialogInterface, item ->
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            stream.flush()
            stream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_KEY) {
                if (data != null) {
                    val contentUri = data.data
                    try {

                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)

                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        Log.i("SavedImage", "onActivityResult: $saveImageToInternalStorage")
                        iv_place_image.setImageBitmap(selectedImageBitmap)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, e.printStackTrace().toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else if (requestCode == CAMERA_KEY) {
                if (data != null) {
                    val thumbnails: Bitmap = data!!.extras!!.get("data") as Bitmap
                    try {

                        saveImageToInternalStorage = saveImageToInternalStorage(thumbnails)
                        Log.i("SavedImage", "onActivityResult: $saveImageToInternalStorage")
                        iv_place_image.setImageBitmap(thumbnails)

                    } catch (e: IOException) {
                        Toast.makeText(
                            this@AddHappyPlace,
                            "Error: ${e.printStackTrace()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else if (requestCode == PLACE_MAPS_KEY) {
                if (data != null) {
                    val place: Place = Autocomplete.getPlaceFromIntent(data)

                    et_location.setText(place.address)
                    mLatitude = place.latLng?.latitude
                    mLongitude = place.latLng?.longitude
                }

            }
        }
    }

    companion object {
        const val GALLERY_KEY = 200
        const val CAMERA_KEY = 201
        const val PLACE_MAPS_KEY = 202
        const val IMAGE_DIRECTORY = "HappyImageStore"

    }

}
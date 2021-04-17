package br.com.happyplaces.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import java.util.*

class GetAddressFromLatLng(
    context: Context, private val lat: Double, private val lng: Double
) : AsyncTask<Void, String, String>() {

    private val geoCoder: Geocoder = Geocoder(context, Locale.getDefault())
    private lateinit var mAddressListener: AddressListener

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg data: Void?): String {
        val addressList: List<Address>? = geoCoder.getFromLocation(lat, lng, 1)

        try {
            if (addressList != null && addressList.isNotEmpty()) {
                val address: Address = addressList[0]
                val stringBuilder = StringBuilder()

                for (i in 0..address.maxAddressLineIndex) {
                    stringBuilder.append(address.getAddressLine(i)).append(" ")
                }

                stringBuilder.deleteCharAt(stringBuilder.length - 1)
                return stringBuilder.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    override fun onPostExecute(result: String?) {
        if (result == null){
            mAddressListener.onError()
        }else{
            mAddressListener.onAddressFound(result)
        }

        super.onPostExecute(result)
    }

    fun getAddress(){
        execute()
    }

    fun setAddressListener(addressListener:AddressListener){
        mAddressListener = addressListener
    }

    interface AddressListener {
        fun onAddressFound(address: String?)
        fun onError()
    }


}
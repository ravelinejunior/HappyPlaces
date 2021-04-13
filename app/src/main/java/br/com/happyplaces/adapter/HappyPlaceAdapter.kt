package br.com.happyplaces.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.happyplaces.R
import br.com.happyplaces.model.HappyPlaceModel
import com.google.android.material.card.MaterialCardView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_happy_places.view.*

open class HappyPlaceAdapter(
    private val context: Context,
    private var items: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<HappyPlaceAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_happy_places, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val happyPlace: HappyPlaceModel = items[position]
        val uriImage: Uri = Uri.parse(happyPlace.image)

        holder.tv_title.text = happyPlace.title
        holder.iv_image.setImageURI(uriImage)
        holder.tv_description.text = happyPlace.description
        holder.tv_location.text = happyPlace.location

    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_title: TextView = view.tv_title_adapterHappyPlace_id
        val iv_image: CircleImageView = view.iv_adapterHappyPlace_id
        val tv_description: TextView = view.tv_description_adapterHappyPlace_id
        val tv_location: TextView = view.tv_location_adapterHappyPlace_id
        val mCardView: MaterialCardView = view.mCardView_itemAdapter_id
    }

}
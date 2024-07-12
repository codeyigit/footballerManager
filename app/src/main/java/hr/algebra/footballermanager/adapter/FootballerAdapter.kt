package hr.algebra.footballermanager.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.algebra.footballermanager.App
import hr.algebra.footballermanager.FOOTBALLER_ID
import hr.algebra.footballermanager.NavigableFragment
import hr.algebra.footballermanager.R
import hr.algebra.footballermanager.dao.Footballer
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FootballerAdapter(
    private val context: Context,
    private val footballers: MutableList<Footballer>,
    private val navigableFragment: NavigableFragment
)
    : RecyclerView.Adapter<FootballerAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTittle)
        private val ivImage = itemView.findViewById<ImageView>(R.id.ivImage)
        val ivDelete = itemView.findViewById<ImageView>(R.id.ivDelete)

        fun bind(footballer: Footballer) {
            tvTitle.text = footballer.toString()
            Picasso.get()
                .load(File(footballer.picturePath))
                .error(R.mipmap.ic_launcher)
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(itemView = LayoutInflater.from(context).inflate(R.layout.footballer, parent, false))

    override fun getItemCount() = footballers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivDelete.setOnLongClickListener {
            GlobalScope.launch (Dispatchers.Main){
                withContext(Dispatchers.IO) {
                    (context?.applicationContext as App).getFootballerDao().delete(footballers[position])
                    File(footballers[position].picturePath!!).delete()
                }
                footballers.removeAt(position)
                notifyDataSetChanged()
            }
            true
        }
        holder.itemView.setOnLongClickListener {
            navigableFragment.navigate(Bundle().apply {
                putLong(FOOTBALLER_ID, footballers[position]._id!!)
            })
            true
        }
        holder.bind(footballers[position])
    }
}
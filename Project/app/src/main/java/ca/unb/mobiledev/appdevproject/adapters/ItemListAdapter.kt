package ca.unb.mobiledev.appdevproject.adapters

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.appdevproject.R
import ca.unb.mobiledev.appdevproject.entities.Item

class ItemListAdapter(private val items : MutableList<Item>) :
    RecyclerView.Adapter<ItemListAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_view, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {

        val item = items.elementAt(holder.absoluteAdapterPosition)

        holder.itemIDTextView.text = holder.resources?.getString(R.string.itemID, item.itemID)
        holder.itemDescTextView.text = holder.resources?.getString(R.string.itemDescription, item.description)

        Log.d("scanned", item.toString())
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // Inner ViewHolder Class
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemIDTextView : TextView = itemView.findViewById(R.id.itemID)
        val itemDescTextView : TextView = itemView.findViewById(R.id.description)
        val resources: Resources? = itemView.context.resources
    }
}
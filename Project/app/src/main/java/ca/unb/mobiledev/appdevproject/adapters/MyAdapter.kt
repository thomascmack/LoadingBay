package ca.unb.mobiledev.appdevproject.adapters

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.appdevproject.R
import ca.unb.mobiledev.appdevproject.classes.ItemList

class MyAdapter(private val items : ItemList) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scanned_list_view, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {

        val item = items.elementAt(position)

        Log.d("scanned", item.toString())

        holder.courseIdTextView.text = holder.resources!!.getString(R.string.item_id, item.upc)
        holder.courseNameTextView.text = holder.resources.getString(R.string.item_name, item.upc.toString())
        holder.itemFlagTextView.text = item.flag
        if(item.damaged) {
            holder.damagedTextView.text = holder.resources.getString(R.string.damaged)
        }
        else {
            holder.damagedTextView.text = holder.resources.getString(R.string.not_damaged)
        }

        //holder.itemView.setOnClickListener { listener(course) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // Inner ViewHolder Class
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseIdTextView: TextView = itemView.findViewById(R.id.itemID)
        val courseNameTextView: TextView = itemView.findViewById(R.id.itemName)
        val itemFlagTextView: TextView = itemView.findViewById(R.id.flag)
        val damagedTextView : TextView = itemView.findViewById(R.id.damaged)
        val resources: Resources? = itemView.context.resources
    }
}
package ca.unb.mobiledev.appdevproject

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val items : ArrayList<InventoryItem>) :
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

        holder.courseIdTextView.text = holder.resources!!.getString(R.string.item_id, item.id)
        holder.courseNameTextView.text = holder.resources.getString(R.string.item_name, item.name)
        holder.itemQuantityTextView.text = holder.resources.getString(R.string.quantity, item.quantity)
        holder.damagedTextView.text = holder.resources.getString(R.string.num_damaged, item.damaged)

        //holder.itemView.setOnClickListener { listener(course) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // Inner ViewHolder Class
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseIdTextView: TextView = itemView.findViewById(R.id.itemID)
        val courseNameTextView: TextView = itemView.findViewById(R.id.itemName)
        val itemQuantityTextView: TextView = itemView.findViewById(R.id.quantity)
        val damagedTextView : TextView = itemView.findViewById(R.id.damaged)
        val resources: Resources? = itemView.context.resources
    }
}
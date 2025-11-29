package ca.unb.mobiledev.appdevproject.adapters

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.appdevproject.R
import ca.unb.mobiledev.appdevproject.classes.ProductList

class MyAdapter(private val products : ProductList) :
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

        val product = products.elementAt(position)

        Log.d("scanned", product.toString())

        holder.upcTextView.text = holder.resources!!.getString(R.string.item_id, product.product.upc)

        holder.itemNameTextView.text = holder.resources.getString(R.string.item_name, product.product.itemName)

        holder.itemsReceivedTextView.text = holder.resources.getString(R.string.receivedCount, products.countReceived(product.product.upc), products.countExpected(product.product.upc))

        holder.damagedTextView.text = holder.resources.getString(R.string.damaged, products.countDamaged(product.product.upc))
        //holder.itemView.setOnClickListener { listener(course) }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    // Inner ViewHolder Class
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val upcTextView: TextView = itemView.findViewById(R.id.itemID)
        val itemNameTextView: TextView = itemView.findViewById(R.id.itemName)
        val itemsReceivedTextView: TextView = itemView.findViewById(R.id.received)
        val damagedTextView : TextView = itemView.findViewById(R.id.damaged)
        val resources: Resources? = itemView.context.resources
    }
}
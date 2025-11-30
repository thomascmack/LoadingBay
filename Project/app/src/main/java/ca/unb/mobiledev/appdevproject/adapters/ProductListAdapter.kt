package ca.unb.mobiledev.appdevproject.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.appdevproject.R
import ca.unb.mobiledev.appdevproject.classes.ProductList

class ProductListAdapter(private val products : ProductList) :
    RecyclerView.Adapter<ProductListAdapter.MyViewHolder>() {

        var mExpandedPosition = - 1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_list_view, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val isExpanded = holder.absoluteAdapterPosition == mExpandedPosition
        holder.itemRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded
        holder.itemView.setOnClickListener {
            mExpandedPosition = if (isExpanded) -1 else holder.absoluteAdapterPosition
            notifyItemChanged(holder.absoluteAdapterPosition)
        }

        val product = products.elementAt(holder.absoluteAdapterPosition)

        holder.itemRecyclerView.adapter = ItemListAdapter(products, product.product.upc, this)

        holder.upcTextView.text = holder.resources!!.getString(R.string.item_id, product.product.upc)

        holder.itemNameTextView.text = holder.resources.getString(R.string.item_name, product.product.itemName)

        holder.itemsReceivedTextView.text = holder.resources.getString(R.string.receivedCount, products.countReceived(product.product.upc), products.countExpected(product.product.upc))

        holder.damagedTextView.text = holder.resources.getString(R.string.count_damaged, products.countDamaged(product.product.upc))
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
        val itemRecyclerView : RecyclerView = itemView.findViewById(R.id.itemRecyclerView)
    }
}
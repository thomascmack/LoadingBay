package ca.unb.mobiledev.appdevproject.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.appdevproject.R
import ca.unb.mobiledev.appdevproject.activities.ItemEditActivity
import ca.unb.mobiledev.appdevproject.classes.ProductList
import ca.unb.mobiledev.appdevproject.entities.Item

class ItemListAdapter(val productList : ProductList, upc : Long, val parentAdapter : ProductListAdapter) :
    RecyclerView.Adapter<ItemListAdapter.MyViewHolder>() {

        var items = productList.getProduct(upc)?.items ?: mutableListOf()
    lateinit var mRecyclerView : RecyclerView
    lateinit var context : Context


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_view, parent, false)
        return MyViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        mRecyclerView = recyclerView
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {

        val item = items.elementAt(holder.absoluteAdapterPosition)

        updateView(item, holder)

        holder.deleteButton.setOnClickListener {
            productList.removeItem(holder.absoluteAdapterPosition, items)
            notifyDataSetChanged()
            parentAdapter.notifyDataSetChanged()
        }

        holder.editButton.setOnClickListener {
            val intent = Intent(context, ItemEditActivity::class.java)
            intent.putExtra("itemID", item.itemID)
            intent.putExtra("itemName", productList.getItemName(item))
            intent.putExtra("upc", item.upc)
            intent.putExtra("damaged", item.damaged)
            intent.putExtra("description", item.description)
            (context as Activity).startActivityForResult(intent , 0)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateView(item : Item, holder : MyViewHolder) {
        holder.itemNameTextView.text = holder.resources?.getString(R.string.item_name, productList.getItemName(item))
        holder.itemFlagTextView.text = holder.resources?.getString(R.string.flag, item.flag)
        holder.itemDescTextView.text = holder.resources?.getString(R.string.itemDescription, item.description)

        if(item.flag != "Missing") {
            holder.deleteButton.visibility = View.VISIBLE
            holder.editButton.visibility = View.VISIBLE
        }
        else {
            holder.deleteButton.visibility = View.GONE
            holder.editButton.visibility = View.GONE
        }

        if(item.damaged) {
            holder.itemDamagedTextView.visibility = View.VISIBLE
        }
        else {
            holder.itemDamagedTextView.visibility = View.GONE
        }
    }

    // Inner ViewHolder Class
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemNameTextView : TextView = itemView.findViewById(R.id.itemName)
        val itemFlagTextView : TextView = itemView.findViewById(R.id.flag)
        val itemDescTextView : TextView = itemView.findViewById(R.id.description)
        val itemDamagedTextView : TextView = itemView.findViewById(R.id.damaged)
        val deleteButton : Button = itemView.findViewById(R.id.deleteButton)
        val editButton : Button = itemView.findViewById(R.id.editButton)
        val resources: Resources? = itemView.context.resources
    }
}
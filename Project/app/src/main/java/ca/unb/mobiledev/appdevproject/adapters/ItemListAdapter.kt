package ca.unb.mobiledev.appdevproject.adapters

import android.app.Activity
import android.app.Dialog
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

private lateinit var dialog : Dialog
class ItemListAdapter(val productList : ProductList, val upc : Long, val parentAdapter : ProductListAdapter) :
    RecyclerView.Adapter<ItemListAdapter.MyViewHolder>() {

        var items = productList.getProduct(upc)?.items ?: mutableListOf()
    lateinit var mRecyclerView : RecyclerView
    lateinit var context : Context
    val EDIT_REQUEST_CODE = 1


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
            val dialog = Dialog(context, R.style.DialogWindowTheme)
            dialog.setContentView(R.layout.delete_item_dialog)
            dialog.show()
            val confirmButton: Button = dialog.findViewById(R.id.confirmButton)
            val backButton: Button = dialog.findViewById(R.id.backButton)
            val warningText : TextView = dialog.findViewById(R.id.warningText)
            warningText.text = context.getString(R.string.delete_item, productList.getItemName(item))

            confirmButton.setOnClickListener {
                productList.removeItem(holder.absoluteAdapterPosition, items)
                if(items.isEmpty()) productList.removeProduct(upc)
                notifyDataSetChanged()
                parentAdapter.notifyDataSetChanged()
                dialog.cancel()
            }

            backButton.setOnClickListener {
                dialog.cancel()
            }
        }

        holder.editButton.setOnClickListener {
            val intent = Intent(context, ItemEditActivity::class.java)
            intent.putExtra("itemID", item.itemID)
            intent.putExtra("itemName", productList.getItemName(item))
            intent.putExtra("upc", item.upc)
            intent.putExtra("damaged", item.damaged)
            intent.putExtra("description", item.description)
            (context as Activity).startActivityForResult(intent , EDIT_REQUEST_CODE)
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
            holder.item.background = holder.resources!!.getDrawable(R.drawable.item_received)
            //holder.item.setBackgroundColor(holder.resources!!.getColor(R.color.light_green))
        }
        else {
            holder.deleteButton.visibility = View.GONE
            holder.editButton.visibility = View.GONE
            holder.item.background = holder.resources!!.getDrawable(R.drawable.item_missing)
            //holder.item.setBackgroundColor(holder.resources!!.getColor(R.color.off_white))
        }

        if(item.damaged) {
            holder.itemDamagedTextView.visibility = View.VISIBLE
            holder.item.background = holder.resources!!.getDrawable(R.drawable.item_damaged)
            //holder.item.setBackgroundColor(holder.resources!!.getColor(R.color.light_orange))
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
        val item: View = itemView.findViewById(R.id.item)
    }
}
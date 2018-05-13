package com.example.android.starbucksfinder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
/**
 * Created by leech on 5/6/2018.
 */

/**
 * Provide views to RecyclerView with data from dataSet.
 *
 * Initialize the dataset of the Adapter.
 *
 * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
 */
class ListItemAdapter(val dataSet: ArrayList<Store>, private var mCallBacks: ListFragment) :
        RecyclerView.Adapter<ListItemAdapter.ViewHolder>() {
    init {
        if (dataSet.size==0){
            dataSet.add(Store("No result", "Please try another location", 0.0, 0.0, false))
        }
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        val textViewAddress: TextView
        val textViewOpen: TextView

        init {
            v.setOnClickListener(this);
            textViewAddress = v.findViewById(R.id.textView_address)
            textViewOpen = v.findViewById(R.id.textView_open)
        }

        override fun onClick(p0: View?) {
            mCallBacks.listItemClicked(adapterPosition);
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        var v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.text_row_item, viewGroup, false)
        // Define click listener for the ViewHolder's View.
        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.textViewAddress.text = "${dataSet[position].name}\n${dataSet[position].vicinity}"
        val openClosed = if (dataSet[position].openNow) "open" else "closed"
        viewHolder.textViewOpen.text = "$openClosed"

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    companion object {
        private val TAG = "ListItemAdapter"
    }
}

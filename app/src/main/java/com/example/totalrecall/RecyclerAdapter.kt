package com.example.totalrecall

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.generateViewId
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.totalrecall.data.Resource
import com.example.totalrecall.data.ResourceRepository
import com.example.totalrecall.data.ResourceTagRel
import com.example.totalrecall.data.Tag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecyclerAdapter(private val resources: MutableList<Resource>, private val resourceRepository: ResourceRepository) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var onClickListener: View.OnClickListener? = null

    fun setOnClickListener(clickListener: View.OnClickListener) {
        onClickListener = clickListener
    }

    inner class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.card)
        val textView: TextView = view.findViewById(R.id.resource_name_recycler)

        init {
            cardView.setOnClickListener(onClickListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = resources[position].title
        holder.cardView.tag = resources[position].resourceId
    }

    override fun getItemCount(): Int {
            return resources.size
    }

    fun setList(list: List<Resource>) {
        resources.clear()
        resources.addAll(list)
    }

    fun addToList(r: Resource): Int {
        resources.add(r)
        return resources.lastIndex
    }

    fun removeFromList(i: Int): Int {

        for((x, r) in resources.withIndex()) {
            if (r.resourceId == i) {
                resources.remove(r)
                return x
            }
        }
        return -1

    }

}
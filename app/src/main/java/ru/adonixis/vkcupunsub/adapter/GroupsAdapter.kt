package ru.adonixis.vkcupunsub.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_group.view.*
import ru.adonixis.vkcupunsub.R
import ru.adonixis.vkcupunsub.models.VKGroup
import java.util.*


class GroupsAdapter(
    private val groups : ArrayList<VKGroup>,
    private val context: Context
) : RecyclerView.Adapter<UnsubViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnsubViewHolder {
        return UnsubViewHolder(LayoutInflater.from(context).inflate(R.layout.item_group, parent, false))
    }

    override fun onBindViewHolder(holder: UnsubViewHolder, position: Int) {
        val group = groups[position]
        val groupName = group.name
        val groupPhoto = group.photo200
        holder.tvGroupName.text = groupName

        Glide
            .with(context)
            .load(groupPhoto)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder_document_image_72)
            .into(holder.icGroup)

        if (group.isChecked) {
            holder.icCheck.visibility = View.VISIBLE
            holder.imageBorderFrame.visibility = View.VISIBLE
        } else {
            holder.icCheck.visibility = View.GONE
            holder.imageBorderFrame.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return groups.size
    }
}

class UnsubViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val icGroup = view.icGroup!!
    val tvGroupName = view.tvGroupName!!
    val icCheck = view.icCheck!!
    val imageBorderFrame = view.imageBorderFrame!!
}
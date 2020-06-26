package com.kok.roadtoyou.ui.addplan

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.item_add_place.view.*
import java.util.*
import kotlin.collections.ArrayList

class MakePlanRecyclerViewAdapter(
    var items: ArrayList<AddPlaceItem>,
    var listener: RecyclerViewAdapterEventListener,
    var startDragListener: OnStartDragListener
) :RecyclerView.Adapter<MakePlanRecyclerViewAdapter.ViewHolder>(),  ItemTouchHelperCallback.OnItemMoveListener {

    lateinit var context: Context

    interface RecyclerViewAdapterEventListener {
        fun onChangeCallback(view: View, items: ArrayList<AddPlaceItem>)
    }

    interface OnStartDragListener {
        fun onStartDrag(dragHolder: ViewHolder)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var placeCount: TextView = itemView.findViewById(R.id.tv_placeCount)
        var placeNm: TextView = itemView.findViewById(R.id.tv_placeNm)
        var placeType: TextView = itemView.findViewById(R.id.tv_placeType)
        var moveBtn: ImageView = itemView.findViewById(R.id.movebtn)
        var syncBtn : ImageView = itemView.findViewById(R.id.syncbtn)
        var editBtn : ImageView = itemView.findViewById(R.id.editbtn)

        init {
            editBtn.setOnClickListener { view ->
                val popupMenu = PopupMenu(context, view)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.edit_order ->{
                            moveBtn.visibility = View.VISIBLE
                            syncBtn.visibility = View.VISIBLE
                            true
                        }
                        R.id.deletePlace ->{
                            items.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            for (i in 0 until items.size) {
                                items[i].count = i + 1
                            }
                            listener.onChangeCallback(itemView, items)
                            notifyDataSetChanged()
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.inflate(R.menu.edit_plan_menu)

                try {
                    val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                    fieldMPopup.isAccessible = true
                    val mPopup = fieldMPopup.get(popupMenu)
                    mPopup.javaClass
                        .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        .invoke(mPopup, true)
                } catch (e: Exception){
                    Log.e("Main", "Error showing menu icons.", e)
                } finally {
                    popupMenu.show()
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.item_add_place, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.placeCount.text = items[position].count.toString()
        holder.placeNm.text = items[position].title
        holder.placeType.text = when ( items[position].type) {
            12 -> "관광지"
            14 -> "문화 시설"
            15 -> "행사/공연/축제"
            28 -> "레포츠"
            32 -> "숙박"
            39 -> "음식점"
            else -> "기타"
        }

        holder.moveBtn.setOnTouchListener { view, motionEvent ->
            if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                startDragListener.onStartDrag(holder)
            }
            return@setOnTouchListener false
        }

        holder.itemView.syncbtn.setOnClickListener {
            holder.itemView.movebtn.visibility = View.GONE
            holder.itemView.syncbtn.visibility = View.GONE

            holder.itemView.editbtn.visibility = View.VISIBLE
            listener.onChangeCallback(it, items)
            notifyDataSetChanged()
        }
    }

    override fun onItemMove(
        fromPosition: Int,
        toPosition: Int,
        viewHolder: RecyclerView.ViewHolder
    ): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }

        for (i in 0 until items.size) {
            items[i].count = i + 1
        }

        notifyItemMoved(fromPosition, toPosition)
        //notifyDataSetChanged()
        listener.onChangeCallback(viewHolder.itemView, items)
        return true
    }

}
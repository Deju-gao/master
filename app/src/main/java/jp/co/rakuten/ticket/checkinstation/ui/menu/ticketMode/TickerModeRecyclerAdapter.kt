package jp.co.rakuten.ticket.checkinstation.ui.menu.ticketMode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jp.co.rakuten.ticket.checkinstation.R

//ticket mode adapter
class TickerModeRecyclerAdapter(private val onItemClick: (Int) -> Unit): RecyclerView.Adapter<TickerModeRecyclerAdapter.ViewHolder>() {

    //mode list
    var items: List<String> = emptyList()
    //current select index
    var currentSelectIndex: Int = 0

    override fun getItemCount(): Int = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.ticket_mode_row,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        if (position == currentSelectIndex) {
            holder.itemView.setBackgroundResource(R.drawable.bg_ticket_mode_fill_row)
        }
        else {
            holder.itemView.setBackgroundResource(R.drawable.bg_ticket_mode_empty_row)
        }
        holder.titleView.text = item
        holder.itemView.setOnClickListener { onItemClick(position) }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.modeTitle)
    }

}
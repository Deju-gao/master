package jp.co.rakuten.ticket.checkinstation.ui.general.generalVerify

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectTarget.TicketItem

class TicketListAdapter(
    private val context: Context,
) :
    RecyclerView.Adapter<TicketListAdapter.ViewHolder>() {
    var items: List<TicketItem> = emptyList()

    override fun getItemCount(): Int = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.ticket_normal_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.contentView.text = item.content
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contentView: TextView = itemView.findViewById(R.id.content)
    }
}

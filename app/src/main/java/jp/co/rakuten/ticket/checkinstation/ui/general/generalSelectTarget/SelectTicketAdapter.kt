package jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectTarget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import jp.co.rakuten.ticket.checkinstation.R

class SelectTicketAdapter(
    private val context: Context,
    private val onItemClick: (ticket: TicketItem) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<TicketItem> = emptyList()

    override fun getItemCount(): Int = items.count()

    override fun getItemViewType(position: Int): Int {
        return when (items[position].status) {
            TicketStatus.OVER -> {
                R.layout.ticket_over_row
            }
            TicketStatus.PRINTING -> {
                R.layout.ticket_printing_row
            }
            TicketStatus.ENABLE -> {
                R.layout.ticket_enable_row
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            R.layout.ticket_over_row -> {
                return OverViewHolder(
                    LayoutInflater.from(context).inflate(
                        R.layout.ticket_over_row,
                        parent,
                        false
                    )
                )
            }
            R.layout.ticket_printing_row -> {
                return PrintingViewHolder(
                    LayoutInflater.from(context).inflate(
                        R.layout.ticket_printing_row,
                        parent,
                        false
                    )
                )
            }
            R.layout.ticket_enable_row -> {
                return EnableViewHolder(
                    LayoutInflater.from(context).inflate(
                        R.layout.ticket_enable_row,
                        parent,
                        false
                    )
                )
            }
            else ->{
                return OverViewHolder(
                    LayoutInflater.from(context).inflate(
                        R.layout.ticket_over_row,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when(holder){
            is OverViewHolder ->{
                holder.contentView.text = item.content
                holder.dateView.text = context.getString(R.string.ticket_date_unit,item.date)
            }
            is PrintingViewHolder ->{
                holder.contentView.text = item.content
                holder.dateView.text = context.getString(R.string.ticket_date_unit, item.date)
            }
            is EnableViewHolder ->{
                holder.contentView.text = item.content
                var isSelect = false
                if (item.isSelected) {
                    isSelect = true
                    holder.iconView.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.on_check))
                }
                else {
                    isSelect = false
                    holder.iconView.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.off_check))
                }
                holder.itemView.setOnClickListener {
                    if (isSelect){
                        item.isSelected = false
                        onItemClick(item)
                        isSelect = false
                        holder.iconView.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.off_check))
                    }else{
                        item.isSelected = true
                        onItemClick(item)
                        isSelect = true
                        holder.iconView.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.on_check))
                    }
                }
            }
        }
    }

    class OverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contentView: TextView = itemView.findViewById(R.id.content)
        var dateView: TextView = itemView.findViewById(R.id.date)
    }

    class PrintingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contentView: TextView = itemView.findViewById(R.id.content)
        var dateView: TextView = itemView.findViewById(R.id.date)
    }

    class EnableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iconView: ImageView = itemView.findViewById(R.id.icon)
        var contentView: TextView = itemView.findViewById(R.id.content)
    }
}

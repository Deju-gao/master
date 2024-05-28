package jp.co.rakuten.ticket.checkinstation.ui.menu.printerSetting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jp.co.rakuten.ticket.checkinstation.R

//ticket mode adapter
class PrinterSettingRecyclerAdapter(private val onItemClick: (Int) -> Unit): RecyclerView.Adapter<PrinterSettingRecyclerAdapter.ViewHolder>() {

    //mode list
    var items: List<HashMap<String, String>> = emptyList()
    //current select index
    var currentSelectIndex: Int = 0

    override fun getItemCount(): Int = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.printer_setting_row,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.printerName.text = item["PrinterName"]
        holder.targetInfo.text = item["Target"]
        holder.itemView.setOnClickListener { onItemClick(position) }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val printerName: TextView = itemView.findViewById(R.id.printerName)
        val targetInfo: TextView = itemView.findViewById(R.id.targetInfo)
    }

}
package android.spendingmanager.pv239.muni.fi.cz.spendingmanager.account

import android.app.Activity
import android.graphics.Color
import android.spendingmanager.pv239.muni.fi.cz.spendingmanager.R
import android.spendingmanager.pv239.muni.fi.cz.spendingmanager.transaction.Transaction
import android.spendingmanager.pv239.muni.fi.cz.spendingmanager.transaction.TransactionType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class LastTransactionsAdapter(
        val context : Activity,
        var transactions : List<Transaction>
) : BaseAdapter() {

    fun update (list : List<Transaction>) {
        transactions = list
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val transaction = getItem(position)

        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.account_transaction_item, parent, false)
        }

        val tranCatTv = view?.findViewById(R.id.account_transaction_item_category_name_tv) as TextView
        tranCatTv.text = transaction.category.categoryName

        val price = view.findViewById(R.id.account_transaction_item_price_tv) as TextView
        val sign = if (transaction.type == TransactionType.INCOME) "+" else "-"
        price.text = "$sign ${transaction.price} CZK"
        val fontColor = if(transaction.type == TransactionType.EXPENDITURE) Color.RED else Color.GREEN
        price.setTextColor(fontColor)

        val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val dateTv = view.findViewById(R.id.account_transaction_item_date_tv) as TextView
        dateTv.text = dateFormatter.format(transaction.datetime.time)

        val noteTv = view.findViewById(R.id.account_transaction_item_note_tv) as TextView
        noteTv.text = transaction.description

        return view
    }

    override fun getItem(position: Int): Transaction = transactions[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = transactions.size
}
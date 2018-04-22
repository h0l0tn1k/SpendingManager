package android.spendingmanager.pv239.muni.fi.cz.spendingmanager.planning

import android.spendingmanager.pv239.muni.fi.cz.spendingmanager.categories.Category
import android.spendingmanager.pv239.muni.fi.cz.spendingmanager.categories.CategoryType
import android.spendingmanager.pv239.muni.fi.cz.spendingmanager.transaction.Transaction
import android.spendingmanager.pv239.muni.fi.cz.spendingmanager.transaction.TransactionType
import java.util.*

class PlannedTransaction : Transaction {

    var frequency : TransactionFrequency? = null

    constructor(
            frequency : TransactionFrequency
    ) : super(TransactionType.EXPENDITURE, 250, Category("id", "Lunch", CategoryType.DEFAULT), "Some meet and fruits", GregorianCalendar(2018, 3, 12, 12, 34) ,"" ) {
        this.frequency = frequency
    }
}
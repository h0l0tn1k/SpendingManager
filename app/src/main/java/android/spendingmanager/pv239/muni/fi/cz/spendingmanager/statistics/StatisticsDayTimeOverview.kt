package android.spendingmanager.pv239.muni.fi.cz.spendingmanager.statistics

import android.os.Bundle
import android.spendingmanager.pv239.muni.fi.cz.spendingmanager.R
import android.spendingmanager.pv239.muni.fi.cz.spendingmanager.categories.Category
import android.spendingmanager.pv239.muni.fi.cz.spendingmanager.categories.DefaultCategories
import android.spendingmanager.pv239.muni.fi.cz.spendingmanager.firebase.FirebaseDb
import android.spendingmanager.pv239.muni.fi.cz.spendingmanager.transaction.Transaction
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class StatisticsDayTimeOverview : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.statistics_day_time_overview, container, false)

        val weeksSpiner = view.findViewById<View>(R.id.weeksSpinner) as Spinner
        val items = arrayOf("1", "2", "3", "4 (month)", "52 (year)")
        val weeksSpinerAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, items)
        weeksSpiner.adapter = weeksSpinerAdapter
        val spinnerPosition = weeksSpinerAdapter.getPosition("4 (month)")
        weeksSpiner.setSelection(spinnerPosition)

        val categoryListView = view.findViewById<View>(R.id.categories_list) as ListView
        val categories : MutableList<String> = mutableListOf<String>()
        val categoriesList : MutableList<Category> = mutableListOf<Category>()
        var transactions : MutableList<Transaction> = mutableListOf()

        val categoriesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Category>()
                snapshot.children.mapNotNullTo(list) {
                    val category = it.getValue<Category>(Category::class.java)
                    category?.key = it.key
                    category
                }
                categories.clear()
                list.forEach { x -> categoriesList.add(x) }
                DefaultCategories.getDefaultCategories().forEach { x -> categoriesList.add(x) }

                categoriesList.forEachIndexed { index, category ->
                    categories.add(category.categoryName)
                }
                val categoriesAdapter = ArrayAdapter<String>(activity, R.layout.statistics_date_graphs_catlist, categories)
                categoryListView.adapter = categoriesAdapter
                categoryListView.itemsCanFocus = false
                categoryListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                categoriesList.forEachIndexed { index, category ->
                    categoryListView.setItemChecked(index,true)
                }
                // Set categoryListView height (not working automatically)
                val params = categoryListView.layoutParams
                params.height = 120 * categoriesList.count()
                categoryListView.layoutParams = params
                categoryListView.requestLayout()

                val transactionsListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dataSnapshot.children.mapNotNullTo(transactions) {
                            val transaction = it.getValue<Transaction>(Transaction::class.java)
                            transaction?.key = it.key
                            transaction
                        }

                        var btnWeekSpinner = view.findViewById<View>(R.id.btnWeeksSpinner) as Button
                        btnWeekSpinner.setOnClickListener {
                            GenerateGraphs(view, weeksSpiner, categoryListView, categories, transactions)
                        }
                        GenerateGraphs(view, weeksSpiner, categoryListView, categories, transactions)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        println("loadPost:onCancelled ${databaseError.toException()}")
                    }
                }

                FirebaseDb.getUserReference("transactions")?.addValueEventListener(transactionsListener)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        FirebaseDb.getUserReference("categories")?.addValueEventListener(categoriesListener)

        return view
    }

    private fun GenerateGraphs(view: View, weeksSpiner: Spinner, categoryListView : ListView, categories: List<String>, transactions : MutableList<Transaction>)
    {
        val currentDate = GregorianCalendar.getInstance()
        val onlyNumbers = Regex("[^0-9]")
        val weeksCount : Int = onlyNumbers.replace(weeksSpiner.selectedItem.toString(), "").toInt()

        val chart = view.findViewById<View>(R.id.chart) as LineChart
        chart.removeAllViews()


        var colorsList : List<Int> = StatisticsHelper().getColors()

        // Graph Data
        val dataSets : MutableList<LineDataSet> = mutableListOf<LineDataSet>()

        categories.forEachIndexed { index, category ->
            if (categoryListView.isItemChecked(index))
            {
                val yVals : MutableList<Entry> = mutableListOf<Entry>()

                for (i in 0..24) {
                    yVals.add(Entry(i.toFloat(), StatisticsHelper().CalculateTimeTransactions(category, transactions, weeksCount, currentDate, i).toFloat()))
                }

                val set: LineDataSet
                set = LineDataSet(yVals, category)
                set.fillAlpha = 110

                set.color = colorsList.get(index % colorsList.count())
                set.setCircleColor(colorsList.get(index % colorsList.count()))
                set.lineWidth = 1f
                set.circleRadius = 3f
                set.setDrawCircleHole(false)
                set.valueTextSize = 9f
                set.setDrawFilled(false)

                dataSets.add(set)
            }
        }

        val lineData = LineData(dataSets.toList())

        chart.data = lineData
        chart.description.isEnabled = false

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setLabelCount(24, true)
    }

}
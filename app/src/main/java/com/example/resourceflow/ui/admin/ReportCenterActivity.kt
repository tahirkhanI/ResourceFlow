package com.example.resourceflow.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R

class ReportCenterActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ReportsAdapter
    private val allReports = mutableListOf<Report>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_center)

        recycler = findViewById(R.id.recyclerReports)

        // ✅ Pass both parameters: list + onLongPress lambda
        adapter = ReportsAdapter(allReports) { report ->
            // Handle long press here
            Toast.makeText(this, "Report long pressed: ${report.reportId}", Toast.LENGTH_SHORT).show()

            // Example: you could show a dialog here for updating status
            // showUpdateStatusDialog(report)
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // TODO: Fetch reports from API
        // fetchReports()
    }

    // Example function if you want to update report status
    private fun showUpdateStatusDialog(report: Report) {
        // TODO: Implement AlertDialog to update status
        Toast.makeText(this, "Update status for report: ${report.reportId}", Toast.LENGTH_SHORT).show()
    }
}

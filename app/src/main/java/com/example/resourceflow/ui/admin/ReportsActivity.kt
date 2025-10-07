package com.example.resourceflow.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R
import com.example.resourceflow.network.ApiClient
import com.example.resourceflow.network.BasicResponse
import com.example.resourceflow.ui.admin.ReportsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.example.resourceflow.ui.admin.Report


class ReportsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReportsAdapter
    private val reportsList = mutableListOf<Report>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        recyclerView = findViewById(R.id.recyclerReports)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReportsAdapter(reportsList) { report ->
            showStatusDialog(report)
        }
        recyclerView.adapter = adapter

        fetchReports()
    }

    private fun fetchReports() {
        ApiClient.apiService.getReports().enqueue(object : Callback<ReportsResponse> {
            override fun onResponse(call: Call<ReportsResponse>, response: Response<ReportsResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    reportsList.clear()
                    reportsList.addAll(response.body()!!.reports)
                    adapter.updateList(reportsList)
                } else {
                    Toast.makeText(this@ReportsActivity, "Failed to load reports", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ReportsResponse>, t: Throwable) {
                Toast.makeText(this@ReportsActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showStatusDialog(report: Report) {
        val options = arrayOf("Completed", "In Progress")
        AlertDialog.Builder(this)
            .setTitle("Update Status")
            .setItems(options) { _, which ->
                val newStatus = if (which == 0) "Completed" else "In Progress"
                updateReportStatus(report, newStatus)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateReportStatus(report: Report, newStatus: String) {
        ApiClient.apiService.updateReportStatus(report.reportId, newStatus)
            .enqueue(object : Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@ReportsActivity, "Status updated!", Toast.LENGTH_SHORT).show()

                        val index = reportsList.indexOfFirst { it.reportId == report.reportId }
                        if (index != -1) {
                            reportsList[index] = reportsList[index].copy(status = newStatus)
                            adapter.updateList(reportsList)
                        }
                    } else {
                        Toast.makeText(this@ReportsActivity, "Failed to update status", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Toast.makeText(this@ReportsActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

package com.example.resourceflow.ui.admin.resourcemanage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R
import com.example.resourceflow.network.AppConfig

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Data class for resource
data class Resource(
    val id: Int,
    val classroom_id: Int,
    val resource_name: String,
    val availability: String,
    val quantity: Int,
    val created_at: String
)

// Data class for resource list response
data class ResourceListResponse(
    val success: Boolean,
    val resources: List<Resource>? = null,
    val error: String? = null
)

// Data class for add resource request
data class AddResourceRequest(
    val classroom_id: Int,
    val resource_name: String,
    val quantity: Int,
    val availability: String
)

// Data class for add resource response
data class AddResourceResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null
)

// Data class for update quantity request
data class UpdateQuantityRequest(
    val resource_id: Int,
    val quantity: Int
)

// Data class for update quantity response
data class UpdateQuantityResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null
)

// Retrofit API interface
interface ResourceApi {
    @GET("get_classroom_resources.php")
    fun getClassroomResources(@Query("classroom_id") classroomId: Int): Call<ResourceListResponse>

    @POST("add_resource.php")
    fun addResource(@Body request: AddResourceRequest): Call<AddResourceResponse>

    @POST("update_resource_quantity.php")
    fun updateResourceQuantity(@Body request: UpdateQuantityRequest): Call<UpdateQuantityResponse>
}

// RecyclerView Adapter for resources
class ResourceAdapter(
    private val resources: MutableList<Resource>,
    private val onUpdateQuantityClick: (Int, Int) -> Unit
) : RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder>() {

    class ResourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvResourceName: TextView = itemView.findViewById(R.id.tvResourceName)
        val etQuantity: EditText = itemView.findViewById(R.id.etQuantity)
        val tvAvailability: TextView = itemView.findViewById(R.id.tvAvailability)
        val btnSaveQuantity: Button = itemView.findViewById(R.id.btnSaveQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resource, parent, false)
        return ResourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        val resource = resources[position]
        holder.tvResourceName.text = "Resource: ${resource.resource_name}"
        holder.etQuantity.setText(resource.quantity.toString())
        holder.tvAvailability.text = "Availability: ${resource.availability}"
        holder.btnSaveQuantity.setOnClickListener {
            val quantityStr = holder.etQuantity.text.toString()
            if (quantityStr.isNotEmpty() && quantityStr.toIntOrNull() != null && quantityStr.toInt() > 0) {
                onUpdateQuantityClick(resource.id, quantityStr.toInt())
            } else {
                holder.itemView.context.getString(R.string.invalid_quantity).let {
                    Toast.makeText(holder.itemView.context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = resources.size

    fun updateResources(newResources: List<Resource>) {
        resources.clear()
        resources.addAll(newResources)
        notifyDataSetChanged()
    }
}

class ClassroomResourcesActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var rvResources: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoResources: TextView
    private lateinit var etResourceName: EditText
    private lateinit var etQuantity: EditText
    private lateinit var cbAvailability: CheckBox
    private lateinit var btnAddResource: Button
    private lateinit var adapter: ResourceAdapter
    private val resources = mutableListOf<Resource>()
    private var classroomId: Int = 0
    private lateinit var roomNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classroom_resources)

        // Get classroom details from intent
        classroomId = intent.getIntExtra("classroom_id", 0)
        roomNumber = intent.getStringExtra("room_number") ?: "Unknown"

        // Initialize views
        tvTitle = findViewById(R.id.tvTitle)
        rvResources = findViewById(R.id.rvResources)
        progressBar = findViewById(R.id.progressBar)
        tvNoResources = findViewById(R.id.tvNoResources)
        etResourceName = findViewById(R.id.etResourceName)
        etQuantity = findViewById(R.id.etQuantity)
        cbAvailability = findViewById(R.id.cbAvailability)
        btnAddResource = findViewById(R.id.btnAddResource)

        // Set title
        tvTitle.text = "Resources for Room $roomNumber"

        // Set up RecyclerView
        adapter = ResourceAdapter(resources) { resourceId, newQuantity ->
            updateResourceQuantity(resourceId, newQuantity)
        }
        rvResources.layoutManager = LinearLayoutManager(this)
        rvResources.adapter = adapter

        // Add resource button click
        btnAddResource.setOnClickListener {
            addResource()
        }

        // Fetch resources
        fetchResources()
    }

    private fun fetchResources() {
        progressBar.visibility = View.VISIBLE
        tvNoResources.visibility = View.GONE
        rvResources.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL) // Replace with your XAMPP server URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ResourceApi::class.java)
        api.getClassroomResources(classroomId).enqueue(object : Callback<ResourceListResponse> {
            override fun onResponse(call: Call<ResourceListResponse>, response: Response<ResourceListResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val resourceResponse = response.body()
                    if (resourceResponse?.success == true && resourceResponse.resources != null) {
                        if (resourceResponse.resources.isEmpty()) {
                            tvNoResources.visibility = View.VISIBLE
                            rvResources.visibility = View.GONE
                        } else {
                            tvNoResources.visibility = View.GONE
                            rvResources.visibility = View.VISIBLE
                            adapter.updateResources(resourceResponse.resources)
                        }
                    } else {
                        Toast.makeText(this@ClassroomResourcesActivity, resourceResponse?.error ?: "Error fetching resources", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@ClassroomResourcesActivity, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResourceListResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ClassroomResourcesActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun addResource() {
        val resourceName = etResourceName.text.toString().trim()
        val quantityStr = etQuantity.text.toString().trim()
        val availability = if (cbAvailability.isChecked) "Available" else "Unavailable"

        // Validate inputs
        if (resourceName.isEmpty()) {
            Toast.makeText(this, "Please enter a resource name", Toast.LENGTH_SHORT).show()
            return
        }
        if (quantityStr.isEmpty() || quantityStr.toIntOrNull() == null || quantityStr.toInt() <= 0) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
            return
        }

        val quantity = quantityStr.toInt()
        val request = AddResourceRequest(classroomId, resourceName, quantity, availability)

        progressBar.visibility = View.VISIBLE

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL) // Replace with your XAMPP server URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ResourceApi::class.java)
        api.addResource(request).enqueue(object : Callback<AddResourceResponse> {
            override fun onResponse(call: Call<AddResourceResponse>, response: Response<AddResourceResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val addResponse = response.body()
                    if (addResponse?.success == true) {
                        Toast.makeText(this@ClassroomResourcesActivity, addResponse.message ?: "Resource added", Toast.LENGTH_LONG).show()
                        etResourceName.text.clear()
                        etQuantity.text.clear()
                        cbAvailability.isChecked = true
                        fetchResources() // Refresh the list
                    } else {
                        Toast.makeText(this@ClassroomResourcesActivity, addResponse?.error ?: "Error adding resource", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@ClassroomResourcesActivity, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AddResourceResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ClassroomResourcesActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateResourceQuantity(resourceId: Int, newQuantity: Int) {
        val request = UpdateQuantityRequest(resourceId, newQuantity)
        progressBar.visibility = View.VISIBLE

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL) // Replace with your XAMPP server URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ResourceApi::class.java)
        api.updateResourceQuantity(request).enqueue(object : Callback<UpdateQuantityResponse> {
            override fun onResponse(call: Call<UpdateQuantityResponse>, response: Response<UpdateQuantityResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val updateResponse = response.body()
                    if (updateResponse?.success == true) {
                        Toast.makeText(this@ClassroomResourcesActivity, updateResponse.message ?: "Quantity updated", Toast.LENGTH_LONG).show()
                        fetchResources() // Refresh the list
                    } else {
                        Toast.makeText(this@ClassroomResourcesActivity, updateResponse?.error ?: "Error updating quantity", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@ClassroomResourcesActivity, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UpdateQuantityResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ClassroomResourcesActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}

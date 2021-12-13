package com.github.ai.fprovider.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.ai.fprovider.demo.databinding.ActivityMainBinding
import com.github.ai.fprovider.demo.databinding.ListItemBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "soft-input-lifecycle"
        }

        binding.recyclerView.adapter = ItemAdapter(
            items = createAdapterItems()
        )
    }

    private fun createAdapterItems(): List<Item> {
        return listOf(
            Item(
                title = "Show keyboard in new Activity",
                description = "Simple example that shows how to show keyboard in new Activity " +
                    "without any *magical* delays",
                example = Example.SIMPLE_WITH_LIFECYCLE
            ),
            Item(
                title = "Show keyboard without SoftInputLifecycleOwner",
                description = "Keyboard won't be shown, despite the fact that imm.showSoftInput " +
                    "was called",
                example = Example.SIMPLE_WITHOUT_LIFECYCLE
            )
        )
    }
}

class ItemAdapter(
    private val items: List<Item>
) : RecyclerView.Adapter<ItemViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class ItemViewHolder(
    private val binding: ListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Item) {
        binding.title.text = item.title
        binding.description.text = item.description

        binding.root.setOnClickListener {
            val context = binding.root.context
            context.startActivity(
                ExampleActivity.newLaunchIntent(context, item.example)
            )
        }
    }
}

data class Item(
    val title: String,
    val description: String,
    val example: Example
)



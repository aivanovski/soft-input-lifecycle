package com.github.ai.fprovider.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.ai.fprovider.demo.examples.NoSoftInputLifecycleExampleFragment
import com.github.ai.fprovider.demo.examples.SimpleExampleFragment

class ExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        val example = intent.getSerializableExtra(EXTRA_EXAMPLE) as Example

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, getFragmentByExample(example))
            .commit()
    }

    private fun getFragmentByExample(example: Example): Fragment {
        return when (example) {
            Example.SIMPLE_WITH_LIFECYCLE -> SimpleExampleFragment()
            Example.SIMPLE_WITHOUT_LIFECYCLE -> NoSoftInputLifecycleExampleFragment()
        }
    }

    companion object {

        private const val EXTRA_EXAMPLE = "example"

        fun newLaunchIntent(context: Context, example: Example): Intent =
            Intent(context, ExampleActivity::class.java)
                .apply {
                    putExtra(EXTRA_EXAMPLE, example)
                }
    }
}
package com.example.totalrecall

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.totalrecall.data.AppDatabase
import com.example.totalrecall.data.ResourceRepository
import com.example.totalrecall.data.ResourceTagRel
import com.example.totalrecall.data.Tag
import com.example.totalrecall.databinding.FragmentResourceDetailBinding
import com.example.totalrecall.databinding.FragmentSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var resourceRepository: ResourceRepository
    private val tags = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view: View = binding.root

        val navController = findNavController()

        resourceRepository = ResourceRepository(AppDatabase.getInstance(requireContext()).resourceDao())
        CoroutineScope(Dispatchers.IO).launch {
            setAutoCompleteTextViewAdapter(binding.autocompleteTextViewSearch, resourceRepository.getAllTagNames())
        }

        binding.addTagToSearchButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val tag: Tag? =
                        resourceRepository.getTagByName(binding.autocompleteTextViewSearch.text.toString())
                    when {
                        (tag == null) -> showToast("Tag does not exist")
                        (tags.contains(tag.tagId)) -> showToast("Tag already included")
                        else -> {
                            addViewToFlow(tag)
                            tags.add(tag.tagId)
                        }
                    }
                } catch(ex: Exception) {
                    showToast("Error: " + ex.message)
                }
            }
        }

        binding.confirmSearch.setOnClickListener {
            navController.previousBackStackEntry?.savedStateHandle?.set("TAGS", tags.toIntArray())
            findNavController().popBackStack()
        }

        return view
    }

    private fun setAutoCompleteTextViewAdapter(autoCompleteTextView: AutoCompleteTextView, tags: List<String>) {
        this.activity?.runOnUiThread {
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(),
                android.R.layout.select_dialog_item, tags)
            autoCompleteTextView.threshold = 2
            autoCompleteTextView.setAdapter(adapter)
        }
    }

    private fun addViewToFlow(tag: Tag) {
        var textView: TextView
        var d: Drawable?
        val bgColor = Color.parseColor(tag.color)
        this.activity?.runOnUiThread {
            textView = TextView(context)
            textView.text = tag.name
            textView.id = ViewCompat.generateViewId()
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            d = AppCompatResources.getDrawable(requireContext(), R.drawable.tag_background)
            d?.colorFilter = PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_ATOP)
            textView.background = d
            textView.setTextColor(
                if (Color.luminance(bgColor) > .5) {
                    ContextCompat.getColor(requireContext(), R.color.black)
                }
                else {
                    ContextCompat.getColor(requireContext(), R.color.white)
                }
            )
            textView.background = d
            textView.setPadding(12, 2, 12, 2)
            binding.root.addView(textView)
            binding.tagFlowSearch.addView(textView)
        }

    }

    private fun showToast(toast: String?, length: Int = Toast.LENGTH_SHORT) {
        this.activity?.runOnUiThread {
            Toast.makeText(requireContext(), toast, length).show()
        }
    }
}
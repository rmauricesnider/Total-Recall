package com.example.totalrecall

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.ViewCompat.generateViewId
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.totalrecall.data.*
import com.example.totalrecall.databinding.FragmentResourceDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "RES_ID"

class ResourceDetailFragment : Fragment() {
    private var resourceIdParam: Int? = null
    private var _binding: FragmentResourceDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var resource: Resource
    private var resourceRepository: ResourceRepository? = null
    private var confirmDeleteResource: Int = 0
    private lateinit var contributors: List<Contributor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_PARAM1)) {
                resourceIdParam = it.getInt(ARG_PARAM1)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResourceDetailBinding.inflate(inflater, container, false)
        val view: View = binding.root

        setHasOptionsMenu(true)

        resourceRepository = ResourceRepository(AppDatabase.getInstance(requireContext()).resourceDao())
        if (resourceIdParam != null) {
            CoroutineScope(Dispatchers.IO).launch {
                resource = resourceRepository!!.getResource(resourceIdParam!!)
                contributors = resourceRepository!!.getContributorsForResource(resourceIdParam!!)
                binding.titleText.text = resource.title
                binding.linkText.text = resource.link
                binding.linkText.movementMethod = LinkMovementMethod.getInstance()
                binding.descriptionText.text = resource.description
                addViewsToFlow(resourceRepository!!.getTagsFromResourceId(resource.resourceId))
                addRowsToTable(contributors)
                setAutoCompleteTextViewAdapter(binding.autocompleteTextView, resourceRepository!!.getAllTagNames())
            }
        }

        binding.addTagToResourceButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val tag: Tag? =
                        resourceRepository!!.getTagByName(binding.autocompleteTextView.text.toString())
                    if (tag == null) {
                        showToast("Tag does not exist")
                    } else {
                        val resourceTagRel = ResourceTagRel(resource.resourceId, tag.tagId)
                        resourceRepository!!.addResourceTagRel(resourceTagRel)
                        binding.autocompleteTextView.text.clear()
                        addViewsToFlow(listOf(tag))
                    }
                } catch(ex:Exception) {
                    showToast(ex.message)
                }
            }
        }
        return view
    }

    private fun showToast(toast: String?, length: Int = Toast.LENGTH_SHORT) {
        this.activity?.runOnUiThread {
            Toast.makeText(requireContext(), toast, length).show()
        }
    }

    private fun addViewsToFlow(tags: List<Tag>) {
        val onLongClickLister = View.OnLongClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                it as TextView
                resourceRepository?.removeResourceTagRelation(resource.resourceId, it.tag as Int)
                showToast("Removed ${it.text}")
                removeViewFromTagFlow(it)
            }
            true
        }
        val onClickLister = View.OnClickListener {
            showToast("Hold down to delete")
        }

        var textView: TextView
        var d: Drawable?
        var bgColor: Int
        this.activity?.runOnUiThread {
            for (tag in tags) {
                bgColor = Color.parseColor(tag.color)
                textView = TextView(context)
                textView.text = tag.name
                textView.id = generateViewId()
                textView.tag = tag.tagId
                d = AppCompatResources.getDrawable(requireContext(), R.drawable.tag_background)
                d?.colorFilter = PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_ATOP)
                textView.background = d
                textView.setTextColor(
                    if (Color.luminance(bgColor) > .5) { getColor(requireContext(), R.color.black) }
                    else { getColor(requireContext(), R.color.white) }
                    )
                textView.setPadding(20, 6, 20, 6)
                textView.setOnClickListener(onClickLister)
                textView.setOnLongClickListener(onLongClickLister)
                binding.root.addView(textView)
                binding.tagFlow.addView(textView)
            }
        }
    }

    private fun addRowsToTable(contributors: List<Contributor>) {
        lateinit var contributorView: View
        for(contributor in contributors) {
            contributorView = View.inflate(requireContext(), R.layout.contributor_line, null)
            contributorView.findViewById<TextView>(R.id.contributor_name).text = contributor.name
            contributorView.findViewById<TextView>(R.id.contributor_type).text = contributor.contribution
            binding.contributorsTable.addView(contributorView)
        }
    }

    private fun removeViewFromTagFlow(view: View) {
        view as TextView
        this.activity?.runOnUiThread {
            binding.root.removeView(view)
            binding.tagFlow.removeView(view)
        }
    }

    private fun setAutoCompleteTextViewAdapter(autoCompleteTextView: AutoCompleteTextView, tags: List<String>) {
        this.activity?.runOnUiThread {
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(),
                android.R.layout.select_dialog_item, tags)
            autoCompleteTextView.threshold = 2
            autoCompleteTextView.setAdapter(adapter)
        }
    }

    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId ){
        R.id.delete_menu_button -> {
            if(confirmDeleteResource == 0) {
                confirmDeleteResource++
                showToast("Tap again to confirm delete resource")
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    resourceRepository!!.deleteResource(resource)
                }
                findNavController().previousBackStackEntry?.savedStateHandle?.set("DELETED", resource.resourceId)
                findNavController().popBackStack()
            }
            true
        }
        R.id.edit_menu_button -> {
            val bundle = Bundle()
            bundle.putInt("RES_ID", resource.resourceId)
            findNavController().navigate(R.id.detail_to_add, bundle)
            super.onOptionsItemSelected(item)
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }
}

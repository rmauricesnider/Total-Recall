package com.example.totalrecall

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.example.totalrecall.data.*
import com.example.totalrecall.databinding.FragmentAddResourceBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.min

private const val RESOURCE_ID = "RES_ID"

class AddResourceFragment : Fragment() {
    private var existingResourceID: Int? = null

    private var _binding: FragmentAddResourceBinding? = null
    private val binding get() = _binding!!

    private lateinit var resource: Resource
    private lateinit var contributors: List<Contributor>
    private var shareLink: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if(it.containsKey(RESOURCE_ID)) {
                existingResourceID = it.getInt(RESOURCE_ID)
            }
            //if(it.containsKey(SHARE_LINK)) {
            //    newResourceLink = it.getString(SHARE_LINK)
            //}
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddResourceBinding.inflate(inflater, container, false)
        val view: View = binding.root
        val resourceDao: ResourceDAO = AppDatabase.getInstance(requireContext()).resourceDao()
        val resourceRepository = ResourceRepository(resourceDao)
        val isUpdate = (existingResourceID != null)

        val intent = activity?.intent
        if(intent?.action == Intent.ACTION_SEND) {
            shareLink = intent.getStringExtra(Intent.EXTRA_TEXT)
            intent.removeExtra(Intent.EXTRA_TEXT)
        }

        ArrayAdapter.createFromResource(requireContext(), R.array.spinner_list, android.R.layout.simple_spinner_item)
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.typeField.adapter = adapter
                }
        if(!shareLink.isNullOrEmpty()) {
            binding.linkField.setText(shareLink)
            binding.titleField.setText("")
            binding.descriptionField.setText("")
            binding.typeField.setSelection(0)
        } else if (isUpdate) {
            CoroutineScope(Dispatchers.IO).launch {
                resource = resourceRepository.getResource(existingResourceID!!)
                contributors = resourceRepository.getContributorsForResource(resource.resourceId)
                fillUpdateFields(resource, contributors)
            }
        }

        binding.commitAdd.setOnClickListener {
            val contributors = mutableListOf<Contributor>()
            val tableChildren = binding.editContributorsTable
            lateinit var name: String
            lateinit var contribution: String
            lateinit var childRow: TableRow
            val title: String = binding.titleField.text.toString()
            val link: String = binding.linkField.text.toString()
            val type: ResourceType = ResourceType.values()[binding.typeField.selectedItemPosition] //could not find better way to do this :/
            val description: String = binding.descriptionField.text.toString()

            var emptyContributorField = false

            var i = 1
            while(i < tableChildren.childCount) {
                childRow = tableChildren.getChildAt(i) as TableRow
                name = (childRow[0] as EditText).text.toString()
                contribution = (childRow[1] as EditText).text.toString()
                if(name.isEmpty() || contribution.isEmpty()) {
                    emptyContributorField = true
                    break
                }
                contributors.add(Contributor(name, contribution, 0, i - 1))
                i++
            }

            if(title.isEmpty() || link.isEmpty() || description.isEmpty() || emptyContributorField) {
                showToast("Can't have empty fields")
            } else {
                val newResource = Resource(title = title, link = link,
                    dateAdded = Date().toString(), type = type, description = description)

                CoroutineScope(Dispatchers.IO).launch {
                    if (!isUpdate) {
                        val id = resourceRepository.addResource(newResource).toInt()
                        for (contributor in contributors) {
                            contributor.resourceId = id
                            resourceRepository.addContributor(contributor)
                        }
                    } else {
                        newResource.resourceId = resource.resourceId
                        resourceRepository.updateResource(newResource)

                        val numOldContributors =
                            resourceRepository.getContributorCountByResource(resource.resourceId)
                        val numNewContributors = contributors.size

                        for (contributor in contributors) {
                            contributor.resourceId = resource.resourceId
                        }

                        for (x in numNewContributors until numOldContributors) { //delete contributors if new list is shorter
                            resourceRepository.deleteContributor(resource.resourceId, x)
                        }

                        for (x in 0 until min(numOldContributors,
                            numNewContributors)) { //update contributors
                            resourceRepository.updateContributor(contributors[x])
                        }

                        for (x in numOldContributors until numNewContributors) { //add contributors
                            resourceRepository.addContributor(contributors[x])
                        }

                    }
                }

                if (!isUpdate) {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("ADDED",
                        newResource.resourceId)
                }
                findNavController().popBackStack()
            }
        }

        binding.addContributorButton.setOnClickListener { addContributorRow() }

        return view
    }

    private fun addContributorRow(contributor: Contributor? = null) {
        val contributorView: View = View.inflate(requireContext(), R.layout.add_contributor_line, null)
        contributorView.findViewById<ImageButton>(R.id.remove_contributor_button).setOnClickListener {
            binding.editContributorsTable.removeView(it.parent as View)
        }

        if(contributor != null) {
            contributorView.findViewById<EditText>(R.id.contributor_name_field).setText(contributor.name)
            contributorView.findViewById<EditText>(R.id.contributor_type_field).setText(contributor.contribution)
        }

        binding.editContributorsTable.addView(contributorView)
    }

    private fun fillUpdateFields(resource: Resource, contributors: List<Contributor>) {
        binding.commitAdd.text = "Update Resource"
        activity?.findViewById<Toolbar>(R.id.toolbar)?.title = "Update"
        activity?.runOnUiThread {
            binding.titleField.setText(resource.title)
            binding.linkField.setText(resource.link)
            binding.descriptionField.setText(resource.description)
            binding.typeField.setSelection(resource.type.ordinal)

            for(contributor in contributors) {
                addContributorRow(contributor)
            }
        }
    }

    private fun showToast(toast: String?, length: Int = Toast.LENGTH_SHORT) {
        this.activity?.runOnUiThread {
            Toast.makeText(requireContext(), toast, length).show()
        }
    }
}
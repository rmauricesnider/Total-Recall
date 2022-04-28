package com.example.totalrecall

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.example.totalrecall.data.*
import com.example.totalrecall.databinding.FragmentAddResourceBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private const val RESOURCE_ID = "RES_ID"
private const val SHARE_LINK = "SHARE_LINK"

class AddResourceFragment : Fragment() {
    private var existingResourceID: Int? = null

    private var _binding: FragmentAddResourceBinding? = null
    private val binding get() = _binding!!

    private lateinit var resource: Resource
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

        val intent = activity?.intent
        if(intent?.action == Intent.ACTION_SEND) {
            //val bundle = Bundle()
            shareLink = intent.getStringExtra(Intent.EXTRA_TEXT)
        }

        ArrayAdapter.createFromResource(requireContext(), R.array.spinner_list, android.R.layout.simple_spinner_item)
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.typeField.adapter = adapter
                }
        if(!shareLink.isNullOrEmpty()) {
            binding.linkField.setText(shareLink)

            binding.titleField.setText("")
            binding.authorField.setText("")
            binding.descriptionField.setText("")
            binding.typeField.setSelection(0)
        } else if (existingResourceID != null ) {
            CoroutineScope(Dispatchers.IO).launch {
                resource = resourceRepository.getResource(existingResourceID!!)
                binding.titleField.setText(resource.title)
                binding.authorField.setText(resource.author)
                binding.linkField.setText(resource.link)
                binding.publisherField.setText(resource.publisher)
                binding.descriptionField.setText(resource.description)
                binding.typeField.setSelection(resource.type.ordinal)
                binding.commitAdd.text = "Update Resource"

                activity?.findViewById<Toolbar>(R.id.toolbar)?.title = "Update"
            }
        }

        binding.commitAdd.setOnClickListener {
            val newResource = Resource(title = binding.titleField.text.toString(),
                    author = binding.authorField.text.toString(),
                    publisher = null,
                    link = binding.linkField.text.toString(),
                    dateAdded = Date().toString(),
                    type = ResourceType.values()[binding.typeField.selectedItemPosition], //could not find better way to do this :/
                    description = binding.descriptionField.text.toString())

            CoroutineScope(Dispatchers.IO).launch {
                if (existingResourceID == null) {
                    resourceRepository.addResource(newResource)
                } else {
                    newResource.resourceId = resource.resourceId
                    resourceRepository.updateResource(newResource)
                }
            }

            if(existingResourceID == null) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("ADDED",
                    newResource.resourceId)
            }
            findNavController().popBackStack()
        }

        return view
    }
}
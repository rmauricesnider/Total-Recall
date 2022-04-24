package com.example.totalrecall

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.example.totalrecall.data.*
import com.example.totalrecall.databinding.FragmentAddResourceBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private const val ARG_PARAM1 = "RES_ID"

class AddResourceFragment : Fragment() {
    private var param1: Int? = null

    private var _binding: FragmentAddResourceBinding? = null
    private val binding get() = _binding!!

    private lateinit var resource: Resource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
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

        ArrayAdapter.createFromResource(requireContext(), R.array.spinner_list, android.R.layout.simple_spinner_item)
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.typeField.adapter = adapter
                }

        if (param1 != null) {
            CoroutineScope(Dispatchers.IO).launch {
                resource = resourceRepository.getResource(param1!!)
                binding.titleField.setText(resource.title)
                binding.authorField.setText(resource.author)
                binding.linkField.setText(resource.link)
                //binding.publisherField.text = resource.publisher
                binding.descriptionField.setText(resource.description)
                binding.typeField.setSelection(resource.type.ordinal)
                binding.commitAdd.text = "Update Resource"
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
                if (param1 == null) {
                    resourceRepository.addResource(newResource)
                } else {
                    newResource.resourceId = resource.resourceId
                    resourceRepository.updateResource(newResource)
                }
            }

            if(param1 == null) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("ADDED",
                    newResource.resourceId)
            }
            findNavController().popBackStack()
        }

        return view
    }
}
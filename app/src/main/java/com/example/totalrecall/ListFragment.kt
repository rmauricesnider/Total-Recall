package com.example.totalrecall

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.example.totalrecall.data.AppDatabase
import com.example.totalrecall.data.Resource
import com.example.totalrecall.data.ResourceRepository
import com.example.totalrecall.data.Tag
import com.example.totalrecall.databinding.FragmentItemListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ListFragment: Fragment() {

    //private var param1: List<Int>? = null
    private var _binding: FragmentItemListBinding ?= null
    private val binding get() = _binding!!
    private var tagIds = intArrayOf()
    private lateinit var resourceRepository: ResourceRepository
    private val hexChars: CharArray = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'A', 'B', 'C', 'D', 'E', 'F')
    private lateinit var recyclerAdapter: RecyclerAdapter

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        val view = binding.root
        val layoutManager = LinearLayoutManager(context)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = layoutManager


        setHasOptionsMenu(true)

        resourceRepository = ResourceRepository(AppDatabase.getInstance(requireContext()).resourceDao())

        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<IntArray>("TAGS")
            ?.observe(viewLifecycleOwner) { result ->
                CoroutineScope(Dispatchers.IO).launch {
                    tagIds = result
                    recyclerAdapter.setList(resourceRepository.getResourcesByTags(tagIds))
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("ADDED")
                ?.observe(viewLifecycleOwner) { result ->
            CoroutineScope(Dispatchers.IO).launch {
                var resource: Resource
                do {
                    resource = resourceRepository.getResource(result)
                } while (resource == null)

                val i = recyclerAdapter.addToList(resource)
                recyclerAdapter.notifyItemInserted(i)
            }
        }
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("DELETED")
            ?.observe(viewLifecycleOwner) { result ->
                CoroutineScope(Dispatchers.IO).launch {
                    val i = recyclerAdapter.removeFromList(result)
                    recyclerAdapter.notifyItemRemoved(i)
                    showToast("Deleted resource")
                }
            }

        CoroutineScope(Dispatchers.IO).launch {
            val mutableList = resourceRepository.getAllResources().toMutableList()
            recyclerAdapter = RecyclerAdapter(mutableList, resourceRepository)
            recyclerAdapter.setOnClickListener {
                val num: Int = it.tag as Int
                if (container != null) {
                    val bundle = Bundle()
                    bundle.putInt("RES_ID", num)
                    Navigation.findNavController(it)
                        .navigate(R.id.resource_detail_fragment, bundle)
                }
            }
            recyclerView.adapter = recyclerAdapter
        }



        binding.tagButton.setOnClickListener {
            val text: String = binding.tagTextView.text.toString().trim()
            binding.tagTextView.text.clear()
            if (text.length < 2) {
                showToast("Tag must be at least 2 characters long")
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    if (resourceRepository.getTagByName(text) == null) {
                        val rnd = Random()
                        var color = "#"
                        while(color.length < 7) {
                            color = color.plus(hexChars[rnd.nextInt(hexChars.size)])
                        }
                        val tag = Tag(name = text, color = color)
                        resourceRepository.addTag(tag)
                        showToast("Added tag $text")
                    } else {
                        showToast("Tag already exists")
                    }
                }
            }
        }

        return view
    }

    private fun clearTags() {
        CoroutineScope(Dispatchers.IO).launch {
            resourceRepository.clearUnboundTags()
        }
    }

    private fun showToast(toast: String?) {
        this.activity?.runOnUiThread {
            Toast.makeText(requireContext(),
                toast,
                Toast.LENGTH_SHORT).show()
        }
    }

    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.add_item_bar_action -> {
            findNavController().navigate(R.id.list_to_add)
            true
        }

        R.id.search_item_bar_action -> {
            findNavController().navigate(R.id.list_to_search)
            true
        }

        R.id.clear_unbound_tags_bar_action -> {
            clearTags()
            showToast("Deleted unbound tags")
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

}
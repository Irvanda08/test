package com.example.test.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.data.response.Animal
import com.example.test.ui.adapter.ListHistoryAdapter

class DashboardFragment : Fragment() {

    private lateinit var rvAnimal: RecyclerView
    private val list = ArrayList<Animal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        rvAnimal = view.findViewById(R.id.rv_animal)
        rvAnimal.setHasFixedSize(true)

        list.addAll(getListAnimal())
        showRecyclerList()

        return view
    }

    private fun getListAnimal(): ArrayList<Animal> {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val listAnimal = ArrayList<Animal>()
        for (i in dataName.indices) {
            val animal = Animal(dataName[i], dataDescription[i], dataPhoto.getResourceId(i, -1))
            listAnimal.add(animal)
        }
        return listAnimal
    }

    private fun showRecyclerList() {
        rvAnimal.layoutManager = LinearLayoutManager(context)
        val listAnimalAdapter = ListHistoryAdapter(list)
        rvAnimal.adapter = listAnimalAdapter
    }
}

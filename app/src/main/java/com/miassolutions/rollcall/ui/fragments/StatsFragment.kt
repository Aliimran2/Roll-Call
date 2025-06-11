package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.Stats
import com.miassolutions.rollcall.databinding.FragmentAttendanceBinding
import com.miassolutions.rollcall.databinding.FragmentStatsBinding
import com.miassolutions.rollcall.ui.adapters.StatsListAdapter
import com.miassolutions.rollcall.utils.showToast


class StatsFragment : Fragment(R.layout.fragment_stats) {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStatsBinding.bind(view)

        val statsList = List(30) {
            Stats(it + 1, "20-06-25", it + 1, it + 1, it + 1, it.toDouble())
        }

        val adapter = StatsListAdapter()
        adapter.submitList(statsList)

        binding.rvStats.adapter = adapter

        menuProvider()


    }

    private fun menuProvider() {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_attendance, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.action_add_att -> {
                            showToast("adding...")
                            true
                        }

                        R.id.sort_asc -> {
                            showToast("sorting asc...")
                            true
                        }

                        R.id.sort_desc -> {
                            showToast("sorting desc...")
                            true
                        }

                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
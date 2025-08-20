package com.miassolutions.rollcall.ui.dashboard

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentDashboardBinding
import com.miassolutions.rollcall.extenstions.addMenu
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.hide
import com.miassolutions.rollcall.extenstions.show
import com.miassolutions.rollcall.extenstions.showSnackbar
import com.miassolutions.rollcall.ui.settings.SettingsViewModel
import com.miassolutions.rollcall.utils.toFormattedDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DashboardViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        setupUi()
        dashboardUi()
        observeState()
        observeEvents()


    }

    private fun observeEvents() {
        collectLatestFlow {
            viewModel.uiEvent.collectLatest { event ->
                when(event){
                    DashboardUiEvent.NavigateToAttendance -> {
                        val action = DashboardFragmentDirections.actionDashboardFragmentToStatsFragment()
                        findNavController().navigate(action)
                    }
                    DashboardUiEvent.NavigateToSettings -> {
                        findNavController().navigate(
                            DashboardFragmentDirections.actionDashboardFragmentToSettingsFragment()
                        )
                    }
                    DashboardUiEvent.NavigateToStudents -> {
                        findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToStudentsFragment())
                    }
                    DashboardUiEvent.NavigateToUserProfile -> {
                        findNavController().navigate(
                            DashboardFragmentDirections.actionDashboardFragmentToUserProfileFragment()
                        )
                    }
                    DashboardUiEvent.ShowReportMessage -> {
                            showSnackbar("Generate Report")
                    }
                }
            }
        }
    }

    private fun observeState() {
        collectLatestFlow {
            viewModel.uiState.collectLatest { state ->
                binding.apply {
                    if (state.isAttendanceTaken) {
                        tvAttendanceWarn.hide()
                        infoCard.root.show()
                        state.counts?.let { bindAttendance(it) }
                    } else {
                        tvAttendanceWarn.show()
                        tvAttendanceWarn.text = getString(
                            R.string.date_attendance_for_today_is_not_taken_yet,
                            LocalDate.now().toFormattedDate("dd-MM-yyyy EE")
                        )
                        infoCard.root.hide()
                    }

                    state.userName?.let {
                        topLayout.tvTitle.text = getString(R.string.welcome, it)
                    }
                    state.instituteName?.let {
                        topLayout.tvSubtitle.text = it
                    }

                    state.profileImgUri?.let {url ->
                        Glide.with(requireContext())
                            .load(url)
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_error_image)
                            .into(topLayout.ivUserProfile)
                    }
                }

            }
        }
    }

    private fun dashboardUi() {
        binding.apply {
            attendanceCard.ivCard.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_attendances))
            attendanceCard.tvCard.text = ContextCompat.getString(requireContext(), R.string.attendance)
            studentsCard.ivCard.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_students_m))
            studentsCard.tvCard.text = ContextCompat.getString(requireContext(), R.string.students)
            userCard.ivCard.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_person))
            userCard.tvCard.text = ContextCompat.getString(requireContext(), R.string.set_user_name)
            reportCard.ivCard.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_bars))
            reportCard.tvCard.text = ContextCompat.getString(requireContext(), R.string.report)


        }
    }

    private fun bindAttendance(counts: AttendanceCounts) = binding.infoCard.apply {
        tvDate.text = LocalDate.now().toFormattedDate("dd-MM-yyyy EE")
        dbTotalCard.tvCount.text = counts.total
        dbTotalCard.tvCountTitle.text = getString(R.string.total)

        dbPresentCard.tvCount.text = counts.present
        dbPresentCard.tvCount.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.green_present)
        )
        dbPresentCard.tvCountTitle.text = getString(R.string.present)

        dbAbsentCard.tvCount.text = counts.absent
        dbAbsentCard.tvCount.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.red_absent)
        )
        dbAbsentCard.tvCountTitle.text = getString(R.string.absent)


    }

    private fun setupUi() = binding.apply {
        attendanceCard.root.setOnClickListener { viewModel.onAttendanceClicked() }
        reportCard.root.setOnClickListener { viewModel.onReportClicked() }
        userCard.root.setOnClickListener { viewModel.onUserClicked() }
        studentsCard.root.setOnClickListener { viewModel.onStudentsClicked() }

        addMenu(R.menu.menu_dashboard) { item ->
            if (item.itemId == R.id.settingsFragment) {
                viewModel.onSettingsClicked()
                true
            } else false
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
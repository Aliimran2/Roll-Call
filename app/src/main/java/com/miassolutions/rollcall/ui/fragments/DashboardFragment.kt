package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentDashboardBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.hide
import com.miassolutions.rollcall.extenstions.show
import com.miassolutions.rollcall.extenstions.showToast
import com.miassolutions.rollcall.extenstions.toFormattedDate
import com.miassolutions.rollcall.ui.model.AttendanceUIModel
import com.miassolutions.rollcall.ui.model.StatsUiModel
import com.miassolutions.rollcall.ui.viewmodels.DashboardViewModel
import com.miassolutions.rollcall.ui.viewmodels.SettingsViewModel
import com.miassolutions.rollcall.utils.toLocalDate
import com.miassolutions.rollcall.utils.toMillis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel by viewModels<SettingsViewModel>()
    private val dashboardViewModel by viewModels<DashboardViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

//        dashboardViewModel.setDate(setToDayDate())

        observeViewModel()




        binding.apply {


            attendanceCard.apply {
                ivCard.setImageResource(R.drawable.ic_attendances)
                tvCard.text = getString(R.string.attendance)
            }.root.setOnClickListener {

                val action =
                    DashboardFragmentDirections.actionDashboardFragmentToStatsFragment()
                findNavController().navigate(action)

            }

            settingsCard.apply {
                ivCard.setImageResource(R.drawable.ic_settings)
                tvCard.text = getString(R.string.settings)
            }.root.setOnClickListener {
                val action = DashboardFragmentDirections.actionDashboardFragmentToSettingsFragment()
                findNavController().navigate(action)

            }

            userCard.apply {
                ivCard.setImageResource(R.drawable.ic_person)
                tvCard.text = getString(R.string.profile)
            }.root.setOnClickListener {
                val action =
                    DashboardFragmentDirections.actionDashboardFragmentToUserProfileFragment()
                findNavController().navigate(action)
            }

            studentsCard.apply {
                ivCard.setImageResource(R.drawable.ic_students_m)
                tvCard.text = getString(R.string.students)
            }.root.setOnClickListener {
                val action = DashboardFragmentDirections.actionDashboardFragmentToStudentsFragment()
                findNavController().navigate(action)
            }
        }

    }

    private fun setToDayDate() : Long{
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND,0)
        }
        return calendar.time.time
    }

    private fun bindAttendance(item: DashboardViewModel.Counts) {
        binding.infoCard.apply {
//            tvDate.text = dashboardViewModel.selectedDate.value.toFormattedDate("dd.MM.yyyy EE")
            dbTotalCard.tvCount.text = item.total
            dbTotalCard.tvCountTitle.text = getString(R.string.total)
            dbPresentCard.tvCount.text = item.present
            dbPresentCard.tvCount.setTextColor(ContextCompat.getColor(requireContext(),R.color.green_present))
            dbPresentCard.tvCountTitle.text = getString(R.string.present)
            dbAbsentCard.tvCount.text = item.absent
            dbAbsentCard.tvCount.setTextColor(ContextCompat.getColor(requireContext(),R.color.red_absent))
            dbAbsentCard.tvCountTitle.text = getString(R.string.absent)
        }
    }

    private fun observeViewModel() {

        collectLatestFlow {
            dashboardViewModel.isAttendanceTaken.collectLatest { taken ->
                if (taken){
                    binding.tvAttendanceWarn.hide()
                    binding.infoCard.root.show()
                } else {
                    binding.tvAttendanceWarn.show()
                    binding.tvAttendanceWarn.text = getString(
                        R.string.date_attendance_for_today_is_not_taken_yet,
                        setToDayDate().toFormattedDate("dd.MM.yyyy EE")
                    )
                    binding.infoCard.root.hide()
                }
            }
        }


        collectLatestFlow {
            dashboardViewModel.attendanceCounts.collectLatest {
                bindAttendance(it)
            }

        }


        settingsViewModel.userName.observe(viewLifecycleOwner) {
            it?.let {
                binding.topLayout.tvTitle.text = getString(R.string.welcome, it)
            }
        }
        settingsViewModel.instituteName.observe(viewLifecycleOwner) {
            it?.let {
                binding.topLayout.tvSubtitle.text = it
            }
        }

        settingsViewModel.userProfileImage.observe(viewLifecycleOwner) {
            it?.let { userImage ->
                Glide.with(requireContext())
                    .load(userImage)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_error_image)
                    .into(binding.topLayout.ivUserProfile)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package com.miassolutions.rollcall.ui.fragments

import WeekendPastDateValidatorUtil
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.common.Constants.DUPLICATE_REG_NUMBER
import com.miassolutions.rollcall.common.Constants.DUPLICATE_ROLL_NUMBER
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.databinding.FragmentAddStudentBinding
import com.miassolutions.rollcall.extenstions.addMenu
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showLongToast
import com.miassolutions.rollcall.extenstions.showMaterialDatePicker
import com.miassolutions.rollcall.extenstions.showSnackbar
import com.miassolutions.rollcall.extenstions.showToast
import com.miassolutions.rollcall.extenstions.toFormattedDate
import com.miassolutions.rollcall.ui.MainActivity
import com.miassolutions.rollcall.ui.viewmodels.AddStudentViewModel
import com.miassolutions.rollcall.utils.BFormTextWatcher
import com.miassolutions.rollcall.utils.StudentImagePicker
import com.miassolutions.rollcall.utils.StudentInsertResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

@AndroidEntryPoint
class AddStudentFragment : Fragment(R.layout.fragment_add_student) {

    private lateinit var studentImagePicker: StudentImagePicker

    private val viewModel by viewModels<AddStudentViewModel>()

    private var _binding: FragmentAddStudentBinding? = null
    private val binding get() = _binding!!

    private var dob: Long = System.currentTimeMillis()
    private var doa: Long = System.currentTimeMillis()

    private val args by navArgs<AddStudentFragmentArgs>()

    private var currentStudent: StudentEntity? = null

    private var studentImageUriStr = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddStudentBinding.bind(view)

        studentImagePicker = StudentImagePicker(this){uri ->
            Glide.with(requireContext())
                .load(uri)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_error_image)
                .into(binding.ivStudentImage)
            //save in db uri.toString()
            studentImageUriStr = uri.toString()
        }

        binding.ivStudentImage.setOnClickListener {
            studentImagePicker.requestAndPickImage()
        }

        args.studentId?.let { viewModel.fetchStudentById(it) }

        setToolbarTitle()
        setupDatePickers()
        setupValidationListeners()
        observeViewModel()
        menuProvider()

        binding.etBForm.addTextChangedListener(BFormTextWatcher(binding.etBForm))


    }

    private fun isValidBForm(bForm: String): Boolean {
        val pattern = Regex("^\\d{5}-\\d{7}-\\d{1}$")
        return pattern.matches(bForm)
    }


    private fun prefillForm(student: StudentEntity) {
        currentStudent = student
        binding.apply {
            student.studentImage?.let {
                Glide.with(requireContext())
                    .load(it)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(binding.ivStudentImage)
            }

            etStudentName.setText(student.studentName)
            etFatherName.setText(student.fatherName)
            etRegNumber.setText(student.regNumber.toString())
            etRollNumber.setText(student.rollNumber.toString())
            etBForm.setText(student.bForm)
            etDOB.setText(student.dob.toFormattedDate())
            etDOA.setText(
                student.doa?.toFormattedDate() ?: System.currentTimeMillis().toFormattedDate()
            )
            etPhone.setText(student.phoneNumber)
            etAddress.setText(student.address)

            dob = student.dob
            doa = student.doa ?: System.currentTimeMillis()
        }
    }

    private fun setupValidationListeners() {
        binding.apply {
            etRegNumber.doAfterTextChanged { etRegNumber.error = null }
            etRollNumber.doAfterTextChanged { etRollNumber.error = null }
            etStudentName.doAfterTextChanged { etStudentName.error = null }
            etFatherName.doAfterTextChanged { etFatherName.error = null }
            etDOB.doAfterTextChanged { etDOB.error = null }
        }
    }

    private fun showDatePicker(
        selection: Long? = null,
        inputMode: Int,
        onDateSelected: (Long) -> Unit,
    ) {

        val validator = WeekendPastDateValidatorUtil()
        validator.isWeekendDisabled = false

        val constraintsBuilder = CalendarConstraints.Builder()
            .setFirstDayOfWeek(Calendar.MONDAY)
            .setValidator(validator)

        showMaterialDatePicker(
            title = "Select date",
            inputMode = inputMode,
            selection = selection,
            constraints = constraintsBuilder.build(),
            onDateSelected = { onDateSelected(it) }
        )
    }

    private fun setupDatePickers() {
        binding.etDOB.setOnClickListener {

            showDatePicker(null, MaterialDatePicker.INPUT_MODE_TEXT) {
                binding.etDOB.setText(it.toFormattedDate())
                dob = it
            }
        }
        binding.etDOA.setOnClickListener {
            val today = MaterialDatePicker.todayInUtcMilliseconds()
            showDatePicker(today, MaterialDatePicker.INPUT_MODE_CALENDAR) {
                binding.etDOA.setText(it.toFormattedDate())
                doa = it
            }
        }

    }

    private fun setToolbarTitle() {
        val toolbar =
            (requireActivity() as MainActivity).findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = if (args.studentId == null) "Add New Student" else "Update Student"
    }

    private fun menuProvider() {
        addMenu(R.menu.menu_add_student) { item ->
            when (item.itemId) {
                R.id.action_save -> {
                    val bForm = binding.etBForm.text.toString()
                    if (isValidBForm(bForm) || bForm.isBlank()) {
                        saveStudent()
                    } else {
                        binding.tilBForm.error = "Invalid B-Form format"
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun observeViewModel() {
        collectLatestFlow {
            launch {
                viewModel.studentToEdit.collectLatest { student ->
                    student?.let { prefillForm(it) }
                }
            }

            launch {
                viewModel.toastMessage.collect { result ->
                    when (result) {
                        is StudentInsertResult.Failure -> {
                            when (result.reason) {
                                DUPLICATE_REG_NUMBER -> {
                                    binding.etRegNumber.requestFocus()
                                    binding.etRegNumber.error = "Reg no already exists"
                                }

                                DUPLICATE_ROLL_NUMBER -> {
                                    binding.etRollNumber.requestFocus()
                                    binding.etRollNumber.error = "Roll no already exists"
                                }

                                else -> showLongToast("Failed: ${result.reason}")
                            }
                        }

                        is StudentInsertResult.Success -> {

                            showToast(
                                if (currentStudent == null) "Student added!" else "Student updated!"
                            )
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    private fun saveStudent() = with(binding) {

        val regNumberStr = etRegNumber.text.toString()
        val rollNumberStr = etRollNumber.text.toString()
        val studentName = etStudentName.text.toString()
        val fatherName = etFatherName.text.toString()
        val dobStr = etDOB.text.toString()
        val phoneNumber = etPhone.text.toString()
        val bForm = etBForm.text.toString()
        val klass = "8th B" //todo
        val address = etAddress.text.toString()

        when {
            regNumberStr.isBlank() -> {
                etRegNumber.requestFocus()
                etRegNumber.error = "Enter reg. number"
                return
            }

            rollNumberStr.isBlank() -> {
                etRollNumber.requestFocus()
                etRollNumber.error = "Enter roll number"
                return
            }

            studentName.isBlank() -> {
                etStudentName.requestFocus()
                etStudentName.error = "Enter student name"
                return
            }

            fatherName.isBlank() -> {
                etFatherName.requestFocus()
                etFatherName.error = "Enter father's name"
                return
            }

            dobStr.isBlank() -> {
                etDOB.requestFocus()
                etDOB.error = "Enter date of birth"
                return
            }
        }

        val reg = regNumberStr.toIntOrNull()
        val roll = rollNumberStr.toIntOrNull()

        if (reg == null || roll == null) {
            showLongToast("Invalid Registration or Roll Number")
            return
        }


        val student = StudentEntity(
            studentImage = studentImageUriStr,
            studentId = currentStudent?.studentId ?: UUID.randomUUID().toString(),
            regNumber = reg,
            rollNumber = roll,
            studentName = studentName,
            fatherName = fatherName,
            dob = dob,
            doa = doa,
            phoneNumber = phoneNumber,
            classId = klass,
            address = address,
            bForm = bForm
        )

        if (currentStudent != null) {
            viewModel.updateStudent(student)
            showSnackbar("Student updated")

        } else {
            viewModel.insertStudent(student)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
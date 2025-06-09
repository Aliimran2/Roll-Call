package com.miassolutions.rollcall.utils

import android.content.Context
import android.net.Uri
import com.miassolutions.rollcall.data.entities.Student
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

object ImportFromExcel {

    fun readStudentsFromExcel(context: Context, uri: Uri): List<Student> {
        val students = mutableListOf<Student>()
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

        inputStream.use { stream ->
            val workbook = WorkbookFactory.create(stream)
            val sheet = workbook.getSheetAt(0)  // First sheet

            for (rowIndex in 1..sheet.lastRowNum) { // Skip header row
                val row = sheet.getRow(rowIndex) ?: continue

                val regNumber = row.getCell(0)?.numericCellValue?.toInt() ?: continue
                val rollNumber = row.getCell(1)?.numericCellValue?.toInt() ?: continue
                val studentName = row.getCell(2)?.stringCellValue ?: ""
                val fatherName = row.getCell(3)?.stringCellValue ?: ""
                val dob = row.getCell(4)?.stringCellValue ?: ""
                val klass = row.getCell(5)?.stringCellValue ?: ""
                val phoneNumber = row.getCell(6)?.stringCellValue ?: ""
                val address = row.getCell(7)?.stringCellValue ?: ""

                students.add(
                    Student(
                        regNumber = regNumber,
                        rollNumber = rollNumber,
                        studentName = studentName,
                        fatherName = fatherName,
                        dob = dob,
                        klass = klass,
                        phoneNumber = phoneNumber,
                        address = address
                    )
                )
            }

            workbook.close()
        }

        return students
    }
}
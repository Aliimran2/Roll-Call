package com.miassolutions.rollcall.utils

import android.content.Context
import android.net.Uri
import com.miassolutions.rollcall.data.entities.StudentEntity
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory

object ImportFromExcel {


    fun readStudentsFromExcel(context: Context, uri: Uri): List<StudentEntity> {
        val studentEntities = mutableListOf<StudentEntity>()

        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val workbook = WorkbookFactory.create(stream)
                val sheet = workbook.getSheetAt(0)

                for (rowIndex in 1..sheet.lastRowNum) {
                    val row = sheet.getRow(rowIndex) ?: continue

                    try {
                        val regNumber = row.getCell(0)?.let {
                            if (it.cellType == CellType.NUMERIC) it.numericCellValue.toInt()
                            else it.stringCellValue.toInt()
                        } ?: continue

                        val rollNumber = row.getCell(1)?.let {
                            if (it.cellType == CellType.NUMERIC) it.numericCellValue.toInt()
                            else it.stringCellValue.toInt()
                        } ?: continue

                        val studentName = row.getCell(2)?.toString()?.trim() ?: ""
                        val fatherName = row.getCell(3)?.toString()?.trim() ?: ""
                        val dob = row.getCell(4)?.toString()?.trim() ?: ""
                        val klass = row.getCell(5)?.toString()?.trim() ?: ""
                        val phoneNumber = row.getCell(6)?.toString()?.trim() ?: ""
                        val address = row.getCell(7)?.toString()?.trim() ?: ""

                        studentEntities.add(
                            StudentEntity(
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
                    } catch (e: Exception) {
                        // Optionally log or collect skipped rows
                        continue
                    }
                }

                workbook.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList() // gracefully return empty if file is bad
        }

        return studentEntities
    }


}

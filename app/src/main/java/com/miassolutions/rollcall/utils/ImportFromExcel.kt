package com.miassolutions.rollcall.utils

import android.content.Context
import android.net.Uri
import com.miassolutions.rollcall.data.entities.StudentEntity
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.util.Date

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
                        val bForm = row.getCell(4)?.let { cell ->
                            when (cell.cellType) {
                                CellType.STRING -> cell.stringCellValue.trim()
                                CellType.NUMERIC -> cell.numericCellValue.toLong().toString().padStart(13, '0')
                                    .let { "${it.substring(0,5)}-${it.substring(5,12)}-${it.substring(12)}" }
                                else -> null
                            }
                        }
                        val dobMillis = row.getCell(5)?.let {
                            if (it.cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(it)) {
                                it.dateCellValue.time
                            } else null
                        } ?: continue // dob is mandatory

                        val doaMillis = row.getCell(6)?.let {
                            if (it.cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(it)) {
                                it.dateCellValue.time
                            } else null
                        }

                        val klass = row.getCell(7)?.toString()?.trim()
                        val phoneNumber = row.getCell(8)?.toString()?.trim()
                        val address = row.getCell(9)?.toString()?.trim()

                        studentEntities.add(
                            StudentEntity(
                                regNumber = regNumber,
                                rollNumber = rollNumber,
                                studentName = studentName,
                                fatherName = fatherName,
                                bForm = bForm,
                                dob = dobMillis,
                                doa = doaMillis,
                                classId = klass!!,
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

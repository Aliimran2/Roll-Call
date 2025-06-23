package com.miassolutions.rollcall.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.miassolutions.rollcall.data.entities.StudentEntity
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.Q)
fun exportExcelToDownloadsWithMediaStore(context: Context, students: List<StudentEntity>) {
    try {
        val fileName = "students_${System.currentTimeMillis()}.xlsx"
        val mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            resolver.openOutputStream(uri)?.use { outputStream ->
                val workbook = XSSFWorkbook()
                val sheet = workbook.createSheet("Students")

                val creationHelper = workbook.creationHelper
                val dateCellStyle = workbook.createCellStyle().apply {
                    dataFormat = creationHelper.createDataFormat().getFormat("dd-MM-yyyy")
                }

                val headers = listOf(
                    "ID", "Image", "Reg#", "Roll#", "Name", "Father", "B-Form",
                    "DOB", "DOA", "Class", "Phone", "Address"
                )
                val headerRow = sheet.createRow(0)
                headers.forEachIndexed { i, header ->
                    headerRow.createCell(i).setCellValue(header)
                }

                students.forEachIndexed { i, student ->
                    val row = sheet.createRow(i + 1)

                    row.createCell(0).setCellValue(student.studentId)
                    row.createCell(1).setCellValue(student.studentImage ?: "")
                    row.createCell(2).setCellValue(student.regNumber.toDouble())
                    row.createCell(3).setCellValue(student.rollNumber.toDouble())
                    row.createCell(4).setCellValue(student.studentName)
                    row.createCell(5).setCellValue(student.fatherName)
                    row.createCell(6).setCellValue(student.bForm ?: "")

                    // ✅ DOB as Date cell
                    val dobDate = Date(student.dob)
                    val dobCell = row.createCell(7)
                    dobCell.setCellValue(dobDate)
                    dobCell.cellStyle = dateCellStyle

                    // ✅ DOA as Date cell (nullable)
                    val doaDate = student.doa?.let { Date(it) }
                    val doaCell = row.createCell(8)
                    doaDate?.let {
                        doaCell.setCellValue(it)
                        doaCell.cellStyle = dateCellStyle
                    }

                    row.createCell(9).setCellValue(student.classId ?: "")
                    row.createCell(10).setCellValue(student.phoneNumber ?: "")
                    row.createCell(11).setCellValue(student.address ?: "")
                }

                workbook.write(outputStream)
                workbook.close()
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // Avoid using Toast/Dialogs here (IO thread)
    }
}


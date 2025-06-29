package com.miassolutions.rollcall.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.miassolutions.rollcall.data.entities.StudentEntity
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream
import java.util.Date

@RequiresApi(Build.VERSION_CODES.Q)
fun copySampleExcelFromAssets(context: Context, assetFileName: String, targetFileName: String = "sample_students.xlsx") {
    try {
        val resolver = context.contentResolver
        val mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, targetFileName)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(uri)?.use { outputStream ->
                context.assets.open(assetFileName).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}



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

                    row.createCell(9).setCellValue(student.klass ?: "")
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


@RequiresApi(Build.VERSION_CODES.Q)
fun generateSampleExcelFile(context: Context) {
    val fileName = "SampleStudentData.xlsx"

    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

    if (uri == null) {
        Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show()
        return
    }

    resolver.openOutputStream(uri)?.use { outputStream ->
        writeSampleExcel(outputStream)
        Toast.makeText(context, "Exported to Downloads", Toast.LENGTH_SHORT).show()
    }
}

private fun writeSampleExcel(outputStream: OutputStream) {
    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("Students")

    val headerRow = sheet.createRow(0)
    headerRow.createCell(0).setCellValue("Reg Number")
    headerRow.createCell(1).setCellValue("Roll Number")
    headerRow.createCell(2).setCellValue("Student Name")
    headerRow.createCell(3).setCellValue("Father Name")
    headerRow.createCell(4).setCellValue("Date of Birth (dd-MM-yyyy)")
    headerRow.createCell(5).setCellValue("Class")
    headerRow.createCell(6).setCellValue("Phone Number")
    headerRow.createCell(7).setCellValue("Address")

    val sampleRow = sheet.createRow(1)
    sampleRow.createCell(0).setCellValue(1.0)
    sampleRow.createCell(1).setCellValue(1.0)
    sampleRow.createCell(2).setCellValue("John Doe")
    sampleRow.createCell(3).setCellValue("Mr. Doe")
    sampleRow.createCell(4).setCellValue("01-01-2000")
    sampleRow.createCell(5).setCellValue("10th Grade")
    sampleRow.createCell(6).setCellValue("9876543210")
    sampleRow.createCell(7).setCellValue("New Delhi")

    workbook.write(outputStream)
    workbook.close()
}
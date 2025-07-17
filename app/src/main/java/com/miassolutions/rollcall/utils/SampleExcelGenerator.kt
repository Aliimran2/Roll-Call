//package com.miassolutions.rollcall.utils
//
//import android.content.ContentValues
//import android.content.Context
//import android.os.Build
//import android.os.Environment
//import android.provider.MediaStore
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import org.apache.poi.xssf.usermodel.XSSFWorkbook
//import java.io.File
//import java.io.FileOutputStream
//import java.io.OutputStream
//
//object SampleExcelGenerator {
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    fun generateSampleExcelFile(context: Context) {
//        val fileName = "SampleStudentData.xlsx"
//
//        val resolver = context.contentResolver
//        val contentValues = ContentValues().apply {
//            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
//            put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
//            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
//        }
//
//        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
//
//        if (uri == null) {
//            Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        resolver.openOutputStream(uri)?.use { outputStream ->
//            writeSampleExcel(outputStream)
//            Toast.makeText(context, "Exported to Downloads", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun writeSampleExcel(outputStream: OutputStream) {
//        val workbook = XSSFWorkbook()
//        val sheet = workbook.createSheet("Students")
//
//        val headerRow = sheet.createRow(0)
//        headerRow.createCell(0).setCellValue("Reg Number")
//        headerRow.createCell(1).setCellValue("Roll Number")
//        headerRow.createCell(2).setCellValue("Student Name")
//        headerRow.createCell(3).setCellValue("Father Name")
//        headerRow.createCell(4).setCellValue("Date of Birth (dd-MM-yyyy)")
//        headerRow.createCell(5).setCellValue("Class")
//        headerRow.createCell(6).setCellValue("Phone Number")
//        headerRow.createCell(7).setCellValue("Address")
//
//        val sampleRow = sheet.createRow(1)
//        sampleRow.createCell(0).setCellValue(1.0)
//        sampleRow.createCell(1).setCellValue(1.0)
//        sampleRow.createCell(2).setCellValue("John Doe")
//        sampleRow.createCell(3).setCellValue("Mr. Doe")
//        sampleRow.createCell(4).setCellValue("01-01-2000")
//        sampleRow.createCell(5).setCellValue("10th Grade")
//        sampleRow.createCell(6).setCellValue("9876543210")
//        sampleRow.createCell(7).setCellValue("New Delhi")
//
//        workbook.write(outputStream)
//        workbook.close()
//    }
//}

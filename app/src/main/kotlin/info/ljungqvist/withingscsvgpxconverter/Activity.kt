package info.ljungqvist.withingscsvgpxconverter

import com.opencsv.CSVReader
import java.util.*
import java.util.zip.ZipInputStream


fun readActivities(zipIn: ZipInputStream): List<Activity> = run {
    val reader = CSVReader(zipIn.reader())
    reader.readNext() // skip header
    generateSequence { reader.readNext() }
        .filter { it[5] == TYPE_RUNNING }
        .map {
            Activity(
                dataFormat.parse(it[0]),
                dataFormat.parse(it[1])
            )
        }
//        .filter { it.start.time > 1591822800000L }
        .filter { it.start.time > 1541690640000L }
//        .filter { it.end.time < 1606341600000L }
        .sortedBy { it.start }
        .toList()
}

data class Activity(
    val start: Date,
    val end: Date
)

private const val TYPE_RUNNING = "Running"

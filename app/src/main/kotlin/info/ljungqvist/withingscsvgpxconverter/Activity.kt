package info.ljungqvist.withingscsvgpxconverter

import com.opencsv.CSVReader
import java.util.*
import java.util.zip.ZipInputStream


fun readActivities(zipIn: ZipInputStream): List<Activity> = run {
    val reader = CSVReader(zipIn.reader())
    reader.readNext() // skip header
    generateSequence { reader.readNext() }
            .filter { it[5] in listOf(TYPE_RUNNING, TYPE_WALKING) }
            .map {
                Activity(
                        dataFormat.parse(it[0]),
                        dataFormat.parse(it[1]),
                        it[5]
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
        val end: Date,
        val type: String,
)

private const val TYPE_RUNNING = "Running"
private const val TYPE_WALKING = "Walking"

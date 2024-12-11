package info.ljungqvist.withingscsvgpxconverter

import com.opencsv.CSVReader
import java.util.*
import java.util.zip.ZipInputStream


fun readActivities(zipIn: ZipInputStream): List<Activity> = run {
    val reader = CSVReader(zipIn.reader())
    reader.readNext() // skip header
    generateSequence { reader.readNext() }
        .map { row ->
            ActivityType.entries
                .find { row[5] == it.withingsType }
                ?.let { row to it }
        }
        .filterNotNull()
        .filter { (_, type) ->
            type in listOf(ActivityType.RUN, ActivityType.WORKOUT)
        }
        .map { (row, type) ->
            Activity(
                dataFormat.parse(row[0]),
                dataFormat.parse(row[1]),
                type
            )
        }
        .filter { it.start.time > 1704060000000L } // 2024-01-01
        .sortedBy { it.start }
        .toList()
}

data class Activity(
    val start: Date,
    val end: Date,
    val type: ActivityType,
)

enum class ActivityType(val type: String, val withingsType: String, val number: Int) {
    RUN("Run", "Running", 9),
    WALK("Walk", "Walking", 10),
    WORKOUT("Workout", "Gym class", 11),
}

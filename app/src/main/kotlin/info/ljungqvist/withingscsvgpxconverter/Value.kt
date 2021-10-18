package info.ljungqvist.withingscsvgpxconverter


import com.opencsv.CSVReader
import java.util.*
import java.util.zip.ZipInputStream


fun readValues(zipIn: ZipInputStream): List<Value> = run {
    val reader = CSVReader(zipIn.reader())
    reader.readNext() // skip header
    generateSequence { reader.readNext() }
        .flatMap {
            val values = it[2].parseArray()
                .map { value -> value.toDouble() }
            it[1].parseArray()
                .map { duration -> duration.toInt() }
                .foldIndexed(emptyList<Value>() to dataFormat.parse(it[0])) { index, (list, date), duration ->
                    (list + Value(date, values[index])) to Date(date.time + duration * 1000)
                }
                .first
                .asSequence()
        }
        .sortedBy { it.start }
        .toList()
        .let { list -> list + Value(Date(), list.last().value) }
}

data class Value(
    val start: Date,
    val value: Double
)

fun String.parseArray(): List<String> =
    drop(1)
        .dropLast(1)
        .split(",")



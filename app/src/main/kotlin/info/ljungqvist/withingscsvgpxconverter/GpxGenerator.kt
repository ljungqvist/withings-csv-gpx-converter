package info.ljungqvist.withingscsvgpxconverter

import java.util.*

fun generateGpx(
        activities: List<Activity>?,
        altitudes: List<Value>?,
        hrs: List<Value>?,
        latitudes: List<Value>?,
        longitudes: List<Value>?,
): List<ParsedActivity>? =
        if (activities != null && altitudes != null && hrs != null && latitudes != null && longitudes != null) {
            generateGpxInternal(activities, altitudes, hrs, latitudes, longitudes)
        } else {
            println("ERROR activities: ${activities != null}, altitudes: ${altitudes != null}, hrs: ${hrs != null}, latitudes: ${latitudes != null}, longitudes: ${longitudes != null}")
            null
        }

private fun generateGpxInternal(
        activities: List<Activity>,
        altitudes: List<Value>,
        hrs: List<Value>,
        latitudes: List<Value>,
        longitudes: List<Value>,
): List<ParsedActivity> =
        activities.map { a ->
            generateActivity(a, altitudes, hrs, latitudes, longitudes)
        }

private fun generateActivity(
        activity: Activity,
        altitudes: List<Value>,
        hrs: List<Value>,
        latitudes: List<Value>,
        longitudes: List<Value>,
): ParsedActivity {
    var date = activity.start

    println("ALT ${altitudes[0]}")
    println("HR ${hrs[0]}")
    println("LATITUDES ${latitudes[0]}")
    println("LONGITUDES ${longitudes[0]}")

    val altitudesIterator = ValueIterator(altitudes, date, "ALT")
    val hrIterator = ValueIterator(hrs, date, "HR")
    val latitudesIterator = ValueIterator(latitudes, date, "LATITUDES")
    val longitudesIterator = ValueIterator(longitudes, date, "LONGITUDES")
    val iterators = listOf(altitudesIterator, hrIterator, latitudesIterator, longitudesIterator)

    val res = mutableListOf(
            GpxPoint(
                    date,
                    latitudesIterator.valueAt(date),
                    longitudesIterator.valueAt(date),
                    altitudesIterator.valueAt(date),
                    hrIterator.valueAt(date).toInt()
            )
    )

    while (date < activity.end) {
        date = iterators.fold(activity.end) { d, it ->
            minOf(d, it.upperDate)
        }
        iterators.forEach { iter ->
            if (iter.upperDate == date) {
                iter.next()
            }
        }
        res += GpxPoint(
                date,
                latitudesIterator.valueAt(date),
                longitudesIterator.valueAt(date),
                altitudesIterator.valueAt(date),
                hrIterator.valueAt(date).toInt()
        )
    }

    return ParsedActivity(res, activity.type);

}

private abstract class InterpolatingIterator<T, V>(
        private val list: List<T>,
        startDate: Date,
        private val tag: String
) {
    protected abstract fun start(t: T): Date
    protected abstract fun value(t: T): V
    protected abstract infix fun V.p(t: V): V
    protected abstract infix fun V.m(t: V): V
    protected abstract infix fun V.t(d: Double): V

    private var lowerId = list.binarySearch { start(it).compareTo(startDate) }
            .let { index ->
                println(tag)
                println(startDate)
                println(start(list[0]))
                if (index < 0) -index - 2 else index
            }

    var lower = list[lowerId]
        private set
    var upper = list[lowerId + 1]
        private set
    val lowerDate: Date
        get() = start(lower)
    val upperDate: Date
        get() = start(upper)

    fun valueAt(date: Date): V {
        val rate = (date.time - lowerDate.time).toDouble() / (upperDate.time - lowerDate.time)
        return value(lower).let { l ->
            l p ((value(upper) m l) t rate)
        }
    }

    fun next() {
        lowerId++
        lower = upper
        try {
            upper = list[lowerId + 1]
        } catch (e: Exception) {
            println(tag)
            throw e
        }
    }

}

private class ValueIterator(list: List<Value>, startDate: Date, tag: String) :
        InterpolatingIterator<Value, Double>(list, startDate, tag) {
    override fun start(t: Value): Date = t.start
    override fun value(t: Value): Double = t.value
    override fun Double.p(t: Double): Double = this + t
    override fun Double.m(t: Double): Double = this - t
    override fun Double.t(d: Double): Double = this * d
}

data class GpxPoint(
        val date: Date,
        val lat: Double,
        val lon: Double,
        val ele: Double,
        val hr: Int,
)

data class ParsedActivity(
        val points: List<GpxPoint>,
        val type: String,
)

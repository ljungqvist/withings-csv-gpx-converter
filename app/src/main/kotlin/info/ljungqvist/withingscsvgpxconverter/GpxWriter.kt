package info.ljungqvist.withingscsvgpxconverter

import java.io.PrintStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun writeGpx(out: PrintStream, points: List<GpxPoint>) {
    out.print("""
<?xml version="1.0" encoding="UTF-8"?>
<gpx xmlns="http://www.topografix.com/GPX/1/1" xmlns:gpxtpx="http://www.garmin.com/xmlschemas/TrackPointExtension/v2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.1" creator="Withings Steel HR" xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v2 http://www.garmin.com/xmlschemas/TrackPointExtensionv2.xsd">
 <metadata>
  <time>${dataFormatZ.format(points.first().date)}</time>
 </metadata>
 <trk>
  <name>Morning Run</name>
  <type>9</type>
  <trkseg>""")
    points.forEach { point ->
        out.print("""
  <trkpt lon="${point.lon}" lat="${point.lat}">
    <ele>${point.ele}</ele>
    <time>${dataFormatZ.format(point.date)}</time>
    <extensions>
      <gpxtpx:TrackPointExtension>
       <gpxtpx:hr>${point.hr}</gpxtpx:hr>
      </gpxtpx:TrackPointExtension>
    </extensions>
  </trkpt>
        """)
    }
    out.print("""
  </trkseg>
 </trk>
</gpx>""")

}

val dataFormatZ: DateFormat
    get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .apply { timeZone = TimeZone.getTimeZone("GMT") }


import android.location.Location

class Point {
    companion object {
        fun VenezaPark(): Location {
            val merdinha = Location("")
            merdinha.latitude = -7.870345 // Substituir pelas coordenadas reais
            merdinha.longitude = -34.835556
            return merdinha
        }

        fun ShoppingTacaruna(): Location {
            val location = Location("")
            location.latitude = -8.038157 // Substituir pelas coordenadas reais
            location.longitude = -34.871584
            return location
        }
    }
}

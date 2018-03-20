package resumes.generators

import java.util

import com.google.maps.model.{AddressComponent, LatLng}
import com.google.maps.{GeoApiContext, GeocodingApi}

import scala.util.Random

object AddressGenerator {

  case class Address(zipCode: String,
                     stateShortName: String,
                     stateFullName: String,
                     city: String,
                     street: String,
                     house: String
                    )

  val key = "AIzaSyCK1Ywod_Rn1bJ7YjKUWCQt-hTjT_lvBDY"
  val context = new GeoApiContext.Builder()
    .apiKey(key)
    .build()

  def generateAddress(location: String): Address = {
    val cityCoordinates = GeocodingApi.geocode(context, location).await()(0).geometry.location
    val latDelta = Random.nextDouble() / 10
    val lngDelta = Random.nextDouble() / 10
    val addressCoordinates = new LatLng(cityCoordinates.lat + latDelta, cityCoordinates.lng + lngDelta)
    val address = GeocodingApi
      .reverseGeocode(context, addressCoordinates)
      .await()(0)
    val addressComponents: List[AddressComponent] = scala.collection.JavaConverters.asScalaBuffer(util.Arrays.asList(address.addressComponents: _*)).toList

    def getValue(addressPart: String) = {
      addressComponents.find(x => {
        x.types(0).toCanonicalLiteral.equals(addressPart)
      }).get
    }

    val zipCode = getValue("postal_code").longName
    val stateShortName = getValue("administrative_area_level_1").shortName
    val stateFullName = getValue("administrative_area_level_1").longName
    val city = getValue("locality").longName
    val street = getValue("route").longName
    val house = addressComponents(0).longName
    Address(
      zipCode = zipCode,
      stateShortName = stateShortName,
      stateFullName = stateFullName,
      city = city,
      street = street,
      house = house
    )
  }

  def main(args: Array[String]): Unit = {
    println(generateAddress("Columbus, OH"))
    println(generateAddress("Columbus, OH"))
    println(generateAddress("Columbus, OH"))
    println(generateAddress("Columbus, OH"))
    println(generateAddress("Ann Arbor, MI"))
    println(generateAddress("Ann Arbor, MI"))
    println(generateAddress("Ann Arbor, MI"))
    println(generateAddress("Ann Arbor, MI"))

  }


}

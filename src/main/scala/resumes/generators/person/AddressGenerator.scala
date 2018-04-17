package resumes.generators.person

import java.util

import com.google.maps.model.{AddressComponent, LatLng}
import com.google.maps.{GeoApiContext, GeocodingApi}
import resumes.company.PositionManager.Position
import resumes.generators.Utils
import resumes.generators.education.EducationGenerator.Education
import resumes.generators.person.PersonGenerator.Comment

import scala.util.Random

object AddressGenerator {

  case class Address(zipCode: String,
                     stateShortName: String,
                     stateFullName: String,
                     city: String,
                     street: String,
                     house: String
                    )
  val MULTIPLE_LOCATIONS = "ML"

  val key = "AIzaSyCK1Ywod_Rn1bJ7YjKUWCQt-hTjT_lvBDY"
  val context = new GeoApiContext.Builder()
    .apiKey(key)
    .build()

  val sanFranciscoLocation = Utils.trueFalseDistribution(forTrue = 4, forFalse = 1)

  def generateAddress(
                       education: List[Education],
                       real_work_experience: Boolean,
                       position: Position
                     ): (Address, Comment) = {
    if (real_work_experience && position.address.isDefined) {
      var location = position.address.get

      if (location.equals(MULTIPLE_LOCATIONS)) {
        if (sanFranciscoLocation.sample()) {
          location = "San Francisco, CA"
        } else {
          location = "New York City, NY"
        }
      }
      (getAddressByLocation(location), "Got address from position")
    } else {
      (getAddressByLocation(education(0).university.city + ", " + education(0).university.state), "Got location from last university")
    }
  }

  def getAddressByLocation(location: String): Address = {
    try {
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
    } catch {
      case e: Exception => {
        getAddressByLocation(location)
      }
    }
  }

}

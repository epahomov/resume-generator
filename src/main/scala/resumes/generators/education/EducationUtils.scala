package resumes.generators.education

import resumes.generators.Utils
import resumes.generators.education.Enums.Major._
import resumes.company.PositionManager.Area
object EducationUtils {

  lazy val computerScienceAreaMajorGenerator = {
    val distribution = List(
      (Computer_Science, 10),
      (Information_Technology, 2),
      (Engineering, 1),
      (Mathematics, 1)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  lazy val hardwareAreaMajorGenerator = {
    val distribution = List(
      (Engineering, 7),
      (Computer_Engineering, 10),
      (Computer_Engineering_Technology, 10),
      (Computer_Science, 3),
      (Information_Technology, 1),
      (Mathematics, 2)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  lazy val designAreaMajorGenerator = {
    val distribution = List(
      (Graphic_Design, 1),
      (Interior_Design, 1),
      (Fashion_Design, 1),
      (Industrial_Design, 1),
      (Art, 1),
      (Fine_Arts, 1),
      (Media_Production, 1),
      (Landscape_Architecture, 1)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  lazy val financeAreaMajorGenerator = {
    val distribution = List(
      (Finance, 1),
      (Business, 1),
      (Economics, 1),
      (Accounting, 1)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  lazy val PRAreaMajorGenerator = {
    val distribution = List(
      (English_Language_and_Literature, 1),
      (Media_Production, 1),
      (Media_Studies, 1),
      (Political_Science, 1),
      (Psychology, 1),
      (Advertising, 1),
      (Journalism, 1),
      (Social_sciences_and_history, 1),
      (Communications, 1)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  lazy val MajorToArea = Map (
    Computer_Science -> Area.Computer_Science,
    Information_Technology -> Area.Computer_Science,
    Engineering -> Area.Hardware,
    Mathematics -> Area.Computer_Science,
    Computer_Engineering -> Area.Hardware,
    Computer_Engineering_Technology -> Area.Hardware,
    Graphic_Design -> Area.Design,
    Interior_Design -> Area.Design,
    Fashion_Design -> Area.Design,
    Industrial_Design -> Area.Design,
    Art -> Area.Design,
    Fine_Arts -> Area.Design,
    Media_Production -> Area.Design,
    Landscape_Architecture -> Area.Design,
    Finance -> Area.Finance,
    Business -> Area.Finance,
    Economics -> Area.Finance,
    Accounting -> Area.Finance,
    English_Language_and_Literature -> Area.PR,
    Media_Production -> Area.PR,
    Media_Studies -> Area.PR,
    Political_Science -> Area.PR,
    Psychology -> Area.PR,
    Advertising -> Area.PR,
    Journalism -> Area.PR,
    Social_sciences_and_history -> Area.PR,
    Communications -> Area.PR
  )



  lazy val totallyRandomMajorGenerator = {
    val distribution = List(
      (Computer_Science, 1),
      (Design, 1),
      (Information_Technology, 1),
      (Communications, 1),
      (Political_Science, 1),
      (Business, 1),
      (English_Language_and_Literature, 1),
      (Psychology, 1),
      (Nursing, 1),
      (Chemical_Engineering, 1),
      (Biology, 1),
      (Engineering, 1),
      (Social_sciences_and_history, 1),
      (History, 1),
      (Accounting, 1),
      (Health_Professions, 1),
      (Trades_and_Personal_Services, 1),
      (Education, 1),
      (Journalism, 1),
      (Computer_Engineering_Technology, 1),
      (Computer_Engineering, 1),
      (Building_and_Construction, 1),
      (Mathematics, 1)
    )
    Utils.getGeneratorFrequency(distribution)
  }




}

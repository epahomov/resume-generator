package resumes.generators.education

object Enums {

  object Degree extends Enumeration {
    type Degree = Value
    val Associate = Value("Associate")
    val Bachelor = Value("Bachelor")
    val Master = Value("Master")
  }

  object Major extends Enumeration {
    type Major = Value
    val Computer_Science = Value("Computer Science")
    val Design = Value("Design")
    val Graphic_Design = Value("Graphic Design")
    val Interior_Design = Value("Interior Design")
    val Fashion_Design = Value("Fashion Design")
    val Industrial_Design  = Value("Industrial Design")
    val Art  = Value("Art")
    val Fine_Arts  = Value("Fine Arts")
    val Media_Production  = Value("Media Production")
    val Media_Studies  = Value("Media Studies")
    val Landscape_Architecture  = Value("Landscape Architecture")
    val Information_Technology = Value("Information Technology")
    val Communications = Value("Communications")
    val Political_Science = Value("Political Science")
    val Business = Value("Business administration and management")
    val English_Language_and_Literature = Value("English Language and Literature")
    val Advertising = Value("Advertising")
    val Psychology = Value("Psychology")
    val Nursing = Value("Nursing")
    val Chemical_Engineering = Value("Chemical Engineering")
    val Biology = Value("Biology")
    val Engineering = Value("Engineering")
    val Computer_Engineering_Technology = Value("Computer Engineering and Technology")
    val Computer_Engineering = Value("Computer Engineering")
    val Social_sciences_and_history = Value("Social sciences and history")
    val History = Value("History")
    val Accounting = Value("Accounting")
    val Health_Professions = Value("Health Professions")
    val Trades_and_Personal_Services = Value("Trades and Personal Services")
    val Education = Value("Education")
    val Journalism = Value("Journalism")
    val Building_and_Construction = Value("Building and Construction")
    val Mathematics = Value("Mathematics")
    val Finance = Value("Finance")
    val Economics = Value("Economics")
  }

}

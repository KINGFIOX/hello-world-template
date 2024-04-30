class WeeklyWeatherForecast(val temperatures: Seq[Double]) {

  private def convertCtoF(temp: Double): Double = temp * 1.8 + 32

  def forecastInFahrenheit: Seq[Double] = temperatures.map(convertCtoF)

  // Override equals method
  override def equals(obj: Any): Boolean = obj match {
    case other: WeeklyWeatherForecast => this.temperatures == other.temperatures
    case _                            => false
  }

  // Override hashCode method
  override def hashCode(): Int = temperatures.hashCode()

  // Override toString method
  override def toString: String =
    s"WeeklyWeatherForecast(${temperatures.mkString(",")})"
}

// Companion object to mimic case class apply method
object WeeklyWeatherForecast {
  def apply(temperatures: Seq[Double]): WeeklyWeatherForecast =
    new WeeklyWeatherForecast(temperatures)

  def unapply(forecast: WeeklyWeatherForecast): Option[Seq[Double]] = Some(
    forecast.temperatures
  )
}

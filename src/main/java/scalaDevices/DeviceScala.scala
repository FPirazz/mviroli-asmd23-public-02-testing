trait DeviceScala {
  @throws(classOf[IllegalStateException])
  def on(): Unit
  def off(): Unit
  def isOn(): Boolean
  def reset(): Unit
}
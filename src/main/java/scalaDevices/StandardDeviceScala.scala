class StandardDeviceScala(failingPolicy: FailingPolicyScala) extends DeviceScala {
  private var deviceOn: Boolean = false

  require(Option(failingPolicy).isDefined, "failingPolicy cannot be null")

  override def on(): Unit = {
    if (!failingPolicy.attemptOn()) {
      throw new IllegalStateException()
    }
    this.deviceOn = true
  }

  override def off(): Unit = {
    this.deviceOn = false
  }

  override def isOn: Boolean = this.deviceOn

  override def reset(): Unit = {
    this.off()
    failingPolicy.reset()
  }

  override def toString: String = {
    s"StandardDevice{policy=${failingPolicy.policyName()}, on=$deviceOn}"
  }
}


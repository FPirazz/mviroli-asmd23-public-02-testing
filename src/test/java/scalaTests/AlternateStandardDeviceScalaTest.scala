import org.junit.jupiter.api.Assertions.{assertEquals, assertThrows}
import org.junit.jupiter.api.{BeforeEach, Test}
import org.mockito.Mockito.{verify, verifyNoInteractions, when}
import org.mockito.{Mock, MockitoAnnotations, Spy}

class AlternateStandardDeviceScalaTest {

  private var device: DeviceScala = _
  @Mock
  var stubFailingPolicy: FailingPolicyScala = _
  @Spy
  var spyRandomPolicy: FailingPolicyScala = _

  @BeforeEach
  def init(): Unit = {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  def testMock(): Unit = {
    device = new StandardDeviceScala(this.stubFailingPolicy)
    // multiple stubbing
    when(this.stubFailingPolicy.attemptOn()).thenReturn(false)
    when(this.stubFailingPolicy.policyName()).thenReturn("mock")
    assertThrows(classOf[IllegalStateException], () => device.on())
    assertEquals(s"StandardDevice{policy=mock, on=false}", device.toString)
  }

  @Test
  def testSpy(): Unit = {
    device = new StandardDeviceScala(this.spyRandomPolicy)
    // no interactions with the spy yet
    verifyNoInteractions(this.spyRandomPolicy)
    try {
      device.on()
    } catch {
      case _: IllegalStateException =>
    }
    // has attemptOn been called?
    verify(this.spyRandomPolicy).attemptOn()
  }
}


# 02Lab - Advanced testing, mocking, integration

## Task 1: Reorganise
The repo has a device example, discussed in room. Play with it. Reorganise tests, which are currently grouped by type 
of mock. Seek for the perfect unit test!

### Work Done:

After re-viewing the examples available in the lab, the first thing that occurred to me, would be to re-organize the
tests to have one Full Coverage Test that includes all the coverages criteria (e.g. it tests for Function Coverage,
Statement Coverage, etc.), and maintain a dynamic system to instantiate both different types of Devices and/or policies
based on the needs of the user testing. Most of the changed classes fall under the "revisedDevices" package, both in
the main and test directory, considering also that the main principles that I considered using were the usual ones
used for good OOP programming (KISS, DRY etc.) and also the ones used in Software Engineering as a whole, like modularity,
abstraction, lack of repetitions etc.

In order to do this I revised the Device and StandardDevice classes by turning the Device 
interface into a abstract class like this:
```
public abstract class Device {
    private FailingPolicy failingPolicy;
    private boolean on = false;

    public Device(FailingPolicy failingPolicy) {
        this.failingPolicy = Objects.requireNonNull(failingPolicy);
    }
    
    abstract void on() throws IllegalStateException;
    abstract void off();
    abstract boolean isOn();
    abstract void reset();
}
```

The methods remain to be implemented by the class extending the abstract class, by otherwise the instantiation of the
failing policy, and checking whether it's not null, it's common for all devices, therefore it is always meant to be done,
and I've also made all variables protected instead of private, so the classes that inherits from the abstract one can
access them. The reason I've done this is so then other types of devices can be defined for further testing (e.g. a 
"BrokenDevice" together with the already existing StandardDevice one). The same idea has been applied to the 
FailingPolicy class by transforming it into an abstract class in order to extend more types of failing policies, by
creating an example one like AfterTwoFailing, which mean the policy fails after two times the device has been turned on.

Test wise, I created a RevisedDevicesBlueprintTest class to act as a Template to create different testing classes based
on the device to test, structured like this:
```
public abstract class RevisedDevicesBlueprintTest {
    protected Device device;
    protected FailingPolicy failingPolicy;

    void initMockStandardDevice() {
        this.failingPolicy = mock(FailingPolicy.class);
        this.device = new StandardDevice(this.failingPolicy);
    }

    void initMockBrokenDevice() {
        this.failingPolicy = mock(FailingPolicy.class);
        this.device = new BrokenDevice(this.failingPolicy);
    }

    void initSpyStandardDeviceRandomFailing() {
        this.failingPolicy = spy(new RandomFailing());
        this.device = new StandardDevice(this.failingPolicy);
    }

    void initSpyBrokenDeviceRandomFailing() {
        this.failingPolicy = spy(new RandomFailing());
        this.device = new BrokenDevice(this.failingPolicy);
    }

    void initSpyStandardDeviceAfterTwoFailing() {
        this.failingPolicy = spy(new AfterTwoFailing());
        this.device = new StandardDevice(this.failingPolicy);
    }

    void initSpyBrokenDeviceAfterTwoFailing() {
        this.failingPolicy = spy(new AfterTwoFailing());
        this.device = new BrokenDevice(this.failingPolicy);
    }

}
```
Where the main idea is to let the user building the test, have "free rein" over the type of test to make, considering
that usually a certain Testing Double entity may be used instead of one of the other presents (e.g. a Dummy is used
instead of a Stub) based on the scenario envisioned to be tested by the user. Examples of test files are as follows:

Standard Mock Device ([RevisedStandardDeviceTest](src/test/java/revisedDevices/RevisedStandardDeviceTest.java)):
```
public class RevisedStandardDeviceTest extends RevisedDevicesBlueprintTest{
    @BeforeEach
    @Override
    void initMockStandardDevice() {
        super.initMockStandardDevice();
    }
    @Test
    @DisplayName("Switch on Device")
    void testCanBeSwitchedOn() {
        when(this.failingPolicy.attemptOn()).thenReturn(true);
        device.on();
        assertTrue(device.isOn());
    }
    @Test
    @DisplayName("Device won't switch on if failing")
    void testWontSwitchOn() {
        // multiple stubbing
        when(this.failingPolicy.attemptOn()).thenReturn(false);
        when(this.failingPolicy.policyName()).thenReturn("mock");
        assertThrows(IllegalStateException.class, () -> device.on());
        assertEquals("StandardDevice{policy=mock, on=false}", device.toString());
    }
}
```

Broken Mock Device ([RevisedBrokenDeviceTest](src/test/java/revisedDevices/RevisedBrokenDeviceTest.java)):
```
public class RevisedBrokenDeviceTest extends RevisedDevicesBlueprintTest{

    @BeforeEach
    @Override
    void initMockBrokenDevice() {
        super.initMockBrokenDevice();
    }

    @Test
    @DisplayName("Switch on Device")
    void testCanBeSwitchedOn() {
        when(this.failingPolicy.attemptOn()).thenReturn(true);
        device.on();
        assertFalse(device.isOn());
    }

    @Test
    @DisplayName("Device won't switch on")
    void testWontSwitchOn() {
        // multiple stubbing
        when(this.failingPolicy.attemptOn()).thenReturn(false);
        when(this.failingPolicy.attemptOn()).thenReturn(false);
        when(this.failingPolicy.attemptOn()).thenReturn(false);
        when(this.failingPolicy.policyName()).thenReturn("mock");
        assertThrows(IllegalStateException.class, () -> device.on());
        assertEquals("BrokenDevice{policy=mock, on=false}", device.toString());
    }

}
```

Standard After Two Failing Device ([RevisedAfterTwoFailingTest](src/test/java/revisedDevices/RevisedAfterTwoFailingTest.java)):
```
public class RevisedAfterTwoFailingTest extends RevisedDevicesBlueprintTest {

    @BeforeEach
    @Override
    void initSpyStandardDeviceAfterTwoFailing() {
        super.initSpyStandardDeviceAfterTwoFailing();
    }

    @Test
    @DisplayName("AttemptOn is called as expected")
    void testReset() {
        device.isOn();

        verifyNoInteractions(this.failingPolicy);
        try{
            device.on();
        } catch (IllegalStateException e){}

        verify(this.failingPolicy).attemptOn();
        device.reset();

        assertEquals(2,
                Mockito.mockingDetails(this.failingPolicy).getInvocations().size());
    }

    @Test
    @DisplayName("Device fails after two times turned on")
    void testFailing() {
        device.isOn();

        verifyNoInteractions(this.failingPolicy);
        device.on();
        verify(this.failingPolicy, times(1)).attemptOn();
        device.on();

        try{
            device.on();
        } catch (IllegalStateException e){}

        verify(this.failingPolicy, times(3)).attemptOn();

        assertEquals(3,
                Mockito.mockingDetails(this.failingPolicy).getInvocations().size());
    }
}
```
And the other tests in the same package. By using that blueprint class we can see that the development of individual
Devices and Failing Policies became quite easy, manageable and clean to develop and read.

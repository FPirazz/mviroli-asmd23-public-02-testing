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

## Task 3: REENGINEER
Take an existing implemented small app with GUI, e.g. an OOP exam. Add a requirement that it outputs to console some 
relevant messages, through a log class. Now you have an App with at least 3 classes (GUI, Model, Log). How would you 
write integration tests for it? Search here: https://bitbucket.org/mviroli/oop2023-esami (2023, 2022,. . . )

### Work Done:

I've chosen the first exam project from the given link, so that is exercise a01a, contained in the package a01a.sol2, with
[this file](src/main/java/a01a/sol2/Test.java) being the test class.

I started by implement a basic Log class that makes use of the Logger library that Java has integrated, like this:
```
public class Log {
    private static Logger LOGGER;

    public static void setLOGGER(Logger LOGGERNEW) {
        LOGGER = LOGGERNEW;
    }
    
    public static Logger getLOGGER() {
        return LOGGER;
    }
}
```

Implemented in a few locations inside the [LogicImpl](src/main/java/a01a/sol2/LogicImpl.java) class to basically do
informative prints of what's going on in response to the GUI being interacted with.

When it comes to how I wrote integration tests for the application, I wrote tests for the application in its entirety:
for the Logic, GUI and Log, via the tests I wrote inside the a01a.sol2 package in the test directory.

First of all, I tested the Logic (and Log) classes of the application, by writing Unit tests to try and test all the
functionalities that the application needs to serve to the User, and try to have the most coverage possible.

This can be seen in the [LogicTest](src/test/java/a01a/sol2/LogicTest.java) class, where I tested every function of the
app, and had an almost 100% coverage of the Logic class and a 100% one of the Log class. Unfortunately, this line of code:
```
@Override
public boolean isOver() {
    if(this.marks.stream().anyMatch(p -> p.x() == this.size || p.y() == -1)) Log.getLOGGER().log(Level.INFO, "Application is over");
    return this.marks.stream().anyMatch(p -> p.x() == this.size || p.y() == -1); <=====
}
```
The coverage will never be 100% for every branch, because either:
* Both clauses are false;
* The first one is false and the second is true;
But if the first one is true, then the second will never be considered, therefore there will never be 100% coverage.

I have also written tests for the GUI, trying to also satisfy what is asked of the 4th task. The tests again can be
seen in the [GUITest](src/test/java/a01a/sol2/GUITest.java) class, unfortunately though I *had* to modify the GUI class
to include methods to perform tests, mainly to expose different components to be interacted with.

I used a Spy object from Mockito to keep track of the calls done to the Logic object, making sure that the "clicks" on
the button were done correctly following the instructions. Before the final version of GUITest, I also tried a Mock
of the Logic, which was definitively usable and doable, but I preferred to use the Spy considering the small amount of 
business logic.

## Task 4: GUI-TESTER
Generally, GUIs are a problem with testing. How do we test them? How do we automatise as most as possible testing of an 
app with a GUI? Play with a simple example and derive some useful consideration.

### Work Done:

Testing the GUI was definitely trickier than the business logic, especially considering that, in this case, I had to
expose some private attributes and behaviour of the class to the external environment, it is to be considered though
that I could've also implemented a separate class to inherit from the GUI one, and used that as a testing ground, instead
of the real thing.

I didn't really have any problems using the mock objects offered by Mockito to build the tests, the most "difficult" was
more to build code that would actually act as user interaction, so for example this newly part developed in the GUI class:
```
// New method to handle button clicks
public void handleButtonClick(JButton button) {
    Position position = this.cells.get(button);
    this.logic.hit(position);

    for (var entry : this.cells.entrySet()) {
        entry.getKey().setText(
                this.logic
                        .getMark(entry.getValue())
                        .map(String::valueOf)
                        .orElse(" "));
    }
}
```
Otherwise every other aspect was not really different from testing any type of object that had a dependency, but once
again thanks to TD entity that was easily testable.
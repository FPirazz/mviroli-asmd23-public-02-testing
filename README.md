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

## Task 2: TOOLING
Experiment with installing/using Mockito with Scala and/or in VSCode. Is VSCode better at all here? What’s the state of 
mocking technologies for Scala?

### Work Done:

I used the original Device example from the repo as an example to convert the code from firstly Java to Scala, as seen
in the following code snippets from the [FailingPolicyScala](src/main/java/scalaDevices/FailingPolicyScala.scala) class:
```
trait FailingPolicyScala {
  def attemptOn(): Boolean
  def reset(): Unit
  def policyName(): String
}

object FailingPolicyScala {
  private class RandomFailingScalaImpl extends FailingPolicyScala {
    private val random = new Random()
    private var failed = false

    override def attemptOn(): Boolean = {
      failed = failed || random.nextBoolean()
      !failed
    }

    override def reset(): Unit = {
      failed = false
    }

    override def policyName(): String = "random"
  }
  def apply(): FailingPolicyScala = new RandomFailingScalaImpl
}
```

The [DeviceScala](src/main/java/scalaDevices/DeviceScala.scala) class:
```
trait DeviceScala {
  @throws(classOf[IllegalStateException])
  def on(): Unit
  def off(): Unit
  def isOn(): Boolean
  def reset(): Unit
}
```
And the [StandardDeviceScala](src/main/java/scalaDevices/StandardDeviceScala.scala) one:
```
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
```
The Scala files worked quite well with Mockito, as we can see from the executable test class 
[AlternateStandardDeviceScalaTest](src/test/java/scalaTests/AlternateStandardDeviceScalaTest.scala), which when run,
perfectly executes with no problem. I also did find online a specific Mockito library for Scala, or rather, a library
that compiles Mockito in respect to the latest Scala version to make sure that there aren't any problems with the
library and the language, since Mockito is built mainly for Java.

When it comes to VSCode instead, from an "operative" standpoint, it really is not more useful than IntelliJ, I actually
think it's more of a hindrance, since, for IntelliJ, you just have to add one line correctly to your build tool of choice
(obviously here we used SBT), and that's really it. Meanwhile for VSCode, we need to go through a multi-steps process
which involves:
* Metals, which is a Scala plugin for VSCode;
* Change the settings in SBT instead of Bloop (Bloop is related to Metals);
* Then, VSCode doesn't *actually* immediately recognizes Java files from a bare bone installation, so we need more plugins
to recognize the language with the intelli-sense, through plugins like "Extension Pack for Java";
* And then, once again, we need to go to the "**settings.json**" file inside the "**.vscode**" folder to define the
JDK we want to use.

So all in all, it really is not very worth it to use VSCode, if obviously tools like IntelliJ are available.

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

## Task 5: TESTING-LLM

LLMs/ChatGPT can arguably help in write/improve/complete/implement/reverse-engineer a JUnit test, either unit or 
integration test. Experiment with this, based on the above tasks or in other cases. Is ChatGPT useful for all that?

### Work Done:

Just like with the first lab, I've used GPT with the *"Search"* and *"Reason"* option, and using as a baseline the
classes, both the tests and the entities created originally with the lab, so that is the normal StandardDevice and the
RandomFailing policy, where I supplied the code of each and one of them.

* Write: The prompt I've given to write tests from zero, it's this:
```
Could you pretty pretty please help me write JUnit tests for a java application, both Unit and Integration test? 
Consider to use the Mockito library for costructs like Test Doubles through Mocks (So using data structures like 
Spy and Mock) and Stubs (So using data structures like Dummy, Stub and Fake). The application is just a basic idea of a 
Device and a Failing Policy, and I want to test both considering the mostly basic scenarios. The code is as follows: 
Device Interface:
"
...
",
FailingPolicy Interface:
"
...
",
An implemented FailingPolicy called RandomFailing:
"
...
",
And finally an implemented Device called StandardDevice:
"
...
" 
```
Once again, the prompts look very silly, but they're useful (again in my experience) for better results. The results are
two Java class files produced by GPT, inside the package *GPTtests.written* inside the test folder.

Firstly, GPT produced a class that contains Unit Tests in the class 
[StandardDeviceUnitTestGPT](src/test/java/GPTtests/written/StandardDeviceUnitTestGPT.java), where it actually lacked
the annotation to initialize the mock variables declared (The line of code that was lacking was in the setup method).
The mocking present is done correctly, and the test is not too bad, unfortunately I couldn't manage to produce a result
where the spy construct was used, and if it was used, it was used correctly; but otherwise the results can be used
somewhat.

Meanwhile for the Integration tests, GPT produced the 
[StandardDeviceIntegrationTestGPT](src/test/java/GPTtests/written/StandardDeviceIntegrationTestGPT.java) class, which
includes again good integration tests for the devices at hand, covering most cases, tested through the coverage tool.

* Improve/Complete: For this task I've used the already present 
[AlternateStandardDeviceTest](src/test/java/devices/AlternateStandardDeviceTest.java) class to feed GPT, with the prompt
being:
```
Could you pretty pretty please help me improve JUnit tests for a java application, both Unit and Integration test? Consider to use the Mockito library for costructs like Test Doubles through Mocks (So using data structures like Spy and Mock) and Stubs (So using data structures like Dummy, Stub and Fake). The application is just a basic idea of a Device and a Failing Policy, and I want to test both considering the mostly basic scenarios. The code is as follows:
Device Interface:
"
...
",
An implemented FailingPolicy called RandomFailing:
"
...
",
And finally an implemented Device called StandardDevice:
"
...
".
Meanwhile, here are some of the tests I've written:
"
...
"
```
The class produced, [StandardDeviceUnitTestGPT.java](src/test/java/GPTtests/improveComplete/StandardDeviceUnitTestGPT.java),
is actually a pretty consistent and extended test class well produced, both in the code and the displayNames to explain
the test. Curiously, in counterpart to the writing task earlier, because spies were mentioned in the code, it was easier
for GPT to actually use it, which makes me wonder if GPT actually lacks the knowledge base for Mockito constructs.

* Reverse-engineer: When it comes to reverse-engineering the original entities (Device and FailigPolicy), I actually used
both of the available original devices test, so I gave GPT the 
[AlternateStandardDeviceTest.java](src/test/java/devices/AlternateStandardDeviceTest.java) and
[StandardDeviceTest.java](src/test/java/devices/StandardDeviceTest.java) classes. The prompt given is as follows:
```
Could you pretty pretty please help me reverse-engineer Java classes from JUnit tests for a java application? Consider that I used the Mockito library for costructs like Test Doubles through Mocks (So using data structures like Spy and Mock) and Stubs (So using data structures like Dummy, Stub and Fake). The application is just a basic idea of a Device and a Failing Policy, and I want to test both considering the mostly basic scenarios. The code for the tests is as follows:
This is one:
"
...
",
And this is another:
"
...
"
```
With the results present inside the *GPTtests* package inside the java source folder, once again, like the first lab, the
results are surprisingly accurate, apart from the reset function of the FailingPolicy, but otherwise the classes are
very, very good, proving that reverse-engineering can be done effectively.

So all in all GPT can be used to produce JUnit tests very easily as a sort of boilerplate producer, making writing
certain tests faster, but it definitely, at least as of writing this README, lacks certain information and knowledge if 
one wants to use specific technologies.

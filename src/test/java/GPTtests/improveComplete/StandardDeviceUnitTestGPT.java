package GPTtests.improveComplete;

import devices.FailingPolicy;
import devices.RandomFailing;
import devices.StandardDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StandardDeviceUnitTestGPT {

    @Mock
    private FailingPolicy mockPolicy;

    private StandardDevice device;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        device = new StandardDevice(mockPolicy);
    }

    @Test
    @DisplayName("Device turns on successfully when policy permits")
    void testOnSuccess() {
        // Arrange: policy allows turning on
        when(mockPolicy.attemptOn()).thenReturn(true);
        when(mockPolicy.policyName()).thenReturn("mockPolicy");

        // Act
        device.on();

        // Assert
        assertTrue(device.isOn(), "Device should be on when policy permits");
        verify(mockPolicy, times(1)).attemptOn();
    }

    @Test
    @DisplayName("Device fails to turn on when policy denies")
    void testOnFailure() {
        // Arrange: policy denies turning on
        when(mockPolicy.attemptOn()).thenReturn(false);
        when(mockPolicy.policyName()).thenReturn("mockPolicy");

        // Act & Assert: on() should throw IllegalStateException
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> device.on());
        assertFalse(device.isOn(), "Device should not be on after failure");
        verify(mockPolicy, times(1)).attemptOn();
    }

    @Test
    @DisplayName("Device turns off correctly")
    void testOff() {
        when(mockPolicy.attemptOn()).thenReturn(true);
        device.on();
        assertTrue(device.isOn(), "Device should be on after turning on");

        // Act
        device.off();

        // Assert
        assertFalse(device.isOn(), "Device should be off after calling off()");
    }

    @Test
    @DisplayName("Reset turns device off and resets the policy")
    void testReset() {
        when(mockPolicy.attemptOn()).thenReturn(true);
        device.on();
        assertTrue(device.isOn(), "Device should be on before reset");

        // Act
        device.reset();

        // Assert
        assertFalse(device.isOn(), "Device should be off after reset");
        verify(mockPolicy, times(1)).reset();
    }

    @Test
    @DisplayName("toString reflects current state and policy name")
    void testToString() {
        when(mockPolicy.attemptOn()).thenReturn(true);
        when(mockPolicy.policyName()).thenReturn("mockPolicy");

        // Initially off
        assertEquals("StandardDevice{policy=mockPolicy, on=false}", device.toString(),
                "toString should show off state initially");

        // After turning on
        device.on();
        assertEquals("StandardDevice{policy=mockPolicy, on=true}", device.toString(),
                "toString should show on state after turning on");
    }

    @Test
    @DisplayName("Verify spy interaction with a real policy")
    void testSpyPolicy() {
        // Use a spy wrapping a real instance of RandomFailing
        RandomFailing realPolicy = new RandomFailing();
        FailingPolicy spyPolicy = spy(realPolicy);
        device = new StandardDevice(spyPolicy);

        // Before interaction, no method calls should have been made.
        verifyNoInteractions(spyPolicy);

        try {
            device.on();
        } catch (IllegalStateException e) {
            // ignore if it fails on first attempt
        }
        // Verify that attemptOn() was indeed called
        verify(spyPolicy, atLeastOnce()).attemptOn();
    }
}

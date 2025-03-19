package GPTtests.written;

import devices.FailingPolicy;
import devices.StandardDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StandardDeviceUnitTestGPT {

    @Mock
    private FailingPolicy failingPolicy;

    private StandardDevice device;

    @BeforeEach
    public void setup() {
        // This annotation below was lacking
        MockitoAnnotations.openMocks(this);
        // Inject the mock FailingPolicy into the device.
        device = new StandardDevice(failingPolicy);
    }

    @Test
    public void testDeviceOn_Successful() {
        // Arrange: Stub the attemptOn method to return true.
        when(failingPolicy.attemptOn()).thenReturn(true);

        // Act
        device.on();

        // Assert: The device should now be on.
        assertTrue(device.isOn());
        // Verify that attemptOn was called exactly once.
        verify(failingPolicy, times(1)).attemptOn();
    }

    @Test
    public void testDeviceOn_Failure() {
        // Arrange: Stub the attemptOn method to return false.
        when(failingPolicy.attemptOn()).thenReturn(false);

        // Act & Assert: Expect an IllegalStateException when turning on.
        assertThrows(IllegalStateException.class, () -> device.on());
        // Verify that attemptOn was called exactly once.
        verify(failingPolicy, times(1)).attemptOn();
    }

    @Test
    public void testDeviceOff() {
        // Arrange: First, stub to allow turning on.
        when(failingPolicy.attemptOn()).thenReturn(true);
        device.on();
        assertTrue(device.isOn());

        // Act: Turn the device off.
        device.off();

        // Assert: The device should be off.
        assertFalse(device.isOn());
    }

    @Test
    public void testResetCallsFailingPolicyReset() {
        // Arrange: Stub the attemptOn to return true.
        when(failingPolicy.attemptOn()).thenReturn(true);
        device.on();
        assertTrue(device.isOn());

        // Act: Reset the device.
        device.reset();

        // Assert: The device should be off.
        assertFalse(device.isOn());
        // And the failingPolicy's reset method should have been called.
        verify(failingPolicy, times(1)).reset();
    }

    @Test
    public void testCallSequenceOnDevice() {
        // Arrange: Make attemptOn return true.
        when(failingPolicy.attemptOn()).thenReturn(true);

        // Act: Turn on, then reset.
        device.on();
        device.reset();

        // Assert: Verify the order of operations on the failingPolicy.
        InOrder inOrder = inOrder(failingPolicy);
        inOrder.verify(failingPolicy).attemptOn();
        inOrder.verify(failingPolicy).reset();
    }
}

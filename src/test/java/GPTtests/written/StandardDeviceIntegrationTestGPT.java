package GPTtests.written;

import devices.FailingPolicy;
import devices.RandomFailing;
import devices.StandardDevice;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StandardDeviceIntegrationTestGPT {

    @Test
    public void testDeviceToString() {
        // Arrange: Use the real RandomFailing policy.
        FailingPolicy realPolicy = new RandomFailing();
        StandardDevice device = new StandardDevice(realPolicy);

        // Act
        String result = device.toString();

        // Assert: Verify that the device description contains expected text.
        assertTrue(result.contains("policy=random"));
        assertTrue(result.contains("on=false"));
    }

    @RepeatedTest(5)
    public void testDeviceOnWithRandomFailing() {
        // Arrange: Create a device with the real RandomFailing policy.
        FailingPolicy realPolicy = new RandomFailing();
        StandardDevice device = new StandardDevice(realPolicy);

        // Act: Reset before turning on.
        device.reset();
        try {
            device.on();
            // Assert: If no exception, device should be on.
            assertTrue(device.isOn());
        } catch (IllegalStateException e) {
            // In case of failure, the device should remain off.
            assertFalse(device.isOn());
        }
    }

    @Test
    public void testResetIntegration() {
        // Arrange: Use the real RandomFailing policy.
        FailingPolicy realPolicy = new RandomFailing();
        StandardDevice device = new StandardDevice(realPolicy);

        // Act: Attempt to turn on (which may or may not succeed).
        try {
            device.on();
        } catch (IllegalStateException ignored) { }

        // Ensure device state is known, then reset.
        device.reset();

        // Assert: After reset, the device should be off.
        assertFalse(device.isOn());
    }
}

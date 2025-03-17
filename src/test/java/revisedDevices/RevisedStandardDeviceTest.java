package revisedDevices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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

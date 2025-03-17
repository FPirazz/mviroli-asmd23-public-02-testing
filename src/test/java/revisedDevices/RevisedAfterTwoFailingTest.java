package revisedDevices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

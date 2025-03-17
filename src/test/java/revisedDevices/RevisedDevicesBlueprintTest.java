package revisedDevices;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

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

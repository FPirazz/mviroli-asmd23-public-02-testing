package revisedDevices;

import java.util.Objects;

public abstract class Device {
    protected FailingPolicy failingPolicy;
    protected boolean on = false;

    public Device(FailingPolicy failingPolicy) {
        this.failingPolicy = Objects.requireNonNull(failingPolicy);
    }

    abstract void on() throws IllegalStateException;
    abstract void off();
    abstract boolean isOn();
    abstract void reset();
}

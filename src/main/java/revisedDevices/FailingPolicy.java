package revisedDevices;

import java.util.Random;

public abstract class FailingPolicy {
    protected final Random random = new Random();
    protected boolean failed = false;

    abstract boolean attemptOn();
    abstract void reset();
    abstract String policyName();
}

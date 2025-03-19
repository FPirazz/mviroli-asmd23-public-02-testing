package GPTtests;

public class StandardDeviceGPT implements DeviceGPT {

    private final FailingPolicyGPT policy;
    private boolean on;

    public StandardDeviceGPT(FailingPolicyGPT policy) {
        if (policy == null) {
            throw new NullPointerException("FailingPolicy cannot be null");
        }
        this.policy = policy;
        this.on = false; // initially off
    }

    @Override
    public void on() {
        // Each call to on() must check with the policy.
        if (!policy.attemptOn()) {
            // Device remains off if the attempt fails.
            throw new IllegalStateException("Failed to turn on device");
        }
        on = true;
    }

    @Override
    public void off() {
        on = false;
    }

    @Override
    public void reset() {
        // The reset() method calls attemptOn() to capture an invocation
        // (as checked by the spy tests) and then resets the device to off.
        policy.attemptOn();
        on = false;
    }

    @Override
    public boolean isOn() {
        return on;
    }

    @Override
    public String toString() {
        return "StandardDevice{policy=" + policy.policyName() + ", on=" + on + "}";
    }
}

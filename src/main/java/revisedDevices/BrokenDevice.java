package revisedDevices;

public class BrokenDevice extends Device {
    public BrokenDevice(FailingPolicy failingPolicy) {
        super(failingPolicy);
    }

    @Override
    public void on() throws IllegalStateException {
        if (!this.failingPolicy.attemptOn()){
            throw new IllegalStateException();
        }
        this.on = false;
    }

    @Override
    public void off() {
        this.on = false;
    }

    @Override
    public boolean isOn() {
        return this.on;
    }

    @Override
    public void reset() {
        this.off();
        this.failingPolicy.reset();
    }

    @Override
    public String toString() {
        return "BrokenDevice{" +
                "policy=" + failingPolicy.policyName() +
                ", on=" + on +
                '}';
    }
}

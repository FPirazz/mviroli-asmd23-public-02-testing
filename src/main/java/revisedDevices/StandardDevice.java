package revisedDevices;

public class StandardDevice extends Device {
    public StandardDevice(FailingPolicy failingPolicy) {
        super(failingPolicy);
    }

    @Override
    public void on() throws IllegalStateException {
        if (!this.failingPolicy.attemptOn()){
            throw new IllegalStateException();
        }
        this.on = true;
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
        return "StandardDevice{" +
                "policy=" + failingPolicy.policyName() +
                ", on=" + on +
                '}';
    }
}

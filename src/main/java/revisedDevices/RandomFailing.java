package revisedDevices;

import java.util.Random;

public class RandomFailing extends FailingPolicy {
    @Override
    public boolean attemptOn() {
        this.failed = this.failed || random.nextBoolean();
        return !this.failed;
    }

    @Override
    public void reset() {
        this.failed = false;
    }

    @Override
    public String policyName() {
        return "random";
    }
}

package revisedDevices;

public class AfterTwoFailing extends FailingPolicy{
    int interactions = 0;

    @Override
    boolean attemptOn() {
        this.failed = interactions < 2;
        return this.failed;
    }

    @Override
    void reset() {
        this.failed = false;
        this.interactions = 0;
    }

    @Override
    String policyName() {
        return "After Two";
    }
}

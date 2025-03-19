package GPTtests;

import java.util.Random;

public class RandomFailingGPT implements FailingPolicyGPT {

    private final Random random = new Random();

    @Override
    public boolean attemptOn() {
        // For demonstration, randomly return true or false.
        return random.nextBoolean();
    }

    @Override
    public String policyName() {
        return "RandomFailing";
    }
}

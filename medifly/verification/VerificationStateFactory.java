package medifly.verification;

public class VerificationStateFactory {
    public VerificationState create(VerificationStateEnum state) {
        VerificationState out = null;
        switch (state) {
            case PENDING:
                out = new PendingVerification();
                break;
            case VERIFIED:
                out = new VerifiedState();
                break;
            case REJECTED:
                out = new RejectedState();
        };

        return out;
    }
}

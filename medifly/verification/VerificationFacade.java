package medifly.verification;

public class VerificationFacade {
    private final VerificationStateFactory stateFactory = new VerificationStateFactory();
    
    public void verifyPrescription(String docId, VerificationStateEnum state, Prescription prescription) {
        if (prescription == null) 
            throw new IllegalArgumentException("Prescription cannot be null");
        
        prescription.setState(stateFactory.create(state));
        System.out.println("Prescription " + docId + " verified as " + prescription.getStatus().getState());
    }
}

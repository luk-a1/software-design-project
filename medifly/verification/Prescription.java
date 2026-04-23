package medifly.verification;

import java.util.ArrayList;
import java.util.List;

public class Prescription {
    private final String id;
    private final String documentName;
    private VerificationState status = new PendingVerification();
    private final List<VerificationObserver> observers = new ArrayList<>();

    public Prescription(String id, String documentName){ this.id = id; this.documentName = documentName; }
    public VerificationState getStatus(){ return status; }
    public void attach(VerificationObserver observer){ observers.add(observer); }
    public void setState(VerificationState status){ this.status = status; for (VerificationObserver o : observers) o.onPrescriptionStatusChange(this); }
}

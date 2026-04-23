package medifly.delivery;

import java.util.ArrayList;
import java.util.List;

public class Delivery {
    private final String missionId;
    private final DeliveryDrone drone;
    private final List<DeliveryObserver> observers = new ArrayList<>();
    private DeliveryState state = new PendingDispatch();
    private int progressStep = 0;

    public Delivery(String missionId, DeliveryDrone drone) { this.missionId = missionId; this.drone = drone; }
    public String getMissionId(){ return missionId; }
    public DeliveryDrone getDrone(){ return drone; }
    public String getStateName(){ return state.getState(); }
    public void attach(DeliveryObserver o){ observers.add(o); }
    private void notifyObservers(){ for (DeliveryObserver o : observers) o.deliveryStatusChanged(); }
    public void dispatchDrone(){ drone.acceptMission(); state = new ReadyForPickup(); notifyObservers(); }
    public void updateStatus(){ progressStep++; state = (progressStep == 1 ? new InTransit() : new Finished()); notifyObservers(); }
}

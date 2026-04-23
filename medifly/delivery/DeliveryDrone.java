package medifly.delivery;

public class DeliveryDrone {
    private final String droneId;
    private DroneStatus status = DroneStatus.AVAILABLE;
    public DeliveryDrone(String droneId){ this.droneId = droneId; }
    public void acceptMission(){ status = DroneStatus.BUSY; System.out.println("Drone " + droneId + " accepted mission."); }
    public void updateLocation(){ System.out.println("Drone " + droneId + " location updated."); }
    public void returnToBase(){ status = DroneStatus.RETURNING; System.out.println("Drone " + droneId + " returning to base."); }
    public DroneStatus getStatus(){ return status; }
}

package cloud.qasino.games.pattern.strategy;

public class MainFlow {
    public void trigger(String[] args) {
        Vehicle mersedes = new LuxuryVehile(new LuxuryDrive());
        mersedes.drive();

        Vehicle audi = new SportsVehicle(new LuxuryDrive());
        audi.drive();

        Vehicle bmw=new LuxuryVehile(new NormalDrive());
        bmw.drive();

        // Encapsulates algorithms for drive
        // Enhances flexibility and extensibility - dynamically switch algorithm
        // Promotes code reusability - use algorithms drive also for other classes
    }
}

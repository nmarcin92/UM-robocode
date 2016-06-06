import robocode.ScannedRobotEvent;

/**
 * @author Marcin Nowak
 */
public class Enemy {
    private double bearing, distance, energy, velocity, heading;
    private String name;
    private double x, y;

    public Enemy() {
        reset();
    }

    public boolean isPresent() {
        return !"".equals(name);
    }

    public void update(ScannedRobotEvent event, UMRobot robot) {
        bearing = normalizeBearing(event.getBearing());
        distance = event.getDistance();
        energy = event.getEnergy();
        velocity = event.getVelocity();
        name = event.getName();
        heading = event.getHeading();
        double absBearingDeg = (robot.getHeading() + event.getBearing());
        if (absBearingDeg < 0) {
            absBearingDeg += 360;
        }
        x = robot.getX() + Math.sin(Math.toRadians(absBearingDeg)) * event.getDistance();
        y = robot.getY() + Math.cos(Math.toRadians(absBearingDeg)) * event.getDistance();

    }

    public void reset() {
        bearing = 0;
        distance = 0;
        energy = 0;
        velocity = 0;
        name = "";
        x = 0;
        y = 0;
    }

    public double getBearing() {
        return bearing;
    }

    public double getDistance() {
        return distance;
    }

    public double getEnergy() {
        return energy;
    }

    public double getVelocity() {
        return velocity;
    }

    public String getName() {
        return name;
    }

    public double getHeading() {
        return heading;
    }

    private double normalizeBearing(double angle) {
        while (angle >  180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getFutureX(long when) {
        return x + Math.sin(Math.toRadians(getHeading())) * getVelocity() * when;
    }

    public double getFutureY(long when) {
        return y + Math.cos(Math.toRadians(getHeading())) * getVelocity() * when;
    }
}

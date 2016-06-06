import robocode.*;

import java.awt.geom.Point2D;

public class UMRobot extends AdvancedRobot {

    private byte scanDirection = 1;
    private byte moveDirection = 1;
    private Enemy enemy = new Enemy();
    private static final long MIN_WALL_DIST = 70;
    private int wallHandledCounter = 0;

    @Override
    public void run() {
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);

        addCustomEvent(new Condition("wall_near") {
            public boolean test() {
                return (
                        // we're too close to the left wall
                        (getX() <= MIN_WALL_DIST ||
                                getX() >= getBattleFieldWidth() - MIN_WALL_DIST ||
                                getY() <= MIN_WALL_DIST ||
                                getY() >= getBattleFieldHeight() - MIN_WALL_DIST)
                );
            }
        });

        enemy.reset();
        while (true) {
            turnRadarRight(360);
            execute();
        }

    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        if (!enemy.isPresent()  || event.getDistance() < enemy.getDistance() || event.getName().equals(enemy.getName())) {
            enemy.update(event, this);
        }
        setTurnRadarRight(getHeading() - getRadarHeading() + enemy.getBearing());

        doMove();

        double firePower = Math.min(500 / enemy.getDistance(), 3);
        double bulletSpeed = 20 - firePower * 3;
        long time = (long)(enemy.getDistance() / bulletSpeed);
        double futureX = enemy.getFutureX(time);
        double futureY = enemy.getFutureY(time);
        double absDeg = absoluteBearing(getX(), getY(), futureX, futureY);
        setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));

        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10 && enemy.getDistance() < 350) {


            setFire(Math.min(400 / enemy.getDistance(), 3));
        }
        scanDirection *= -1;
        setTurnRadarRight(360 * scanDirection);
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        if (event.getName().equals(enemy.getName())) {
            enemy.reset();
        }
    }

    double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2-x1;
        double yo = y2-y1;
        double hyp = Point2D.distance(x1, y1, x2, y2);
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) { // both pos: lower-Left
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actuall 360 - ang
        } else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
        }

        return bearing;
    }

    private double normalizeBearing(double angle) {
        while (angle >  180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    public void doMove() {

        setTurnRight(enemy.getBearing() + 90 - (10 * moveDirection));


        if (getTime() % 40 == 0) {
            moveDirection *= -1;
            setAhead(300 * moveDirection);
        }
    }

    @Override
    public void onCustomEvent(CustomEvent event) {
        if ("wall_near".equals(event.getCondition().getName())) {
            if (wallHandledCounter < 0) {
                wallHandledCounter = 40;
                moveDirection *= -1;
                setAhead(1000 * moveDirection);
            } else {
                wallHandledCounter--;
            }
        }

    }
}

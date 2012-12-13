import javaclient3.PlayerClient;
import javaclient3.structures.PlayerConstants;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;

public class ObstacleBot extends Thread {
    public static boolean running = true;
    public static final int QUANTITY = 4;
    public static final double SPEED = 0.25;
    public static final double MAX_DISTANCE = 1;
    PlayerClient robot = null;
    Position2DInterface pos2D = null;
    RangerInterface sonar = null;
    double speed = 0;
    int number;

    public ObstacleBot(int number) {
        super();
        try {
            robot = new PlayerClient("localhost", 6665);
            pos2D = robot.requestInterfacePosition2D(number,PlayerConstants.PLAYER_OPEN_MODE);
            sonar = robot.requestInterfaceRanger(number,PlayerConstants.PLAYER_OPEN_MODE);
        } catch (PlayerException e) {
            System.err.println("ObstacleBot: Cannot create interface number " + number);
            System.exit(1);
        }
        robot.runThreaded(-1,-1);
        this.number = number;
    }

    public void run() {
        if (number % 2 == 0) {
            pos2D.setSpeed(-SPEED,0);
        } else {
            pos2D.setSpeed(SPEED,0);
        }
        double[] sonarValues;
        while (running) {
            while (!sonar.isDataReady());
            sonarValues = sonar.getData().getRanges();
            if ((sonarValues[0] < MAX_DISTANCE) || (sonarValues[1] < MAX_DISTANCE) || (sonarValues[2] < MAX_DISTANCE)){
                pos2D.setSpeed(-SPEED,0);
            } else if ((sonarValues[3] < MAX_DISTANCE) || (sonarValues[4] < MAX_DISTANCE) || (sonarValues[5] < MAX_DISTANCE)) {
                pos2D.setSpeed(SPEED,0);
            }
            try {Thread.sleep(150);} catch (InterruptedException e) {}
        }
    }

    public static void main(String[] args) {
	ObstacleBot[] bots = new ObstacleBot[QUANTITY];
        for(int i=0; i<QUANTITY; i++) {
            bots[i] = new ObstacleBot(i+1);
        }
        for(int i=0; i<QUANTITY; i++) {
            bots[i].start();
        }
    }
}

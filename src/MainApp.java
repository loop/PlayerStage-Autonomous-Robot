import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerPose2d;

public class MainApp {

	private static Position2DInterface pos2D = null;
	private static RangerInterface sonar = null;
	private static PlayerClient robot = null;
	private static double[] sonarValues;
	private static double x;
	private static double y;

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("R04749");
		} else {
			connectToRobot();
			x = Double.parseDouble(args[0]);
			y = Double.parseDouble(args[1]);
			robotMovementToTarget(x, y);
		}
	}

	protected static void robotMovementToTarget(final double x, final double y) {
		PlayerPose2d goTo = new PlayerPose2d(x, y, 0);
		pos2D.setPosition(goTo, new PlayerPose2d(), 0);
		collectionThread(x, y);

	}

	private static void connectToRobot() {
		try {
			robot = new PlayerClient("localhost", 6665);
			pos2D = robot.requestInterfacePosition2D(0,
					PlayerConstants.PLAYER_OPEN_MODE);
			sonar = robot.requestInterfaceRanger(0,
					PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println("Weebob: Error connecting to Player!\n>>>"
					+ e.toString());
			System.exit(1);
		}
		robot.runThreaded(-1, -1);
	}

	private static void collectionThread(final double x, final double y) {
		Thread collection = new Thread() {

			public void run() {
				while (true) {
					while (!sonar.isDataReady())
						;
					sonarValues = sonar.getData().getRanges();
					if (isClose(sonarValues)) {
						switch (checkDirection(sonarValues)) {
						case 0:
							turnLeft(1, sonarValues);
							break;
						case 1:
							turnLeft(-1, sonarValues);
							break;
						case 2:
							turnRight(1);
							break;
						case 3:
							turnRight(-1);
							break;
						}
						robotMovementToTarget(x, y);
					}
					try {
						sleep(200);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		collection.start();
	}

	protected static int checkDirection(double[] sonarValues2) {
		if (sonarValues2[0] < sonarValues2[1]) {
			return 0;
		} else {
			return 1;
		}
	}

	protected static void turnRight(int i) {

	}

	protected static void turnLeft(int i, double[] sonarValues2) {
		if (i == 1) {
			pos2D.setSpeed(0, 0.5);
			try {
				Thread.sleep(3142);
			} catch (InterruptedException e) {

			}
		} else if (i == -1) {
			pos2D.setSpeed(0, -0.5);
			try {
				Thread.sleep(3142);
			} catch (InterruptedException e) {
			}
		}
		goForwardABit(sonarValues2);

	}

	private static void goForwardABit(double[] sonarValues2) {

		while (!(sonarValues2[6] > 7 || sonarValues2[7] > 7))
		{
			pos2D.setSpeed(1, 0);
		}

		robotMovementToTarget(x, y);

	}

	private static boolean isClose(double[] sonarValues2) {
		double distanceAvoid = 0.5;
		if (sonarValues2[0] < distanceAvoid || sonarValues2[1] < distanceAvoid) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
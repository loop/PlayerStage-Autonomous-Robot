import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerPose2d;

public class MainApp {

	double x, y;
	Position2DInterface pos2D = null;
	RangerInterface sonar = null;
	PlayerClient robot = null;
	PlayerPose2d pp2dTarget;

	public MainApp(String[] args) {
		if (args.length == 0) {
			System.out.println("R04749");
		}
		if (args.length == 2) {
			connectToRobot();
			x = Double.parseDouble(args[0]);
			y = Double.parseDouble(args[1]);

			System.out.println("X: " + x + " Y: " + y);

			moveRobotToTarget();
			targetChecker tcCheckTarget = new targetChecker(pos2D, true);
			AvoidanceThread atAvoidStuff = new AvoidanceThread(pos2D, sonar, x,
					y, true);
			tcCheckTarget.start();
			atAvoidStuff.start();
		}
	}

	public void connectToRobot() {
		try {
			robot = new PlayerClient("localhost", 6665);
			pos2D = robot.requestInterfacePosition2D(0,
					PlayerConstants.PLAYER_OPEN_MODE);
			sonar = robot.requestInterfaceRanger(0,
					PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println("Robot: Error connecting to Player!\n>>>"
					+ e.toString());
			System.exit(1);
		}
		robot.runThreaded(-1, -1);
	}

	public void moveRobotToTarget() {
		pp2dTarget = new PlayerPose2d(x, y, 0);
		pos2D.setPosition(pp2dTarget, new PlayerPose2d(), 1);
		System.out.println("Going to target.");
	}

	private class targetChecker extends Thread {

		private boolean isProgramRunning;
		private Position2DInterface pos2D;

		public targetChecker(Position2DInterface pos2D, boolean isProgramRunning) {
			super();
			this.pos2D = pos2D;
			this.isProgramRunning = isProgramRunning;
		}

		public boolean robotTargetChecker() {
			double currentX, currentY;
			if (pos2D.isDataReady()) {
				currentX = pos2D.getX();
				currentY = pos2D.getY();
				if ((currentX > x - 0.1) && (currentX < x + 0.1)
						&& (currentY > y - 0.1) && (currentY < y + 0.1)) {
					return true;
				}
			} else {
				return false;
			}
			return false;
		}

		@Override
		public void run() {
			while (isProgramRunning) {
				if (robotTargetChecker()) {
					System.out.println("Target reached. \nX: " + pos2D.getX()
							+ " Y: " + pos2D.getY());
					System.exit(0);
				}
				try {
					sleep(3000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private class AvoidanceThread extends Thread {
		private double[] sonarValues;
		private double distanceLimit = 0.5;
		private double sideDistanceLimit = distanceLimit - 0.3;
		private Position2DInterface pos2D;
		private RangerInterface sonar;
		double x, y;
		private boolean isProgramRunning;

		public AvoidanceThread(Position2DInterface pos2D,
				RangerInterface sonar, double x, double y,
				boolean isProgramRunning) {
			super();
			this.pos2D = pos2D;
			this.sonar = sonar;
			this.isProgramRunning = isProgramRunning;
			this.x = x;
			this.y = y;
		}

		@Override
		public void run() {
			while (isProgramRunning) {
				while (!sonar.isDataReady())
					;
				sonarValues = sonar.getData().getRanges();
				if (checkObstacleDistanceLimit(sonarValues)) {
					switch (robotTurnChecker(sonarValues)) {
					case 0:
						turnLeftOrRight(1);
						break;
					case 1:
						turnLeftOrRight(-1);
						break;
					case 2:
						turnLeftOrRight2(1);
						break;
					case 3:
						turnLeftOrRight2(-1);
						break;
					case 4:
						System.out.println("Error.");
					}
					moveRobotToTarget();
				}
				try {
					sleep(200);
				} catch (InterruptedException e) {
				}
			}
		}

		public void moveRobotToTarget() {
			pp2dTarget = new PlayerPose2d(x, y, 0);
			System.out.println("Moving to co ordinates...");
			pos2D.setPosition(pp2dTarget, new PlayerPose2d(), 1);
		}

		public boolean checkObstacleDistanceLimit(double[] sonarValuesalues) {
			if ((sonarValuesalues[0] < distanceLimit)
					|| (sonarValuesalues[1] < distanceLimit)
					|| (sonarValuesalues[2] < sideDistanceLimit)
					|| (sonarValuesalues[3] < sideDistanceLimit)
					|| (sonarValuesalues[6] < sideDistanceLimit)
					|| (sonarValuesalues[7] < sideDistanceLimit)) {
				return true;
			} else {
				return false;
			}
		}

		public int robotTurnChecker(double[] sonarValuesalues) {
			if ((sonarValuesalues[0] < distanceLimit)
					|| (sonarValuesalues[1] < distanceLimit)) {
				double frontLeftSensor = sonarValuesalues[1];
				double frontRightSensor = sonarValuesalues[0];
				if (frontLeftSensor > frontRightSensor) {
					return 0;
				} else {
					return 1;
				}
			} else if ((sonarValuesalues[6] < sideDistanceLimit)
					|| (sonarValuesalues[7] < sideDistanceLimit)) {
				return 2;
			} else if ((sonarValuesalues[2] < sideDistanceLimit)
					|| (sonarValuesalues[3] < sideDistanceLimit)) {
				return 3;
			} else
				return 4;
		}

		public void turnLeftOrRight(int direction) {
			System.out.println("turnLeftOrRight :" + direction);
			int sonarSensorNumber;
			if (direction == 1) {
				sonarSensorNumber = 6;
			} else {
				sonarSensorNumber = 2;
			}
			pos2D.setSpeed(0, 0.3 * direction);
			try {
				sleep(3142);
			} catch (InterruptedException e) {
			}
			pos2D.setSpeed(1, 0);
			while (true) {
				if (sonar.isDataReady()) {
					sonarValues = sonar.getData().getRanges();
				}
				if (isClear(sonarValues[sonarSensorNumber],
						sonarValues[sonarSensorNumber], sonarValues)) {
					break;
				}
			}
			pos2D.setSpeed(1, 0);
			try {
				sleep(800);
			} catch (InterruptedException e) {
			}
		}

		public void turnLeftOrRight2(int direction) {
			System.out.println("turnLeftOrRight2 :" + direction);
			pos2D.setSpeed(0, 0.3 * direction);
			try {
				sleep(3142);
			} catch (InterruptedException e) {
			}
			pos2D.setSpeed(1, 0);
			try {
				sleep(800);
			} catch (InterruptedException e) {
			}
		}

		public boolean isClear(double one, double two, double[] sonarValues) {
			if (((checkObstacleDistanceLimit(sonarValues)) || one > (distanceLimit + 1.5) && two > (distanceLimit + 1.5))) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static void main(String[] args) {
		new MainApp(args);
	}

}
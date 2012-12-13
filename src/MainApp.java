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
			initThread();
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
		System.out.println("Moving to co ordinates...");
		pos2D.setPosition(pp2dTarget, new PlayerPose2d(), 1);
	}

	public void initThread() {
		OdometryThread oThread = new OdometryThread(pos2D);
		oThread.setRunning(true);
		oThread.start();
		OAThread oaThread = new OAThread(pos2D, sonar, x, y);
		oaThread.setRunning(true);
		oaThread.start();
	}

	public void printFinal() {
		System.out.print("FINAL>>>\nX: " + pos2D.getX() + "\nY: "
				+ pos2D.getY());
	}

	// OBSTACLE AVOIDANCE THREAD
	public class OAThread extends Thread {
		private Position2DInterface pos2D;
		private RangerInterface sonar;
		private double[] sonarV;
		private double threshold = 0.5;
		private double sThreshold = threshold - 0.4;
		double x, y;
		private boolean running;

		public void setRunning(boolean running) {
			this.running = running;
		}

		public OAThread(Position2DInterface pos2D, RangerInterface sonar,
				double x, double y) {
			super();
			this.pos2D = pos2D;
			this.sonar = sonar;
			this.x = x;
			this.y = y;
		}

		@Override
		public void run() {
			while (running) {
				while (!sonar.isDataReady())
					;
				sonarV = sonar.getData().getRanges();
				if (isClose(sonarV)) {
					switch (checkDirection(sonarV)) {
					case 0:
						turnA(1);
						break;
					case 1:
						turnA(-1);
						break;
					case 2:
						turnB(1);
						break;
					case 3:
						turnB(-1);
						break;
					case 4:
						System.out.println("This shouldn't happen");
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

		public boolean isClose(double[] sonarValues) {
			if ((sonarValues[0] < threshold) || (sonarValues[1] < threshold)
					|| (sonarValues[2] < sThreshold)
					|| (sonarValues[3] < sThreshold)
					|| (sonarValues[6] < sThreshold)
					|| (sonarValues[7] < sThreshold)) {
				return true;
			} else {
				return false;
			}
		}

		public int checkDirection(double[] sonarValues) {
			if ((sonarValues[0] < threshold) || (sonarValues[1] < threshold)) {
				double fLeft = sonarValues[1];
				double fRight = sonarValues[0];
				if (fLeft > fRight) {
					return 0;
				} else {
					return 1;
				}
			} else if ((sonarValues[2] < sThreshold)
					|| (sonarValues[3] < sThreshold)) {
				return 3;
			} else if ((sonarValues[6] < sThreshold)
					|| (sonarValues[7] < sThreshold)) {
				return 2;
			} else
				return 4;
		}

		public void turnA(int direction) {
			System.out.println("TurnA :" + direction);
			int a;
			if (direction == 1) {
				a = 6;
			} else {
				a = 2;
			}
			pos2D.setSpeed(0, 0.5 * direction);
			try {
				sleep(3142);
			} catch (InterruptedException e) {
			}
			pos2D.setSpeed(1, 0);
			while (true) {
				if (sonar.isDataReady()) {
					sonarV = sonar.getData().getRanges();
				}
				if (isClear(sonarV[a], sonarV[a + 1], sonarV)) {
					break;
				}
			}
			pos2D.setSpeed(1, 0);
			try {
				sleep(500);
			} catch (InterruptedException e) {
			}
			// pos2D.setSpeed(0, -0.5 * direction); try { sleep(3142); } catch
			// (InterruptedException e) {}
		}

		public void turnB(int direction) {
			System.out.println("TurnB :" + direction);
			pos2D.setSpeed(0, 0.5 * direction);
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

		public boolean isClear(double one, double two, double[] sonarV) {
			if ((one > (threshold + 1) && two > (threshold + 1))
					|| (isClose(sonarV))) {
				System.out.println("Sensors clear:\n1. " + one + "\n2. " + two);
				return true;
			} else {
				return false;
			}
		}
	}

	// ODOMETRY THREAD
	public class OdometryThread extends Thread {

		private Position2DInterface pos2D;
		private boolean running;

		public void setRunning(boolean running) {
			this.running = running;
		}

		public OdometryThread(Position2DInterface pos2D) {
			super();
			this.pos2D = pos2D;
		}

		@Override
		public void run() {
			while (running) {
				if (checkGoal()) {
					System.out
							.println("Goal has been reached...\nClosing in 10 seconds");
					System.out.println("X: " + pos2D.getX());
					System.out.println("Y: " + pos2D.getY());
					System.exit(0);
				}
				try {
					sleep(6000);
				} catch (InterruptedException e) {
				}
			}
		}

		public boolean checkGoal() {
			double currX, currY;
			if (pos2D.isDataReady()) {
				currX = pos2D.getX();
				currY = pos2D.getY();
				if ((currX > x - 0.1) && (currX < x + 0.1) && (currY > y - 0.1)
						&& (currY < y + 0.1)) {
					return true;
				}
			} else {
				return false;
			}
			return false;
		}
	}

	public static void main(String[] args) {
		new MainApp(args);
	}

}
import java.text.DecimalFormat;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;

public class MainApp {

	private static Position2DInterface pos2D = null;
	private static RangerInterface sonar = null;
	private static PlayerClient robot = null;

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("R04749");
		} else if (args[0].equals("0") && Double.parseDouble(args[1]) > 0) {
			testThree(roundTwoDecimals(Double.parseDouble(args[1])));
		} else {
			testFour(roundTwoDecimals(Double.parseDouble(args[0])),
					roundTwoDecimals(Double.parseDouble(args[1])));
		}
	}

	protected static void testThree(final double y) {

		connectToRobot();
		Thread thirdTestThread = new Thread() {

			public void run() {

				while (true) {
					if (pos2D.getY() == y
							|| ((pos2D.getY() >= y - 0.1) && (pos2D.getY() <= y + 0.1))) {
						pos2D.setSpeed(0, 0);
						System.out.println(pos2D.getY());
					} else {
						pos2D.setSpeed(0.4, 0);
						
					}
					try {
						sleep(50);
					} catch (InterruptedException e) {
					}
				}
			}
		};

		if (pos2D.getYaw() == (Math.PI / 2)) {
			thirdTestThread.start();
		} else {
			test5(pos2D, 0, y);
		}
	}

	protected static void testFour(final double x, final double y) {
		boolean robotIsRunning = false;

		connectToRobot();

		robot.runThreaded(-1, -1);

		if (pos2D.getY() == y) {
			if (pos2D.getYaw() == 0) {
				if (pos2D.getX() > x) {
					pos2D.setSpeed(-0.5, 0);
				} else {
					pos2D.setSpeed(0.5, 0);
				}
				robotIsRunning = true;
			} else if (pos2D.getYaw() == -(Math.PI)) {
				if (pos2D.getX() > x) {
					pos2D.setSpeed(0.5, 0);
				} else {
					pos2D.setSpeed(-0.5, 0);
				}
				robotIsRunning = true;
			}
			while (robotIsRunning) {
				if (pos2D.getX() >= (x - 0.1) && pos2D.getX() <= (x + 0.1)) {
					pos2D.setSpeed(0, 0);
					break;
				}
			}
		} else if (pos2D.getX() == x) {
			if (pos2D.getYaw() == Math.PI / 2) {
				if (pos2D.getY() > y) {
					pos2D.setSpeed(-0.5, 0);
				} else {
					pos2D.setSpeed(0.5, 0);
				}
				robotIsRunning = true;
			} else if (pos2D.getYaw() == -(Math.PI / 2)) {
				if (pos2D.getY() > y) {
					pos2D.setSpeed(0.5, 0);
				} else {
					pos2D.setSpeed(-0.5, 0);
				}
				robotIsRunning = true;
			}
			while (robotIsRunning) {
				if (pos2D.getY() >= (y - 0.1) && pos2D.getY() <= (y + 0.1)) {
					pos2D.setSpeed(0, 0);
					break;
				}

			}
		} else {
			test5(pos2D, x, y);
		}
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

	private static void test5(final Position2DInterface pos2d2, final double x,
			final double y) {
		Thread thirdTestThread = new Thread() {

			public void run() {
				boolean robotIsRunning = true;

				while (robotIsRunning) {
					if (!yawIsTarget(pos2d2, x, y)) {
						pos2D.setSpeed(0, 0.05);
					} else if (yawIsTarget(pos2d2, x, y)) {
						robotIsRunning = false;
						goForward(pos2d2, x, y);
					}
				}
			}
		};

		thirdTestThread.start();
	}

	private static void goForward(final Position2DInterface pos2d2,
			final double x, final double y) {
		Thread targetThread = new Thread() {

			public void run() {
				boolean robotIsRunning = true;
				while (robotIsRunning) {
					System.out.println(roundTwoDecimals(pos2d2.getX()) + "-"
							+ roundTwoDecimals(pos2d2.getY()) + "Target is" + x
							+ "and" + y);
					if ((roundTwoDecimals(pos2D.getY()) >= (y - 0.1) && roundTwoDecimals(pos2D
							.getY()) <= (y + 0.1))
							&& (roundTwoDecimals(pos2D.getX()) >= (x - 0.1) && roundTwoDecimals(pos2D
									.getX()) <= (x + 0.1))) {
						pos2d2.setSpeed(0, 0);
						robotIsRunning = false;
					} else {
						if(checkDistance(pos2d2, x, y)){
							pos2d2.setSpeed(0.1, 0);
						} else {
							pos2d2.setSpeed(1, 0);
						}		
					}
					try {
						sleep(10);
					} catch (InterruptedException e) {
					}
				}
			}

			private boolean checkDistance(Position2DInterface pos2d2, double x,
					double y) {
				double currentX = pos2d2.getX();
				double currentY = pos2d2.getY();
				double distance = Math.sqrt((x-currentX)*(x-currentX) + (y-currentY)*(y-currentY));
				if(distance < 2){
					return true;
				} else {
				return false;
				}
			}
		};

		targetThread.start();
	}

	public static boolean yawIsTarget(Position2DInterface pos2D, double x,
			double y) {
		double angle = Math.atan2((y - pos2D.getY()), (x - pos2D.getX()));
		System.out.println(roundTwoDecimals(angle) + "-"
				+ roundTwoDecimals(pos2D.getYaw()));
		if (roundTwoDecimals(pos2D.getYaw()) == roundTwoDecimals(angle)) {
			return true;
		} else {
			return false;
		}
	}

	private static double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}
}
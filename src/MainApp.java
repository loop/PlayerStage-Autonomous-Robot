import java.text.DecimalFormat;

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

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("R04749");
		} else {
			test(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
		}
	}

	protected static void test(double x, double y) {
		connectToRobot();
		PlayerPose2d goTo = new PlayerPose2d(x, y, Math.PI);
		pos2D.setPosition(goTo, new PlayerPose2d(), 1);

	}

	private static void printPositions() {
		System.out.println(pos2D.getX() + " and " + pos2D.getY());
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

	private static double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}
}
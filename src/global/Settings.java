package global;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Settings {

	/*
	 * IMPORTANT:
	 *
	 * Anything that can be considered a constant should go in here.
	 */

	// server
	public static final String HOST_NAME = "localhost";
	public static final int PORT_NUMBER = 9001;

	// UI
	public static final int MOVE_TIMEOUT = 30;
	public static final int GRID_SIZE = 10;
	public static final int IMAGE_CELL_SIZE = 36;
	public static final int[] SHIP_SIZES = {5, 4, 3, 3, 2};
	public static final DateFormat SERVER_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	public static final int RES_X = 1024;
	public static final int RES_Y = 768;
	

}

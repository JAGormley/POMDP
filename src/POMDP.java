import java.util.Scanner;



// normalize, end states, sensor info (if col3&1 *.9, otherwise 1, OPPOSITE for non-col-3


public class POMDP {

	private static Scanner scanner;
	private static double[][] grid;

	public POMDP(int[] start){
		boolean stateGiven = false;
		grid = new double[3][4];
		if (start != null){
			grid[start[0]][start[1]] = 1.0;
			stateGiven = true;
		}

		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if ((i == 1 || i == 2) && j == 3){
					grid[i][j] = 0;
				}
				else if (!stateGiven) grid[i][j] = .111;
			}
		}
	}

	public static void main(String [] args)
	{
		scanner = new Scanner(System.in);
		String rawIn = null;
		System.out.print("argument format --> "
				+ "ACTION-SEQUENCE: \"a1,a2...a*\" \n"
				+ "OBSERVATION-SEQUENCE: \"o1,o2...o*\" \n"
				+ "START-STATE(optional): \"x,y\" \n"
				+ "eg> up,down 2,2 1,2 \n"
				+ "enter args>");
		rawIn = scanner.nextLine();
		String[] in = parseArguments(rawIn);
		int[] start = null;
		if (in.length == 3){
			start = new int[2];
			String[] sstate = in[2].split(",");
			start[0] = Integer.valueOf(sstate[1])-1;
			start[1] = Integer.valueOf(sstate[0])-1;
		}
		POMDP pom = new POMDP(start);

		printGrid();
		System.out.println("...");
		String[] actions = in[0].split(",");
		String[] obs = in[1].split(",");
		pom.move(actions, obs);

	}


	private void move(String[] actions, String[] obs) {
		for (int i = 0; i < actions.length; i++) {
			double[][] gridCopy = deepCopyDoubleMatrix(grid);
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 3; k++) {
					if ((j == 1 && k == 1)){} //skip pillar
					else updateCell(j, k, actions[i], obs[i], gridCopy);
				}
			}
			printGrid();
			System.out.println("...");
			normalize();
		}
	}

	private void updateCell(int cellX, int cellY, String action, String obs, double[][] gridCopy) {
		double origVal = gridCopy[cellY][cellX];

		double sumProb = 0;

		if (!obs.equals("end"))
			switch (action) {
			case "up":
				if (wallCheck(cellX, cellY, "up") && (wallCheck(cellX, cellY, "left"))){
					sumProb += origVal*0.9f;
				}
				else if (wallCheck(cellX, cellY, "up")&&wallCheck(cellX, cellY, "right")){}
				else if (wallCheck(cellX, cellY, "up"))
					sumProb += origVal*0.8f;

				if (!wallCheck(cellX, cellY, "down"))
					if (!(cellX==3&&cellY==2))
						sumProb += gridCopy[cellY-1][cellX]*0.8f;

				if(wallCheck(cellX, cellY, "left"))
					sumProb += origVal*0.1f;
				else sumProb += gridCopy[cellY][cellX-1]*0.1f;

				if(wallCheck(cellX, cellY, "right"))
					sumProb += origVal*0.1f;
				else sumProb += gridCopy[cellY][cellX+1]*0.1f;

				grid[cellY][cellX] = sumProb;
				break;

			case "down":
				if (wallCheck(cellX, cellY, "down") && (wallCheck(cellX, cellY, "left") || wallCheck(cellX, cellY, "right"))){
					sumProb += origVal*0.9f;
				}
				else if (wallCheck(cellX, cellY, "down"))
					sumProb += origVal*0.8f;

				if (!wallCheck(cellX, cellY, "up"))
					if (cellX == 3){}
					else sumProb += gridCopy[cellY+1][cellX]*0.8f;

				if(wallCheck(cellX, cellY, "left"))
					sumProb += origVal*0.1f;
				else sumProb += gridCopy[cellY][cellX-1]*0.1f;

				if(wallCheck(cellX, cellY, "right"))
					sumProb += origVal*0.1f;
				else sumProb += gridCopy[cellY][cellX+1]*0.1f;

				grid[cellY][cellX] = sumProb;
				break;

			case "left":
				if (wallCheck(cellX, cellY, "left") && (wallCheck(cellX, cellY, "down") || wallCheck(cellX, cellY, "up"))){
					sumProb += origVal*0.9f;
				}
				else if (wallCheck(cellX, cellY, "left"))
					sumProb += origVal*0.8f;

				if (wallCheck(cellX, cellY, "down"))
					sumProb += origVal*0.1f;
				else sumProb += gridCopy[cellY-1][cellX]*0.1f;

				if(wallCheck(cellX, cellY, "up"))
					sumProb += origVal*0.1f;
				else sumProb += gridCopy[cellY+1][cellX]*0.1f;

				if(!wallCheck(cellX, cellY, "right")){
					if ((cellX == 2 && cellY != 0)){}
					else sumProb += gridCopy[cellY][cellX+1]*0.8f;
				}

				grid[cellY][cellX] = sumProb;
				break;


			case "right":
				if (wallCheck(cellX, cellY, "right") && (wallCheck(cellX, cellY, "down") || wallCheck(cellX, cellY, "up"))){
					sumProb += origVal*0.9f;
				}
				else if (wallCheck(cellX, cellY, "right"))
					if (cellX == 3 && cellY != 0)
						sumProb += origVal*0.8f;

				if (wallCheck(cellX, cellY, "down"))
					sumProb += origVal*0.1f;
				else sumProb += gridCopy[cellY-1][cellX]*0.1f;

				if(wallCheck(cellX, cellY, "up"))
					sumProb += origVal*0.1f;
				else sumProb += gridCopy[cellY+1][cellX]*0.1f;

				if(!wallCheck(cellX, cellY, "left"))
					sumProb += gridCopy[cellY][cellX-1]*0.8f;

				grid[cellY][cellX] = sumProb;
				break;

			default:
				break;
			}

		if (cellX == 3)
			if (obs.equals("1"))
				grid[cellY][cellX] = grid[cellY][cellX]*0.9;
			else grid[cellY][cellX] = grid[cellY][cellX]*0.1;
		else if (obs.equals("1")){
			grid[cellY][cellX] = grid[cellY][cellX]*0.1;
		}
		else grid[cellY][cellX] = grid[cellY][cellX]*0.9;
	}


	private boolean wallCheck(int cellX, int cellY, String action) {
		switch (action) {
		case "up":
			if (cellY==2 || (cellX==1&&cellY==0))
				return true;
			break;
		case "down":
			if (cellY==0 || (cellX==1&&cellY==2))
				return true;
			break;
		case "left":
			if (cellX==0 || (cellX==2&&cellY==1))
				return true;
			break;
		case "right":
			if (cellX==3 || (cellX==0&&cellY==1))
				return true;
			break;

		default:
			break;
		}
		return false;
	}

	public static String[] parseArguments(String input){
		String trimIn = input.trim();
		String[] FTPArgs = trimIn.split("\\s+");
		return FTPArgs;
	}

	public static void printGrid(){
		//		System.out.print("- - - - - - - - - - - - - - - - - - - - - - - - - ");
		for (int j = 0; j < 3; j++) {
			System.out.println();
			for (int i = 0; i < 4; i++) {
				if ((i == 1 && j == 1)){
					System.out.print("|    "+"*******"+"    ");
				}
				else System.out.print("|    "+String.format("%.5f",grid[2-j][i])+"    ");
			}
			System.out.println("|");
			//			System.out.print("- - - - - - - - - - - - - - - - - - - - - - - - - ");
		}
	}
	
	private void normalize(){
		double total = getTotal();
		for (int j = 0; j < 4; j++) {
			for (int k = 0; k < 3; k++) {
				grid[k][j] /= total;
			}
		}
		
	}
	

	private double getTotal() {
		double total = 0;
		for (int j = 0; j < 4; j++) {
			for (int k = 0; k < 3; k++) {
				total += grid[k][j];
			}
		}
		return total;
	}

	/*
	 * code taken from: 
	 * http://stackoverflow.com/questions/9106131/how-to-clone-a-multidimensional-array-in-java
	 */
	public static double[][] deepCopyDoubleMatrix(double[][] input) {
		if (input == null)
			return null;
		double[][] result = new double[input.length][];
		for (int r = 0; r < input.length; r++) {
			result[r] = input[r].clone();
		}
		return result;
	}

}



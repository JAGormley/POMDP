import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class POMDP {

	private static Scanner scanner;
	private static double[][] grid;

	public POMDP(int[] start){
		boolean stateGiven = false;
		grid = new double[3][4];
		if (start != null){
			grid[start[0]][start[1]] = 1;
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
		while (true){
			scanner = new Scanner(System.in);
			String rawIn = null;
			System.out.print("argument format --> "
					+ "ACTION-SEQUENCE: \"a1,a2...a*\" "
					+ "OBSERVATION-SEQUENCE: \"o1,o2...o*\" "
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

			String[] actions = in[0].split(",");
			String[] obs = in[1].split(",");
			pom.move(actions, obs);
		}
	}


	private void move(String[] actions, String[] obs) {
		for (int i = 0; i < actions.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				for (int k = 0; k < grid[i].length; k++) {
					if ((j == 2 && k == 2)){} //skip pillar
					else updateCell(j, k, actions[i], obs[i]);
				}
			}
			printGrid();
			System.out.println("...");
		}
	}

	private void updateCell(int cellX, int cellY, String action, String obs) {
		double origVal = grid[cellX][cellY];
		// multiply original value by prob of ending up there given eg left move, 
		// add probs of ending up in other states (mult by their belief numbers: WHY?)
		// multiply everything by obs, which is .9 if 3rdCol+1 OR other+2 (.1 otherwise)
		switch (action) {
		case "up":
			// against a the ceiling
			if (cellY == 3 || (cellX == 2 && cellY == 1) || terminal(cellX, cellY)){
				if (cellX == 1 && cellY == 3)
					grid[cellX][cellY] = .9*origVal + 0.8*grid[cellX+1][cellY];
				else 
					grid[cellX][cellY] = .8*origVal + 0.1*grid[cellX+1][cellY] + + 0.1*grid[cellX-1][cellY];
			}	
			else {

			}
			break;
		case "down":
			// against a floor 
			if (cellY == 1 || (cellX == 2 && cellY == 3) || terminal(cellX, cellY)){

			}
			else {

			}
			break;
		case "left":
			// against a left wall 
			if (cellX == 1 || (cellX == 3 && cellY == 2) || terminal(cellX, cellY)){

			}
			else {

			}
			break;
			// against a right wall
		case "right":
			if (cellX == 4 || (cellX == 1 && cellY == 2) || terminal(cellX, cellY)){

			}
			else {

			}
			break;
		default:
			break;
		}
		//		else {
		//			System.out.println("END");
		//  left: .8*currentVal
		//		}
	}

	private boolean terminal(int cellX, int cellY) {
		return (cellX == 3 && (cellY == 2 || cellY == 3));
	}

	public static String[] parseArguments(String input){
		String trimIn = input.trim();
		String[] FTPArgs = trimIn.split("\\s+");
		return FTPArgs;
	}

	public static void printGrid(){
		//		System.out.print("- - - - - - - - - - - - - - - - - - - - - - - - - ");
		for (int i = 0; i < grid.length; i++) {
			System.out.println();
			for (int j = 0; j < grid[i].length; j++) {
				if ((2-i == 1 && j == 1)){
					System.out.print("|    "+"*****"+"    ");
				}
				else System.out.print("|    "+grid[2-i][j]+"    ");
			}
			System.out.println("|");
			//			System.out.print("- - - - - - - - - - - - - - - - - - - - - - - - - ");
		}
	}

}

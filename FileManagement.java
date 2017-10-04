/**
 * Program Name: ProgramC: heuristic depth-first-search (hDFS)
 * @author Jack Hysell
 * @date 7/16/2017
 * @description	This program performs a heuristic depth-first-search (hDFS) on a 
 * directed weighted graph that is imported as an adjacency matrix in .txt format. 
 * After the operation has been performed, results detailing the grpahs traversal 
 * order, vertices start and finish times, edge classification, topological sort, 
 * and strongly connected components are printed to and output file also in .txt 
 * format containing the original files name appended with "_out.txt."
 * 
 * This program contains four classes:
 * 	FileManagement: Allows user to select a .txt file to import, then formats that 
 * 					file and sends it off to the DFS class.
 *  
 * 	DFS: 			Performs the heuristic depth-first-search on the graph provided.
 * 
 * 	Vertex:			Allows us to create a unique object for each vertex in the graph
 * 					containing all of the vital information needed to perform hDFS.
 * 
 * Edge:			Allows us to create a unique object for each edge in the graph 
 * 					containing vital information needed to get the edge classification.	
 * 
 * @sources
 * 	This is the one source I used to get some inspiration for how I may go about designing
 * 	this program. In the end, I'm happy to say that the  code here was actually not copy 
 * 	pasted from anywhere. 
 * 	https://www.dropbox.com/s/3dzueevdm87yso2/Graph.java									
 */

package hDFS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FileManagement extends Application {
	
	public static File inFile;
	public static String[][] stringMatrix;
	public static String outFileName;
	public static PrintWriter output;
	public static OutputStreamWriter out;
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FileManagement.fileChooser();
		FileManagement.readFile();
		FileManagement.createMatrix();
		DFS.setGraph(stringMatrix);
		DFS.driver();	
	}	
	
	public static void createMatrix() throws IOException {
		
		// Get selected file to read from, from FileReader
		FileReader fileReader = new FileReader(inFile);
		FileReader fileReader2 = new FileReader(inFile);

		// Use buffered reader to read one line at a time
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		// Use fileReader2 to get the size for the Matrix
		int size = getSize(fileReader2);
		stringMatrix = new String[size][size];
		
		System.out.println("Matrix Size: " + size);
		
		int row = 0;
		String line;
		// Use while loop to iterate through each line
		while ((line = bufferedReader.readLine()) != null) {
			
			String[] itemsInRow = line.trim().split("\\s+");

				// FILL finalMatrix
				for (int i = 0; i < itemsInRow.length; i++) {
					stringMatrix[row][i] = itemsInRow[i];
				}
				row++;
		}
		bufferedReader.close();
	}
	
	public static void readFile() throws IOException {
		
		System.out.println("File Exists: " + inFile.exists());
		
		// Get file name, rename file, write file with new name
		String inFileName = inFile.getName();
		outFileName = inFileName.replaceAll(".txt", "_out.txt");
		output = new java.io.PrintWriter(outFileName);

		System.out.println("File Name: " + inFileName + "\nOut File Name: " + outFileName.toString());
		
		output.close();
	}
	
	public static void fileChooser() throws IOException {
		// Create the window, grid pane, and scene
		Window stage = null;

		// Create file chooser and set the directory
		FileChooser fileChooser = new FileChooser();
		File defaultDirectory = new File(System.getProperty("user.dir"));
		fileChooser.setTitle("Choose a text file: ");
		fileChooser.setInitialDirectory(defaultDirectory);
		inFile = fileChooser.showOpenDialog(stage);
	}

	public static int getSize(FileReader fileReader) throws IOException {

		BufferedReader bufferedReader = new BufferedReader(fileReader);

		String line = bufferedReader.readLine();
		String[] itemsInRow = line.trim().split("\\s+");
		int size = itemsInRow.length;

		return size;
	}
		
	public static void printStringMatrix(String[][] incomingMatrix, String print) {

		String[][] matrix = incomingMatrix;

		System.out.println("\n" + print);
		for (int r = 0; r < matrix.length; r++) {
			for (int c = 0; c < matrix[r].length; c++) {
				System.out.print(matrix[r][c] + "  ");
			}
			System.out.println();
		}
		System.out.println();
	}
}

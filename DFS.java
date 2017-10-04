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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

public class DFS {

	private static int count = 1;
	private static Vertex[] vertexList;
	private static Edge[] edgeList;
	private static String[][] graph;
	private static ArrayList<String> vertex;
	private static ArrayList<Integer> edges, finishTimes;
	private static LinkedList<Integer> children, edgeType;
	private static Stack<Integer> edgeStack = new Stack<Integer>();
	private static Stack<Integer> topoStack = new Stack<Integer>();
	private static PrintWriter output; 
	private static StringBuffer travelOrder;

	/**
	 * driver
	 * public static void driver()
	 * This method is the driver of the program. It provides the order of operations
	 * to execute the hDFS program as assigned. 
	 * @throws IOException
	 */
	public static void driver() throws IOException {

		output = new java.io.PrintWriter(FileManagement.outFileName);
		travelOrder = new StringBuffer();

		//Build Graph
		buildVertexList();

		// Execute hDFS
		hDFS(0, count++, travelOrder);
		System.out.println("\n");
		output.println(travelOrder);

		// After hDFS is complete, printout the start and finish times!
		finishTimes();
		System.out.println();
		output.println();

		// Print the edge types
		edgeClassification();
		output.println();

		// Print topological sort
		topologicalSort();
		output.println();

		// Get Transpose
		transpose(graph);

		// Get SCC
		stronglyConnectedComponents();

		output.close();
	}

	/**
	 * buildVertexList
	 * public static void buildVertexList()
	 * Constructs an array of Vertex objects
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void buildVertexList() throws IOException {

		vertices(graph);
		edges = new ArrayList<Integer>();
		edgeType = new LinkedList<Integer>();
		children = new LinkedList<Integer>();
		vertexList = new Vertex[graph.length - 1];
		edgeList = new Edge[graph.length - 1];

		// Build Vertex List Array
		for (int i = 1; i < graph.length; i++) {
			// Temporary string array of current row
			ArrayList<String> temp = new ArrayList<String>();

			// Temporary string for current row
			for (int t = 0; t < graph.length; t++) {
				temp.add(graph[i][t]);
			}

			// Obtains the edges for each vertex
			for (int j = 1; j < graph.length; j++) {
				if (!graph[i][j].equals(".")) {
					edges.add(Integer.parseInt(graph[i][j]));
					// Leave this unsorted to use later for getting edge types
					edgeType.add(Integer.parseInt(graph[i][j]));
				}
				// Sorts edges in ascending order
				Collections.sort(edges);
			}

			// Get index value of vertex's children
			for (int k = 0; k < edges.size(); k++) {
				// Get each edge in edges
				int e = edges.get(k);
				// Convert that edge to a string
				String imposter = Integer.toString(e);
				// Determine index that imposter belongs to and add to children
				int indexOf = temp.indexOf(imposter) - 1;
				// Resets that edge to prevent duplicate entry
				temp.set(indexOf + 1, ".");

				children.add(indexOf);
			}

			Vertex vertexObject = new Vertex(vertex.get(i - 1), (LinkedList<Integer>) children.clone(), false, 0, 0, null);
			vertexList[i - 1] = vertexObject;

			Edge edgeObject = new Edge(vertex.get(i - 1), (LinkedList<Integer>) edgeType.clone());
			edgeList[i - 1] = edgeObject;

			// Clear All fields to start a new list with each iteration
			temp.clear();
			edges.clear();
			children.clear();
			edgeType.clear();
		}
	}

	/**
	 * hDFS
	 * public static void hDFS(int index, int c, StringBuffer t)
	 * Recursively solves the heuristic depth first search on a weighted 
	 * directed graph. The start node will always be 0 or A, and when given
	 * a choice between two or more paths will always take the shortest first.
	 * In the event that two paths are of the same length, this program will 
	 * favor the path also by alphabetical order. 
	 * @param index
	 * @param c
	 * @param t
	 * @throws IOException
	 */
	public static void hDFS(int index, int c, StringBuffer t) throws IOException {
		// If vertex has not been visited - Mark visited & set start time = true
		if (!vertexList[index].getVisited()) {
			vertexList[index].setVisited(true);
			vertexList[index].setStart(c);
			System.out.print(vertexList[index].getNodeName() + " ");
			travelOrder.append(vertexList[index].getNodeName() + " ");

			// If vertex has children, visit them!
			if (!vertexList[index].getChildren().isEmpty()) {
				// While there are children to visit
				while (vertexList[index].getChildren().iterator().hasNext()) {
					// If that child hasn't been visited, visit it!
					if (!vertexList[vertexList[index].getChildren().getFirst()].getVisited()) {
						edgeStack.push(index);
						vertexList[vertexList[index].getChildren().getFirst()].setParent(vertexList[index].getNodeName());
						hDFS(vertexList[index].getChildren().removeFirst(), count++, travelOrder);
					} 
					else {
						vertexList[index].getChildren().pop();
					}		
				}
				vertexList[index].setFinish(count++);
			}

			// If vertex has no children, recurse!
			else {
				vertexList[index].setFinish(count++);		
			}
		}
	}

	/**
	 * finishTimes
	 * public static void finishTimes()
	 * This method will print a node and its start and finish times. This method
	 * assumes that hDFS has already been run on the array of Vertices. 
	 */
	public static void finishTimes() {
		// Print Finish Times
		output.println();
		for (int i = 0; i < vertex.size(); i++) {
			System.out.println(vertexList[i].getNodeName() + " " + vertexList[i].getStart() + " " + vertexList[i].getFinish());
			output.println(vertexList[i].getNodeName() + " " + vertexList[i].getStart() + " " + vertexList[i].getFinish());
		}
	}

	/**
	 * topologicalSort
	 * public static void topologicalSort()
	 * Determines the topological sort of the graph once hDFS has been
	 * ran on the array of Vertices.
	 */
	public static void topologicalSort() {
		
		finishTimes = new ArrayList<Integer>();
		ArrayList<String> topoSort = new ArrayList<String>();
		System.out.println();

		for (int i = 0; i < vertexList.length; i++) {
			finishTimes.add(vertexList[i].getFinish());
		}

		Collections.sort(finishTimes);
		Collections.reverse(finishTimes);

		for (int i = 0; i < finishTimes.size(); i++) {
			int edge = finishTimes.get(i);

			for (int j = 0; j < vertexList.length; j++) {
				if (edge == vertexList[j].getFinish()) {
					topoSort.add(vertexList[j].getNodeName());
				}
			}
		}
		for (int k = 0; k < topoSort.size(); k++) {
			String V = topoSort.get(k);
			System.out.print(V + " ");
			output.print(V + " ");
		}
	}

	/**
	 * edgeClassification
	 * public static void edgeClassification()
	 * This method will use the Array of vertices to determine the edge
	 * classification for each edge in the graph provided hDFS has been 
	 * performed.
	 */
	public static void edgeClassification() {

		for (int u = 1; u < graph.length; u++) {
			if (!edgeList[u - 1].getChildren().isEmpty()) {
				for (int v = 1; v < graph.length; v++) {
					if (!graph[u][v].equals(".")) {
						System.out.print(vertexList[u - 1].getNodeName() + vertexList[v - 1].getNodeName());
						output.print(vertexList[u - 1].getNodeName() + vertexList[v - 1].getNodeName());
					} else if (graph[u][v].equals(".")) {
						continue;
					}
					// Cross Edge
					if ((vertexList[u - 1].getStart() > vertexList[v - 1].getStart())) {
						if ((vertexList[u - 1].getFinish() > vertexList[v - 1].getFinish())) {
							System.out.print(" C\n");
							output.println(" C\n");
						} 
						// Back Edge
						else {
							System.out.print(" B\n");
							output.println(" B\n");
						}	
					}
					// Forward Edge
					else {
						if (vertexList[v - 1].getParent() == vertexList[u - 1].getNodeName()) {
							System.out.print(" T\n");
							output.println(" T\n");	
						} 
						// Tree Edge
						else {
							System.out.print(" F\n");
							output.println(" F\n");
						}
					}
				}
			}
		}
	}

	/**
	 * transpose
	 * public static void transpose(String[][] g)
	 * In order to determine the strongly connected components, we must first
	 * retrieve the transpose of the original graph. This method performs that 
	 * task. 
	 * @param g
	 * 	represents: String[][] graph
	 * @throws IOException
	 */
	public static void transpose(String[][] g) throws IOException {

		graph = new String[g.length][g.length];
		graph = g;
		String[][] transpose = new String[graph.length][graph.length];

		for(int r = 0; r < graph.length; r++) {
			for(int c = 0; c < graph.length; c++) {
				transpose[r][c] = graph[c][r];
			}
		}

		for(int t = 0; t < finishTimes.size(); t++) {
			for (int f = 0; f < vertexList.length; f++) {
				if(finishTimes.get(t).equals(vertexList[f].getFinish())) {
					topoStack.add(f);
					break;
				}
			}	
		}	
		graph = transpose;
		System.out.println("\n");		

		buildVertexList();			
	}

	/**
	 * stronglyConnectedComponents
	 * public static void stronglyConnectedComponents()
	 * Using the transpose of the original graph, this method re-runs the 
	 * hDFS to determine what the graphs strongly connected components are. 
	 * @throws IOException
	 */
	public static void stronglyConnectedComponents() throws IOException {

		Collections.reverse(topoStack);
		output.println();
		while (!topoStack.isEmpty()) {
			if (vertexList[topoStack.peek()].getVisited() != true) {
				travelOrder = new StringBuffer();
				hDFS(topoStack.pop(), count++, travelOrder);
				System.out.println();
				output.println(travelOrder);
			}
			else  {
				topoStack.pop();
			}
		}
	}

	/**
	 * vertices
	 * public static void vertices(String[][] g)
	 * Retrieves the vertices of a 2D graph and stores them in an 
	 * ArrayList. 
	 * @param g
	 */
	public static void vertices(String[][] g) {

		vertex = new ArrayList<String>();
		for (int y = 1; y < graph.length; y++) {
			vertex.add(graph[0][y]);
		}
	}

	//Getter & Setter for graph
	public static String[][] getGraph() {
		return graph;
	}

	public static void setGraph(String[][] graph) {
		DFS.graph = graph;
	}
}

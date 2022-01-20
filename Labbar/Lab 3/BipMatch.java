import java.io.*;
import java.util.LinkedList;

public class BipMatch 
{
	Kattio io = new Kattio(System.in, System.out);
	int e;
	int nodes, s, t, edges, x, y, capacity, totFlow;
	Edge edgeX, edgeY;
	Node[] graph;

	// Step 3 of lab 3
	BipMatch() throws IOException 
	{
		// Reads the bipartite graph input and creates a flowgraph
		readWriteBipartiteGraph();
		totFlow = edmondsKarp();
		writeTotFlow();

		io.close();
	}

	// Reads input and creates a flowgraph at the same time.
	void readWriteBipartiteGraph() 
	{
		// Läs antal hörn och kanter
		x = io.getInt();
		y = io.getInt();
		e = io.getInt();

		nodes = (x + y + 2);
		edges = (e + x + y);
		s = 0;
		t = nodes - 1;
		capacity = 1;

		graph = new Node[nodes];

		// Initialize each node, creating a linked list inside each cell
		for (int i = 0; i < nodes; i++) 
		{
			graph[i] = new Node();
		}

		// Koppla S till X
		for (int i = 1; i < x + 1; i++) 
		{
			edgeX = new Edge(s, i, 0, capacity);
			edgeY = new Edge(i, s, 0, 0);

			edgeX.setReverse(edgeY);
			edgeY.setReverse(edgeX);

			graph[s].edges.add(edgeX);
			graph[i].edges.add(edgeY);
		}

		// Läs in kanterna
		for (int i = 0; i < e; i++) 
		{
			int a = io.getInt();
			int b = io.getInt();

			edgeX = new Edge(a, b, 0, capacity);
			edgeY = new Edge(b, a, 0, 0);

			edgeX.setReverse(edgeY);
			edgeY.setReverse(edgeX);

			graph[a].edges.add(edgeX);
			graph[b].edges.add(edgeY);
		}

		// Koppla Y till T
		for (int i = x + 1; i < t; i++) 
		{
			edgeX = new Edge(i, t, 0, capacity);
			edgeY = new Edge(t, i, 0, 0);

			edgeX.setReverse(edgeY);
			edgeY.setReverse(edgeX);

			graph[i].edges.add(edgeX);
			graph[t].edges.add(edgeY);

		}

	}

	int edmondsKarp() 
	{
		int maxFlow = 0;

		while (true) 
		{
			// Stores edge used to get to node i
			Edge[] toEdge = new Edge[nodes];

			LinkedList<Node> queue = new LinkedList<>();
			queue.add(graph[s]);

			// BFS körs. Medans vi har en stig från s till t
			while (!queue.isEmpty()) 
			{
				Node currentNode = queue.remove(0);

				for (Edge e : currentNode.edges) 
				{
					// If edge hasn't been visited, doesn't point to source and can send flow
					if (toEdge[e.y] == null && e.y != s && e.capacity > e.flow) 
					{
						toEdge[e.y] = e;
						queue.add(graph[e.y]);
					}
				}
			}

			// If there's no path from s to t. Break then return maxFlow
			if (toEdge[t] == null) 
			{
				break;
			}

			int bottleNeck = Integer.MAX_VALUE;

			// Find bottleNeck value in the path
			for (Edge e = toEdge[t]; e != null; e = toEdge[e.x]) 
			{
				bottleNeck = Math.min(bottleNeck, e.capacity - e.flow);
			}

			// Add flow values, flow comes in reverseved
			for (Edge e = toEdge[t]; e != null; e = toEdge[e.x]) 
			{
				e.flow += bottleNeck;
				e.reverse.flow -= bottleNeck;
			}
			maxFlow += bottleNeck;
		}

		return maxFlow;
	}

	void writeTotFlow() 
	{
		io.println(x + " " + y);
		io.println(totFlow);

		for (int i = 1; i < x + 1; i++) 
		{
			for (Edge e : graph[i].edges) 
			{
				if (e.flow > 0 && e.x != s && e.y != t) 
				{
					io.println((e.x) + " " + (e.y));
				}
			}
		}
	}

	public static void main(String[] args) throws IOException 
	{
		new BipMatch();
	}

}

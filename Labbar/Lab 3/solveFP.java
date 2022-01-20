import java.util.ArrayList;

public class SolveFP 
{
  Kattio io;
  int nodes, s, t, edges, x, y, capacity, totFlow; 
  Edge edgeX, edgeY;
  Node[] graph;

  // Här görs allt
  SolveFP()
  {
    // Brings the input
    io = new Kattio(System.in, System.out);
    
    // Reads the input and builds the graph 
    readInput();
    totFlow = edmondsKarp();
    writeTotFlow();
    io.close();
  }

  void readInput()
  {
    nodes = io.getInt();
		s = io.getInt() - 1;
		t = io.getInt() - 1;
		edges = io.getInt();
    
    graph = new Node[nodes];

    // Initialize each node, creating a linked list inside each cell
    for (int i = 0; i < nodes; i++)
    {
      graph[i] = new Node();
    }

    for(int i = 0; i < edges; i++)
    {
      x = io.getInt() - 1;
      y = io.getInt() - 1;
		  capacity = io.getInt();

      edgeX = new Edge(x, y, 0, capacity);
      edgeY = new Edge(y, x, 0, 0);

      edgeX.setReverse(edgeY);
      edgeY.setReverse(edgeX);

      graph[x].edges.add(edgeX);
      graph[y].edges.add(edgeY);
    } 
  }

  int edmondsKarp()
  {
    int maxFlow = 0;
    
    while (true)
    {
      // Stores edge used to get to node i 
      Edge[] toEdge = new Edge[nodes];
      
      ArrayList<Node> queue = new ArrayList<>();
      queue.add(graph[s]);


      //BFS körs. Medans vi har en stig från s till t 
      while(!queue.isEmpty())
      {
        Node currentNode = queue.remove(0);

        for (Edge e : currentNode.edges) 
        {
          // If edge hasn't been visited, doesn't point to source and can send flow
          if(toEdge[e.y] == null && e.y != s && e.capacity > e.flow)
          {
            toEdge[e.y] = e;
            queue.add(graph[e.y]);
          }
        }
      }

      // If there's no path from s to t. Break then return maxFlow
      if(toEdge[t] == null)
      {
        break;
      }

      int bottleNeck = Integer.MAX_VALUE;

      // Find bottleNeck value in the path
      for(Edge e = toEdge[t]; e != null; e = toEdge[e.x])
      {
        bottleNeck = Math.min(bottleNeck, e.capacity - e.flow);
      }
      
      // Add flow values, flow comes in reverseved
      for(Edge e = toEdge[t]; e != null; e = toEdge[e.x])
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

    io.println(nodes);
    io.println((s+1) + " " + (t+1) + " " + totFlow);

    StringBuilder sb = new StringBuilder();

    int count = 0;
    for (int i = 0; i < nodes; i++) 
    {
      for (Edge e : graph[i].edges) 
      {
        if(e.flow > 0)
        {
          count++;
          sb.append((i+1) + " " + (e.y+1) + " " + e.flow);
          sb.append("\n");
        }
      }
    }

    io.println(count);
    io.println(sb.toString());
  }

  
  public static void main(String[] args) 
  {
    new SolveFP();
  }

}
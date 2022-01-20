/*
* Adeel Hussain & Rakin Ali
* Generated: 2021-10-24, Updated: 2021-10-25
* Input: 
* Dependencies: Kattio.java
* Reference: https://kth.instructure.com/courses/27078/assignments/167028
*/

import java.io.IOException;

public class ReduceToFlow
{
	Kattio io;
  int x;
  int y;
  int e;
  int capacity;
	int graphX[];
	int graphY[];

	// Här görs allt -> Konstruktör
	ReduceToFlow() throws IOException
	{
    io = new Kattio(System.in, System.out);
	  readBipartiteGraph();
    writeFlowGraph();
    readWriteMaxFlowSolution();
    io.close();
	}

  // Reads a file 
	void readBipartiteGraph() 
	{
		// Läs antal hörn och kanter
		x = io.getInt(); y = io.getInt(); e = io.getInt();

		graphX = new int[e];
		graphY = new int[e];

		// Läs in kanterna
		for (int i = 0; i < e; i++) 
		{
			int a = io.getInt();
			int b = io.getInt();
			graphX[i] = a+1;
			graphY[i] = b+1;
		}
	}


  //Här skriver vi till ut till en fil. 
  void writeFlowGraph()
  {
    int nodes = (x+y+2), edges = (e+x+y), s = 1, t = nodes;
		capacity = 1; 
    
    io.println(nodes);
    io.println(s + " " + t);
    io.println(edges);
		//From S to nodes in X
		for(int i = 2; i < x+2; i++) 
		{
			io.println(s + " " + i + " " + capacity);
		}

		//From nodes in X to nodes in Y
    for(int i = 0; i < e; ++i)
    {
			io.println(graphX[i] + " " + graphY[i] + " " + capacity);
    }

		//From nodes in Y to node T
		for (int i = x+2; i < t; i++) 
		{
			io.println(i + " " + t + " " + capacity);
		}
		
    io.flush();    
  }

	void readWriteMaxFlowSolution() 
  {
		// Läs in antal hörn, kanter, källa, sänka, och totalt flöde
		// (Antal hörn, källa och sänka borde vara samma som vi i grafen vi
		// skickade iväg)
		int v = io.getInt();
		int s = io.getInt();
		int t = io.getInt();
		int totflow = io.getInt();
		int e = io.getInt();

		io.println(x + " " + y);
		io.println(totflow);

		for (int i = 0; i < e; ++i) 
		{
			// Get the flows 
			int a = io.getInt();
			int b = io.getInt();
			int f = io.getInt();
    
      // If start and end points aren't included. Print them but minus 1 
			if(a != s && t != b)
			{
				io.println((a-1) + " " + (b-1));
			}
		}
	}

	public static void main(String[] args) throws Exception
	{
		new ReduceToFlow();
	}

}
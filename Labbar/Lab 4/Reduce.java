/*
	Reduktion från Graf-färgning från till Rollbesttäning     
	1) Samma person kan spela flera roller men samma roll kan innehas av en person
		-> Samma färg till olika noder men samma nod kan enast innhas av en färg

	2)Inga monologer
		-> Alla noder måste ha en kant, inga kanter som börjar och slutar på samma nod, dvs öglor
	
	3) Varje roll förekommer i minst scen 
		-> Alla noder ska ha en kant
	
	4) P1 och P2 ska få minst en roll och inte spela mot varandra
		-> Minst två noder ska vara med och de ska inte kopplas till varandra -> Låt de ha samma färg 
	
	--> Indata-Format
	Noder = Roler
	Edges = Scener
	Färger = Skådespelare

*/ 

public class Reduce
{
	// Global variables 
	static int v, e, colours, actors, roles, scenes; 
 	static Kattio io = new Kattio(System.in, System.out);
	public static void main(String[] args) 
	{
		// Get the in-data
		v = io.getInt();					// Number of vertices (roles)
		e = io.getInt(); 					// Number of edges (scenes)
		colours = io.getInt(); 		// Number of colours (actors)

		/* If nodes less than or equal to total colors => Always Yes - instance -> Minimal rolecrew problem */ 
		if(v <= colours)
		{
			io.println("3\n2\n3");
			io.println("1 1\n1 2\n1 3");
			io.println("2 1 3\n2 2 3");
		}
		else
		{
			/*
			* Minimum required production to solve roleproblem is:
			* 3 roles
			* 2 scenes
			* 3 actors
			*/

			// Convert to coloring problem
			roles = v + 2;									//Minimum number of roles needed is 3, minimum v is 1
			scenes = e + v + 1;							//We connect role 2 with all other original roles(hence + v), and role 1 to connect with 1 of the original roles (hence + 1)
			actors = colours + 2;						//Minimum number of actors needed is 3, minimum colors is 1
			io.println(roles + "\n" + scenes + "\n" + actors); // First row
			
			// All original actors can play all original roles (except for actor 1 and 2)
			for(int i = 0; i < v; i++)
			{
				io.print(colours + " ");
				for(int j = 3; j <= actors; j++)
				{
					io.print(j + " ");
				}
				io.println("");
			}

			// Insert last two actors to last two roles
			io.println("1 1\n1 2");

			// Have scenes with all roles together/connected with last role (scene with all actors connected/togeter with actor 2, except actor 1)
			for(int i = 1; i < roles-1; i++)
			{
				io.println(2 + " " + i + " " + roles);
			}

			//Actor 1 plays in the same scene as role 1
			io.println("2 " + (roles-1) + " 1");
			
			//Read remaining scenes from original production and print them out
			int a, b;
			for(int i = 0; i < e; i++)
			{
				a = io.getInt();
				b = io.getInt();
				io.println(2 + " " + a + " " + b);
			}
		}	
		io.close();
	}
}
import java.util.ArrayList;

public class CastingHeuristic 
{

	static Kattio io = new Kattio(System.in, System.out);
	static int roles, scenes, actors;

	static ArrayList<Integer>[] rolesToActors; 		// Roles. and the actors that play them
	static ArrayList<Integer>[] scenesToRoles; 		// Scenes.
	static ArrayList<Integer>[] actorsArray; 			// Actors AND THEIR ROLES

	@SuppressWarnings("unchecked")

	/* Method that reads input */
	static void readInput() {
		int actorsToRead, rolesToRead, actor, role;

		/* Get the in-data */
		roles = io.getInt();
		scenes = io.getInt();
		actors = io.getInt();

		/* Create array of Array list */
		rolesToActors = new ArrayList[roles + 1];					// +1 because of 0-indexing
		scenesToRoles = new ArrayList[scenes + 1]; 				// +1 because of 0-indexing
		actorsArray = new ArrayList[actors + roles + 1]; 	// +1 because of 0-indexing

		/* Read the roles for each actor */
		for (int i = 1; i <= roles; i++) {

			// How many Actors playes this role?
			actorsToRead = io.getInt();
			rolesToActors[i] = new ArrayList<Integer>();

			// Giving roll 1..n to all the actors
			for (int j = 0; j < actorsToRead; j++) 
			{
				actor = io.getInt();
				rolesToActors[i].add(actor);
			}
		}

		/* Read the scenes for each role */
		for (int i = 1; i <= scenes; i++) 
		{
			// How many roles in the scene
			rolesToRead = io.getInt();
			
			// Putting the roles to the scenes
			scenesToRoles[i] = new ArrayList<Integer>();
			
			for (int j = 0; j < rolesToRead; j++) 
			{
				role = io.getInt();
				scenesToRoles[i].add(role);
			}
		}
	}

	/* Check if roles are not in the same scene, True if they are not in the same scene */
	static boolean rolesSeparate(int role1, int role2) 
	{
		for (int i = 1; i < scenesToRoles.length; i++) 
		{
			// Does the scene have role 1 and role2? If yes then false
			if ((scenesToRoles[i]).contains(role1) && (scenesToRoles[i]).contains(role2)) 
			{
				return false;
			}
		}
		return true;
	}
	

	/**
	* Function to check if the actor can play the role so it doesn't end up in a scene with itself 
	* also check if the role is not in the same scene for the other diva incase actor is 1 or 2 
	*/
	static boolean actorsSeperate(int actor, int role) {
		if (actor == 1 || actor == 2) 
		{
			/* Check for Diva 1 (actor 1) */
			for (int i = 0; i < actorsArray[1].size(); i++) 
			{
				if (!rolesSeparate(actorsArray[1].get(i), role)) 
				{
					return false;
				}
			}
			
			/* Check for Diva 2 (actor 2) */
			for (int i = 0; i < actorsArray[2].size(); i++) 
			{
				if (!rolesSeparate(actorsArray[2].get(i), role)) 
				{
					return false;
				}
			}
		} 
		else 
		{
			/* Check for chosen actor */
			for (int i = 0; i < actorsArray[actor].size(); i++) 
			{
				if (!rolesSeparate(actorsArray[actor].get(i), role)) 
				{
					return false;
				}
			}
		}
		return true;
	}

	static void solver() 
	{
    /*
    * Save all possible diva roles in a list then use this to find a solution
    */
		
		ArrayList<Integer> diva1 = new ArrayList<Integer>();
		ArrayList<Integer> diva2 = new ArrayList<Integer>();

    /*
    * Creates an ArrayList, the list contains the roles the actor will play
    * in the final solution
    */
		for (int i = 0; i < actorsArray.length; i++) {
			actorsArray[i] = new ArrayList<Integer>();
		}

    /*
    * First check all the roles to find possible roles to assign to the divas (Actor 1 & Actor 2)
    * and then add them to the diva list  
    */
		for (int i = 1; i < rolesToActors.length; i++) {
			if ((rolesToActors[i]).contains(1)) {
				diva1.add(i);
			}
			if ((rolesToActors[i]).contains(2)) {
				diva2.add(i);
			}
		}

		/**
		 * Check if the divas are in the same scene (Possible No-instance)
		 * if they are not in the same scene then add them to the actors array
		 * then clear the rolesToActors for the role when a role is taken
		 */
		for (int i = 0; i < diva1.size(); i++) 
		{
			for (int j = 0; j < diva2.size(); j++) 
			{
				// Assign roles to the divas
				if (rolesSeparate(diva1.get(i), diva2.get(j))) 
				{
          // This is a solution
					actorsArray[1].add(diva1.get(i));
					actorsArray[2].add(diva2.get(j));

          // Clear the array so no other actor can play the same role
					rolesToActors[diva1.get(i)].clear();
					rolesToActors[diva2.get(j)].clear();
					break;
				}
			}
			/* Exit for-loop */
			if (actorsArray[1].size() != 0) 
			{
				break;
			}
		}

		/* Check all remaining actors to find first best role solution */
		for (int i = 1; i < rolesToActors.length; i++) 
		{
      // if RolesToActor = size = 0 -> Role taken
			for (int j = 0; j < rolesToActors[i].size(); j++) 
			{
				int currentActor = rolesToActors[i].get(j);

				/* Get the first best solution */
				if (actorsSeperate(currentActor, i)) 
				{
					actorsArray[currentActor].add(i);
					rolesToActors[i].clear();
				}
			}
		}

		/* Assign super actors to leftout roles */
		int superActor = actors + 1;
		for (int i = 1; i <= roles; i++) 
		{
			if (rolesToActors[i].size() != 0) 
			{
				actorsArray[superActor++].add(i);
			}
		}
	}

	static void printSolve() 
	{
		// Counts how many actors got roles
		int counter = 0;
		for (int i = 1; i < actorsArray.length; i++) {
			if ((actorsArray[i].size() != 0)) {
				counter++;
			}
		}
		
		io.println(counter);
		
		for (int i = 1; i < actorsArray.length; i++) 
		{
			if (actorsArray[i].size() != 0) 
			{
				io.print(i + " " + actorsArray[i].size());
				for (int j = 0; j < actorsArray[i].size(); j++) 
				{
					io.print(" " + actorsArray[i].get(j));
				}
				io.println();
			}
		}

    io.flush();
	}

	public static void main(String[] args) 
	{
		readInput();
		solver();
		printSolve();
	}

}

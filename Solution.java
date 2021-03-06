import java.util.Random;
/**
 * This object is used to encapsulate the solution
 * to a vehicle routing problem determined by the
 * Artificial Bee Colony algorithm.
 *
 * Contains utility methods that compute various
 * parameters associated with the ABC algorithm
 * (like the fitness value).
 *
 * @author  Ajinkya Dhaigude
 * @author  Sameer Raghuram
 */
 
class Solution implements Cloneable, Comparable<Solution>{

    // shared variables
	private Node route[];	// Solution path eg {0->1->2->3->0->4->5->6->0->7->8}
	private int totalNodes;	// Total nodes
	private double fitness = 0.0;
    private int TRIAL_LIMIT = 20;
    private int trial = 0;
    public int id;

	/**
	 * Empty constructor
	 */
	public Solution(){}

	/**
	 * Constructor to initialize shared variables.
	 *
	 * @param  allNodes
	 * @param  totVehicles
	 * @param  id
	 */
	public Solution(Node allNodes[], int totVehicles, int id){

		this.id = id;
		totalNodes = allNodes.length;
		route = new Node[totalNodes + totVehicles];

		// Initialize nodes in the route
		for(int i=0; i<route.length; i++){
			if(i < totalNodes)
				route[i] = allNodes[i];
			else
				route[i] = allNodes[0]; 	// depot
		}
	}

	/**
	 * Compute the fitness value of the route.
	 *
	 * @return double	route fitness
     */
	public double computeFitness(){

		double distance = computeDistance();
		return 1/distance;
	}

	/**
	 * Computes the total distance covered by all the
	 * vehicles for the current route.
	 *
	 * @return	double	route distance
     */
	public double altComputeDistance(){
		double distance = 0;

		for(int i=0; i<route.length-1; i++){

			distance += getDistance(route[i], route[i+1]);
		}
		return distance;
	}

	/**
	 * Computes the total distance covered by all the
	 * vehicles for the current route.
	 *
	 * @return	double	route distance
     */
	public double computeDistance(){
		double distance = 0;

		for(int i=0; i<route.length-1; i++){
			double nextDistance = getDistance(route[i], route[i+1]);
			if(nextDistance == 0){
				distance  = Double.POSITIVE_INFINITY;
				break;
			}
			distance += nextDistance;
		}
		return distance;
	}

	/**
	 * Computes the euclidean distance between two nodes.
	 * @param n1	Node 	First Node
	 * @param n2	Node 	Second Node
     * @return		double	distance between nodes
     */
	public double getDistance(Node n1, Node n2){
		int yDiff = (n2.y - n1.y);
		int xDiff = (n2.x - n1.x);
		return Math.sqrt((yDiff * yDiff) + (xDiff * xDiff));
	}

	/**
	 * Setter for fitness value
	 *
	 * @param fitness	fitness to set
     */
	public void setFitness(double fitness){
		this.fitness = fitness;
	}

	/**
	 * Getter for fitness
	 *
	 * @return	double 	fitness value of solution
     */
	public double getFitness(){
		return fitness;
	}

	/**
	 * Getter for route array
	 * @return	Node[]	Route computed in solution
     */
	public Node[] getRoute(){
		return this.route;
	}


	/**
	 * Set the route to a given route
	 *
	 * @param route
     */
	public void setRoute(Node[] route){
		if(route[0] == null){
			System.out.println("route is null during copy or deep copy");
		}
		this.route = route;
	}

	/**
	 * Setter for the trial value of the solution
	 * @param trial
     */
    public void setTrial(int trial){
		this.trial = trial;
    }

	/**
	 * Increment the trial by a given amount
	 * @param num
     */
	public void incTrial(int num){
		this.trial += num;
    }

	/**
	 * Getter for the trial value of the solution
	 * @return	int		Number of times the solution has been explored
     */
    public int getTrial(){
		return this.trial;
    }

	/**
	 * Checks if the trial number exceeds the maximum number of
	 * trials allowed for a solution.
	 * @return	boolean 	true if the solution is exhausted
     */
	public boolean isExhausted(){
		return this.trial>this.TRIAL_LIMIT;
	}

	/**
	 * Swaps the location of two nodes in a route.
	 *
	 * @param idx1
	 * @param idx2
     */
	public void swap(int idx1, int idx2){
		Node temp = route[idx1];
		route[idx1] = route[idx2];
		route[idx2] = temp;
	}

	/**
	 * Randomizes the order of the nodes in the
	 * route to obtain a random solution.
	 *
	 * @param rand	Random	pseudorandom-generator
     */
	void genRandomSolution(Random rand){

		for(int i=1; i< route.length - 1; i++){
			int idx2 = rand.nextInt(route.length - 2) + 1;
			swap(i, idx2);
		}
		//this.fitness = computeFitness();
	}

	/**
	 * Performs a local search on the current solution
	 * Computes the fitness as a result.
	 *
	 * @param rand	Random	Pseeudorandom generator.
	 * @returns 	double	Fitness value of solution
     */
	public double exploitSolution(Random rand){

		double oldFitness = computeFitness();
		int idx1 = rand.nextInt(route.length - 2) + 1;
		int idx2 = rand.nextInt(route.length - 2) + 1;
		swap(idx1, idx2);
		double newFitness = computeFitness();
		if(oldFitness > newFitness) {
			// Increment the number of trials to indicate exhaustion of
			// a food source
			incTrial(1);
			swap(idx1, idx2); //revert
			newFitness = oldFitness;		//revert to old fitness
		}
		// reset the number of trials of solution to indicate improvement
		if(!(oldFitness == newFitness)){
			setTrial(0);
		}
		setFitness(newFitness);

		return this.fitness;
	}


	/**
	 * Utility to compare different Solution instances
	 * in our program. Solutions are ordered according to
	 * their fitness value
	 *
	 * @param other Solution	Solution being compared to
	 * @return 		int			Natural ordering
     */
	@Override
	public int compareTo(Solution other) {
		// This soltion is better
		if(this.fitness > other.fitness){
			return -1;
		}
		// The other solution is better
		else if(this.fitness < other.fitness){
			return 1;
		}
		// The solutions are identical
		else{
			return 0;
		}
	}

	/**
	 * Returns a string representation of the routes
	 * of all the vehicles as described by the solution.
	 *
	 * @return	String	Solution String repr
     */
	public String toString(){
		double distance = computeDistance();

		if(distance != Double.POSITIVE_INFINITY){
			String toReturn1 = "Fitness: "+distance+"\nRoute:";
			String toReturn2="";
			int route_no = 0;
			int vehicle_no = 1;
			for(Node n:route){
				if(n.isDepot()) {
					if(route_no != 0)
						toReturn2 += n.toString() + "\n";
					if(route_no != route.length - 1) {
						toReturn2 += "\n Vehicle " + vehicle_no + ": " + n.toString();
						vehicle_no++;
					}
					route_no++;
				}
				else {
					toReturn2 += n.toString();
					route_no++;
				}

			}
			return  toReturn1 + toReturn2;
		}
		else{
			distance = altComputeDistance();
			String toReturn1 = "Fitness: "+distance+"\nRoute:";
			String toReturn2="";
			int route_no = 0;
			int vehicle_no = 1;
			for(Node n:route){

				if(n.isDepot()) {

					if(route_no != 0)
						toReturn2 += n.toString() + "\n";

					if(route_no != route.length - 1) {
						toReturn2 += "\n Vehicle " + vehicle_no + ": " + n.toString();
						vehicle_no++;
					}
					route_no++;
				}
				else {
					toReturn2 += n.toString();
					route_no++;
				}

			}
			return  toReturn1 + toReturn2;
		}
	}
	
	/**
	 * Implement method to clone object
	 */
	public Object clone()
	{
		try{
			Solution soln = (Solution) super.clone();
			soln.copy(this);
			return soln;
		}
		catch(CloneNotSupportedException e){
			throw new RuntimeException("Bad code");
		}
	}
	
	/**
	 * Method to deep copy shared variables.
	 *
	 * @param  soln  Solution instance
	 */
	public Solution copy(Solution soln){
		this.setFitness(soln.getFitness());
		this.setRoute(soln.getRoute());
		this.setTrial(soln.getTrial());
		this.id = soln.id;
		this.totalNodes = soln.totalNodes;
		return this;
	}
}
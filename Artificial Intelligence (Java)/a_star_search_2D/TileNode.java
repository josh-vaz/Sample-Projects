import java.lang.Math;

public class TileNode {
	public TileNode parent;
	public int location[];
	public int g;
	public int h;
	public int f;
	
	public TileNode(int[] location, TileNode parent){
		this.location = location;
		this.parent = parent;
		g = h = f = 0;				//Functions default to 0
	}

	public void calcFunctions(int[] goal, int pastG) {
		g = pastG + 1;															//Move forward one space from previous position
		h = Math.abs(location[0] - goal[0]) + Math.abs(location[1] - goal[1]);	//Manhattan Distance from goal
		f = g + h;
	}
	
	public boolean equals(TileNode n){
		if(location[0] == n.location[0] && location[1] == n.location[1]) return true;	//Compares location only
		return false;
	}
	
}

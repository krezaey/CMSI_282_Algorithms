package pathfinder.informed;

import java.util.*;

/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, A* search.
 */
public class Pathfinder {

    /**
     * Given a MazeProblem, which specifies the actions and transitions available in
     * the search, returns a solution to the problem as a sequence of actions that
     * leads from the initial to a goal state.
     * 
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return An ArrayList of Strings representing actions that lead from the
     *         initial to the goal state, of the format: ["R", "R", "L", ...]
     */
    public static ArrayList<String> solve(MazeProblem problem) {
        Set<MazeState> key = new HashSet<MazeState>();
        key.add(problem.KEY_STATE);
        ArrayList<String> keySolution = Pathfinder.starSearch(problem, problem.INITIAL_STATE, key);
        ArrayList<String> goalSolution = Pathfinder.starSearch(problem, problem.KEY_STATE, problem.GOAL_STATES);
        if (keySolution == null || goalSolution == null) { return null; }
        keySolution.addAll(goalSolution);
        return keySolution;
    }

    /**
     * Helper method to perform the A* star search.
     * 
     * @param MazeProblem         problem
     * @param Pathfinder          pf
     * @param MazeState           initial
     * @param Set<SearchTreeNode> goal
     * @return ArrayList<String> solution
     */

    private static ArrayList<String> starSearch(MazeProblem problem, MazeState initial,
            Set<MazeState> goal) {
        HashSet<MazeState> visited = new HashSet<MazeState>();
        PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<SearchTreeNode>();
        ArrayList<String> solution = new ArrayList<String>();


        frontier.add( new SearchTreeNode(initial, null, null, 0));

        while (!frontier.isEmpty()) {
            SearchTreeNode current = frontier.poll();
            
            if (goal.contains(current.state)) {
                while (current.parent != null) {
                    solution.add(0, current.action);
                    current = current.parent;
                }
                return solution;
            } 
            
            if (!visited.contains(current.state)) {
                visited.add(current.state);
                for (Map.Entry<String, MazeState> action : problem.getTransitions(current.state).entrySet()) {
                    SearchTreeNode child = new SearchTreeNode(action.getValue(), action.getKey(), current, 0);
                    child.cost += (current.cost + problem.getCost(child.state) + getFutureCost(child, goal) - getFutureCost(current, goal));
                    if (!visited.contains(child.state)) { frontier.add(child); 
                }
            }              
        }
        
    }
        return null;
    }
    

    
    /**
     * Calculates the Manhattan Distance.
     * 
     * @param MazeState first
     * @param MazeState second
     * @return int distance
     */
    private static int ManhattanDistance(MazeState first, MazeState second) {
        return Math.abs(first.col - second.col) + Math.abs(first.row - second.row);
    }

    /**
     * Heuristic that uses Manhattan Distance to take into account the predicted
     * cost.
     * 
     * @param SearchTree node
     * @param MazeState destination
     * @return int cost
     */
    private static int getFutureCost(SearchTreeNode node, Set<MazeState> destination) {
        int smallestDistance = Integer.MAX_VALUE;
        for (MazeState goal : destination) {
            smallestDistance = Math.min(smallestDistance, ManhattanDistance(node.state, goal));
        }
        return smallestDistance;
    }
}

/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree. [!] NOTE: Feel free to change this however you see fit to adapt your
 * solution for A* (including any fields, changes to constructor, additional
 * methods)
 */
class SearchTreeNode implements Comparable<SearchTreeNode> {

    MazeProblem problem;
    MazeState state;
    String action;
    SearchTreeNode parent;
    int cost;

    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     * 
     * @param state  The MazeState (row, col) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     * @param cost   The cost at the current node
     */
    SearchTreeNode(MazeState state, String action, SearchTreeNode parent, int cost) {
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.cost = cost;
    }
    
    /**
     * Compares the cost of the passed in SearchTreeNode with the current node.
     * Indicates whether the cost of the current node is equal, greater than or less
     * than the node that it is being compared to.
     * 
     * @param SearchTreeNode other
     * @return int 0, 1, -1 if equal / greater than / less than
     */
    public int compareTo(SearchTreeNode other) {
        return this.cost - other.cost;
    }
}

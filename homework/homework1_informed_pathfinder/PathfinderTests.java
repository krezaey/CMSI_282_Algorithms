package pathfinder.informed;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

/**
 * Unit tests for Maze Pathfinder. Tests include completeness and
 * optimality.
 */
public class PathfinderTests {
    
    @Test
    public void testPathfinder_t0() {
        String[] maze = {
            "XXXXXXX",
            "XI...KX",
            "X.....X",
            "X.X.XGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        // result will be a 2-tuple (isSolution, cost) where
        // - isSolution = 0 if it is not, 1 if it is
        // - cost = numerical cost of proposed solution
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]); // Test that result is a solution
        assertEquals(6, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t1() {
        String[] maze = {
            "XXXXXXX",
            "XI....X",
            "X.MMM.X",
            "X.XKXGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(14, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t2() {
        String[] maze = {
            "XXXXXXX",
            "XI.G..X",
            "X.MMMGX",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(10, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t3() {
        String[] maze = {
            "XXXXXXX",
            "XI.G..X",
            "X.MXMGX",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution); // Ensure that Pathfinder knows when there's no solution
    }
    
    //@Test
    public void testPathfinder_t4() {
        String[] maze = {
                "XXXXXXXXXXXXXXXXXX",
                "XGX.K....I.......X",
                "X.XXXXXXXXXX.XXXXX",
                "X.......X........X",
                "X.XXXXX.X..XXXXX.X",
                "X.....X.X..X.....X",
                "XXXXXXX.XXXX.XXXXX",
                "X.......X........X",
                "X.....X.XXXXXXX..X",
                "X.....X..........X",
                "XXXXXXXXXXXXXXXXXX"
            };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(54, result[1]); // Ensure that the solution is optimal
    }
    //@Test
    public void testPathfinder_t6() {
        String[] maze = {
                "XXXXXXXXXXXXXXXXXX",
                "XGX.K....I.......X",
                "X.XXXXXXXXXX.XXXXX",
                "X.......X........X",
                "X.XXXXX.X..XXXXX.X",
                "X.....X.X..X.....X",
                "XXXXXXX.XXXX.XXXXX",
                "X.......X........X",
                "X.....X.XXXXXXX..X",
                "XG....X..........X",
                "XXXXXXXXXXXXXXXXXX"
            };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(50, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t7() {
        String[] maze = {
                "XXXXXXXXXXXXXXXXXX",
                "XGX.K....I.......X",
                "XXXXXXXXXXXX.XXXXX",
                "X.......X........X",
                "X.XXXXX.X..XXXXX.X",
                "X.....X.X..X.....X",
                "XXXXXXX.XX.X.XXXXX",
                "XM......X........X",
                "XMM...X.XXXXXXX..X",
                "XGMMMMX..........X",
                "XXXXXXXXXXXXXXXXXX"
            };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(50, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t8() {
        String[] maze = {
                "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
                "XGX.K....I.........................................X",
                "XXXXXXXXXXXX.XXXXXXXXX.............................X",
                "XG......X.........XXXX.............................X",
                "X.XXXXX.X..XXXXX...................................X",
                "X.....X.X..X..................X....................X",
                "XXXXXXX.XXXXXXXXXX.................................X",
                "X.......X......G.X.................................X",
                "X.....X.XXXXXXX..X.................................X",
                "X....GX..........X................................GX",
                "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
            };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(59, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t9() {
        String[] maze = {
                "XXXXXXXXXXXXXXXXXX",
                "XGX.K....I.......X",
                "XXXXXXXXXXXX.XXXXX",
                "X.......X........X",
                "X.XXXXX.X..XXXXX.X",
                "X.....X.X..X.....X",
                "XXXXXXX.XX.X.XXXXX",
                "X.......X........X",
                "X.....X.XXXXXXX..X",
                "XG....X..........X",
                "XXXXXXXXXXXXXXXXXX"
            };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(46, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t10() {
        String[] maze = {
            "XXXXXXX",
            "XI....X",
            "X.MMMXX",
            "X.XKXGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution); // Ensure that Pathfinder knows when there's no solution
        
    }    
    //@Test
    public void testPathfinder_t11() {
        String[] maze = {
                "XXXXXXXXXXXXXXXXXX",
                "XGX......I.......X",
                "XXXXXXXXX.XXXXXXXX",
                "X.......X..X.....X",
                "X.XXXXX.X..X..K..X",
                "X.....X.X..X.....X",
                "XXXXXXX.XX.XXXXXXX",
                "X.......X........X",
                "X.....X.XXXXXXX..X",
                "XG....X..........X",
                "XXXXXXXXXXXXXXXXXX"
            };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution); // Ensure that Pathfinder knows when there's no solution 
    }
    
    //@Test
    public void testPathfinder_t12() {
        String[] maze = {
                "XXXXXXXXXXXXXXXXXX",
                "XGX......I.......X",
                "XXXXXXXXX.XXXXXXXX",
                "X.......X........X",
                "X.XXXXX.X.....K..X",
                "X.....X.X........X",
                "XXXXXXX.XX..XXXXXX",
                "XXXXX...X........X",
                "X...X.X.XXXXXXX..X",
                "XG..X.X..........X",
                "XXXXXXXXXXXXXXXXXX"
            };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution); // Ensure that Pathfinder knows when there's no solution 
    }
}
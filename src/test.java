public class test {

	public static void main(String[] args) throws Exception {
/*
		// Quest 1
		logipix puzzle0 = new logipix("test.txt");
		logipix puzzle1 = new logipix("LogiX.txt");
		logipix puzzle2 = new logipix("TeaCup.txt");
		logipix puzzle3 = new logipix("Man.txt");
		logipix puzzle4 = new logipix("test2.txt");

		// Ques 3
		System.out.println("The List of broken lines of the cell (0,0) in puzzle 0: ");
		logipix.printb(puzzle0.setOfBrokenLines(new int[] { 0, 0 }));
		System.out.println("");

		// Quest 2
		System.out.println("Puzzle Logix :");
		System.out.println(puzzle1);

		// Quest 6
		// result of combinations and exclusions:
		System.out.println("Combinations and exclusions over puzzle LogiX :");
		puzzle1.combinationAndExclusion(true); // means preview = true : we want the method to show the grid when finished
		System.out.println("Combinations and exclusions over puzzle TeaCup :");
		puzzle2.combinationAndExclusion(true);
		System.out.println("Combinations and exclusions over puzzle Man :");
		puzzle3.combinationAndExclusion(true);
		System.out.println("Combinations and exclusions over puzzle Vieux poste :");
		puzzle4.combinationAndExclusion(true);
		
		
		long start = 0;
		// Ques 4 and 5 & 7 : note that we need combination and exclusion to be called on the puzzle before calling optimisedSolve
		System.out.println("A draw of the solution :");
		start = System.currentTimeMillis();
		puzzle1.optimisedSolve();
		System.out.println("Solved in : " + (System.currentTimeMillis()-start) + "ms using the optimized solver");
		puzzle1 = new logipix("LogiX.txt");
		start = System.currentTimeMillis();
		puzzle1.solve();
		System.out.println("Solved in : " + (System.currentTimeMillis()-start) + "ms using the normal backtracking solver");
		System.out.println("Puzzle's state after solving :"); // note that 0 means unmarked 1 means maybe marked and 2
																// means marked
		System.out.println(puzzle1);

		// for the rest of puzzles :
		System.out.println("A draw of the solution :");
		start = System.currentTimeMillis();
		puzzle2.optimisedSolve();
		System.out.println("Solved in : " + (System.currentTimeMillis()-start) + "ms using the optimized solver");
		puzzle2 = new logipix("TeaCup.txt");
		start = System.currentTimeMillis();
		puzzle2.solve();
		System.out.println("Solved in : " + (System.currentTimeMillis()-start) + "ms using the normal backtracking solver");
		System.out.println("Puzzle's state after solving :"); // note that 0 means unmarked 1 means maybe marked and 2
																// means marked
		System.out.println(puzzle2);

		

		System.out.println("A draw of the solution :");
		start = System.currentTimeMillis();
		puzzle3.optimisedSolve();
		System.out.println("Solved in : " + (System.currentTimeMillis()-start) + "ms using the optimized solver");
		puzzle3 = new logipix("Man.txt");
		start = System.currentTimeMillis();
		puzzle3.solve();
		System.out.println("Solved in : " + (System.currentTimeMillis()-start) + "ms using the normal backtracking solver");
		System.out.println("Puzzle's state after solving :"); // note that 0 means unmarked 1 means maybe marked and 2
																// means marked
		System.out.println(puzzle3);
		
		*/
		
		//System.out.println("A draw of the solution :");
		//puzzle4.optimisedSolve();
		logipix puzzle4 = new logipix("test2.txt");
		puzzle4.solve();
		System.out.println("Puzzle's state after solving :"); // note that 0 means unmarked 1 means maybe marked and 2
																// means marked
		System.out.println(puzzle4);
		
		
	}
}

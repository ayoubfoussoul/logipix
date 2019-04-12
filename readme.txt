Tests are done in test.java:

Used methods and how to use them :

- logipix puzzle0 = new logipix("test.txt"); 
=> to initialize a logipix from a text file given in arguments

- logipix.printb(puzzle0.setOfBrokenLines(new int[] { 0, 0 })); 
=> Method printb that prints a LinkedList<LinkedList<int[]>> : this will print all the broken-lines going from the cell (0,0) of the puzzle 'puzzle0'

- System.out.println(puzzle0); 
=> prints the puzzle every cell is like [cell|state] (state=0,1 or 2 see description in the top of logipix.java)

- puzzle0.combinationAndExclusion(true); 
=> does combinationAndExclusion on "puzzle0" and draws a preview of the solution when finished if the argument is true, and in both cases true/false it fills a hashMap of useful broken-lines (those that are not blocked after exclusions) of each cell which is an attribute of puzzle0 and which will be used in quest 7 (so must call this method on a puzzle before calling optimalSolve() otherwise the hashMap of useful broken-lines : this.brokenLines will be empty!!)

puzzle0.optimisedSolve();
=> solves using only useful brokenLines (means left brokenLines after exclusion) and draws the solution when finished
(note that drawing a solution is different from printing it see description in the top of logipix.java)

puzzle0.solve();
=> solves using normal backtracking on all possible brokenLines

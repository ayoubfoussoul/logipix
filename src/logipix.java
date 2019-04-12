import java.io.*;
import java.util.*;

/* - in order to facilitate the code and to note use dynamic objects I decided to use only LinkedLists, HashMaps/sets and normal arrays 
 * to implement the solver
 * - the program memory complexity is after optimizations of quest 6 an O(size(cell_clue_brokenLines)) which is the biggest
 * thing in memory means O(width * height * 3^max(clues values))
 * - the program time complexity in quest 5 is O(3^sum(val clues)) !! where it is some thing near O(3^max(clues values)*height^2*width^2)
 * in quest 7 !
 * - the program is optimized to store the maximum of what it can store since memory is not a big deal in this case :
 * (O(width * height * 3^max(clues values)) isn't a lot in normal puzzles)
 * - sometimes injections between {0,.., height}x{0,....,width} -> N are used to store clues/cells having to indexes in HashMaps
 * using only one Integer key (euclidian division)
 * 
 * - Explication of the program :
 * 
 * Quest 1 & 2: 
 * 
 *  * we read from the textfile and fill this.puzzle naturaly, the priority queue this.clues with a comparator giving priority to clues with less value 
 *  and which will be needed in backtracking and finally we initialize width and height and this.state which indicates the state of every cell (0 : not marks 
 *  , 1 : maybe marked and 2 marked)
 *  * we that set method toString and draw naturally using puzzle and state matrix
 *  
 * Quest 3 :
 * 
 *  * we take a LinkedList of broken lines where each brokenLine will be a list of cells : LinkedList<int[]> and a cell is int[2]
 *  * we take the clue in input we add it to a LinkedList<int[]> tmp so that this will be the first broken line and than add this to results
 *  * we call a recursive backtracking function that :
 *  * takes the last broken line in result
 *  * stores the clue in went from and the cell it arrived to (clue->cell1->cell2->cell3 so we store clue and clue3)
 *  * than look in the neighbors of cell3 looking for another free cell IF we did not reach the limit of cells in a brokenLine which is the clue's value
 *  * if so we look at the last cell if it had the clue's value we clone the brokenLine we are in and add a copy to results
 *  
 * Quest 4 & 5:
 * 
 * * we keep a boolean variable solved in the attributes of the logipix this will tell if solved so that in the backtracking we stop looking for 
 * other lines and in stead of unmarking cells we hard mark them (means set their state to 2)
 * * we go through cells in the order of priority queue this.cells and we choose a broken line we mark it ( set state to 1) we move to the next
 * non marked cell and try to solve until is we find it unsolvable (no broken line that fits) we go back and so on...
 * * when we achieve the fact that their is no more clue left untrained we set solved to true and draw the solution..
 * 
 * Quest 6 : // longest question :)
 * * the idea here is to store all broken lines in cell_clue_brokenLines where each key is a tuple (i,j,k,l) (coded with an integer) representing a cell
 * and a clue and the value is all the broken lines using both the cell and the clue
 * * we now try to go through this map and see if there is cell used in exactly the number of broken lines (not emptied before by this same algo) of a clue
 * which means used in all useful clue's broken lines
 * * we than update (and here we use the map to access in constant time any cell and clue) every clue using this cell in all cells of the puzzle
 * * that we fill this.brokenLines which gives useful brokenLines of each cell
 * * we look for cells that have no more than 1 broken line we do the same updates as before on the cells on these broken lines and repeat this until
 * there is no more broken lines being excluded
 * 
 * 
 * now we must backtrack because all of what we can be certain of is done ..
 * Quest 7 : 
 * we repeat quest 5 using useful broken lines and not all the broken lines 
 * we notice a tremendous change in solving time between 5 and 7 .. (44s vs 0.4s / 25s vs 0.08s in TeaCup and Man respectively)
 * 
 */

public class logipix {
	int[][] puzzle;
	int[][] state; // 0 if not used 1 if used 2 if maybe used
	PriorityQueue<int[]> clues; // [i,j] coordinates of clues which will be a PriorityQueue needed and broken in
								// quest 5
	int height;
	int width;
	boolean solved = false;
	HashMap<Integer, LinkedList<LinkedList<int[]>>> brokenLines = new HashMap<>(); // needed in quest 6 : I acutually
																					// need a double
	// keyed hashmap so I will use the key i*p+j
	// where p = this.width + 1 : (i-i')p=(j-j') =>
	// i=i' and j=j'

	// ++++++++++++++++++++++++++++++ Quest 1 & 2 ++++++++++++++++++++++++++++++

	public logipix(String filePath) throws Exception {

		File file = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(file));

		this.width = Integer.parseInt(br.readLine());
		this.height = Integer.parseInt(br.readLine());
		this.puzzle = new int[height][width];
		this.clues = new PriorityQueue<>(new Comparator<int[]>() {
			public int compare(int[] clue1, int[] clue2) {
				return puzzle[clue1[0]][clue1[1]] - puzzle[clue2[0]][clue2[1]];
			}
		});

		String[] row;
		for (int i = 0; i < height; i++) {
			row = br.readLine().split(" ");
			for (int j = 0; j < width; j++) {
				this.puzzle[i][j] = Integer.parseInt(row[j]);
				if (puzzle[i][j] != 0)
					clues.add(new int[] { i, j }); // will be compared with clues already set in puzzle so ok
			}
		}
		br.close();

		this.state = new int[this.height][this.width];

	}

	public String toString() { // puzzle where each cell is like [case number|case state]
		String result = "";
		for (int i = 0; i < this.height; i++) {
			result += "[" + ((this.puzzle[i][0] < 10) ? " " + this.puzzle[i][0] : this.puzzle[i][0]) + "|"
					+ this.state[i][0] + ", ";
			for (int j = 1; j < this.width - 1; j++) {
				result += ((this.puzzle[i][j] < 10) ? " " + this.puzzle[i][j] : this.puzzle[i][j]) + "|"
						+ this.state[i][j] + ", ";
			}
			result += ((this.puzzle[i][this.width - 1] < 10) ? " " + this.puzzle[i][this.width - 1]
					: this.puzzle[i][this.width - 1]) + "|" + this.state[i][this.width - 1] + "] \n";
		}
		return result;
	}

	public void draw() { // draws the puzzle with " " being active cells and "X" inactive ones
		String result = "";
		for (int i = 0; i < this.height; i++) {
			result += "[" + (!(this.state[i][0] == 0) ? " " : "X") + ", ";
			for (int j = 1; j < this.width - 1; j++) {
				result += (!(this.state[i][j] == 0) ? " " : "X") + ", ";
			}
			result += (!(this.state[i][this.width - 1] == 0) ? " " : "X") + "] \n";
		}
		System.out.println(result);
	}

	// ++++++++++++++++++++++++++++++ Quest 3 ++++++++++++++++++++++++++++++

	public LinkedList<LinkedList<int[]>> setOfBrokenLines(int[] clue) { // why linkedlist of linkedlist because we need
																		// order in both
		LinkedList<LinkedList<int[]>> result = new LinkedList<>();
		LinkedList<int[]> lastList = new LinkedList<>();

		lastList.add(clue);
		this.state[clue[0]][clue[1]] = 1;
		result.add(lastList);

		this.generateSetOfBrokenLines(result);

		result.removeLast();
		return result;
	}

	@SuppressWarnings("unchecked")
	private void generateSetOfBrokenLines(LinkedList<LinkedList<int[]>> result) {

		LinkedList<int[]> lastList = result.getLast();
		int[] lastPair = lastList.getLast();
		int[] clue = lastList.getFirst();

		// verify if the last list has the size of the clue and the lastPair too and
		// clone the lastList
		if (lastList.size() == this.puzzle[clue[0]][clue[1]]
				&& lastList.size() == this.puzzle[lastPair[0]][lastPair[1]]) {
			result.add((LinkedList<int[]>) lastList.clone());
		}

		// looking for new pairs
		else if (((this.puzzle[lastPair[0]][lastPair[1]] == 0) && (lastList.size() < this.puzzle[clue[0]][clue[1]]))
				|| (lastPair[0] == clue[0] && lastPair[1] == clue[1])) {

			for (int[] pair : this.neighbours(lastPair)) {
				if (this.state[pair[0]][pair[1]] == 0) {
					this.state[pair[0]][pair[1]] = 1;
					lastList = result.getLast(); // update to lastList
					lastList.addLast(pair);
					this.generateSetOfBrokenLines(result);
				}
			}
		}

		// backtrack
		lastList = result.getLast();
		this.state[lastPair[0]][lastPair[1]] = 0;
		lastList.removeLast();
	}

	private LinkedList<int[]> neighbours(int[] current) {

		LinkedList<int[]> result = new LinkedList<>();
		if (current[0] > 0) {
			if (current[1] > 0) {
				result.add(new int[] { current[0] - 1, current[1] });
				result.add(new int[] { current[0], current[1] - 1 });
			} else
				result.add(new int[] { current[0] - 1, current[1] });
		} else if (current[1] > 0)
			result.add(new int[] { current[0], current[1] - 1 });

		if (current[0] < this.height - 1) {
			if (current[1] < this.width - 1) {
				result.add(new int[] { current[0] + 1, current[1] });
				result.add(new int[] { current[0], current[1] + 1 });
			} else
				result.add(new int[] { current[0] + 1, current[1] });
		} else if (current[1] < this.width - 1)
			result.add(new int[] { current[0], current[1] + 1 });

		return result;
	}

	// ++++++++++++++++++++++++++++++ Quest 4 & 5 ++++++++++++++++++++++++++++++

	public void solve() { // up to O(3^sum(val clues)) which quite a lot !!!

		int[] currentClue = this.clues.poll();

		if (currentClue == null) { // solution found we hard mark everything that stop the program
			this.solved = true;
			this.draw();
		} else {
			if (this.state[currentClue[0]][currentClue[1]] != 0)
				solve();
			else {
				for (LinkedList<int[]> brokenLine : setOfBrokenLines(currentClue)) {
					this.draw();
					solve();
					if (!this.solved) {
						this.unmark(brokenLine);
					} else {
						this.hardMark(brokenLine);
						break;
					}
				}
			}
			if (!this.solved)
				this.clues.add(currentClue);
		}
	}

	private void hardMark(LinkedList<int[]> brokenLine) {
		for (int[] cell : brokenLine) {
			this.state[cell[0]][cell[1]] = 2;
		}
	}

	private void mark(LinkedList<int[]> brokenLine) {
		for (int[] cell : brokenLine) {
			this.state[cell[0]][cell[1]] = 1;
		}
	}

	private void unmark(LinkedList<int[]> brokenLine) {
		for (int[] cell : brokenLine) {
			this.state[cell[0]][cell[1]] = 0;
		}
	}

	// ++++++++++++++++++++++++++++++ Quest 6 ++++++++++++++++++++++++++++++

	// the following method calculates combinations and exclusions and fills
	// this.brokenLines with lines respecting exclusions

	public void combinationAndExclusion(boolean preview) { // ****** O(#clues^3 * height * width * 3^(max clues))
															// *******

		HashMap<Integer, LinkedList<LinkedList<int[]>>> cell_clue_brokenLines = new HashMap<>();// bijection N^4 <-> N ?
		HashMap<Integer, Integer> numOfBrokenLines = new HashMap<>(); // bijection N^2 -> N

		// (i,j) , (a,b) -> (i*(this.width + 1) + j, a*(this.width + 1) + b) ->
		// (i*(this.width + 1) + j)*(this.height + 1)*(this.width+1) + a*(this.width +
		// 1) + b

		// -------- O(height * width * #clue) --------
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				for (int[] clue : this.clues) {
					cell_clue_brokenLines.put(this.key(i, j, clue[0], clue[1]), new LinkedList<LinkedList<int[]>>());
				}
			}
		}

		// filling these HashMaps:
		// -------- O(#clue* 3^(max clues) * height * width) -------
		for (int[] clue : this.clues) {

			this.brokenLines.put(clue[0] * (this.width + 1) + clue[1], new LinkedList<>()); // O(1)

			LinkedList<LinkedList<int[]>> brokenLines = this.setOfBrokenLines(clue); // O(4*3^(clue-1)) = O(3^clue)
			numOfBrokenLines.put(clue[0] * (this.width + 1) + clue[1], brokenLines.size()); // O(1)

			for (LinkedList<int[]> brokenLine : brokenLines) { // O(3^clue)
				for (int[] cell : brokenLine) { // O(height*width)
					cell_clue_brokenLines.get(this.key(cell[0], cell[1], clue[0], clue[1])).add(brokenLine); // O(1)
				}
			}

		}

		// combinations and exclusions
		// -------- O(width * height * (#clues)^2 * 3^(max clues)) -------- !! the part
		// with the highest complexity !!
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				for (int[] clue : this.clues) {

					if (numNotEmpty(cell_clue_brokenLines.get(this.key(i, j, clue[0], clue[1]))) == numOfBrokenLines
							.get(clue[0] * (this.width + 1) + clue[1]) && this.state[i][j] != 2) { // O(3^(max clues))
																									// in numNotEmpty
						// means the clue uses the cell in all its brokenLines

						this.updateOtherCluesBrokenLines(i, j, clue, cell_clue_brokenLines, numOfBrokenLines); // O(#clues)

					}

				}
			}
		}

		// now we construct this.brokenLines based on these adjustements
		// ------ O(#clues * 3^(max clues)) ------
		for (int[] clue : this.clues) {
			for (LinkedList<int[]> brokenLine : cell_clue_brokenLines
					.get(this.key(clue[0], clue[1], clue[0], clue[1]))) {
				if (brokenLine.size() != 0) {
					this.brokenLines.get(clue[0] * (this.width + 1) + clue[1]).add(brokenLine); // O(1)
				}
			}
		}

		// check of single broken lines and re-update stuff do so untill there is no
		// more lines to be updated ...
		// ------ O(#clues * 3^(max clues)) ------
		HashSet<int[]> updated = new HashSet<>();
		HashSet<int[]> toBeUpdated = new HashSet<>();
		for (int[] clue : this.clues) {
			if (this.brokenLines.get(clue[0] * (this.width + 1) + clue[1]).size() == 1) {
				toBeUpdated.add(clue);
			}
		}

		@SuppressWarnings("rawtypes")
		List<int[]> cluesList = new ArrayList(clues); // so that we do not update this.brokenLines of clues already in
														// updated ...

		// ---- O(#clues^3 * height * width
		while (!toBeUpdated.isEmpty()) {

			for (int[] clue : toBeUpdated) { // O(#clues^2 * height * width)
				LinkedList<int[]> toBeUpdatedClue = this.brokenLines.get(clue[0] * (this.width + 1) + clue[1])
						.getFirst();
				for (int[] cell : toBeUpdatedClue) {
					this.updateOtherCluesBrokenLines(cell[0], cell[1], clue, cell_clue_brokenLines, numOfBrokenLines);
				}
			}

			cluesList.removeAll(updated); // O(#clues^2)
			updated.addAll(toBeUpdated); // O(#clues)

			for (int[] clue : cluesList) { // O(#clues * height * width)

				this.brokenLines.put(clue[0] * (this.width + 1) + clue[1], new LinkedList<>());

				for (LinkedList<int[]> brokenLine : cell_clue_brokenLines
						.get(this.key(clue[0], clue[1], clue[0], clue[1]))) {
					if (brokenLine.size() != 0) {
						this.brokenLines.get(clue[0] * (this.width + 1) + clue[1]).add(brokenLine);
					}
				}
			}

			HashSet<int[]> newList = new HashSet<int[]>();
			for (int[] clue : cluesList) { // O(#clues)
				if (this.brokenLines.get(clue[0] * (this.width + 1) + clue[1]).size() == 1) {
					newList.add(clue);
				}
			}

			newList.removeAll(updated); // O(#clues^2)
			toBeUpdated = newList;

		}

		// preview
		// ------- O(height*width)
		if (preview)
			this.draw();

		// than unmark everything
		// ------- O(height * width)
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				this.state[i][j] = 0;
			}
		}

	}

	private void updateOtherCluesBrokenLines(int i, int j, int[] clue,
			HashMap<Integer, LinkedList<LinkedList<int[]>>> cell_clue_brokenLines,
			HashMap<Integer, Integer> numOfBrokenLines) {
		// so hardmark the cell
		this.state[i][j] = 2;

		// and delete all broken lines of the other clues that use it
		for (int[] otherClue : this.clues) {
			// of course only in case we have another clue s
			// System.out.println(i+","+j+ " / "+ otherClue[0]+ "," + otherClue[1]);
			for (LinkedList<int[]> brokenLineToBeDeleted : cell_clue_brokenLines
					.get(this.key(i, j, otherClue[0], otherClue[1]))) {
				if (!brokenLineToBeDeleted.isEmpty() && !eqw(brokenLineToBeDeleted.getFirst(), clue)
						&& !eqw(brokenLineToBeDeleted.getLast(), clue)) {

					brokenLineToBeDeleted.clear();

					numOfBrokenLines.put(otherClue[0] * (this.width + 1) + otherClue[1],
							numOfBrokenLines.get(otherClue[0] * (this.width + 1) + otherClue[1]) - 1);
				}
			}
		}
	}

	private Integer numNotEmpty(LinkedList<LinkedList<int[]>> linkedList) {
		int sum = 0;
		for (LinkedList<int[]> l : linkedList) {
			if (!l.isEmpty())
				sum++;
		}
		return sum;
	}

	private boolean eqw(int[] first, int[] clue) {
		return first[0] == clue[0] && first[1] == clue[1];
	}

	// ++++++++++++++++++++++++++++++ Quest 7 ++++++++++++++++++++++++++++++

	// here we just copy the same thing we did in 5 with sets of broken lines being
	// the ones stored in this.brokenLines after combinationAndExclusion()

	public void optimisedSolve() { // praticaly quest 6 leave us with few lines to backtrack on so we go from the
									// O(3^sum(clues values)) to some thing near O(3^max(clues
									// values)*height^2*width^2) which is far more better

		// we call combinationAndExclusion

		// than we repeat quest 5

		int[] currentClue = this.clues.poll();

		if (currentClue == null) { // solution found we hard mark everything that stop the program
			this.solved = true;
			// System.out.println(this);
			this.draw();

		} else {
			if (this.state[currentClue[0]][currentClue[1]] != 0)
				optimisedSolve();
			else {

				for (LinkedList<int[]> brokenLine : this
						.setOfUnmarked(this.brokenLines.get(currentClue[0] * (this.width + 1) + currentClue[1]))) {
					this.mark(brokenLine);

					optimisedSolve();
					if (!this.solved) {
						this.unmark(brokenLine);
					} else {
						this.hardMark(brokenLine);
						break;
					}
				}

			}
			if (!this.solved)
				this.clues.add(currentClue);
		}

	}

	private LinkedList<LinkedList<int[]>> setOfUnmarked(LinkedList<LinkedList<int[]>> linkedList) {
		LinkedList<LinkedList<int[]>> result = new LinkedList<>();
		for (LinkedList<int[]> l : linkedList) {
			boolean check = true;
			for (int[] cell : l) {
				if (this.state[cell[0]][cell[1]] != 0)
					check = false;
			}
			if (check) {
				result.add(l);
			}
		}
		return result;
	}

	private Integer key(int i, int j, int a, int b) {
		return (i * (this.width + 1) + j) * (this.height + 1) * (this.width + 1) + a * (this.width + 1) + b;
	}

	// ++++++++++++++++++++++++++++++ printing methods for debuging
	// ++++++++++++++++++++++++++++++

	public static void print(LinkedList<int[]> tmp, boolean toLine) {
		for (int[] cell : tmp) {
			print(cell, false);
			System.out.print("->");
		}
		if (toLine)
			System.out.println("");
	}

	public static void printb(LinkedList<LinkedList<int[]>> result) {
		for (LinkedList<int[]> l : result) {
			System.out.print("[");
			print(l, false);
			System.out.print("]->");
		}
		System.out.println("");
	}

	public static void print(int[] cell, boolean toLine) {
		System.out.print("(" + cell[0] + "," + cell[1] + ")");
		if (toLine)
			System.out.println("");
	}

}
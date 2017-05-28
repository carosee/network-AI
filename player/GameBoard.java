// GameBoard.java

package player;

public class GameBoard {
	
	/* variables */
	public static final int SIZE = 8;
	protected int whitecount;
	protected int blackcount;
	protected Chip[][] board;
	
	/* gameboard constructor */
	protected GameBoard() { // creates a new empty gameboard
		board = new Chip[SIZE][SIZE];
		whitecount = 0;
		blackcount = 0;
	}

	/* Makes a copy of the current gameboard with all of the same pieces as the
	 original. This is mainly used for our min-max algorithm to explore the depths. */
	protected GameBoard copy() {
		GameBoard c = new GameBoard();
		for (int x = 0; x <= 7; x++) {
	        for (int y = 0; y <= 7; y++) {
	        	if (hasPiece(x,y)) {
	        		c.addPiece(this.board[x][y], x, y);
	        	}
	        }
	    }
	    return c;
	}

	/* Takes in a move and color to depending if it's a "step" or "add" move and will perform 
	the appropriate move onto the gameboard. */
	protected void performMove(Move m, int color) {
		if (m.moveKind == 1) {
			Chip piece = new Chip(color);
			setPiece(piece, m.x1, m.y1);
		} else if (m.moveKind == 2) {
			movePiece(m.x1, m.y1, m.x2, m.y2);
		}
	}

	/* Checks if the x,y coordinate has a piece contained in that space or not */
	protected boolean hasPiece(int x, int y) {
		if ((x < 0) || (x > 7) || (y < 0) || (y > 7)){
			return false;
		}
		if (board[x][y] == null){
			return false;
		} else {
			return true;
		}
	}

	/* This method gets a chip at the x,y coordinate */
	protected Chip getPiece(int x, int y) {
		if (hasPiece(x,y)) {
			return this.board[x][y];
		} else {
			return null;
		}
	}


	/* This sets a chip onto the x,y coordinate of the gameboard */
	private void addPiece(Chip piece, int x, int y) {
		this.board[x][y] = piece;
		piece.x = x;
		piece.y = y;
		if (piece.color == 0) {
			blackcount++;
		} else {
			whitecount++;
		}
	}

	/* This sets the piece onto the board if the move is valid*/
	private void setPiece(Chip piece, int x, int y) {
		Move m = new Move(x, y);
		if (isValidMove(m, piece.color)) {
			addPiece(piece, x, y);
		}
	}

	/* This method is only used if it's a "step" move and maintains the correct number of black
	and white chips on the board */
	private void movePiece(int x1, int y1, int x2, int y2) {
		Move m = new Move(x1, y1, x2, y2);
		Chip piece = getPiece(x2, y2);
		if (isValidMove(m, piece.color)) {
			addPiece(piece, x1, y1);
			if (piece.color == 0) {
				blackcount--;
			} else {
				whitecount--;
			}
			this.board[x2][y2] = null;
		}
	}


	/*This returns True if there are more than two chips in a connected group. 
	Assumes that there is no piece in this (x,y) position. */
	private boolean hasCluster(int x, int y, int color) {
		int count = 0;
		int x1 = 0;
		int y1 = 0;
		for (int i = x - 1; i <= x + 1; i++){
			for (int j = y - 1; j <= y + 1; j++){
				if (hasPiece(i,j)){
					if (getPiece(i,j).color == color) {
						count++;
						x1 = i;
						y1 = j;
					}
				}
			}
		}
		if (count >= 2) {
			return true;
		} else if (count == 1) {
			int count2 = 0;
			for (int f = x1 - 1; f <= x1 + 1; f++){
				for (int k = y1 - 1; k <= y1 + 1; k++){
					if (hasPiece(f,k)){
						if (getPiece(f,k).color == color) {
							count2++;
						}
					}
				}
			}
			if (count2 >= 2) {
				return true;
			}
		}
		return false;
	}
	
	/* This method takes in a move and color and returns a boolean True or 
	False that evaluates if the given move is valid. This method checks for 
	clusters, chips being in the wrong end zone, and makes sure no illegal moves
	are being made */
	protected boolean isValidMove(Move m, int color) {
		// check if an ADD move is valid
		if (m.moveKind == 1) {
			// can't do an add move if there are already 10 chips on the board
			if ((color == 0 && blackcount >= 10) || (color == 1 && whitecount >= 10)) {
				return false;
			}
			// corners
			if (m.x1 == 0 && m.y1 == 0){
				return false;
			} if (m.x1 == 0 && m.y1 == 7){
				return false;
			} if (m.x1 == 7 && m.y1 == 0){
				return false;
			} if (m.x1 == 7 && m.y1 == 7){
				return false;
			} 
			// check if there is a piece in this position
			if (hasPiece(m.x1, m.y1)){
				return false;
			} 
			// check if there is a cluster of 3
			if (hasCluster(m.x1, m.y1, color)){
				return false;
			}
			// can't put chips in opponent's goal zone
			if (color == 0 && (m.x1 == 0 || m.x1 == 7) && m.y1 >= 1 && m.y1 <= 6){ 
				return false;
			} if (color == 1 && (m.y1 == 0 || m. y1 == 7) && m.x1 >= 1 && m.x1 <= 6) {
				return false;
			}

		}
		// check if a STEP move is valid
		if (m.moveKind == 2) {
			// can't do a step move if there are <10 chips on the board
			if ((color == 0 && blackcount < 10) || (color == 1 && whitecount < 10)) { 
				return false;
			}
			// can't step move to the same place
			if (m.x1 == m.x2 && m.y1 == m.y2) {
				return false;
			}
			// check that there is a chip in (x1, y1) and no chip in (x2, y2)
			if (hasPiece(m.x1,m.y1) || !(hasPiece(m.x2, m.y2))) {
				return false;
			}
			// can only move pieces of your color
			if (getPiece(m.x2, m.y2).color != color) {
				return false;
			}
			// corners
			if (m.x1 == 0 && m.y1 == 0){
				return false;
			} if (m.x1 == 0 && m.y1 == 7){
				return false;
			} if (m.x1 == 7 && m.y1 == 0){
				return false;
			} if (m.x1 == 7 && m.y1 == 7){
				return false;
			} 
			// check if there is a cluster of 3
			if (hasCluster(m.x1, m.y1, color)){
				return false;
			}
			// can't put chips in opponent's goal zone
			if (color == 0 && (m.x1 == 0 || m.x1 == 7) && m.y1 >= 1 && m.y1 <= 6){ 
				return false;
			} if (color == 1 && (m.y1 == 0 || m. y1 == 7) && m.x1 >= 1 && m.x1 <= 6) {
				return false;
			}
		}
		// returns true if move m is valid.
		return true;
	}

	/* This method iterates through all possible combinations depending 
	if the move is an add move or a step move. After checking if it's a 
	valid move, then make a new Move object and insert it in the back of 
	your DList. */
	protected DList calculateValidMoves(int color) {
		DList validmoves = new DList();
		if ((color == 0 && blackcount < 10) || (color == 1 && whitecount < 10)) { //only add moves
			for (int x = 0; x <= 7; x++) {
	          	for (int y = 0; y <= 7; y++) {
	          		Move m = new Move(x, y);
	          		if (isValidMove(m, color)) {
	          			validmoves.insertBack(m);
	          		}
	          	}
	        }
	    }
	    else if ((color == 0 && blackcount == 10) || (color == 1 && whitecount == 10)) { //only step move
	    	for (int x1 = 0; x1 <= 7; x1++) { // looping through all possible combinations of step moves
	    		for (int x2 = 0; x2 <= 7; x2++) {
	    			for (int y1 = 0; y1 <= 7; y1++) {
	    				for (int y2 = 0; y2 <= 7; y2++) {
	    					Move m = new Move(x1, y1, x2, y2);
	    					if (isValidMove(m, color)) {
	    						validmoves.insertBack(m);
	    					}
	    				}
	    			}
	    		}
	    	}
		} 
		return validmoves;
	}

	/* This method iterates right, up, left, down, and all diagonals and finds 
	any chips connected to this chip. Then it inserts the piece in the 
	back of a DList containing all the connections. */
	protected DList findConnections(Chip piece) {
		DList connected = new DList();
		int x = piece.x;
		int y = piece.y;
		Chip p;
		while (x < SIZE - 1) { //right
			x++;
			if (hasPiece(x, y)) {
				p = getPiece(x, y);
				if (p.color == piece.color) {
					connected.insertBack(p);
					break;
				} else {
					break;
				}
			}
		}
		x = piece.x;

		while (y < SIZE - 1) { //up
			y++;
			if (hasPiece(x, y)) {
				p = getPiece(x, y);
				if (p.color == piece.color) {
					connected.insertBack(p);
					break;
				} else {
					break;
				}
			}
		}
		y = piece.y;

		while (x > 0) { //left
			x--;
			if (hasPiece(x, y)) {
				p = getPiece(x, y);
				if (p.color == piece.color) {
					connected.insertBack(p);
					break;
				} else {
					break;
				}
			}
		}
		x = piece.x;

		while (y > 0) { //down
			y--;
			if (hasPiece(x, y)) {
				p = getPiece(x, y);
				if (p.color == piece.color) {
					connected.insertBack(p);
					break;
				} else {
					break;
				}
			}
		}
		y = piece.y;

		while (y < SIZE - 1 && x < SIZE - 1) {
			x++;
			y++;
			if (hasPiece(x, y)) {
				p = getPiece(x, y);
				if (p.color == piece.color) {
					connected.insertBack(p);
					break;
				} else {
					break;
				}
			}
		}
		x = piece.x;
		y = piece.y;

		while (y > 0 && x < SIZE - 1) {
			x++;
			y--;
			if (hasPiece(x, y)) {
				p = getPiece(x, y);
				if (p.color == piece.color) {
					connected.insertBack(p);
					break;
				} else {
					break;
				}
			}
		}
		x = piece.x;
		y = piece.y;

		while (y < SIZE - 1 && x > 0) {
			x--;
			y++;
			if (hasPiece(x, y)) {
				p = getPiece(x, y);
				if (p.color == piece.color) {
					connected.insertBack(p);
					break;
				} else {
					break;
				}
			}
		}
		x = piece.x;
		y = piece.y;

		while (y > 0 && x > 0) {
			x--;
			y--;
			if (hasPiece(x, y)) {
				p = getPiece(x, y);
				if (p.color == piece.color) {
					connected.insertBack(p);
					break;
				} else {
					break;
				}
			}
		}
		x = piece.x;
		y = piece.y;

		return connected;
	}

	private boolean inGoal(Chip c) {
		if (c.color == 0 && (c.y == 7 || c.y == 0)){
			return true;
		}
		if (c.color == 1 && (c.x == 7 || c.x == 0)) {
			return true;
		}
		return false;
	}

	private boolean inEndGoal(Chip c) {
		if (c.color == 0 && (c.y == 7)){
			return true;
		}
		if (c.color == 1 && (c.x == 7)) {
			return true;
		}
		return false;
	}

	/* This method correctly identifies if there is a network on the gameboard */
	protected boolean hasNetwork(int color) {
		if (color == 0) { // loop through starting chips in top row (black's goal zone)
			for (int i = 1; i <= 6; i++) {
				if (hasPiece(i, 0)) {
					boolean network = search(getPiece(i,0));
					if (network) {
						return true;
					}
				}
			}
			return false;
		}

		else { // loop through starting chips in left column (white's goal zone)
			for (int i = 1; i <= 6; i++) {
				if (hasPiece(0,i)) {
					boolean network = search(getPiece(0,i));
					if (network) {
						return true;
					}
				}
			}
			return false;
		}
		
	}

	private boolean search(Chip vertex) {
		DList queue = new DList(); // a list of networkhelper objects
		DList v = new DList(); // a list of visited chips
		v.insertBack(vertex);
		NetworkHelper first = new NetworkHelper(vertex, v);
		queue.insertBack(first);
		while (!queue.isEmpty()) {
			Object o = queue.removeFront();
			NetworkHelper curr = (NetworkHelper)o; //removeFront removes the first item in the list & returns it
			DList connections = findConnections(curr.chip); // a list of chip objects (connections)
			DList visit = curr.visited.copy();

			DListNode c = connections.head.next;
			while (c.item != null) { // looping through list of valid connections of curr.chip
				Chip x = (Chip)c.item;

				if (!visit.contains(c.item) && changeDirection(curr.visited, x)) {
					DList visited2 = visit.copy();
					visited2.insertBack(c.item); // add this chip to visited

					if (visited2.length() >= 6 && inEndGoal(x)) {
						return true;
					}
					if (visited2.length() < 6 && inGoal(x)) {
						c = c.next;
						continue;
					}
					NetworkHelper item = new NetworkHelper(x, visited2);
					queue.insertBack(item);
				}
				c = c.next;
			}
		}
		return false;
	}

	private boolean changeDirection(DList visited, Chip chip) {
		// return true if chips are changing direction
		if (visited.length() < 2) {
			return true;
		}
		DListNode last = visited.back();
		DListNode last2 = visited.prev(last);
		Chip lastchip = (Chip)last.item;
		Chip lastchip2 = (Chip)last2.item;
		if (lastchip.x == lastchip2.x && chip.x == lastchip.x) {
			return false;
		}
		if (lastchip.y == lastchip2.y && chip.y == lastchip.y) {
			return false;
		}
		int shiftx1 = lastchip.x - lastchip2.x;
		int shifty1 = lastchip.y - lastchip2.y;
		int shiftx2 = lastchip.x - chip.x;
		int shifty2 = lastchip.y - chip.y;
		if (shiftx1 == shifty1 && shiftx2 == shifty2) {
			return false;
		}
		return true;
	}


	public static void main(String[] args) {
		// TESTS

		/**System.out.println("Testing GameBoard constructor, addPiece, hasPiece");
		GameBoard gameboard0 = new GameBoard();
		gameboard0.addPiece(new Chip(0), 4, 4);
		gameboard0.addPiece(new Chip(1), 2, 3);
		GameBoard gameboard1 = gameboard0.copy();

		System.out.print("should be true: ");
		boolean result1 = gameboard1.hasPiece(4,4);
		System.out.print(result1);
		System.out.println("");
		

		System.out.print("should be true: ");
		boolean result2 = gameboard1.hasPiece(2,3);
		System.out.println(result2);
		System.out.println("");

		System.out.print("should be false: ");
		boolean result3 = gameboard1.hasPiece(-1,-1);
		System.out.print(result3);
		System.out.println("");
		
		System.out.print("should be false: ");
		boolean result43 = gameboard1.hasPiece(2,2);
		System.out.println(result43);
		System.out.println("");

		Move mv = new Move(6,6);
		gameboard1.performMove(mv, 0);

		System.out.print("should be true: ");
		boolean result31 = gameboard1.hasPiece(6,6);
		System.out.println(result31);
		System.out.println("");

		GameBoard gameboard0 = new GameBoard();
		gameboard0.addPiece(new Chip(0), 4, 0);
		gameboard0.addPiece(new Chip(0), 2, 1);
		gameboard0.addPiece(new Chip(0), 3, 2);
		gameboard0.addPiece(new Chip(0), 5, 2);
		gameboard0.addPiece(new Chip(0), 6, 0);
		gameboard0.addPiece(new Chip(0), 2, 4);
		gameboard0.addPiece(new Chip(0), 3, 5);
		gameboard0.addPiece(new Chip(0), 5, 5);
		gameboard0.addPiece(new Chip(0), 6, 6);
		gameboard0.addPiece(new Chip(0), 1, 7);

		gameboard0.addPiece(new Chip(1), 1, 1);
		gameboard0.addPiece(new Chip(1), 6, 1);
		gameboard0.addPiece(new Chip(1), 2, 2);
		gameboard0.addPiece(new Chip(1), 4, 2);
		gameboard0.addPiece(new Chip(1), 5, 3);
		gameboard0.addPiece(new Chip(1), 1, 4);
		gameboard0.addPiece(new Chip(1), 2, 5);
		gameboard0.addPiece(new Chip(1), 4, 5);
		gameboard0.addPiece(new Chip(1), 0, 6);
		gameboard0.addPiece(new Chip(1), 5, 6);

		DList lst = gameboard0.calculateValidMoves(0);
		System.out.println(lst.length());
		System.out.println(lst.stepmoveToString());

		//Move m = new Move(0, 6, 1 ,6);

		/**System.out.println("Testing hasCluster1");
		GameBoard gameboard2 = new GameBoard();
		gameboard2.addPiece(new Chip(0), 3, 3);
		gameboard2.addPiece(new Chip(0), 4, 4);
		
		System.out.print("should be true: ");
		boolean result4 = gameboard2.hasCluster(5, 5, 0);
		System.out.println(result4);
		System.out.println("");
		
		System.out.println("Testing hasCluster2");
		GameBoard gameboard3 = new GameBoard();
		gameboard3.addPiece(new Chip(0), 3, 3);
		gameboard3.addPiece(new Chip(0), 4, 4);
		gameboard3.addPiece(new Chip(1), 5, 5);
		
		System.out.print("should be true: ");
		boolean result5 = gameboard3.hasCluster(2, 2, 0);
		System.out.println(result5);
		System.out.println("");
		
		System.out.println("Testing hasCluster3");
		GameBoard gameboard4 = new GameBoard();
		gameboard4.addPiece(new Chip(0), 3, 3);
		gameboard4.addPiece(new Chip(1), 4, 4);
		gameboard4.addPiece(new Chip(0), 5, 5);
		
		System.out.print("should be false: ");
		boolean result6 = gameboard4.hasCluster(6, 6, 0);
		System.out.println(result6);
		System.out.println("");
		


		System.out.println("Testing changeDirection");
		GameBoard gameboard5 = new GameBoard();
		gameboard5.addPiece(new Chip(0),1,3);
		gameboard5.addPiece(new Chip(0),3,3);
		gameboard5.addPiece(new Chip(0),5,5);
		DList lst = new DList();
		Chip a = gameboard5.getPiece(1, 3);
		Chip b = gameboard5.getPiece(3, 3);
		Chip c = gameboard5.getPiece(5, 5);
		lst.insertBack(a);
		lst.insertBack(b);
		lst.insertBack(c);
		Chip d = new Chip(0);
		d.x = 6;
		d.y = 6;
		System.out.print("should be false: ");
		boolean result7 = gameboard5.changeDirection(lst, d);
		System.out.println(result7);
		System.out.println("");



		System.out.println("Testing changeDirection2");
		GameBoard gameboard6 = new GameBoard();
		gameboard6.addPiece(new Chip(0),1,3);
		gameboard6.addPiece(new Chip(0),3,3);
		gameboard6.addPiece(new Chip(0),5,5);
		DList lst2 = new DList();
		Chip a2 = gameboard6.getPiece(1, 3);
		Chip b2 = gameboard6.getPiece(3, 3);
		Chip c2 = gameboard6.getPiece(5, 5);
		lst2.insertBack(a2);
		lst2.insertBack(b2);
		lst2.insertBack(c2);
		Chip d2 = new Chip(0);
		d2.x = 5;
		d2.y = 2;
		System.out.print("should be true: ");
		boolean result8 = gameboard6.changeDirection(lst, d2);
		System.out.println(result8);
		System.out.println("");


		System.out.println("Testing changeDirection3");
		GameBoard gameboard7 = new GameBoard();
		gameboard7.addPiece(new Chip(0),1,3);
		gameboard7.addPiece(new Chip(0),3,3);
		gameboard7.addPiece(new Chip(0),3,5);
		DList lst3 = new DList();
		Chip a3 = gameboard7.getPiece(1, 3);
		Chip b3 = gameboard7.getPiece(3, 3);
		Chip c3 = gameboard7.getPiece(3, 5);
		lst3.insertBack(a3);
		lst3.insertBack(b3);
		lst3.insertBack(c3);
		Chip d3 = new Chip(0);
		d3.x = 3;
		d3.y = 6;
		System.out.print("should be false: ");
		boolean result9 = gameboard7.changeDirection(lst3, d3);
		System.out.println(result9);
		System.out.println("");
		
		
		System.out.println("Testing inGoal");
		GameBoard gameboard10 = new GameBoard();
		Chip c = new Chip(0);
		Chip c2 = new Chip(0);
		gameboard10.addPiece(c,5,7);
		gameboard10.addPiece(c2,5,5);
		System.out.print("should be true: ");
		System.out.println(gameboard10.inGoal(c));
		System.out.print("should be false: ");
		System.out.println(gameboard10.inGoal(c2));
		System.out.println("");
		

		System.out.println("Testing hasNetwork1");
		GameBoard gameboard8 = new GameBoard();
		gameboard8.addPiece(new Chip(0),2,0);
		gameboard8.addPiece(new Chip(0),1,3);
		gameboard8.addPiece(new Chip(0),2,5);
		gameboard8.addPiece(new Chip(0),3,5);
		gameboard8.addPiece(new Chip(0),3,3);
		gameboard8.addPiece(new Chip(0),5,5);
		gameboard8.addPiece(new Chip(0),5,7);
		System.out.print("should be true: ");
		boolean result10 = gameboard8.hasNetwork(0);
		System.out.println(result10);
		System.out.println("");
		

		System.out.println("Testing hasNetwork2");
		GameBoard gameboard9 = new GameBoard();
		gameboard9.addPiece(new Chip(0),6,0);
		gameboard9.addPiece(new Chip(0),6,5);
		gameboard9.addPiece(new Chip(0),5,5);
		gameboard9.addPiece(new Chip(0),3,3);
		gameboard9.addPiece(new Chip(0),3,5);
		gameboard9.addPiece(new Chip(0),5,7);
		System.out.print("should be true: ");
		boolean result11 = gameboard9.hasNetwork(0);
		System.out.print(result11);
		System.out.println("");

		System.out.println("Testing hasNetwork3");
		GameBoard gameboard11 = new GameBoard();
		gameboard11.addPiece(new Chip(1),0,1);
		gameboard11.addPiece(new Chip(1),2,2);
		gameboard11.addPiece(new Chip(1),3,3);
		gameboard11.addPiece(new Chip(1),5,1);
		gameboard11.addPiece(new Chip(1),5,2);
		gameboard11.addPiece(new Chip(1),7,3);
		
		System.out.print("should be false: ");
		boolean result12 = gameboard11.hasNetwork(1);
		System.out.print(result12);
		System.out.println("");
		**/
		

		/**System.out.println("Testing hasNetwork4");
		GameBoard gameboard12 = new GameBoard();
		gameboard12.addPiece(new Chip(1),0,1);
		gameboard12.addPiece(new Chip(1),2,2);
		gameboard12.addPiece(new Chip(1),3,3);
		gameboard12.addPiece(new Chip(1),5,1);
		gameboard12.addPiece(new Chip(1),5,2);
		gameboard12.addPiece(new Chip(1),7,3);
		
		
		gameboard12.addPiece(new Chip(0),6,0);
		gameboard12.addPiece(new Chip(0),6,5);
		gameboard12.addPiece(new Chip(0),5,5);
		gameboard12.addPiece(new Chip(0),3,3);
		gameboard12.addPiece(new Chip(0),3,5);
		gameboard12.addPiece(new Chip(0),5,7);
		
		System.out.print("should be true: ");
		boolean result13 = gameboard12.hasNetwork(0);
		System.out.print(result13);
		System.out.println("");
		
		
		GameBoard gameboard12 = new GameBoard();
		gameboard12.addPiece(new Chip(0),1,1);
		gameboard12.addPiece(new Chip(0),2,1);
		gameboard12.addPiece(new Chip(0),4,1);
		gameboard12.addPiece(new Chip(0),5,1);
		gameboard12.addPiece(new Chip(0),1,6);

		gameboard12.addPiece(new Chip(1),0,2);
		gameboard12.addPiece(new Chip(1),1,2);
		gameboard12.addPiece(new Chip(1),4,2);
		gameboard12.addPiece(new Chip(1),1,5);
		gameboard12.addPiece(new Chip(1),4,5);

		//gameboard12.addPiece(new Chip(1),0,4);
		gameboard12.addPiece(new Chip(1),6,1);
		System.out.print("hasnetwork: ");
		System.out.println(gameboard12.hasNetwork(1));
		MachinePlayer samson = new MachinePlayer(1);
		System.out.print("Evals to: ");
		System.out.println(samson.eval(gameboard12, 1));

		DList lst = gameboard12.calculateValidMoves(1);
		System.out.println(lst.addmoveToString());
		**/
		
	}
	
}
// NetworkHelper.java

// helper class for hasNetwork. Stores a chip and a DList of previously visited chips.

package player;

public class NetworkHelper {

	protected Chip chip;
	protected DList visited;

	protected NetworkHelper(Chip chip, DList visited) {
		this.chip = chip;
		this.visited = visited;
	}

}
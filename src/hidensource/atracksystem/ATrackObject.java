package hidensource.atracksystem;

public class ATrackObject {
	public int id = -1;
	public float x, px, velx, vely;
	public float y, py;
	public int width;
	public int height;
	public int color;
	public int mass;
	public float time;

	/**
	 * Constructor
	 */
	public ATrackObject() {
	}

	public int compareById(ATrackObject o) {
		Integer id1 = new Integer(id);
		Integer id2 = new Integer(o.id);
		int x = id1.compareTo(id2);
		return x;
	}
}
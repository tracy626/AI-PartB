import java.awt.Point;
import java.util.HashMap;

public class test {

	public static void main(String[] args) {
		Point a = new Point(1,0);
		Point b = new Point(1,0);
		HashMap<Point, Integer> c = new HashMap();
		
		c.put(a, 1);
		
		System.out.println(c.containsKey(b));
	}

}

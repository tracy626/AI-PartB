import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import aiproj.slider.Move;

public class test {

	public static void main(String[] args) {
		Point a = new Point(1,0);
		Point b = new Point(1,0), f = new Point(2,0);
		HashMap<Point, Integer> c = new HashMap();

		c.put(a, 1);
		HashMap<Point, Integer> d= new HashMap(c);
		d.remove(a);
		d.put(f, 3);
		System.out.println(c.containsKey(a));
	}
}

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
		d.remove(new Point(1,0));
		d.put(f, 3);
		System.out.println(d.containsKey(a));
		
		Integer i =  null;
		int j = 3;
		if (i==null) {
			i = 1;
		}
		System.out.println(Math.max(i, 2));

		System.out.println(i+j);
	}
}

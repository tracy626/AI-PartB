/**
 * Yue Fang 715889 (fang1)
 * Zhe Tang 743398 (zhet1)
 * COMP30024 Artificial Intelligence
 * Project Part B
 */

/**
 * Pair class, help us to return a double and a Integer[] of features of
 * the cut off or determined leaf
 */
public class Pair<T1, T2>{
	private T1 value;
	private T2 feature;
	
	public Pair(T1 value, T2 feature){
		this.value = value;
		this.feature = feature;
	}

	public T1 getValue() {
		return value;
	}

	public T2 getFeature() {
		return feature;
	}

	public void setValue(T1 value) {
		this.value = value;
	}

	public void setFeature(T2 feature) {
		this.feature = feature;
	}
}
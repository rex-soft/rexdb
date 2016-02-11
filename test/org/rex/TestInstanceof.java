package org.rex;

import java.util.HashMap;
import java.util.Map;

public class TestInstanceof {

	public static void main(String[] args) {
		Object m = new HashMap();
		long s= System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			boolean b = m.getClass().isArray();
		}
		System.err.println(System.currentTimeMillis() - s);
	}
}

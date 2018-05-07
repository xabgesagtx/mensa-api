package com.github.xabgesagtx.mensa.scrape;

import java.util.Comparator;
import java.util.Optional;

public class IntegerAwareStringComparator implements Comparator<String> {


	@Override
	public int compare(String o1, String o2) {
		Optional<Integer> int1 = getInteger(o1);
		Optional<Integer> int2 = getInteger(o2);
		if (int1.isPresent() && int2.isPresent()) {
			return int1.get().compareTo(int2.get());
		} else if (int2.isPresent()) {
			return 1;
		} else if (int1.isPresent()) {
			return -1;
		} else {
			return o1.compareTo(o2);
		}
	}

	private Optional<Integer> getInteger(String text) {
		Optional<Integer> result = Optional.empty();
		try {
			result = Optional.of(Integer.parseInt(text));
		} catch (NumberFormatException e) {
			// do nothing
		}
		return result;
	}
}

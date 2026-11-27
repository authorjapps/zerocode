

package org.jsmart.zerocode.core.engine.assertion.field;

import java.math.BigDecimal;
import java.util.Arrays;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.NumberComparator;

import static org.apache.commons.lang3.StringUtils.substringBetween;

public class FieldIsOneOfValueAsserter implements JsonAsserter {
	private final String path;
	final Object expected;

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public Object getExpected() {
		return expected;
	}

	@Override
	public FieldAssertionMatcher actualEqualsToExpected(Object actualResult) {
		boolean areEqual;

		if (expected != null) {
            String expectedString = substringBetween((String) expected, "[", "]");

			// Store collection as a java array
			String[] expectedArray = null;
			
			// Check if it's an empty json array
			if (!expectedString.isEmpty()) {
				// Split into an array
				expectedArray = expectedString.split(",");
			} else {
				expectedArray = new String[] {};
			}
				
			// Remove leading and trailing spaces
			for (int i = 0; i < expectedArray.length; i++) {
				// Checking that this is not a whitespace string (cannot use .isBlank() as we're
				// targeting java 1.8)
				if (!expectedArray[i].trim().isEmpty())
					expectedArray[i] = expectedArray[i].trim();
			}

			if (actualResult != null) {
				// Search list for value
				areEqual = isOneOf(expectedArray, actualResult);
			} else {
				areEqual = false;
			}
		} else {
			// Both null
			if (actualResult == null) {
				areEqual = true;
			} else {
				areEqual = false;
			}
		}

		return areEqual ? FieldAssertionMatcher.aMatchingMessage()
				: FieldAssertionMatcher.aNotMatchingMessage(path, "One Of:" + expected, actualResult);
	}

	private boolean isOneOf(String[] expectedArray, Object actualResult) {
		if (actualResult instanceof Number) {
			NumberComparator comparator = new NumberComparator();
			for (String thisExpected : expectedArray) {
				try {
					if (comparator.compare(new BigDecimal(thisExpected), (Number) actualResult) == 0) {
						return true;
					}
				} catch (NumberFormatException notANumber) {
					// A non numeric item can not match a number, move on to the next item
				}
			}
			return false;
		}

		// The expected items are always text, hence e.g. a boolean true is matched as "true"
		return Arrays.asList(expectedArray).contains(String.valueOf(actualResult));
	}

	public FieldIsOneOfValueAsserter(String path, Object expected) {
		this.path = path;
		this.expected = expected;
	}

}
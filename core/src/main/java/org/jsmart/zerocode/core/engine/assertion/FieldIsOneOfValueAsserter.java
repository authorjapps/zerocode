package org.jsmart.zerocode.core.engine.assertion;

import java.util.Arrays;

public class FieldIsOneOfValueAsserter implements JsonAsserter {
	private final String path;
	final Object expected;

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public AssertionReport actualEqualsToExpected(Object actualResult) {
		boolean areEqual;

		if (expected != null) {
			String expectedString = (String) expected;

			// Remove json string array brackets
			if (expectedString.startsWith("["))
				expectedString = expectedString.substring(1);

			if (expectedString.endsWith("]"))
				expectedString = expectedString.substring(0, expectedString.length() - 1);

			// Split into an array
			final String[] expectedArray = expectedString.split(",");

			// Remove leading and trailing spaces
			for (int i = 0; i < expectedArray.length; i++) {
				// If it's just a leading/trailing space and not a whole blank string
				if (!expectedArray[i].isBlank())
					expectedArray[i] = expectedArray[i].trim();
			}

			if (actualResult != null) {
				// Search list for value
				areEqual = Arrays.asList(expectedArray).contains(actualResult);
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

		return areEqual ? AssertionReport.createFieldMatchesReport()
				: AssertionReport.createFieldDoesNotMatchReport(path, "One Of:" + expected, actualResult);
	}

	public FieldIsOneOfValueAsserter(String path, Object expected) {
		this.path = path;
		this.expected = expected;
	}

}
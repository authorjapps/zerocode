package org.jsmart.zerocode;

import com.fasterxml.jackson.databind.node.NullNode;
import org.jsmart.zerocode.core.domain.Step;

import java.util.Collections;

public class TestUtility {
    private TestUtility() {

    }

    public static Step createDummyStep(String stepName) {
        return new Step(1, null, stepName, "dummy", "dummy", "dummy", null, Collections.emptyList(), NullNode.getInstance(), NullNode.getInstance(), NullNode.getInstance(), "dummy", false);
    }
}

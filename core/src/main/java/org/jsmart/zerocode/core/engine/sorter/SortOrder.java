package org.jsmart.zerocode.core.engine.sorter;

public enum SortOrder {
    NATURAL("natural"), REVERSE("reverse");

    String value;

    SortOrder(String order) {
        this.value = order;
    }

    public String getValue() {
        return value;
    }


}

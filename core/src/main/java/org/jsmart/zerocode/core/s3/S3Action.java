package org.jsmart.zerocode.core.s3;

public enum S3Action {
    UPLOAD("UPLOAD"),
    DOWNLOAD("DOWNLOAD"),
    LIST("LIST");

    private final String action;

    S3Action(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public static S3Action fromString(String action) {
        for (S3Action s3Action : values()) {
            if (s3Action.action.equalsIgnoreCase(action)) {
                return s3Action;
            }
        }
        throw new IllegalArgumentException("Unknown S3 action: " + action);
    }
}

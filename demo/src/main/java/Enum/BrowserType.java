package Enum;


public enum BrowserType {
    CHROME, FIREFOX, EDGE;

    public static BrowserType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Browser type không được để trống trong config.");
        }
        try {
            return BrowserType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Browser type '" + value + "' không được hỗ trợ. Hợp lệ: CHROME, FIREFOX, EDGE");
        }
    }
}
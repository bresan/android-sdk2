package eu.neosurance.sdk;

public class Constants {

    protected static final int PERMISSIONS_MULTIPLE_ACCESSLOCATION = 0x2043;
    protected static final int PERMISSIONS_MULTIPLE_IMAGECAPTURE = 0x2049;
    protected static final int REQUEST_IMAGE_CAPTURE = 0x1702;

    public static String getOs() {
        return "Android";
    }

    public static String getVersion() {
        return "2.0.3";
    }
}

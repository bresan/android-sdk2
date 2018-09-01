package eu.neosurance.sdk.platform.power;

public enum PowerStatus {
    PLUGGED,
    UNPLUGGED;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

package edu.wpi.first.outlineviewer.model;

/**
 * An entry containing an array of raw bytes.
 */
public class RawBytesEntry extends Entry<byte[]> {

  public RawBytesEntry(String key, byte[] value) {
    super(key, value);
  }

  @Override
  protected String getTypeString(byte[] value) {
    return "Raw Data (" + value.length + " bytes)";
  }

  // show an array of hex values "[0x00, 0x01, 0x02, ...]"
  @Override
  public String getDisplayString() {
    StringBuilder sb = new StringBuilder("[");
    byte[] value = getValue();
    for (int i = 0; i < value.length; i++) {
      // convert byte to unsigned int, then show as hex value
      sb.append(String.format("0x%02X", value[i] & 0xFF));
      if (i != value.length - 1) {
        sb.append(", ");
      }
    }
    sb.append(']');
    return sb.toString();
  }

}

package edu.wpi.first.tableviewer.entry;

/**
 *
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
      byte b = value[i];
      int unsigned = (b & 0xFF);
      sb.append(String.format("0x%02X", unsigned));
      if (i != value.length - 1) {
        sb.append(", ");
      }
    }
    sb.append(']');
    return sb.toString();
  }

}

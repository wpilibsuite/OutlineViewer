/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.tableviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

/**
 * Responsible for showing data inside a single NetworkTableEntry: a key, a
 * value, and a type that has been extrapolated from the value.
 *
 * @author Sam
 */
public class LeafNode extends AbstractTreeNode {

    public LeafNode(String key, TableEntryData data) {
        super(data);
        String tableName = key;
        if (tableName.startsWith("/"))
            tableName = tableName.substring(1);
        int lastIndex = tableName.lastIndexOf('/');
        if (lastIndex == -1)
            tableName = "";
        else
            tableName = tableName.substring(0, lastIndex);
        table = NetworkTable.getTable(tableName);
    }

    /**
     * Changes the value of the data displayed within this leaf. This is only a
     * graphical update; use #setValue to actually change the value in the
     * NetworkTable.
     *
     * @param newValue The new value for this leaf to show.
     */
    public void updateValue(Object newValue) {
        data.setValue(newValue);
        outline.tableChanged(new TableModelEvent(outline.getModel()));
    }

    /**
     * Pushes the value of this leaf to the NetworkTable associated with it.
     *
     * @param newValue The value to send to the NetworkTable.
     */
    public void setValue(Object newValue) {
        String type = data.getType();
        String value = newValue.toString();
        if (type.equals("Boolean")) {
            String lower = value.toLowerCase(Locale.ENGLISH);
            if (lower.equals("y") || lower.equals("yes") || lower.equals("t") || lower.equals("true") || lower.equals("on") || lower.equals("1"))
                table.putBoolean(data.getKey(), true);
            else if (lower.equals("n") || lower.equals("no") || lower.equals("f") || lower.equals("false") || lower.equals("off") || lower.equals("0"))
                table.putBoolean(data.getKey(), false);
            else
                System.err.println("Invalid boolean value; expected one of yes, true, 1, no, false, 0");
        } else if (type.equals("Number")) {
            try {
                table.putNumber(data.getKey(), Double.parseDouble(value));
            } catch (NumberFormatException e) {
                System.err.println("Invalid number " + value);
            }
        } else if (type.equals("String")) {
            try {
                table.putString(data.getKey(), StringUtil.unescapeString(value));
            } catch (NumberFormatException e) {
                System.err.println("Invalid string '" + value + "': " + e.getMessage());
            }
        } else if (type.equals("Raw")) {
            value = value.trim();
            if (value.equals("[]")) {
                table.putValue(data.getKey(), new byte[0]);
                return;
            }
            if (!value.startsWith("[")) {
                System.err.println("Invalid array: missing [");
                return;
            }
            if (!value.endsWith("]")) {
                System.err.println("Invalid array: missing ]");
                return;
            }

            String[] arr = value.substring(1, value.length() - 1).split(",");
            byte[] barr = new byte[arr.length];
            for (int i=0; i < arr.length; i++) {
                try {
                    barr[i] = Byte.parseByte(arr[i].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number value at index " + i + ": '" + arr[i].trim() + "'");
                    return;
                }
            }
            table.putValue(data.getKey(), barr);
        } else if (type.startsWith("Boolean[")) {
            value = value.trim();
            if (value.equals("[]")) {
                table.putBooleanArray(data.getKey(), new boolean[0]);
                return;
            }
            if (!value.startsWith("[")) {
                System.err.println("Invalid array: missing [");
                return;
            }
            if (!value.endsWith("]")) {
                System.err.println("Invalid array: missing ]");
                return;
            }

            String[] arr = value.substring(1, value.length() - 1).split(",");
            boolean[] barr = new boolean[arr.length];
            for (int i=0; i < arr.length; i++) {
                String lower = arr[i].trim().toLowerCase(Locale.ENGLISH);
                if (lower.equals("y") || lower.equals("yes") || lower.equals("t") || lower.equals("true") || lower.equals("on") || lower.equals("1"))
                    barr[i] = true;
                else if (lower.equals("n") || lower.equals("no") || lower.equals("f") || lower.equals("false") || lower.equals("off") || lower.equals("0"))
                    barr[i] = false;
                else {
                    System.err.println("Invalid boolean value at index " + i + ": '" + arr[i].trim() + "'; expected one of yes, true, 1, no, false, 0");
                    return;
                }
            }
            table.putBooleanArray(data.getKey(), barr);
        } else if (type.startsWith("Number[")) {
            value = value.trim();
            if (value.equals("[]")) {
                table.putNumberArray(data.getKey(), new double[0]);
                return;
            }
            if (!value.startsWith("[")) {
                System.err.println("Invalid array: missing [");
                return;
            }
            if (!value.endsWith("]")) {
                System.err.println("Invalid array: missing ]");
                return;
            }

            String[] arr = value.substring(1, value.length() - 1).split(",");
            double[] darr = new double[arr.length];
            for (int i=0; i < arr.length; i++) {
                try {
                    darr[i] = Double.parseDouble(arr[i].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number value at index " + i + ": '" + arr[i].trim() + "'");
                    return;
                }
            }
            table.putNumberArray(data.getKey(), darr);
        } else if (type.startsWith("String[")) {
            value = value.trim();
            if (value.equals("[]")) {
                table.putStringArray(data.getKey(), new String[0]);
                return;
            }
            if (!value.startsWith("[")) {
                System.err.println("Invalid array: missing [");
                return;
            }
            if (!value.endsWith("]") || (value.endsWith("\\]") && !value.endsWith("\\\\]"))) {
                System.err.println("Invalid array: missing ]");
                return;
            }
            if (value.contains("]]]")) {
                System.err.println("Invalid array: unescaped ]");
                return;
            }
            // need to replace with unique string to make "\\" at end of value work
            value = value.replaceAll("\\\\\\\\", "]]]");
            // use look-behind assertion to avoid matching "\,"
            String[] arr = value.substring(1, value.length() - 1).split("(?<!\\\\),");
            for (int i=0; i < arr.length; i++) {
                if (arr[i].startsWith(" "))
                    arr[i] = arr[i].substring(1);
                try {
                    arr[i] = StringUtil.unescapeString(arr[i].replaceAll("]]]", "\\\\\\\\"));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid string at index " + i + ": " + e.getMessage());
                }
            }
            table.putStringArray(data.getKey(), arr);
        } else {
            System.err.println("Cannot edit a value of type " + data.getType());
        }
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public String toString() {
        return data.getKey();
    }

    /**
     * Creates the {@code JPopupMenu} to display when this node is
     * right-clicked.
     *
     * @param path The path from the root to this branch.
     * @return The menu for this node.
     */
    public JPopupMenu getMenu(final TreePath path) {
        JPopupMenu popupMenu = new JPopupMenu("Change Item");

        JMenuItem persistentItem = new JCheckBoxMenuItem("Persistent", table.isPersistent(data.getKey()));
        JMenuItem deleteItem = new JMenuItem("Delete item");

        persistentItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (persistentItem.isSelected())
                    table.setPersistent(data.getKey());
                else
                    table.clearPersistent(data.getKey());
            }
        });

        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.delete(data.getKey());
            }
        });

        popupMenu.add(persistentItem);
        popupMenu.add(deleteItem);

        return popupMenu;
    }
}

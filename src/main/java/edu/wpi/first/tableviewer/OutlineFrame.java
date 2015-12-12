/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.tableviewer;

import edu.wpi.first.tableviewer.dialog.AbstractAddDialog;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;

public class OutlineFrame extends JFrame {

    private final Outline outline;
    private final DefaultTreeModel outlineTreeModel;
    private final BranchNode rootBranch;
    private final boolean showMetadata;
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());

    public OutlineFrame(String title, boolean showMetadata) {
        this.showMetadata = showMetadata;

        setTitle(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        rootBranch = new BranchNode("", "Root");
        outlineTreeModel = new DefaultTreeModel(rootBranch);

        OutlineModel outlineModel = DefaultOutlineModel.createOutlineModel(
                outlineTreeModel, new NetworkTableRowModel(), true, "Key");

        outline = new Outline();
        outline.setModel(outlineModel);
        AbstractAddDialog.setOutline(outline);
        AbstractTreeNode.setOutline(outline);
        AbstractTreeNode.setTreeModel(outlineTreeModel);

        outline.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TreePath path = outline.getClosestPathForLocation(e.getX(), e.getY());
                AbstractTreeNode node = (AbstractTreeNode) path.getLastPathComponent();
                if (e.getClickCount() == 2) { // toggle boolean value
                    // commented out because the deselection makes double clicking
                    // difficult and annoying to do accurately
                    
//                    if (node instanceof LeafNode) {
//                        LeafNode l = (LeafNode) node;
//                        if (l.getData().getType() == EntryType.BOOLEAN) {
//                            l.getData().setValue(!(boolean) l.getData().getValue());
//                        }
//                    }
                } else if (SwingUtilities.isRightMouseButton(e)) { // create a menu
                    JPopupMenu menu = node.getMenu(path);
                    if (menu != null) menu.show(outline, e.getX(), e.getY());
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                prefs.putInt("WindowX", getX());
                prefs.putInt("WindowY", getY());
                prefs.putInt("Width", getWidth());
                prefs.putInt("Height", getHeight());
            }

            @Override
            public void windowOpened(WindowEvent e) {
                // default to top-left corner
                setLocation(prefs.getInt("WindowX", 0), 
                            prefs.getInt("WindowY", 0));
                // default to 600x400
                setSize(prefs.getInt("Width", 600),
                        prefs.getInt("Height", 400));
            }
        });

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(outline);
        add(scrollPane, BorderLayout.CENTER);

        NetworkTablesJNI.addEntryListener("", new NetworkTablesJNI.EntryListenerFunction() {
            @Override
            public void apply(int uid, String key, Object value, int flags) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getBranchesToEntry(key, value, flags);
                    }
                });
            }},
            ITable.NOTIFY_IMMEDIATE | ITable.NOTIFY_LOCAL | ITable.NOTIFY_NEW | ITable.NOTIFY_DELETE | ITable.NOTIFY_UPDATE /*| ITable.NOTIFY_FLAGS*/ );

    }

    private void getBranchesToEntry(final String fullKey, Object value, int flags) {
        // a list of all the subtable names leading up to the entry in the given key
        // this splits by / and removes empty // elements
        ArrayList<String> subTableNames = splitDiscardingEmpty(fullKey, "/");
        String key = "";
        AbstractTreeNode currentNode = rootBranch, parentNode;
        for (int i = 0; i < subTableNames.size(); i++) {
	    String name = subTableNames.get(i);
            parentNode = currentNode;
            currentNode = currentNode.get(name);
            key += "/" + name;

            if ((flags & ITable.NOTIFY_DELETE) != 0) { // delete
                if (currentNode == null)
                    break;
                if (i == (subTableNames.size() - 1)) { // reached the leaf
                    outlineTreeModel.removeNodeFromParent(currentNode);
                    break;
                }
                continue;
            }

            // new or update
            if (i == (subTableNames.size() - 1)) { // leaf
                if (currentNode == null) {
                    currentNode = new LeafNode(key, new TableEntryData(name, value));
                    if (currentNode.data.isMetadata() && !showMetadata) {
                        // don't show metadata directly
                        // instead, show the value in the branch's "Type" field
                        ((BranchNode) parentNode).updateType(currentNode.data.getValue().toString());
                    } else {
                        outlineTreeModel.insertNodeInto(currentNode, parentNode, parentNode.getChildCount());
                        TreePath path = new TreePath(currentNode.getPath());
                        outline.expandPath(path);
                    }
                } else {
                    ((LeafNode) currentNode).updateValue(value);
                }
            } else if (currentNode == null) {
                currentNode = new BranchNode(key, name);
                outlineTreeModel.insertNodeInto(currentNode, parentNode, parentNode.getChildCount());
            }
        }
    }

    private ArrayList<String> splitDiscardingEmpty(String str, String separator) {
        ArrayList<String> results = new ArrayList<>();
        for (String string : str.split(separator)) {
            if (string.length() > 0) {
                results.add(string);
            }
        }
        return results;
    }
}

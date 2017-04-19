package edu.wpi.first.tableviewer.dialog;


import edu.wpi.first.tableviewer.OutlineFrame;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

/**
 *
 * @author Sam
 */
public class PreferencesDialog extends javax.swing.JDialog {

    private final Preferences prefs = Preferences.userNodeForPackage(getClass());

    public PreferencesDialog() {
        super();
        setTitle("Preferences");
        initComponents();
        hostField.setText(prefs.get("host", "localhost"));
        metadataBox.setSelected(prefs.getBoolean("metadata", false));
        setResizable(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hostField = new javax.swing.JTextField();
        hostLabel = new javax.swing.JLabel();
        metadataBox = new javax.swing.JCheckBox();
        clientButton = new javax.swing.JButton();
        serverButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        hostLabel.setText("Team number or host:");

        metadataBox.setText("Display metadata");

        clientButton.setText("Start Client");
        clientButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonPressed(evt);
            }
        });

        serverButton.setText("Start Server");
        serverButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonPressed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(hostLabel)
                        .addGap(18, 18, 18)
                        .addComponent(hostField))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(clientButton)
                        .addGap(18, 18, 18)
                        .addComponent(serverButton)
                        .addGap(18, 18, 18)
                        .addComponent(cancelButton))
                    .addComponent(metadataBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hostLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(metadataBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(serverButton)
                    .addComponent(clientButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonPressed
        try {
            if (evt.getSource() == clientButton) { // start client
                String host = hostField.getText();
                if (host.isEmpty()) {
                    return;
                } else if (host.matches("\\d+")) {
                    NetworkTable.setTeam(Integer.valueOf(host));
                } else {
                    NetworkTable.setIPAddress(host);
                }
                NetworkTable.setClientMode();
                NetworkTable.initialize();
                prefs.put("host", host);
            } else { // start server
                NetworkTable.setIPAddress("");
                NetworkTable.setServerMode();
                NetworkTable.initialize();
                prefs.put("host", "");
            }
            prefs.putBoolean("metadata", metadataBox.isSelected());
            new OutlineFrame("Network Table Viewer", metadataBox.isSelected()).setVisible(true);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getClass() + ": " + e.getMessage(), "Error creating table node", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_startButtonPressed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        System.exit(0);
    }//GEN-LAST:event_cancelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clientButton;
    private javax.swing.JTextField hostField;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JCheckBox metadataBox;
    private javax.swing.JButton serverButton;
    // End of variables declaration//GEN-END:variables
}

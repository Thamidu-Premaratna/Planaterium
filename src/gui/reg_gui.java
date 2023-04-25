package gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import model.DbConnect;

public class reg_gui extends javax.swing.JFrame {

    private void loadRoles() {
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `role`");
            ResultSet rs = stmt.executeQuery();

            //Creating a new vector to hold the values
            Vector v1 = new Vector();
            v1.add("Select");
            while (rs.next()) {
                //Admin roles cannot be created using this application
                if (!rs.getString("type").equals("Admin")) {
                    v1.add(rs.getString("type"));
                }
            }
            DefaultComboBoxModel dcbm = new DefaultComboBoxModel(v1);
            cb_role.setModel(dcbm);
            DbConnect.connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public reg_gui() {
        initComponents();
        btn_register.requestFocus();
        loadRoles();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tf_uname = new javax.swing.JTextField();
        cb_role = new javax.swing.JComboBox<>();
        pf_password1 = new javax.swing.JPasswordField();
        btn_register = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_sign_in = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        pf_password2 = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Planaterium App v1.0");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 0, 0, 150));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0, 80)));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 204, 204));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("WELCOME TO ");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 204, 204));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("SL PLANTARIUM");

        tf_uname.setForeground(new java.awt.Color(102, 102, 102));
        tf_uname.setText("Enter Username");
        tf_uname.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tf_unameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tf_unameFocusLost(evt);
            }
        });
        tf_uname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_unameActionPerformed(evt);
            }
        });

        cb_role.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));

        btn_register.setBackground(new java.awt.Color(102, 0, 153));
        btn_register.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btn_register.setForeground(new java.awt.Color(255, 255, 255));
        btn_register.setText("Register");
        btn_register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_registerActionPerformed(evt);
            }
        });

        jLabel5.setForeground(new java.awt.Color(102, 0, 153));
        jLabel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 204, 204));
        jLabel6.setText("Already have an account ?");

        label_sign_in.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        label_sign_in.setForeground(new java.awt.Color(102, 102, 255));
        label_sign_in.setText("Sign In");
        label_sign_in.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_sign_inMouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 204, 204));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Account Registration");

        pf_password2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pf_password2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 10, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(label_sign_in, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(47, 47, 47))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                                    .addComponent(tf_uname)
                                    .addComponent(cb_role, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pf_password1)
                                    .addComponent(pf_password2)
                                    .addComponent(btn_register, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(25, 25, 25))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(tf_uname, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cb_role, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pf_password1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pf_password2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(btn_register, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_sign_in, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 80, 330, 530));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/planatorium.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tf_unameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_unameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_unameActionPerformed

    private void btn_registerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_registerActionPerformed
        char passwordArray1[] = pf_password1.getPassword();
        String password1 = String.valueOf(passwordArray1);
        char passwordArray2[] = pf_password2.getPassword();
        String password2 = String.valueOf(passwordArray2);

        if (tf_uname.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your username!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (password1.isEmpty() || password2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password fields cannot be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (!password1.equals(password2)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (cb_role.getSelectedItem().toString().equals("Select")) {
            JOptionPane.showMessageDialog(this, "Please select your job role!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            //Searching the database for potential similar usernames (not allowed). Passwords can be similar (allowed).
            try {
                ResultSet rs = DbConnect.createConnection().prepareStatement("SELECT * FROM `login` WHERE `username` = ?").executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Username already taken!", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
//                        PreparedStatement stmt = DbConnect.createConnection().prepareStatement("INSERT INTO ")
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(this, "User Created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    tf_uname.setText("");
                    cb_role.setSelectedIndex(0);
                    pf_password1.setText("");
                    pf_password2.setText("");
                }
                DbConnect.connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }//GEN-LAST:event_btn_registerActionPerformed

    private void pf_password2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pf_password2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pf_password2ActionPerformed

    private void label_sign_inMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_sign_inMouseClicked
        new login_gui().setVisible(true);
        this.dispose();

    }//GEN-LAST:event_label_sign_inMouseClicked

    private void tf_unameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_unameFocusGained
        tf_uname.setText("");
        tf_uname.setForeground(Color.black);
    }//GEN-LAST:event_tf_unameFocusGained

    private void tf_unameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_unameFocusLost
        if (tf_uname.getText().isBlank() || tf_uname.getText().equals("Enter Username")) {
            tf_uname.setText("Enter Username");
            tf_uname.setForeground(new Color(102, 102, 102));
        } else {
            tf_uname.setForeground(Color.black);
        }
    }//GEN-LAST:event_tf_unameFocusLost

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new reg_gui().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_register;
    private javax.swing.JComboBox<String> cb_role;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_sign_in;
    private javax.swing.JPasswordField pf_password1;
    private javax.swing.JPasswordField pf_password2;
    private javax.swing.JTextField tf_uname;
    // End of variables declaration//GEN-END:variables
}
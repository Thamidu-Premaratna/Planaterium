package gui;

import com.formdev.flatlaf.FlatLightLaf;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import lu.tudor.santec.jtimechooser.JTimeChooser;

//Custom classes imports
import classes.CircularButton;
import classes.RegEx;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import model.DbConnect;
import classes.SeatMap;

public class dashBoard_gui extends javax.swing.JFrame {

//------------------------------------------------------------------------------    
//                              Global variables (instance)
//------------------------------------------------------------------------------ 
    private int currentUserRoleId;
    private int currentEmployeeId;
    private SeatMap seatMap;

    //Arrays that will be used to keep the seat information
    private String[] seatArr = {"A1", "A2", "A3", "A4", "A5", "A6", "A7"}; //Seats available
    private String[] choosenSeatArr; //Seats choosen for Booking

//------------------------------------------------------------------------------    
//                              Common methods
//------------------------------------------------------------------------------ 
//Method to get the sql.Date from a JDateChooser    
    private java.sql.Date getSQLDate(JDateChooser chooser) {
        java.util.Date utilDate = chooser.getDate();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        return sqlDate;
    }

//Method to get the sql.Time from a JTimeChooser    
    private java.sql.Time getSQLTime(JTimeChooser chooser) {
        int hour = chooser.getHours();
        int min = chooser.getMinutes();
        int sec = chooser.getSeconds();

        java.sql.Time sqlTime = new Time(hour, min, sec);
        return sqlTime;
    }

// Method to convert java.util.Date to java.time.LocalDate
    public java.time.LocalDate convertToLocalDate(java.util.Date dateToConvert) {
        return java.time.LocalDate.ofInstant(
                dateToConvert.toInstant(), java.time.ZoneId.systemDefault());
    }
//A method used to validate fields using RegEx
    //Mobile number : (?:7|01|07)(?:0|1|2|4|5|6|7|8)\\d{7}$
    //Pasword : ^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z])[A-Za-z0-9]{5,8}$ - 5 to 8 characters, aleast 1 upper case, 1 lowercase and 1 alphabet, 1 digit, rest are alphaneumerics
    //Username : ^[a-zA-Z]{4,10}$ - 4 to 10 characters, no digits

    public boolean validateRegex(String input, String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
//------------------------------------------------------------------------------    
//                              Employee
//------------------------------------------------------------------------------ 
//Check if the employee exits (using employee-id), returns a boolean to the caller

    private boolean checkEmployeeExists(int empId) {
        boolean exits = false;
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `employee` WHERE `employee_id` = ?");
            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                exits = true;
            }

            DbConnect.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return exits;
    }

//Clear Employee fields
    private void clearEmployeeFields() {
        tf_emp_id.setText("");
        tf_emp_fname.setText("");
        tf_emp_lname.setText("");
        tf_emp_telno.setText("");
        tf_emp_address.setText("");
        cb_emp_role.setSelectedIndex(0);
        dc_emp_dob.setDate(null);
    }

//Load the Employee table dynamically by retrieving them from the database
    private void loadEmployeeTable() {
        try {
            ResultSet rs = DbConnect.createConnection().prepareStatement("SELECT * FROM `employee` INNER JOIN `role` ON `employee`.`role_id` = `role`.`id` INNER JOIN `status` ON `employee`.`status_id` = `status`.`id`").executeQuery();
            DefaultTableModel dtm = (DefaultTableModel) table_emp.getModel();
            dtm.setRowCount(0);
            while (rs.next()) {
                //Create new vector for each record of the table
                Vector v = new Vector();
                v.add(rs.getString("employee_id"));
                v.add(rs.getString("fname"));
                v.add(rs.getString("lname"));
                v.add(rs.getString("dob"));
                v.add(rs.getString("mobile"));
                v.add(rs.getString("address"));
                v.add(rs.getString("role.type"));
                v.add(rs.getString("status.name"));
                dtm.addRow(v);
            }

            DbConnect.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//Load the fields dynamically by retrieving them from the database
    private void loadEmployeeRoles() {
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `role`");
            ResultSet rs = stmt.executeQuery();

            //Creating a new vector to hold the values
            Vector v1 = new Vector();
            v1.add("Select");
            while (rs.next()) {
                v1.add(rs.getString("type"));
            }
            DefaultComboBoxModel dcbm = new DefaultComboBoxModel(v1);
            cb_emp_role.setModel(dcbm);

            DbConnect.closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//Select the employee role for the combo box, when a row of the employee table is selected
    private void loadEmployeeRoles(String type) {
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `role` WHERE `type` = ?");
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                cb_emp_role.setSelectedItem(type);
            }
            DbConnect.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//Insert funtion for employee data
    private int insertEmployeeData(String fname, String lname, JDateChooser dob, String mobile, String address, int roleId) {
        int rowCount = 0;
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("INSERT INTO `employee`(fname,lname,dob,mobile,address,role_id,status_id) VALUES(?,?,?,?,?,?,?)");
            stmt.setString(1, fname);
            stmt.setString(2, lname);
            stmt.setDate(3, getSQLDate(dob));
            stmt.setString(4, mobile);
            stmt.setString(5, address);
            stmt.setInt(6, roleId);
            stmt.setInt(7, 1); // Status is by default 'Active' for new Employees

            //Stores the amount of rows inserted after successful query exceution
            rowCount = stmt.executeUpdate();

            DbConnect.closeConnection();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return rowCount;
    }

//Update function for employee data (Except status change, There is a seperate function below for that)
    private int updateEmployeeData(String fname, String lname, JDateChooser dob, String mobile, String address, int roleId, int employeeId) {
        int rowCount = 0;
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("UPDATE `employee` SET `fname`= ?,`lname`= ?,`dob`= ?,`mobile`= ?,`address`= ?,`role_id`= ? WHERE `employee_id`= ?");
            stmt.setString(1, fname);
            stmt.setString(2, lname);
            stmt.setDate(3, getSQLDate(dob));
            stmt.setString(4, mobile);
            stmt.setString(5, address);
            stmt.setInt(6, roleId);
            stmt.setInt(7, employeeId);

            //Stores the amount of rows inserted after successful query exceution
            rowCount = stmt.executeUpdate();

            DbConnect.closeConnection();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return rowCount;
    }

//Toggle user status function for employee data
    private int toggleEmployeeStatus(int employeeId) { // This only sets the employee `status` to 'Inactive' (Employee data will not be deleted)
        int rowCount = 0;
        try {
            //Finding the current user status  
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `employee` WHERE `employee_id` = ?");
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String query = "UPDATE `employee` SET `status_id` = 1 WHERE `employee_id` = ?";
                if (rs.getInt("status_id") == 1) { // User is currently 'Active'
                    query = "UPDATE `employee` SET `status_id` = 2 WHERE `employee_id` = ?";
                }
                PreparedStatement stmt2 = DbConnect.createConnection().prepareStatement(query);
                stmt2.setInt(1, employeeId);

                //Stores the amount of rows updated/affected after successful query exceution
                rowCount = stmt2.executeUpdate();
            }

            DbConnect.closeConnection();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return rowCount;
    }

//------------------------------------------------------------------------------    
//                              Show
//------------------------------------------------------------------------------
//Check if the show exits (using show-id), returns a boolean to the caller
    private boolean checkShowExists(int showId) {
        boolean exits = false;
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `show` WHERE `show_id` = ?");
            stmt.setInt(1, showId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                exits = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return exits;
    }

//Clear fields
    private void clearShowFields() {
        tf_show_id.setText("");
        tf_show_name.setText("");
        try {
            tc_show_starttime.setTime(new SimpleDateFormat("hh:mm:ss").parse("00:00:00"));
            tc_show_endtime.setTime(new SimpleDateFormat("hh:mm:ss").parse("00:00:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dc_show_date.setDate(null);

    }

//Load the show table dynamically by retrieving them from the database
    private void loadShowTable() {
        try {
            ResultSet rs = DbConnect.createConnection().prepareStatement("SELECT * FROM `show`").executeQuery();
            DefaultTableModel dtm = (DefaultTableModel) table_show.getModel();
            dtm.setRowCount(0);
            while (rs.next()) {
                //Create new vector for each record of the table
                Vector v = new Vector();
                v.add(rs.getString("show_id"));
                v.add(rs.getString("show_name"));
                v.add(rs.getString("start_time"));
                v.add(rs.getString("end_time"));
                v.add(rs.getString("show_date"));
                v.add(rs.getString("employee_id"));
                dtm.addRow(v);
            }
            DbConnect.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//Load the show table dynamically according to a 'search criteria' by retrieving them from the database

    private void loadShowTable(String searchTerm) {
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `show` WHERE `show_id` LIKE ? OR `show_name` LIKE ?");
            stmt.setString(1, searchTerm + "%");
            stmt.setString(2, searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel dtm = (DefaultTableModel) table_show.getModel();
            dtm.setRowCount(0);
            while (rs.next()) {
                //Create new vector for each record of the table
                Vector v = new Vector();
                v.add(rs.getString("show_id"));
                v.add(rs.getString("show_name"));
                v.add(rs.getString("start_time"));
                v.add(rs.getString("end_time"));
                v.add(rs.getString("show_date"));
                v.add(rs.getString("employee_id"));
                dtm.addRow(v);
            }
            DbConnect.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//Insert function for show data
    private int insertShowData(String showName, JTimeChooser startTime, JTimeChooser endTime, JDateChooser showDate, String showImg) {
        int rowCount = 0;
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("INSERT INTO `show`(show_name,start_time,end_time,show_date,show_img,employee_id) VALUES(?,?,?,?,?,?)");
            stmt.setString(1, showName);
            stmt.setTime(2, getSQLTime(startTime));
            stmt.setTime(3, getSQLTime(endTime));
            stmt.setDate(4, getSQLDate(showDate));
            stmt.setString(5, showImg);
            stmt.setInt(6, currentEmployeeId);

            //Stores the amount of rows inserted after successful query exceution
            rowCount = stmt.executeUpdate();

            DbConnect.closeConnection();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return rowCount;
    }

//Update function for show data
    private int updateShowData(String showName, JTimeChooser startTime, JTimeChooser endTime, JDateChooser showDate, String showImg, int showId) {
        int rowCount = 0;
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("UPDATE `show` SET `show_name`=?,`start_time`=?,`end_time`=?,`show_date`=?,`show_img`=? WHERE `show_id`=?");
            stmt.setString(1, showName);
            stmt.setTime(2, getSQLTime(startTime));
            stmt.setTime(3, getSQLTime(endTime));
            stmt.setDate(4, getSQLDate(showDate));
            stmt.setString(5, showImg);
            stmt.setInt(6, showId);

            //Stores the amount of rows inserted after successful query exceution
            rowCount = stmt.executeUpdate();

            DbConnect.closeConnection();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return rowCount;
    }

// Delete function for show data
    private int deleteShowData(int showId) {
        int rowCount = 0;
        try {
            //Initially delete all tickets with the same showId (Foriegn Key constraint)
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("DELETE FROM `ticket` WHERE `show_id` =?");
            stmt.setInt(1, showId);
            rowCount = stmt.executeUpdate();

            //Delete all tickets with the same showId (Foriegn Key constraint)
            PreparedStatement stmt1 = DbConnect.createConnection().prepareStatement("DELETE FROM `show` WHERE `show_id` =?");
            stmt1.setInt(1, showId);
            rowCount += stmt.executeUpdate();

            DbConnect.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowCount; //The ideal row count should be 2, but there could be instances where tickets with the show id will not exist then the row count will be 1
    }
//------------------------------------------------------------------------------    
//                              Seat Booking - (Dashboard)
//------------------------------------------------------------------------------

//Clear some fields in the Dashboard (Seat booking)
    private void clearSeatFields(){
        label_book_total.setText("0.0");
        label_book_balance.setText("0.0");
        tf_book_payment.setText("");
        DefaultTableModel dtm = (DefaultTableModel)table_book.getModel();
        dtm.setRowCount(0);
    }

//Seat the seating color (According to the seat availability)
    private void initSeatAvailability(){
        if(seatMap != null){ //There exists a seat mapping
            
        }
    }
    
//Load the seat types for the combo box
    private void loadSeatType() {
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `seat_type`");
            ResultSet rs = stmt.executeQuery();

            //Creating a new vector to hold the values
            Vector v1 = new Vector();
            v1.add("Select");
            while (rs.next()) {
                v1.add(rs.getString("name"));
            }
            DefaultComboBoxModel dcbm = new DefaultComboBoxModel(v1);
            cb_book_type.setModel(dcbm);

            DbConnect.closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//Load the payment types for the combo box for cb in the Dashboard tab
    private void loadPaymentType() {
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `payment_method`");
            ResultSet rs = stmt.executeQuery();

            //Creating a new vector to hold the values
            Vector v1 = new Vector();
            v1.add("Select");
            while (rs.next()) {
                v1.add(rs.getString("type"));
            }
            DefaultComboBoxModel dcbm = new DefaultComboBoxModel(v1);

            //Dashboard payment type combo-box
            cb_book_paymenttype.setModel(dcbm);

            DbConnect.closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//Automatically calculate the "Total" as the summation of the "price" column in the table when a new row is added or removed.
    private void updateTotal() {
        double total = 0.0;
        for (int row = 0; row < table_book.getRowCount(); row++) {
            double price = Double.parseDouble(table_book.getValueAt(row, 2).toString());
            total += price; //Add the price of each row (ticket) to the total
        }
        label_book_total.setText(String.valueOf(total));
    }

//Add seat details into the booking list (table)
    private void addSeat(String seatNo) {
        /*
        CREATE VIEW seat_map AS
        SELECT 
        r.id AS rid, r.r_date, r.r_time,r.employee_id,r.show_id,r.ticket_id,
        t.id AS tid,s.seat_id AS sid,s.seat_no,
        st.id AS stid,st.price,st.name
        FROM `reservation` r
        INNER JOIN `ticket` t ON r.ticket_id = t.id
        INNER JOIN `seat` s ON t.seat_id = s.seat_id
        INNER JOIN `seat_type` st ON t.seat_type_id = st.id 
         */
        if (cb_book_type.getSelectedItem().toString().equals("Select")) {
            JOptionPane.showMessageDialog(this, "Select a Seat type!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                String seatType = cb_book_type.getSelectedItem().toString();
                //Get details related to the seat type (Adult/Child/Student)
                PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `seat_type` WHERE `name` = ? ");
                stmt.setString(1, seatType);
                ResultSet rs = stmt.executeQuery();

                Vector v = new Vector();
                rs.next();
                v.add(seatNo);
                v.add(seatType);
                v.add(rs.getString("price"));

                DefaultTableModel dtm = (DefaultTableModel) table_book.getModel();
                dtm.addRow(v);

                DbConnect.closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

//Check if the seat is already inserted into the booking - list table (in order to avoid adding the same seat again)
    private Boolean checkSeatDupplicateEntry(String seatNo){
        DefaultTableModel dtm = (DefaultTableModel)table_book.getModel();
        for (int row = 0; row < dtm.getRowCount(); row++) {
            if(dtm.getValueAt(row, 0).toString().equals(seatNo)){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
//------------------------------------------------------------------------------    
//                              Payment-(History)
//------------------------------------------------------------------------------ 

    /*
    The view created for the table with multiple inner joins
    
    CREATE VIEW payment_history AS
    SELECT 
    s.`show_id` AS sid,s.`show_name`,s.`start_time`,s.`end_time`,s.`show_date`,s.`employee_id` AS sempid,
    r.`id` AS rid,r.`r_date`,r.`r_time`,r.`employee_id` AS rempid,r.`show_id` AS rsid,r.`ticket_id`,
    p.`id` AS pid,p.`given`,p.`total_amount`,p.`date`,p.`payment_method_id`,p.`reservation_id`,
    pm.`id` AS pmid,pm.`type`
    FROM `show` s
    INNER JOIN `reservation` r ON s.show_id = r.show_id
    INNER JOIN `payment` p ON r.id = p.reservation_id
    INNER JOIN `payment_method` pm ON p.payment_method_id = pm.id
    
     */
    //Load the payment history table
    private void loadPaymentHistory() {
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `payment_history`");
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel dtm = (DefaultTableModel) table_payment.getModel();
            dtm.setRowCount(0);
            while (rs.next()) {
                //Create new vector for each record of the table
                Vector v = new Vector();
                v.add(rs.getString("p.reservation_id"));
                v.add(rs.getString("s.show_name"));
                v.add(rs.getString("p.total_amount"));
                v.add(rs.getString("pm.type"));
                v.add(rs.getString("p.date"));
                v.add(rs.getString("p.given"));
                v.add(rs.getDouble("p.given") - rs.getDouble("p.total_amount"));
                dtm.addRow(v);
            }
            DbConnect.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Load the payment history table dynamically according to a 'search criteria' by retrieving them from the database

    private void loadPaymentHistory(String searchTerm) {
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `payment_history` WHERE p.reservation_id LIKE ? OR s.show_name LIKE ?");
            stmt.setInt(1, Integer.parseInt(searchTerm));
            stmt.setString(2, searchTerm);
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel dtm = (DefaultTableModel) table_payment.getModel();
            dtm.setRowCount(0);
            while (rs.next()) {
                //Create new vector for each record of the table
                Vector v = new Vector();
                v.add(rs.getString("p.reservation_id"));
                v.add(rs.getString("s.show_name"));
                v.add(rs.getString("p.total_amount"));
                v.add(rs.getString("pm.type"));
                v.add(rs.getString("p.date"));
                v.add(rs.getString("p.given"));
                v.add(rs.getDouble("p.given") - rs.getDouble("p.total_amount"));
                dtm.addRow(v);
            }
            DbConnect.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//Load the payment types for the combo box for cb in the Admin-only payment tab
    private void loadAdminPaymentType() {
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `payment_method`");
            ResultSet rs = stmt.executeQuery();

            //Creating a new vector to hold the values
            Vector v1 = new Vector();
            v1.add("Select");
            while (rs.next()) {
                v1.add(rs.getString("type"));
            }
            DefaultComboBoxModel dcbm = new DefaultComboBoxModel(v1);

            //Admin-only payment history tab payment type combo-box
            cb_pay_type.setModel(dcbm);

            DbConnect.closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//Load the payment type for Admin-only payment combo box after selecting it from the table
    private void loadAdminPaymentType(String type) {
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `payment_method` WHERE `type` =?");
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                cb_pay_type.setSelectedItem(type);
            }

            DbConnect.closeConnection();
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
        }
    }

//Update function for payment data
    private int updatePaymentData(String showName, JTimeChooser startTime, JTimeChooser endTime, JDateChooser showDate, String showImg, int showId) {
        int rowCount = 0;
        try {
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("UPDATE `show` SET `show_name`=?,`start_time`=?,`end_time`=?,`show_date`=?,`show_img`=? WHERE `show_id`=?");
            stmt.setString(1, showName);
            stmt.setTime(2, getSQLTime(startTime));
            stmt.setTime(3, getSQLTime(endTime));
            stmt.setDate(4, getSQLDate(showDate));
            stmt.setString(5, showImg);
            stmt.setInt(6, showId);

            //Stores the amount of rows inserted after successful query exceution
            rowCount = stmt.executeUpdate();

            DbConnect.closeConnection();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return rowCount;
    }

// Delete function for payment data
    private int deletePaymentData(int showId) {
        int rowCount = 0;
        try {
            //Initially delete all tickets with the same showId (Foriegn Key constraint)
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("DELETE FROM `ticket` WHERE `show_id` =?");
            stmt.setInt(1, showId);
            rowCount = stmt.executeUpdate();

            //Delete all tickets with the same showId (Foriegn Key constraint)
            PreparedStatement stmt1 = DbConnect.createConnection().prepareStatement("DELETE FROM `show` WHERE `show_id` =?");
            stmt1.setInt(1, showId);
            rowCount += stmt.executeUpdate();

            DbConnect.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowCount; //The ideal row count should be 2, but there could be instances where tickets with the show id will not exist then the row count will be 1
    }

//------------------------------------------------------------------------------    
//                              Dashboard constructors
//------------------------------------------------------------------------------
    public dashBoard_gui() {
        initComponents();
    }

//Access control (Depending on user-role)
    public dashBoard_gui(int loginType, int currentEmployeeId, String uname, int currentUserRoleId) {
        initComponents();
        //Custom Action-Listerners
        table_book.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                //The "updateTotal" function will be called every time when the table rows are updated.
                if (e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.UPDATE
                        || e.getType() == TableModelEvent.DELETE) {
                    updateTotal();
                }
            }
        });
        //Loading things common to all access levels
        loadShowTable();
        loadSeatType();
        loadPaymentType();
        //Set values to the private variables in this instance of the dashboard (session)
        this.currentEmployeeId = currentEmployeeId;
        this.currentUserRoleId = currentUserRoleId;

        seatMap = new SeatMap(seatArr); // Creating a new seat map object, that has the Hash Map for the seating information

        label_uname.setText(uname);
        tf_emp_id.setEnabled(false);
        tf_show_id.setEditable(false);

        switch (loginType) {
            case 1 -> { // Receptionist logged in, initializations
                btn_employee.setEnabled(false);
                btn_payment.setEnabled(false);
                jtp.setEnabledAt(4, false);
                jtp.setEnabledAt(3, false);
            }
            case 2 -> { //Administrator logged in, initialiations
                loadEmployeeTable();
                loadEmployeeRoles();
                loadPaymentHistory();
                loadAdminPaymentType();
                tf_pay_id.setEnabled(false);
                tf_pay_balance.setEnabled(false);
            }
            default ->
                throw new AssertionError();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        btn_logout = new javax.swing.JButton();
        label_uname = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jtp = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        cb_book_type = new javax.swing.JComboBox<>();
        label_book_showid = new javax.swing.JLabel();
        btn_book_select = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        JScrollPane = new javax.swing.JScrollPane();
        table_book = new javax.swing.JTable();
        btn_book_clear = new javax.swing.JButton();
        btn_book = new javax.swing.JButton();
        btn_book_receipt = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        label_book_total = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        label_book_balance = new javax.swing.JLabel();
        tf_book_payment = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        cb_book_paymenttype = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        btn_a4 = new CircularButton("");
        btn_a2 = new CircularButton("");
        btn_a1 = new CircularButton("");
        btn_a5 = new CircularButton("");
        btn_a6 = new CircularButton("");
        btn_a7 = new CircularButton("");
        btn_a3 = new CircularButton("");
        jLabel54 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        label_show_image = new javax.swing.JLabel();
        btn_show_getImage = new javax.swing.JButton();
        btn_show_insert = new javax.swing.JButton();
        btn_show_update = new javax.swing.JButton();
        btn_show_delete = new javax.swing.JButton();
        btn_show_clear = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        tf_show_id = new javax.swing.JTextField();
        tf_show_name = new javax.swing.JTextField();
        dc_show_date = new com.toedter.calendar.JDateChooser();
        tc_show_endtime = new lu.tudor.santec.jtimechooser.JTimeChooser();
        tc_show_starttime = new lu.tudor.santec.jtimechooser.JTimeChooser();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_show = new javax.swing.JTable();
        tf_show_search = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        tf_pay_search = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_payment = new javax.swing.JTable();
        jLabel55 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        tf_pay_id = new javax.swing.JTextField();
        tf_pay_showname = new javax.swing.JTextField();
        tf_pay_total = new javax.swing.JTextField();
        tf_pay_given = new javax.swing.JTextField();
        tf_pay_balance = new javax.swing.JTextField();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        cb_pay_type = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        dc_pay_date = new com.toedter.calendar.JDateChooser();
        jPanel14 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_emp = new javax.swing.JTable();
        jPanel17 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        tf_emp_id = new javax.swing.JTextField();
        tf_emp_fname = new javax.swing.JTextField();
        tf_emp_lname = new javax.swing.JTextField();
        tf_emp_address = new javax.swing.JTextField();
        tf_emp_telno = new javax.swing.JTextField();
        btn_emp_add = new javax.swing.JButton();
        btn_emp_update = new javax.swing.JButton();
        btn_emp_clear = new javax.swing.JButton();
        btn_emp_status = new javax.swing.JButton();
        cb_emp_role = new javax.swing.JComboBox<>();
        dc_emp_dob = new com.toedter.calendar.JDateChooser();
        jPanel15 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jTextField23 = new javax.swing.JTextField();
        jTextField24 = new javax.swing.JTextField();
        jTextField25 = new javax.swing.JTextField();
        jTextField26 = new javax.swing.JTextField();
        jTextField27 = new javax.swing.JTextField();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_edit_show = new javax.swing.JTable();
        jPanel20 = new javax.swing.JPanel();
        jCalendar1 = new com.toedter.calendar.JCalendar();
        jPanel9 = new javax.swing.JPanel();
        btn_dashboard = new javax.swing.JButton();
        btn_show = new javax.swing.JButton();
        btn_seats = new javax.swing.JButton();
        btn_employee = new javax.swing.JButton();
        btn_payment = new javax.swing.JButton();
        btn_editscreen = new javax.swing.JButton();
        btn_calender = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Planaterium App v1.0");
        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1920, 1080));

        jPanel1.setBackground(java.awt.SystemColor.menu);
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBackground(java.awt.SystemColor.menu);

        btn_logout.setBackground(new java.awt.Color(255, 0, 0));
        btn_logout.setText("Log Out");
        btn_logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_logoutActionPerformed(evt);
            }
        });

        label_uname.setText("Name");

        jLabel56.setText("Username:");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(1272, Short.MAX_VALUE)
                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_uname, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_logout)
                .addContainerGap())
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_logout, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(label_uname)
                    .addComponent(jLabel56))
                .addContainerGap())
        );

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1530, 40));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("jLabel1");
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Now Showing");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel3.setText("Up comming");

        jLabel5.setText("jLabel4");
        jLabel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel9.setText("jLabel4");
        jLabel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setText("jLabel4");
        jLabel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setText("jLabel4");
        jLabel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setText("jLabel4");
        jLabel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(179, 179, 179)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(81, 81, 81)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 751, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(87, 87, 87)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(126, 126, 126)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(81, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(103, 103, 103)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(58, Short.MAX_VALUE))
        );

        jtp.addTab("Dash board", jPanel2);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Seat Booking");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 440, 50));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Seat type");
        jPanel4.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 170, 40));

        cb_book_type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));
        cb_book_type.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_book_typeActionPerformed(evt);
            }
        });
        jPanel4.add(cb_book_type, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 130, 140, 40));

        label_book_showid.setText("N/A");
        label_book_showid.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                label_book_showidPropertyChange(evt);
            }
        });
        jPanel4.add(label_book_showid, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 70, 80, 40));

        btn_book_select.setText("Select Show");
        btn_book_select.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_book_selectActionPerformed(evt);
            }
        });
        jPanel4.add(btn_book_select, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 140, 40));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Show");
        jPanel4.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 172, 40));

        JScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JScrollPaneMouseClicked(evt);
            }
        });

        table_book.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Seat-No", "Type", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_book.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_bookMouseClicked(evt);
            }
        });
        JScrollPane.setViewportView(table_book);

        jPanel4.add(JScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 380, 230));

        btn_book_clear.setBackground(new java.awt.Color(255, 0, 102));
        btn_book_clear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_book_clear.setForeground(new java.awt.Color(255, 255, 255));
        btn_book_clear.setText("CLEAR");
        btn_book_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_book_clearActionPerformed(evt);
            }
        });
        jPanel4.add(btn_book_clear, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 670, 80, 30));

        btn_book.setBackground(new java.awt.Color(0, 204, 102));
        btn_book.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        btn_book.setForeground(new java.awt.Color(255, 255, 255));
        btn_book.setText("BOOK");
        jPanel4.add(btn_book, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 670, 260, 70));

        btn_book_receipt.setBackground(new java.awt.Color(0, 102, 153));
        btn_book_receipt.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_book_receipt.setForeground(new java.awt.Color(255, 255, 255));
        btn_book_receipt.setText("RECEIPT");
        btn_book_receipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_book_receiptActionPerformed(evt);
            }
        });
        jPanel4.add(btn_book_receipt, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 710, 80, 30));

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder("Summary"));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("TOTAL (RS.) :");

        label_book_total.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        label_book_total.setForeground(new java.awt.Color(51, 204, 0));
        label_book_total.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_book_total.setText("0.0");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("PAYMENT (RS.) :");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("BALANCE (RS.) :");

        label_book_balance.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        label_book_balance.setForeground(new java.awt.Color(102, 102, 255));
        label_book_balance.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_book_balance.setText("0.0");

        tf_book_payment.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tf_book_payment.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tf_book_payment.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_book_paymentKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(label_book_balance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_book_total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tf_book_payment, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_book_total, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(tf_book_payment))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_book_balance, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 490, 380, 160));

        jLabel13.setText("Payment Type");
        jPanel4.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 450, 90, 30));

        cb_book_paymenttype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));
        jPanel4.add(cb_book_paymenttype, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 450, 100, 30));

        jButton1.setText("Delete Entry");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(295, 450, 110, 30));

        jPanel5.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 0, 440, 750));

        btn_a4.setBackground(new java.awt.Color(204, 255, 204));
        btn_a4.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        btn_a4.setText("A4");
        btn_a4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_a4ActionPerformed(evt);
            }
        });
        jPanel5.add(btn_a4, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 160, 40, 40));

        btn_a2.setBackground(new java.awt.Color(204, 255, 204));
        btn_a2.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        btn_a2.setText("A2");
        btn_a2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_a2ActionPerformed(evt);
            }
        });
        jPanel5.add(btn_a2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 210, 40, 40));

        btn_a1.setBackground(new java.awt.Color(204, 255, 204));
        btn_a1.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        btn_a1.setText("A1");
        btn_a1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_a1ActionPerformed(evt);
            }
        });
        jPanel5.add(btn_a1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 260, 40, 40));

        btn_a5.setBackground(new java.awt.Color(204, 255, 204));
        btn_a5.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        btn_a5.setText("A5");
        btn_a5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_a5ActionPerformed(evt);
            }
        });
        jPanel5.add(btn_a5, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 170, 40, 40));

        btn_a6.setBackground(new java.awt.Color(204, 255, 204));
        btn_a6.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        btn_a6.setText("A6");
        btn_a6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_a6ActionPerformed(evt);
            }
        });
        jPanel5.add(btn_a6, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 200, 40, 40));

        btn_a7.setBackground(new java.awt.Color(204, 255, 204));
        btn_a7.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        btn_a7.setText("A7");
        btn_a7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_a7ActionPerformed(evt);
            }
        });
        jPanel5.add(btn_a7, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 260, 40, 40));

        btn_a3.setBackground(new java.awt.Color(204, 255, 204));
        btn_a3.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        btn_a3.setText("A3");
        btn_a3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_a3ActionPerformed(evt);
            }
        });
        jPanel5.add(btn_a3, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 180, 40, 40));

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setForeground(new java.awt.Color(255, 255, 255));
        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/seats.png"))); // NOI18N
        jLabel54.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel5.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 910, -1));

        jtp.addTab("Seats", jPanel5);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select Show", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("sansserif", 0, 14))); // NOI18N

        label_show_image.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_show_image.setText("image");
        label_show_image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btn_show_getImage.setBackground(new java.awt.Color(0, 153, 153));
        btn_show_getImage.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_show_getImage.setForeground(new java.awt.Color(255, 255, 255));
        btn_show_getImage.setText("IMPORT");

        btn_show_insert.setBackground(new java.awt.Color(0, 204, 102));
        btn_show_insert.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_show_insert.setForeground(new java.awt.Color(255, 255, 255));
        btn_show_insert.setText("INSERT");
        btn_show_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_show_insertActionPerformed(evt);
            }
        });

        btn_show_update.setBackground(new java.awt.Color(0, 102, 153));
        btn_show_update.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_show_update.setForeground(new java.awt.Color(255, 255, 255));
        btn_show_update.setText("UPDATE");
        btn_show_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_show_updateActionPerformed(evt);
            }
        });

        btn_show_delete.setBackground(new java.awt.Color(204, 0, 51));
        btn_show_delete.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_show_delete.setForeground(new java.awt.Color(255, 255, 255));
        btn_show_delete.setText("DELETE");
        btn_show_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_show_deleteActionPerformed(evt);
            }
        });

        btn_show_clear.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_show_clear.setText("CLEAR");
        btn_show_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_show_clearActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Show Name");

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("Show ID");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("Starting Time");

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel28.setText("Ending Time");

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText("Show date");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                        .addGap(51, 51, 51)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tf_show_id)
                            .addComponent(tf_show_name)
                            .addComponent(dc_show_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tc_show_endtime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tc_show_starttime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(label_show_image, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(btn_show_getImage, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(btn_show_update, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_show_clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(btn_show_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_show_delete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_show_image, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_show_getImage, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(69, 69, 69)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tf_show_id, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_show_name, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tc_show_starttime, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addGap(34, 34, 34)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tc_show_endtime, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addGap(36, 36, 36)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dc_show_date, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_show_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_show_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_show_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_show_update, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        table_show.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Show ID", "Show Name", "Starting Time", "Ending Time", "Show date", "Emp-ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_show.setRowHeight(25);
        table_show.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_showMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(table_show);
        if (table_show.getColumnModel().getColumnCount() > 0) {
            table_show.getColumnModel().getColumn(0).setPreferredWidth(3);
            table_show.getColumnModel().getColumn(5).setPreferredWidth(3);
        }

        tf_show_search.setForeground(new java.awt.Color(102, 102, 102));
        tf_show_search.setText("Search");
        tf_show_search.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tf_show_searchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tf_show_searchFocusLost(evt);
            }
        });
        tf_show_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_show_searchKeyReleased(evt);
            }
        });

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-search-23.png"))); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tf_show_search, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 888, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tf_show_search)
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jtp.addTab("Show", jPanel6);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        tf_pay_search.setText("Search ");
        tf_pay_search.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tf_pay_searchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tf_pay_searchFocusLost(evt);
            }
        });
        tf_pay_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_pay_searchActionPerformed(evt);
            }
        });
        tf_pay_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_pay_searchKeyReleased(evt);
            }
        });

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));

        table_payment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "R-ID", "Show  Name", "Total Amount", "Type", "Date", "Given", "Balance"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_payment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_paymentMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(table_payment);

        jLabel55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-search-23.png"))); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 933, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel55)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_pay_search, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tf_pay_search)
                    .addComponent(jLabel55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Payment", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 14))); // NOI18N

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("5 000 000");
        jLabel32.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("Show Name");

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel34.setText("R-ID");

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel35.setText("Total Amount");

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel36.setText("Payment Type");

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel37.setText("Date");

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel38.setText("Given");

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel39.setText("Balance");

        tf_pay_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_pay_idActionPerformed(evt);
            }
        });

        jButton16.setBackground(new java.awt.Color(0, 102, 153));
        jButton16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton16.setForeground(new java.awt.Color(255, 255, 255));
        jButton16.setText("UPDATE");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton17.setBackground(new java.awt.Color(204, 0, 51));
        jButton17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton17.setForeground(new java.awt.Color(255, 255, 255));
        jButton17.setText("DELETE");

        jButton18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton18.setText("CLEAR");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        cb_pay_type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));

        jLabel10.setText("Earnings this month :");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(tf_pay_showname))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(tf_pay_total))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cb_pay_type, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(dc_pay_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(tf_pay_given))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(tf_pay_balance))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel13Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                                    .addGroup(jPanel13Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(6, 6, 6))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(tf_pay_id)
                        .addContainerGap())))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_pay_id, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_pay_showname, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_pay_total, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(cb_pay_type))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(dc_pay_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_pay_given, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_pay_balance, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(63, 63, 63))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22))))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 17, Short.MAX_VALUE))
        );

        jtp.addTab("Payment", jPanel11);

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        table_emp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Emp-ID", "First Name", "Last Name", "DOB", "Tel No", "Address", "Role", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_emp.setRowHeight(25);
        table_emp.setRowMargin(2);
        table_emp.getTableHeader().setReorderingAllowed(false);
        table_emp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_empMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(table_emp);
        if (table_emp.getColumnModel().getColumnCount() > 0) {
            table_emp.getColumnModel().getColumn(0).setPreferredWidth(3);
        }

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 939, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        jPanel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("Employee Details");

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel41.setText("Emp-ID");

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel42.setText("First Name");

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel43.setText("Last Name");

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel44.setText("Role");

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel45.setText("Address");

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel46.setText("Tel No");

        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel47.setText("DOB");

        btn_emp_add.setBackground(new java.awt.Color(0, 204, 102));
        btn_emp_add.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_emp_add.setForeground(new java.awt.Color(255, 255, 255));
        btn_emp_add.setText("INSERT");
        btn_emp_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_emp_addActionPerformed(evt);
            }
        });

        btn_emp_update.setBackground(new java.awt.Color(0, 102, 153));
        btn_emp_update.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_emp_update.setForeground(new java.awt.Color(255, 255, 255));
        btn_emp_update.setText("UPDATE");
        btn_emp_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_emp_updateActionPerformed(evt);
            }
        });

        btn_emp_clear.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_emp_clear.setText("CLEAR");
        btn_emp_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_emp_clearActionPerformed(evt);
            }
        });

        btn_emp_status.setBackground(new java.awt.Color(204, 0, 51));
        btn_emp_status.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_emp_status.setForeground(new java.awt.Color(255, 255, 255));
        btn_emp_status.setText("T.STATUS");
        btn_emp_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_emp_statusActionPerformed(evt);
            }
        });

        cb_emp_role.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(btn_emp_add, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_emp_update, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btn_emp_status, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                    .addComponent(btn_emp_clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tf_emp_fname, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tf_emp_lname, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cb_emp_role, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tf_emp_address, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tf_emp_telno, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dc_emp_dob, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tf_emp_id, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 10, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tf_emp_id)
                    .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_emp_fname, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_emp_lname, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb_emp_role, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(tf_emp_address, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5))
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_emp_telno, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(dc_emp_dob, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(49, 49, 49)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(btn_emp_status, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_emp_clear, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
                    .addComponent(btn_emp_add, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_emp_update, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(81, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 78, Short.MAX_VALUE))
        );

        jtp.addTab("Employee", jPanel14);

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));

        jPanel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel48.setText("Edit Screening");

        jLabel49.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel49.setText("Show Name");

        jLabel50.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel50.setText("Employee");

        jLabel51.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel51.setText("Date");

        jLabel52.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel52.setText("Languadge");

        jLabel53.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel53.setText("Status");

        jButton24.setBackground(new java.awt.Color(0, 153, 153));
        jButton24.setText("Insert");

        jButton25.setBackground(new java.awt.Color(0, 153, 153));
        jButton25.setText("edit");

        jButton26.setBackground(new java.awt.Color(0, 153, 153));
        jButton26.setText("clear");

        jButton27.setBackground(new java.awt.Color(0, 153, 153));
        jButton27.setText("Delete");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addComponent(jButton24)
                                .addGap(67, 67, 67)
                                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel18Layout.createSequentialGroup()
                                        .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton27))
                                    .addGroup(jPanel18Layout.createSequentialGroup()
                                        .addComponent(jButton26, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel18Layout.createSequentialGroup()
                                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel18Layout.createSequentialGroup()
                                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel18Layout.createSequentialGroup()
                                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel18Layout.createSequentialGroup()
                                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel18Layout.createSequentialGroup()
                                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 42, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField23)
                    .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField24))
                .addGap(18, 18, 18)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField26, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField27, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE))
                .addGap(49, 49, 49)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton24)
                    .addComponent(jButton25)
                    .addComponent(jButton27))
                .addGap(30, 30, 30)
                .addComponent(jButton26)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        table_edit_show.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Show Name", "Employee", "Date", "Language", "Status"
            }
        ));
        jScrollPane4.setViewportView(table_edit_show);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 919, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jScrollPane4)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 220, Short.MAX_VALUE))
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jtp.addTab("Edit Screen", jPanel15);

        jPanel20.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 1298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 694, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jtp.addTab("Calendar", jPanel20);

        jPanel1.add(jtp, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 1350, 780));

        jPanel9.setBackground(new java.awt.Color(0, 153, 153));

        btn_dashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-billboard-23.png"))); // NOI18N
        btn_dashboard.setText("Dash Board");
        btn_dashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_dashboardMouseClicked(evt);
            }
        });
        btn_dashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_dashboardActionPerformed(evt);
            }
        });

        btn_show.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-tv-show-23.png"))); // NOI18N
        btn_show.setText("Show");
        btn_show.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_showMouseClicked(evt);
            }
        });
        btn_show.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_showActionPerformed(evt);
            }
        });

        btn_seats.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-movie-theater-23.png"))); // NOI18N
        btn_seats.setText("Seats");
        btn_seats.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_seatsMouseClicked(evt);
            }
        });
        btn_seats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_seatsActionPerformed(evt);
            }
        });

        btn_employee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-management-23.png"))); // NOI18N
        btn_employee.setText("Employee");
        btn_employee.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_employeeMouseClicked(evt);
            }
        });
        btn_employee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_employeeActionPerformed(evt);
            }
        });

        btn_payment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-transaction-23.png"))); // NOI18N
        btn_payment.setText("Payment");
        btn_payment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_paymentMouseClicked(evt);
            }
        });
        btn_payment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_paymentActionPerformed(evt);
            }
        });

        btn_editscreen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-film-reel-23.png"))); // NOI18N
        btn_editscreen.setText("Edit Screen");
        btn_editscreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_editscreenMouseClicked(evt);
            }
        });
        btn_editscreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_editscreenActionPerformed(evt);
            }
        });

        btn_calender.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-tear-off-calendar-23.png"))); // NOI18N
        btn_calender.setText("Calendar");
        btn_calender.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_calenderMouseClicked(evt);
            }
        });
        btn_calender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_calenderActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_dashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_show, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                    .addComponent(btn_seats, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                    .addComponent(btn_employee, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                    .addComponent(btn_payment, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                    .addComponent(btn_editscreen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                    .addComponent(btn_calender, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(153, 153, 153)
                .addComponent(btn_dashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_seats, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_show, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_payment, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_employee, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_editscreen, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_calender, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(169, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 180, 780));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_book_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_book_clearActionPerformed
        clearSeatFields();
    }//GEN-LAST:event_btn_book_clearActionPerformed

    private void btn_book_receiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_book_receiptActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_book_receiptActionPerformed

    private void btn_show_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_show_updateActionPerformed
        //Check if the show already exists
        if (tf_show_id.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a show!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            //Check if the show exists
            if (checkShowExists(Integer.parseInt(tf_show_id.getText()))) {
                //The employee_id will not be allowed to be changed!
                if (tf_show_name.getText().isEmpty() || tc_show_starttime == null || tc_show_endtime == null || dc_show_date == null) {
                    JOptionPane.showMessageDialog(this, "All fields are mandatory!", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    //Inserting the data using user-defined method
                    String showImg = "";
                    int rowCount = updateShowData(tf_show_name.getText(), tc_show_starttime, tc_show_endtime, dc_show_date, showImg, Integer.parseInt(tf_show_id.getText()));

                    if (rowCount > 0) { // Update successful (rows affected)
                        JOptionPane.showMessageDialog(this, "Show details update!", "Success", JOptionPane.INFORMATION_MESSAGE);

                        //Clear all fields
                        clearShowFields();
                        //Refresh Table after update
                        loadShowTable();
                    } else { // update was unsuccessful
                        JOptionPane.showMessageDialog(this, "Eror occured while updating data!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }

                }
            } else {
                JOptionPane.showMessageDialog(this, "Show doesnot exists!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_show_updateActionPerformed

    private void btn_show_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_show_deleteActionPerformed
        //Check if the show already exists
        if (tf_show_id.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a show!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            //Check if the show exists
            String showId = tf_show_id.getText();
            if (checkShowExists(Integer.parseInt(showId))) {
                int rowCount = deleteShowData(Integer.parseInt(showId));
                if (rowCount >= 1) { // 2 queries will be executed.
                    JOptionPane.showMessageDialog(this, "Show with id:" + showId + " deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    //Clear all fields
                    clearShowFields();
                    //Refresh Table after update
                    loadShowTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Eror occured while deleting data!", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Show doesnot exists!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_show_deleteActionPerformed

    private void tf_pay_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_pay_searchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_pay_searchActionPerformed

    private void tf_pay_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_pay_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_pay_idActionPerformed

    private void btn_dashboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_dashboardMouseClicked
        // TODO add your handling code here:
        jtp.setSelectedIndex(0);
    }//GEN-LAST:event_btn_dashboardMouseClicked

    private void btn_showMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_showMouseClicked

    }//GEN-LAST:event_btn_showMouseClicked

    private void btn_showActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_showActionPerformed
        jtp.setSelectedIndex(2);
    }//GEN-LAST:event_btn_showActionPerformed

    private void btn_seatsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_seatsMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_seatsMouseClicked

    private void btn_employeeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_employeeMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_employeeMouseClicked

    private void btn_employeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_employeeActionPerformed

        jtp.setSelectedIndex(4);
    }//GEN-LAST:event_btn_employeeActionPerformed

    private void btn_paymentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_paymentMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_paymentMouseClicked

    private void btn_paymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_paymentActionPerformed

        jtp.setSelectedIndex(3);
    }//GEN-LAST:event_btn_paymentActionPerformed

    private void btn_editscreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_editscreenMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_editscreenMouseClicked

    private void btn_editscreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editscreenActionPerformed
        // TODO add your handling code here:
        jtp.setSelectedIndex(5);
    }//GEN-LAST:event_btn_editscreenActionPerformed

    private void btn_calenderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_calenderMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_calenderMouseClicked

    private void btn_calenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_calenderActionPerformed
        // TODO add your handling code here:
        jtp.setSelectedIndex(6);
    }//GEN-LAST:event_btn_calenderActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton18ActionPerformed

    private void btn_seatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_seatsActionPerformed
        jtp.setSelectedIndex(1);
    }//GEN-LAST:event_btn_seatsActionPerformed

    private void btn_dashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_dashboardActionPerformed
        jtp.setSelectedIndex(0);
    }//GEN-LAST:event_btn_dashboardActionPerformed

    private void btn_logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_logoutActionPerformed
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Warning", JOptionPane.OK_CANCEL_OPTION);
        if (choice == 0) {
            this.dispose();
            new login_gui().setVisible(true);
        }
    }//GEN-LAST:event_btn_logoutActionPerformed

    private void table_empMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_empMouseClicked
        if (evt.getClickCount() == 2) {
            int r = table_emp.getSelectedRow();
            if (r != -1) {
                tf_emp_id.setText(table_emp.getValueAt(r, 0).toString());
                tf_emp_fname.setText(table_emp.getValueAt(r, 1).toString());
                tf_emp_lname.setText(table_emp.getValueAt(r, 2).toString());
                try {
                    dc_emp_dob.setDate(new SimpleDateFormat("yyyy-MM-dd").parse((String) table_emp.getValueAt(r, 3)));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                tf_emp_telno.setText(table_emp.getValueAt(r, 4).toString());
                tf_emp_address.setText(table_emp.getValueAt(r, 5).toString());
                loadEmployeeRoles(table_emp.getValueAt(r, 6).toString());
            }
        }
    }//GEN-LAST:event_table_empMouseClicked

    private void btn_emp_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_emp_addActionPerformed
        //Regex for validating mobile number (Sri lanka only)
        //Mobile number : (?:7|01|07)(?:0|1|2|4|5|6|7|8)\\d{7}$
        String telno_regex = "^(?:7|01|07)(?:0|1|2|4|5|6|7|8)\\\\d{7}$";

        //EmployeeId is auto matically generated!
        //Check if all fields are filled - Validations, Except the DOB
        if (tf_emp_fname.getText().isEmpty() || tf_emp_lname.getText().isEmpty() || tf_emp_telno.getText().isEmpty() || tf_emp_address.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields except DOB are mandatory!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (!validateRegex(tf_emp_telno.getText(), telno_regex)) {
            JOptionPane.showMessageDialog(this, "Please enter valid mobile number (Sri lanka)!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (cb_emp_role.getSelectedItem().toString().equals("Select")) {
            JOptionPane.showMessageDialog(this, "Please select a role!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (cb_emp_role.getSelectedItem().toString().equals("Admin")) {
            JOptionPane.showMessageDialog(this, "Admin roles cannot be assigned!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            //Inserting the data using user-defined method
            int rowCount = insertEmployeeData(tf_emp_fname.getText(), tf_emp_lname.getText(), dc_emp_dob, tf_emp_telno.getText(), tf_emp_address.getText(), cb_emp_role.getSelectedIndex());

            if (rowCount > 0) { // Insert successful (rows affected)
                JOptionPane.showMessageDialog(this, "Employee details entered!", "Success", JOptionPane.INFORMATION_MESSAGE);

                //Clear all fields
                clearEmployeeFields();
                //Refresh Table after update
                loadEmployeeTable();
            } else { // Insert was unsuccessful
                JOptionPane.showMessageDialog(this, "Eror occured while inserting data!", "Warning", JOptionPane.WARNING_MESSAGE);
            }

        }

    }//GEN-LAST:event_btn_emp_addActionPerformed

    private void btn_emp_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_emp_clearActionPerformed
        clearEmployeeFields();
        loadEmployeeTable();

    }//GEN-LAST:event_btn_emp_clearActionPerformed

    private void btn_emp_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_emp_updateActionPerformed
        //Regex for validating mobile number (Sri lanka only)
        //Mobile number : (?:7|01|07)(?:0|1|2|4|5|6|7|8)\\d{7}$
        String telno_regex = "^(?:7|01|07)(?:0|1|2|4|5|6|7|8)\\\\d{7}$";

        //Check if the user already exists
        if (tf_emp_id.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a User!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            //Check if the user exists
            if (checkEmployeeExists(Integer.parseInt(tf_emp_id.getText()))) {
                //Check if all fields are filled - Validations, Except the DOB
                if (tf_emp_fname.getText().isEmpty() || tf_emp_lname.getText().isEmpty() || tf_emp_telno.getText().isEmpty() || tf_emp_address.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields except DOB are mandatory!", "Warning", JOptionPane.WARNING_MESSAGE);
                } else if (!validateRegex(tf_emp_telno.getText(), telno_regex)) {
                    JOptionPane.showMessageDialog(this, "Please enter valid mobile number (Sri lanka)!", "Warning", JOptionPane.WARNING_MESSAGE);
                } else if (cb_emp_role.getSelectedItem().toString().equals("Select")) {
                    JOptionPane.showMessageDialog(this, "Please select a role!", "Warning", JOptionPane.WARNING_MESSAGE);
                } else if (cb_emp_role.getSelectedItem().toString().equals("Admin") && currentUserRoleId != 1) { //User currently logged in should be an Admin (roleId = 1)
                    JOptionPane.showMessageDialog(this, "Only Admins can set Admin roles!", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {

                    //Updating the data using user-defined method
                    int rowCount = updateEmployeeData(tf_emp_fname.getText(), tf_emp_lname.getText(), dc_emp_dob, tf_emp_telno.getText(), tf_emp_address.getText(), cb_emp_role.getSelectedIndex(), Integer.parseInt(tf_emp_id.getText()));

                    if (rowCount > 0) { // Update successful (rows affected)
                        JOptionPane.showMessageDialog(this, "Employee details updated!", "Success", JOptionPane.INFORMATION_MESSAGE);

                        //Clear all fields
                        clearEmployeeFields();
                        //Refresh Table after update
                        loadEmployeeTable();

                    } else { // Update was unsuccessful
                        JOptionPane.showMessageDialog(this, "Eror occured while updating data!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }

                }
            } else {
                JOptionPane.showMessageDialog(this, "User doesnot exists!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }


    }//GEN-LAST:event_btn_emp_updateActionPerformed

    private void btn_emp_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_emp_statusActionPerformed
        //Check if the user already exists
        if (tf_emp_id.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter/select a User ID!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            //Check if the user exists
            if (checkEmployeeExists(Integer.parseInt(tf_emp_id.getText()))) {
                int rowCount = toggleEmployeeStatus(Integer.parseInt(tf_emp_id.getText()));
                if (rowCount > 0) { // Employee status update successful (rows are affected)
                    JOptionPane.showMessageDialog(this, "Employee status updated!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    //Refresh Table after update
                    loadEmployeeTable();
                } else { // Employee status update unsuccessful
                    JOptionPane.showMessageDialog(this, "Eror occured while updating employee status!", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "User doesnot exists!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_emp_statusActionPerformed

    private void btn_show_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_show_insertActionPerformed
        //ShowId is automatically generated!
        //Check if all fields are filled - Validations
        if (tf_show_name.getText().isEmpty() || tc_show_starttime == null || tc_show_endtime == null || dc_show_date == null) {
            JOptionPane.showMessageDialog(this, "All fields are mandatory!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            //Inserting the data using user-defined method
            String showImg = "";
            int rowCount = insertShowData(tf_show_name.getText(), tc_show_starttime, tc_show_endtime, dc_show_date, showImg);

            if (rowCount > 0) { // Insert successful (rows affected)
                JOptionPane.showMessageDialog(this, "Show details entered!", "Success", JOptionPane.INFORMATION_MESSAGE);

                //Clear all fields
                clearShowFields();
                //Refresh Table after update
                loadShowTable();
            } else { // Insert was unsuccessful
                JOptionPane.showMessageDialog(this, "Eror occured while inserting data!", "Warning", JOptionPane.WARNING_MESSAGE);
            }

        }

    }//GEN-LAST:event_btn_show_insertActionPerformed

    private void btn_show_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_show_clearActionPerformed
        clearShowFields();
        loadShowTable();
    }//GEN-LAST:event_btn_show_clearActionPerformed

    private void table_showMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_showMouseClicked
        if (evt.getClickCount() == 2) {
            int r = table_show.getSelectedRow();
            if (r != -1) {
                tf_show_id.setText(table_show.getValueAt(r, 0).toString());
                tf_show_name.setText(table_show.getValueAt(r, 1).toString());
                try {
                    tc_show_starttime.setTime(new SimpleDateFormat("hh:mm:ss").parse((String) table_show.getValueAt(r, 2)));
                    tc_show_endtime.setTime(new SimpleDateFormat("hh:mm:ss").parse((String) table_show.getValueAt(r, 3)));
                    dc_show_date.setDate(new SimpleDateFormat("yyyy-MM-dd").parse((String) table_show.getValueAt(r, 4)));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                /*
                String imgpath = "";
                try {
                    PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM `show` WHERE `show_id` = ?");
                    stmt.setInt(1, Integer.parseInt(table_show.getValueAt(r, 0).toString()));
                    ResultSet rs = stmt.executeQuery();
                    imgpath = rs.getString("show_img");
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                }
                label_show_image.setIcon(new javax.swing.ImageIcon(getClass().getResource(imgpath)));
                 */
            }
        }
    }//GEN-LAST:event_table_showMouseClicked

    private void tf_show_searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_show_searchKeyReleased
        //Checking if the search field is empty or not, and invoking the respective methods to fill the table
        if (!tf_show_search.getText().isEmpty() || !tf_show_search.getText().equals("Search")) {
            loadShowTable(tf_show_search.getText());
        } else {
            loadShowTable();
        }


    }//GEN-LAST:event_tf_show_searchKeyReleased

    private void tf_show_searchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_show_searchFocusGained
        if (tf_show_search.getText().equals("Search")) {
            tf_show_search.setText("");
        }
        tf_show_search.setForeground(Color.black);
    }//GEN-LAST:event_tf_show_searchFocusGained

    private void tf_show_searchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_show_searchFocusLost
        if (tf_show_search.getText().isBlank()) {
            tf_show_search.setText("Search");
            tf_show_search.setForeground(new Color(102, 102, 102));
        } else {
            tf_show_search.setForeground(Color.black);
        }
    }//GEN-LAST:event_tf_show_searchFocusLost

    private void label_book_showidPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_label_book_showidPropertyChange

    }//GEN-LAST:event_label_book_showidPropertyChange

    private void cb_book_typeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_book_typeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_book_typeActionPerformed

    private void btn_a1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_a1ActionPerformed
        //Check if Seat is available
        String seatNo = btn_a1.getText();
        if (!checkSeatDupplicateEntry(seatNo) && !label_book_showid.getText().isEmpty() && seatMap.getAvailability(seatNo)) { //It will send a "false" if the seat is already "booked"/"occupied"
            //Call the "addSeat" function to add the details into the booking list table in the dashboard
            addSeat(seatNo);
        }
    }//GEN-LAST:event_btn_a1ActionPerformed

    private void btn_a4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_a4ActionPerformed
        //Check if Seat is available
        String seatNo = btn_a4.getText();
        if (!checkSeatDupplicateEntry(seatNo) && !label_book_showid.getText().isEmpty() && seatMap.getAvailability(seatNo)) { //It will send a "false" if the seat is already "booked"/"occupied"
            //Call the "addSeat" function to add the details into the booking list table in the dashboard
            addSeat(seatNo);
        }
    }//GEN-LAST:event_btn_a4ActionPerformed

    private void btn_a2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_a2ActionPerformed
        //Check if Seat is available
        String seatNo = btn_a2.getText();
        if (!checkSeatDupplicateEntry(seatNo) && !label_book_showid.getText().isEmpty() && seatMap.getAvailability(seatNo)) { //It will send a "false" if the seat is already "booked"/"occupied"
            //Call the "addSeat" function to add the details into the booking list table in the dashboard
            addSeat(seatNo);
        }
    }//GEN-LAST:event_btn_a2ActionPerformed

    private void btn_a5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_a5ActionPerformed
        //Check if Seat is available
        String seatNo = btn_a5.getText();
        if (!checkSeatDupplicateEntry(seatNo) && !label_book_showid.getText().isEmpty() && seatMap.getAvailability(seatNo)) { //It will send a "false" if the seat is already "booked"/"occupied"
            //Call the "addSeat" function to add the details into the booking list table in the dashboard
            addSeat(seatNo);
        }
    }//GEN-LAST:event_btn_a5ActionPerformed

    private void btn_a6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_a6ActionPerformed
        //Check if Seat is available
        String seatNo = btn_a6.getText();
        if (!checkSeatDupplicateEntry(seatNo) && !label_book_showid.getText().isEmpty() && seatMap.getAvailability(seatNo)) { //It will send a "false" if the seat is already "booked"/"occupied"
            //Call the "addSeat" function to add the details into the booking list table in the dashboard
            addSeat(seatNo);
        }
    }//GEN-LAST:event_btn_a6ActionPerformed

    private void btn_a7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_a7ActionPerformed
        //Check if Seat is available
        String seatNo = btn_a7.getText();
        if (!checkSeatDupplicateEntry(seatNo) && !label_book_showid.getText().isEmpty() && seatMap.getAvailability(seatNo)) { //It will send a "false" if the seat is already "booked"/"occupied"
            //Call the "addSeat" function to add the details into the booking list table in the dashboard
            addSeat(seatNo);
        }
    }//GEN-LAST:event_btn_a7ActionPerformed

    private void btn_a3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_a3ActionPerformed
        //Check if Seat is available
        String seatNo = btn_a3.getText();
        if (!checkSeatDupplicateEntry(seatNo) && !label_book_showid.getText().isEmpty() && seatMap.getAvailability(seatNo)) { //It will send a "false" if the seat is already "booked"/"occupied"
            //Call the "addSeat" function to add the details into the booking list table in the dashboard
            addSeat(seatNo);
        }
    }//GEN-LAST:event_btn_a3ActionPerformed

    private void btn_book_selectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_book_selectActionPerformed
        new show_select_gui(this).setVisible(true);
    }//GEN-LAST:event_btn_book_selectActionPerformed

    private void tf_pay_searchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_pay_searchFocusGained
        if (tf_pay_search.getText().equals("Search")) {
            tf_pay_search.setText("");
        }
        tf_pay_search.setForeground(Color.black);

    }//GEN-LAST:event_tf_pay_searchFocusGained

    private void tf_pay_searchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_pay_searchFocusLost
        if (tf_pay_search.getText().isBlank()) {
            tf_pay_search.setText("Search");
            tf_pay_search.setForeground(new Color(102, 102, 102));
        } else {
            tf_pay_search.setForeground(Color.black);
        }
    }//GEN-LAST:event_tf_pay_searchFocusLost

    private void tf_pay_searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_pay_searchKeyReleased
        //Checking if the search field is empty or not, and invoking the respective methods to fill the table
        if (!tf_pay_search.getText().isEmpty() || !tf_pay_search.getText().equals("Search")) {
            loadPaymentHistory(tf_pay_search.getText());
        } else {
            loadPaymentHistory();
        }
    }//GEN-LAST:event_tf_pay_searchKeyReleased

    private void table_paymentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_paymentMouseClicked
        if (evt.getClickCount() == 2) {
            int r = table_show.getSelectedRow();
            if (r != -1) {
                tf_pay_id.setText(table_show.getValueAt(r, 0).toString());
                tf_pay_showname.setText(table_show.getValueAt(r, 1).toString());
                tf_pay_total.setText(table_show.getValueAt(r, 2).toString());
                loadPaymentType();
//table_show.getValueAt(r, 4).toString()
                try {
                    dc_pay_date.setDate(new SimpleDateFormat("yyyy-MM-dd").parse((String) table_show.getValueAt(r, 5)));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                tf_pay_given.setText(table_show.getValueAt(r, 6).toString());
                tf_pay_balance.setText(table_show.getValueAt(r, 7).toString());
            }
        }


    }//GEN-LAST:event_table_paymentMouseClicked

    private void tf_book_paymentKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_book_paymentKeyReleased
        if (!tf_book_payment.getText().isEmpty()) {
            label_book_balance.setForeground(new Color(102, 102, 255));
            try {
                double given = Double.parseDouble(tf_book_payment.getText().toString());
                double total = Double.parseDouble(label_book_total.getText().toString());
                String value = String.valueOf(given - total);

                label_book_balance.setText((value));
            } catch (Exception e) {
                label_book_balance.setForeground(Color.red);
                label_book_balance.setText("Invalid");
            }
        }
    }//GEN-LAST:event_tf_book_paymentKeyReleased

    private void JScrollPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JScrollPaneMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_JScrollPaneMouseClicked

    private void table_bookMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_bookMouseClicked
        if (evt.getClickCount() >= 3) {
            int r = table_book.getSelectedRow();
            if (r != -1) {
                DefaultTableModel model = (DefaultTableModel) table_book.getModel();
                model.removeRow(r);
            }
        }
    }//GEN-LAST:event_table_bookMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int r = table_book.getSelectedRow();
        if (r != -1) {
            DefaultTableModel model = (DefaultTableModel) table_book.getModel();
            model.removeRow(r);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton16ActionPerformed

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                new dashBoard_gui().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JButton btn_a1;
    private javax.swing.JButton btn_a2;
    private javax.swing.JButton btn_a3;
    private javax.swing.JButton btn_a4;
    private javax.swing.JButton btn_a5;
    private javax.swing.JButton btn_a6;
    private javax.swing.JButton btn_a7;
    private javax.swing.JButton btn_book;
    private javax.swing.JButton btn_book_clear;
    private javax.swing.JButton btn_book_receipt;
    private javax.swing.JButton btn_book_select;
    private javax.swing.JButton btn_calender;
    private javax.swing.JButton btn_dashboard;
    private javax.swing.JButton btn_editscreen;
    private javax.swing.JButton btn_emp_add;
    private javax.swing.JButton btn_emp_clear;
    private javax.swing.JButton btn_emp_status;
    private javax.swing.JButton btn_emp_update;
    private javax.swing.JButton btn_employee;
    private javax.swing.JButton btn_logout;
    private javax.swing.JButton btn_payment;
    private javax.swing.JButton btn_seats;
    private javax.swing.JButton btn_show;
    private javax.swing.JButton btn_show_clear;
    private javax.swing.JButton btn_show_delete;
    private javax.swing.JButton btn_show_getImage;
    private javax.swing.JButton btn_show_insert;
    private javax.swing.JButton btn_show_update;
    private javax.swing.JComboBox<String> cb_book_paymenttype;
    private javax.swing.JComboBox<String> cb_book_type;
    private javax.swing.JComboBox<String> cb_emp_role;
    private javax.swing.JComboBox<String> cb_pay_type;
    private com.toedter.calendar.JDateChooser dc_emp_dob;
    private com.toedter.calendar.JDateChooser dc_pay_date;
    private com.toedter.calendar.JDateChooser dc_show_date;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTabbedPane jtp;
    private javax.swing.JLabel label_book_balance;
    public javax.swing.JLabel label_book_showid;
    private javax.swing.JLabel label_book_total;
    private javax.swing.JLabel label_show_image;
    private javax.swing.JLabel label_uname;
    private javax.swing.JTable table_book;
    private javax.swing.JTable table_edit_show;
    private javax.swing.JTable table_emp;
    private javax.swing.JTable table_payment;
    private javax.swing.JTable table_show;
    private lu.tudor.santec.jtimechooser.JTimeChooser tc_show_endtime;
    private lu.tudor.santec.jtimechooser.JTimeChooser tc_show_starttime;
    private javax.swing.JTextField tf_book_payment;
    private javax.swing.JTextField tf_emp_address;
    private javax.swing.JTextField tf_emp_fname;
    private javax.swing.JTextField tf_emp_id;
    private javax.swing.JTextField tf_emp_lname;
    private javax.swing.JTextField tf_emp_telno;
    private javax.swing.JTextField tf_pay_balance;
    private javax.swing.JTextField tf_pay_given;
    private javax.swing.JTextField tf_pay_id;
    private javax.swing.JTextField tf_pay_search;
    private javax.swing.JTextField tf_pay_showname;
    private javax.swing.JTextField tf_pay_total;
    private javax.swing.JTextField tf_show_id;
    private javax.swing.JTextField tf_show_name;
    private javax.swing.JTextField tf_show_search;
    // End of variables declaration//GEN-END:variables
}

package classes;

import java.util.HashMap;
import java.util.Map;
import model.DbConnect;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SeatMap {

    private final Map<String, Boolean> seatMap;

    public SeatMap(String[] seatArr) {
        seatMap = new HashMap<>();
        initSeatMap(seatArr);
        /*
        Method for printing the Hash map into console (testing)
        
        for (Map.Entry<String, Boolean> entry : seatMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
       }
        System.out.println(seatMap.get("A1"));
         */
    }

    //Initialize seat Map. All seats will be by default "non-occupied"
    private void initSeatMap(String[] seatArr) {
        for (String seatNo : seatArr) {
            seatMap.put(seatNo, Boolean.TRUE);
        }
    }

    //Set the seat map with information, by using the "show_id" to retrieve information about a specific show ans its seat mapping
    public void setSeatMap(int showId) {
        try {
            /*
            CREATE VIEW seat_map AS
            SELECT 
            r.id AS rid, r.r_date, r.r_time,r.employee_id,r.show_id,r.ticket_id,
            t.id AS tid,s.seat_id AS sid,s.seat_no,
            st.id AS stid,st.price,st.name
            FROM `reservation` r
            INNER JOIN `ticket` t ON r.ticket_id = t.id
            INNER JOIN `seat` s ON t.seat_id = s.seat_id
            INNER JOIN `seat_type` st ON t.seat_type_id = st.id
             */
            PreparedStatement stmt = DbConnect.createConnection().prepareStatement("SELECT * FROM seat_map WHERE `show_id` = ?"); //Retrieving data from the "view" created "seat_map"
            stmt.setInt(1, showId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                seatMap.put(rs.getString("seat_no"), Boolean.FALSE);
            }

            DbConnect.closeConnection();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    //The Hash Map will store the seat number and availability of the seat (boolean value) as a key value pair.
    public void addSeat(String seatNumber, Boolean isAvailable) {
        seatMap.put(seatNumber, isAvailable);
    }

    public Map<String, Boolean> getSeat() {
        return seatMap;
    }

    public void toggleAvailability(String seatNumber) {
        if (seatMap.get(seatNumber)) { //Checks if the seat is occupied or not (key the value associated with the "key" (seaNumber) )
            seatMap.put(seatNumber, Boolean.FALSE); //Change "isAvailable" = False
        } else {// isAvailable = False (seatocuupied)
            seatMap.put(seatNumber, Boolean.TRUE);   //Change "isAvailable" = True
        }
    }

    //Returns true or false (seat availability)
    public Boolean getAvailability(String seatNumber) {
        return seatMap.get(seatNumber); //Sends the "value" (Boolean) related to the "key" (seatNumber)
    }
    
    //Return a seat array with seats that are occupied
    public String[] getOccupiedSeats(){
        String[] seatArr = {""};
        for (Map.Entry<String, Boolean> entry : seatMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
       }
        return seatArr;
    }
}

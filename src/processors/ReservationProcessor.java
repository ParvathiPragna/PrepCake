package processors;

import java.util.*;
import java.util.stream.Collectors;

public class ReservationProcessor {
    public static void main(String[] args) {
        /*
         Constraints:
         - RESERVE userId resourceId start end
           * Reserve resource for interval [start, end).
           * Fail if overlapping ACTIVE reservation exists.
         - CANCEL userId resourceId start end
           * Cancel reservation if owned by user and matches interval.
         - GET_RESERVATIONS resourceId currentTime
           * Return all ACTIVE reservations for resource not expired (currentTime < end).
         - GET_USER_RESERVATIONS userId currentTime
           * Return all ACTIVE reservations for user not expired.
         - Expiration: reservations automatically expire after endTime.
         - Status: ACTIVE, CANCELLED, EXPIRED.
        */

        String[][] queries = {
                {"RESERVE","alice","room1","1","5","0"},   // success
                {"RESERVE","bob","room1","3","6","1"},     // conflict -> false
                {"RESERVE","bob","room2","2","4","2"},     // success
                {"GET_RESERVATIONS","room1","3"},          // alice active
                {"CANCEL","alice","room1","1","5","4"},    // cancel
                {"GET_RESERVATIONS","room1","4"},          // empty
                {"GET_USER_RESERVATIONS","bob","3"}        // bob’s room2 active
        };

        // Expected outputs for validation
        String[] expected = {
                "true",   // alice reserve room1
                "false",  // bob conflict on room1
                "true",   // bob reserve room2
                "alice[1-5]", // active reservation for room1 at time=3
                "true",   // cancel alice’s reservation
                "",       // no active reservations for room1 at time=4
                "room2[2-4]" // bob’s reservation still active at time=3
        };

        ReservationSystem rs = new ReservationSystem();
        String[] results = rs.processQueries(queries);

        System.out.println("=== Test Results ===");
        boolean allPass = true;
        for (int i = 0; i < queries.length; i++) {
            System.out.println(Arrays.toString(queries[i]) + " -> " + results[i] +
                    " | expected: " + expected[i]);
            if (!Objects.equals(results[i], expected[i])) {
                allPass = false;
            }
        }
        System.out.println(allPass ? "All test cases PASSED" : "Some test cases FAILED");
    }
}
class ReservationSystem {
    Map<String,List<Reservation>> rooms = new HashMap<>();
    Map<String, List<Reservation>> usersMap = new HashMap<>();
    public String[] processQueries(String[][] queries) {
        String[] output = new String[queries.length];
        for(int i = 0; i< queries.length; i++) {
            switch(queries[i][0]) {
                case "RESERVE" -> output[i] = reserveRoomForUser(queries[i][1],queries[i][2],Integer.valueOf(queries[i][3]),Integer.valueOf(queries[i][4]),Integer.valueOf(queries[i][5]));
                case "CANCEL" -> output[i] = cancelReservation(queries[i][1],queries[i][2],Integer.valueOf(queries[i][3]),Integer.valueOf(queries[i][4]));
                case "GET_RESERVATIONS" -> output[i] = getReservationsForRoom(queries[i][1],Integer.valueOf(queries[i][2]));
                case "GET_USER_RESERVATIONS" ->output[i] =  getUserReservations(queries[i][1],Integer.valueOf(queries[i][2]));
            }
        }
        return output;
    }

    String getUserReservations(String user, Integer time) {
        return usersMap.getOrDefault(user,new ArrayList<>())
                .stream()
                .filter(reservation -> reservation.status.equals(Reservation.Status.ACTIVE))
                .filter(reservation -> !reservation.expire(time))
                .filter(reservation -> reservation.isReservationActiveAtTime(time))
                .map(Reservation::getReservationForUser)
                .collect(Collectors.joining(","));    }

    String getReservationsForRoom(String room, Integer time) {
        return rooms.getOrDefault(room,new ArrayList<>())
                .stream()
                .filter(reservation -> reservation.status.equals(Reservation.Status.ACTIVE))
                .filter(reservation -> !reservation.expire(time))
                .filter(reservation -> reservation.isReservationActiveAtTime(time))
                .map(Reservation::getReservationForRoom)
                .collect(Collectors.joining(","));
    }

    String reserveRoomForUser(String user, String room, Integer start, Integer end, Integer bookingTime) {
        var list = rooms.getOrDefault(room,new ArrayList<Reservation>());
        //check if active reservation exists
        var activeReservationsOverlap = list.stream()
                        .filter(reservation1 -> reservation1.status.equals(Reservation.Status.ACTIVE))
                        .filter(reservation2 -> !reservation2.expire(bookingTime))
                        .filter(reservation3 -> reservation3.doesOverlapWithTime(start,end))
                .toList();
        if(activeReservationsOverlap.isEmpty()) {
            var reservation = new Reservation(user,room,start,end);
            list.add(reservation);
            var userReservations = usersMap.getOrDefault(user,new ArrayList<Reservation>());
            userReservations.add(reservation);
            usersMap.put(user,userReservations);
            rooms.put(room,list);
            return "true";
        }
        return  "false";
    }

    String cancelReservation(String user,String room, Integer start, Integer end) {
        var list = usersMap.getOrDefault(user,new ArrayList<Reservation>());
        var cancelledReservations = list.stream()
                .filter(reservation1 -> reservation1.room.equals(room))
                .filter(reservation2 -> reservation2.cancel(user,room,start,end))
                .toList();
        if (!cancelledReservations.isEmpty()) {
            return "true";
        }
        return "false";
    }
}

class Reservation {
    String user;
    String room;
    Integer startTime;
    Integer endTime;

    Status status;
    enum Status {ACTIVE, CANCELLED, EXPIRED}

    Reservation(String user,String room,Integer startTime,Integer endTime) {
        this.status = Status.ACTIVE;
        this.user = user;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    boolean cancel(String user,String room,Integer startTime,Integer endTime) {
        if(this.status.equals(Status.ACTIVE)
                && Objects.equals(this.user, user)
                && Objects.equals(this.room, room)
                && Objects.equals(this.startTime, startTime)
                && Objects.equals(this.endTime,endTime)) {
            this.status = Status.CANCELLED;
            return  true;
        }
        return false;
    }

    boolean expire(Integer currentTime) {
        if(endTime <= currentTime && this.status == Status.ACTIVE) {
            this.status = Status.EXPIRED;
            return true;
        }
        return false;
    }

    boolean doesOverlapWithTime(Integer startTime, Integer endTime){
        if(this.startTime >= endTime || this.endTime <= startTime) {
            return  false;
        }
        return true;
    }

    String getReservationForRoom() {
        return user + "[" + startTime + "-" + endTime + "]";
    }

    String getReservationForUser() {
        return room+ "[" + startTime + "-" + endTime + "]";
    }

    boolean isReservationActiveAtTime(Integer time) {
        if(!status.equals(Status.CANCELLED)) {
            if(time >= startTime && time<=endTime) {
                return true;
            }
        }
        return false;
    }
}
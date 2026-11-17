package processors;


import java.util.*;
import java.util.stream.Collectors;

/*/*
===========================================================
LEVEL 1 - Meeting Room Scheduler
===========================================================

You are building a meeting room booking system.

The company owns multiple meeting rooms.

Each room can have multiple bookings, but bookings
for the same room cannot overlap.

Time intervals follow:

[startTime, endTime)

meaning:

10-20
20-30

DO NOT overlap.


-----------------------------------------------------------
Operation 1
-----------------------------------------------------------

["ADD_ROOM", roomId, capacity]

Add a meeting room.

Return:
"true"  -> room created
"false" -> room already exists


Example:

ADD_ROOM A 10

Result:
true


-----------------------------------------------------------
Operation 2
-----------------------------------------------------------

["BOOK", roomId, startTime, endTime]

Book a room.

A booking succeeds only if there is no existing
booking overlapping the requested interval.

Return:
"success"  -> booking added
"conflict" -> overlap detected
""         -> room not found


Overlap examples:

Existing:
[10,20)

Request:
[15,25)

Conflict:
YES


Existing:
[10,20)

Request:
[20,30)

Conflict:
NO


-----------------------------------------------------------
Operation 3
-----------------------------------------------------------

["GET_BOOKINGS", roomId]

Return the count of successful bookings for the room.

Only completed bookings matter.

If room doesn't exist:

return ""

Example:

Room A:

[10,20)
[30,40)

Return:

2


-----------------------------------------------------------
Example
-----------------------------------------------------------

Input:

ADD_ROOM A 10
BOOK A 10 20
BOOK A 15 25
BOOK A 20 30
GET_BOOKINGS A

Output:

true
success
conflict
success
2


-----------------------------------------------------------
Expected Data Model
-----------------------------------------------------------

class Room {
    String roomId;
    int capacity;
    List<Booking> bookings;
}

class Booking {
    int start;
    int end;
}


-----------------------------------------------------------
Interview Goal
-----------------------------------------------------------

Implement:

1. ADD_ROOM
2. BOOK
3. GET_BOOKINGS

Keep code extensible because later levels will add:

- CANCEL_BOOKING
- RECURRING_BOOKINGS
- MAINTENANCE_WINDOWS
- UTILIZATION_REPORTS

===========================================================

/*
===========================================================
LEVEL 1 - Meeting Room Scheduler
===========================================================

You are building a meeting room booking system.

The company owns multiple meeting rooms.

Each room can have multiple bookings, but bookings
for the same room cannot overlap.

Time intervals follow:

[startTime, endTime)

meaning:

10-20
20-30

DO NOT overlap.


-----------------------------------------------------------
Operation 1
-----------------------------------------------------------

["ADD_ROOM", roomId, capacity]

Add a meeting room.

Return:
"true"  -> room created
"false" -> room already exists


Example:

ADD_ROOM A 10

Result:
true


-----------------------------------------------------------
Operation 2
-----------------------------------------------------------

["BOOK", roomId, startTime, endTime]

Book a room.

A booking succeeds only if there is no existing
booking overlapping the requested interval.

Return:
"success"  -> booking added
"conflict" -> overlap detected
""         -> room not found


Overlap examples:

Existing:
[10,20)

Request:
[15,25)

Conflict:
YES


Existing:
[10,20)

Request:
[20,30)

Conflict:
NO


-----------------------------------------------------------
Operation 3
-----------------------------------------------------------

["GET_BOOKINGS", roomId]

Return the count of successful bookings for the room.

Only completed bookings matter.

If room doesn't exist:

return ""

Example:

Room A:

[10,20)
[30,40)

Return:

2


-----------------------------------------------------------
Example
-----------------------------------------------------------

Input:

ADD_ROOM A 10
BOOK A 10 20
BOOK A 15 25
BOOK A 20 30
GET_BOOKINGS A

Output:

true
success
conflict
success
2


-----------------------------------------------------------
Expected Data Model
-----------------------------------------------------------

class Room {
    String roomId;
    int capacity;
    List<Booking> bookings;
}

class Booking {
    int start;
    int end;
}


-----------------------------------------------------------
Interview Goal
-----------------------------------------------------------

Implement:

1. ADD_ROOM
2. BOOK
3. GET_BOOKINGS

Keep code extensible because later levels will add:

- CANCEL_BOOKING
- RECURRING_BOOKINGS
- MAINTENANCE_WINDOWS
- UTILIZATION_REPORTS

===========================================================

 */


public class BookingRoom {

    public static void main(String[] args) {

     //   testLevel1();

       // testLevel2();

        testLevel3();

        testLevel4();
    }

    static void testLevel1() {

        String[][] input = {
                {"ADD_ROOM", "A", "10"},
                {"ADD_ROOM", "A", "10"},
                {"BOOK", "A", "10", "20"},
                {"BOOK", "A", "15", "25"},
                {"GET_BOOKINGS", "A"}
        };

        String[] expected = {
                "true",
                "false",
                "success",
                "conflict",
                "1"
        };

        runTest(
                "Level1",
                input,
                expected
        );
    }

    static void testLevel2() {

        String[][] input = {
                {"ADD_ROOM", "A", "10"},
                {"ADD_ROOM", "B", "10"},
                {"BOOK", "A", "10", "20"},
                {"AVAILABLE_ROOMS", "12", "15"}
        };

        String[] expected = {
                "true",
                "true",
                "success",
                "B"
        };

        runTest(
                "Level2",
                input,
                expected
        );
    }

    static void testLevel3() {

        String[][] input = {
                {"ADD_ROOM", "A", "10"},
                {"BOOK", "A", "M1", "10", "20"},
                {"BOOK", "A", "M2", "25", "35"},
                {"TOTAL_BOOKED_TIME", "A"},
                {"CANCEL", "M1"},
                {"TOTAL_BOOKED_TIME", "A"}
        };

        String[] expected = {
                "true",
                "success",
                "success",
                "20",
                "success",
                "10"
        };

        runTest(
                "Level3",
                input,
                expected
        );
    }

    static void testLevel4() {

        String[][] input = {
                {"ADD_ROOM", "A", "10"},
                {"BLOCK_ROOM", "A", "50", "100"},
                {"BOOK", "A", "M1", "60", "80"},
                {"BOOK", "A", "M2", "10", "20"}
        };

        String[] expected = {
                "true",
                "success",
                "conflict",
                "success"
        };

        runTest(
                "Level4",
                input,
                expected
        );
    }

    static void runTest(
            String name,
            String[][] operations,
            String[] expected) {

        MeetingRoomManager manager =
                new MeetingRoomManager();

        String[] actual =
                manager.process(operations);

        boolean passed =
                Arrays.equals(expected, actual);

        System.out.println(
                (passed ? "✅ " : "❌ ")
                        + name);

        if (!passed) {

            System.out.println(
                    "Expected="
                            + Arrays.toString(expected));

            System.out.println(
                    "Actual="
                            + Arrays.toString(actual));
        }
    }
}

class MeetingRoomManager {

    Map<String, Room> rooms = new HashMap<>();
    Map<String ,Room> meetings = new HashMap<>();

    public String[] process(String[][] operations) {
        String[] output = new String[operations.length];
        for (int i = 0; i < operations.length; i++) {
            output[i] = switch (operations[i][0]) {
                case "ADD_ROOM" -> addRoom(operations[i][1], Integer.parseInt(operations[i][2]));
                case "BOOK" ->
                        bookRoom(operations[i][1],operations[i][2], Integer.parseInt(operations[i][3]), Integer.parseInt(operations[i][4]));
                case "GET_BOOKINGS" -> getBookings(operations[i][1]);
                case "AVAILABLE_ROOMS" ->
                        getAvailableRooms(Integer.parseInt(operations[i][1]), Integer.parseInt(operations[i][2]));
                case "CANCEL" -> cancelMeeting(operations[i][1]);
                case "TOTAL_BOOKED_TIME" -> totalBookedTime(operations[i][1]);
                case "BLOCK_ROOM" -> blockRoom(operations[i][1],Integer.parseInt(operations[i][2]),Integer.parseInt(operations[i][3]));
                default -> "false";
            };
        }
        return output;
    }

    private String blockRoom(String roomId, int startTime, int endTime) {
        if (!rooms.containsKey(roomId)) {
            return "";
        }
        rooms.get(roomId).addMaintaenceWindow(startTime,endTime);
        return "success";
    }

    private String totalBookedTime(String roomId) {
        if (!rooms.containsKey(roomId)) {
            return "";
        }
        var duration =  rooms.get(roomId)
                .bookings
                .values()
                .stream()
                .mapToInt(booking -> (booking.endTime - booking.startTime))
                .sum();
        return String.valueOf(duration);
    }

    private String cancelMeeting(String meetingId) {
        if(!meetings.containsKey(meetingId)) {
            return "failure";
        }

        var room = meetings.get(meetingId);
        room.cancel(meetingId);
        meetings.remove(meetingId);
        return "success";
    }

    private String getAvailableRooms(int startTime, int endTime) {
        return rooms.values().stream()
                .filter(room -> room.isAvailable(startTime, endTime))
                .map(room -> room.roomId)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining(","));
    }

    private String getBookings(String roomId) {
        if (!rooms.containsKey(roomId)) {
            return "false";
        }

        var room = rooms.get(roomId);
        return String.valueOf(room.bookings.size());
    }

    private String bookRoom(String roomId, String meetingId, int startTime, int endTime) {
        if (!rooms.containsKey(roomId) || meetings.containsKey(meetingId)) {
            return "false";
        }
        var room = rooms.get(roomId);
        if(room.reserve(meetingId,startTime, endTime)) {
            meetings.put(meetingId,room);
            return "success";
        }
        else {
            return "conflict";
        }
    }

    private String addRoom(String roomId, int capacity) {
        if (rooms.containsKey(roomId)) {
            return "false";
        }
        rooms.put(roomId, new Room(roomId, capacity));
        return "true";
    }
}

class Room {
    String roomId;
    Integer capacity;
    Map<String, Booking> bookings;

    List<Booking> maintence;

    Room(String roomId, Integer capacity) {
        this.roomId = roomId;
        this.capacity = capacity;
        this.bookings = new HashMap<>();
        this.maintence = new ArrayList<>();
    }

    boolean reserve(String meetingId, Integer startTime, Integer endTime) {
        if (!isAvailable(startTime, endTime)) {
            return false;
        }
        bookings.put(meetingId,new Booking(startTime, endTime));
        return true;
    }

    boolean isAvailable(Integer startTime, Integer endTime) {
        for (var booking : bookings.values()) {
            if (booking.doesItOverLap(startTime, endTime)) {
                return false;
            }
        }
        for(var blocker : maintence) {
            if(blocker.doesItOverLap(startTime,endTime)) {
                return false;
            }
        }
        return true;
    }
    void addMaintaenceWindow(Integer startTime, Integer endTime) {
        maintence.add(new Booking(startTime,endTime));
    }

    void cancel(String meetingId) {
        if(bookings.containsKey(meetingId)) {
            bookings.remove(meetingId);
        }
    }
}

class Booking {
    Integer startTime;
    Integer endTime;

    Booking(Integer startTime, Integer endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    boolean doesItOverLap(Integer queryStart, Integer queryEnd) {
        return !(queryStart >= endTime || queryEnd <= startTime);
    }
}
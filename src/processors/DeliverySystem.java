package processors;

import java.util.*;
import java.util.stream.Collectors;

public class DeliverySystem {

    /*
    ===========================================================
    INVENTORY MANAGEMENT SYSTEM
    ===========================================================

    LEVEL 1
    -----------------------------------------------------------

    Warehouse inventory management.

    Operations

    -----------------------------------------------------------
    ["ADD_PRODUCT", productId, quantity]

    Return

    true
    false

    false if product already exists

    -----------------------------------------------------------
    ["ADD_STOCK", productId, quantity]

    Increase inventory.

    Return updated stock.

    Missing product:

    invalid_request

    -----------------------------------------------------------
    ["REMOVE_STOCK", productId, quantity]

    Decrease inventory.

    Rules:

    stock cannot go below zero

    Return:

    updated stock

    OR

    insufficient_stock

    -----------------------------------------------------------
    ["GET_STOCK", productId]

    Return current stock.

    Missing product:

    ""

    ===========================================================

    LEVEL 2
    -----------------------------------------------------------

    Product Reservations

    Stock can be reserved.

    Reserved stock cannot be reserved again.

    Available Stock:

    stock - activeReservations

    -----------------------------------------------------------
    ["RESERVE", orderId, productId, quantity]

    Return

    success
    insufficient_stock
    invalid_request

    -----------------------------------------------------------
    ["GET_RESERVED", productId]

    Return total active reservation quantity

    Missing product:

    ""

    ===========================================================

    LEVEL 3
    -----------------------------------------------------------

    Order Fulfillment

    -----------------------------------------------------------
    ["FULFILL", orderId]

    Fulfill reserved inventory.

    Rules:

    Reservation must exist
    Reservation must not already be fulfilled

    Return

    success
    invalid_request

    -----------------------------------------------------------
    ["TOP_PRODUCTS", n]

    Rank products by fulfilled quantity.

    Sort:

    fulfilledQuantity DESC
    productId ASC

    Format

    P1(100),P2(80)

    ===========================================================

    LEVEL 4
    -----------------------------------------------------------

    Reservation Expiration

    Reservation expires after 48 hours.

    -----------------------------------------------------------
    ["RESERVE",
        orderId,
        productId,
        quantity,
        timestamp]

    -----------------------------------------------------------
    ["RELEASE_EXPIRED", currentTimestamp]

    Release all expired reservations.

    Return number released.

    -----------------------------------------------------------
    ["AVAILABLE_STOCK", productId]

    Return

    stock - activeReservations

    Missing product:

    ""

    ===========================================================
    IMPLEMENT BELOW:

    class InventoryManager {

        String[] process(String[][] operations)
    }

    Suggested Models:

    Product
    Reservation

    ===========================================================
    */

    public static void main(String[] args) {

        // LEVEL 1
        testLevel1Basic();
        testLevel1InsufficientStock();
        testLevel1MissingProduct();

        // LEVEL 2
        testLevel2Basic();
        testLevel2StockAccounting();

        // LEVEL 3
        testLevel3Basic();
        testLevel3DoubleFulfill();
        testLevel3TieBreaker();

        // LEVEL 4
        testLevel4SingleExpiry();
        testLevel4MultipleExpiry();
        testLevel4FulfilledNotExpired();

        //hidden
        testHiddenMixedFulfillAndExpire();
        testHiddenMultipleReleaseRuns();
        testHiddenUnknownFulfill();
        testHiddenAvailableAfterExpiry();
        testHiddenAvailableAfterFulfill();
        testHiddenAvailableMissingProduct();
        testHiddenExpiryBoundary48Hours();
        testHiddenReserveDuplicateOrderId();
        testHiddenTopProductsZeroFulfilled();
        testHiddenRemoveReservedStock();
    }

    // ======================================================
    // LEVEL 1
    // ======================================================

    static void testLevel1Basic() {

        String[][] input = {

                {"ADD_PRODUCT", "P1", "100"},

                {"ADD_STOCK", "P1", "50"},

                {"REMOVE_STOCK", "P1", "30"},

                {"GET_STOCK", "P1"}};

        String[] expected = {

                "true",

                "150",

                "120",

                "120"};

        runTest("LEVEL1_BASIC", input, expected);
    }

    static void testLevel1InsufficientStock() {

        String[][] input = {

                {"ADD_PRODUCT", "P1", "10"},

                {"REMOVE_STOCK", "P1", "20"}};

        String[] expected = {

                "true",

                "insufficient_stock"};

        runTest("LEVEL1_INSUFFICIENT", input, expected);
    }

    static void testLevel1MissingProduct() {

        String[][] input = {

                {"GET_STOCK", "UNKNOWN"}};

        String[] expected = {

                ""};

        runTest("LEVEL1_MISSING", input, expected);
    }

    // ======================================================
    // LEVEL 2
    // ======================================================

    static void testLevel2Basic() {

        String[][] input = {

                {"ADD_PRODUCT", "P1", "100"},

                {"RESERVE", "O1", "P1", "30"},

                {"RESERVE", "O2", "P1", "40"},

                {"GET_RESERVED", "P1"}};

        String[] expected = {

                "true",

                "success",

                "success",

                "70"};

        runTest("LEVEL2_BASIC", input, expected);
    }

    // Hidden-test style inventory accounting

    static void testLevel2StockAccounting() {

        String[][] input = {

                {"ADD_PRODUCT", "P1", "100"},

                {"RESERVE", "O1", "P1", "70"},

                {"RESERVE", "O2", "P1", "40"}};

        String[] expected = {

                "true",

                "success",

                "insufficient_stock"};

        runTest("LEVEL2_STOCK_ACCOUNTING", input, expected);
    }

    // ======================================================
    // LEVEL 3
    // ======================================================

    static void testLevel3Basic() {

        String[][] input = {

                {"ADD_PRODUCT", "P1", "100"}, {"ADD_PRODUCT", "P2", "100"},

                {"RESERVE", "O1", "P1", "40"}, {"RESERVE", "O2", "P1", "10"}, {"RESERVE", "O3", "P2", "30"},

                {"FULFILL", "O1"}, {"FULFILL", "O2"}, {"FULFILL", "O3"},

                {"TOP_PRODUCTS", "2"}};

        String[] expected = {

                "true", "true",

                "success", "success", "success",

                "success", "success", "success",

                "P1(50),P2(30)"};

        runTest("LEVEL3_BASIC", input, expected);
    }

    static void testLevel3DoubleFulfill() {

        String[][] input = {

                {"ADD_PRODUCT", "P1", "100"},

                {"RESERVE", "O1", "P1", "50"},

                {"FULFILL", "O1"},

                {"FULFILL", "O1"}};

        String[] expected = {

                "true",

                "success",

                "success",

                "invalid_request"};

        runTest("LEVEL3_DOUBLE_FULFILL", input, expected);
    }

    static void testLevel3TieBreaker() {

        String[][] input = {

                {"ADD_PRODUCT", "B", "100"}, {"ADD_PRODUCT", "A", "100"},

                {"RESERVE", "O1", "A", "50"}, {"RESERVE", "O2", "B", "50"},

                {"FULFILL", "O1"}, {"FULFILL", "O2"},

                {"TOP_PRODUCTS", "2"}};

        String[] expected = {

                "true", "true",

                "success", "success",

                "success", "success",

                "A(50),B(50)"};

        runTest("LEVEL3_TIE_BREAKER", input, expected);
    }

    // ======================================================
    // LEVEL 4
    // ======================================================

    static void testLevel4SingleExpiry() {

        String[][] input = {

                {"ADD_PRODUCT", "P1", "100"},

                {"RESERVE", "O1", "P1", "50", "10"},

                {"RELEASE_EXPIRED", "70"},

                {"AVAILABLE_STOCK", "P1"}};

        String[] expected = {

                "true",

                "success",

                "1",

                "100"};

        runTest("LEVEL4_SINGLE_EXPIRY", input, expected);
    }

    static void testLevel4MultipleExpiry() {

        String[][] input = {

                {"ADD_PRODUCT", "P1", "100"},

                {"RESERVE", "O1", "P1", "10", "5"}, {"RESERVE", "O2", "P1", "20", "10"}, {"RESERVE", "O3", "P1", "30", "60"},

                {"RELEASE_EXPIRED", "70"},

                {"AVAILABLE_STOCK", "P1"}};

        String[] expected = {

                "true",

                "success", "success", "success",

                "2",

                "70"};

        runTest("LEVEL4_MULTI_EXPIRY", input, expected);
    }

    // Strong hidden test

    static void testLevel4FulfilledNotExpired() {

        String[][] input = {

                {"ADD_PRODUCT", "P1", "100"},

                {"RESERVE", "O1", "P1", "50", "5"},

                {"FULFILL", "O1"},

                {"RELEASE_EXPIRED", "100"},

                {"AVAILABLE_STOCK", "P1"}};

        String[] expected = {

                "true",

                "success",

                "success",

                "0",

                "50"};

        runTest("LEVEL4_FULFILLED_NOT_EXPIRED", input, expected);
    }

    // ======================================================
// HIDDEN TESTS
// ======================================================

    static void testHiddenUnknownFulfill() {

        String[][] input = {

                {"FULFILL","UNKNOWN"}
        };

        String[] expected = {

                "invalid_request"
        };

        runTest(
                "HIDDEN_UNKNOWN_FULFILL",
                input,
                expected
        );
    }

    static void testHiddenAvailableAfterFulfill() {

        String[][] input = {

                {"ADD_PRODUCT","P1","100"},

                {"RESERVE","O1","P1","30"},

                {"FULFILL","O1"},

                {"AVAILABLE_STOCK","P1"}
        };

        String[] expected = {

                "true",

                "success",

                "success",

                "70"
        };

        runTest(
                "HIDDEN_AVAILABLE_AFTER_FULFILL",
                input,
                expected
        );
    }

    static void testHiddenAvailableAfterExpiry() {

        String[][] input = {

                {"ADD_PRODUCT","P1","100"},

                {"RESERVE","O1","P1","30","10"},

                {"RELEASE_EXPIRED","70"},

                {"AVAILABLE_STOCK","P1"}
        };

        String[] expected = {

                "true",

                "success",

                "1",

                "100"
        };

        runTest(
                "HIDDEN_AVAILABLE_AFTER_EXPIRY",
                input,
                expected
        );
    }

    static void testHiddenMixedFulfillAndExpire() {

        String[][] input = {

                {"ADD_PRODUCT","P1","100"},

                {"RESERVE","O1","P1","20","5"},

                {"RESERVE","O2","P1","30","5"},

                {"FULFILL","O1"},

                {"RELEASE_EXPIRED","100"},

                {"AVAILABLE_STOCK","P1"}
        };

        String[] expected = {

                "true",

                "success",

                "success",

                "success",

                "1",

                "80"
        };

        runTest(
                "HIDDEN_MIXED_FULFILL_EXPIRE",
                input,
                expected
        );
    }

    static void testHiddenReserveDuplicateOrderId() {

        String[][] input = {

                {"ADD_PRODUCT","P1","100"},

                {"RESERVE","O1","P1","20"},

                {"RESERVE","O1","P1","20"}
        };

        String[] expected = {

                "true",

                "success",

                "invalid_request"
        };

        runTest(
                "HIDDEN_DUPLICATE_ORDER",
                input,
                expected
        );
    }

    static void testHiddenTopProductsZeroFulfilled() {

        String[][] input = {

                {"ADD_PRODUCT","B","100"},

                {"ADD_PRODUCT","A","100"},

                {"TOP_PRODUCTS","2"}
        };

        String[] expected = {

                "true",

                "true",

                "A(0),B(0)"
        };

        runTest(
                "HIDDEN_TOP_PRODUCTS_ZERO",
                input,
                expected
        );
    }

    static void testHiddenRemoveReservedStock() {

        String[][] input = {

                {"ADD_PRODUCT","P1","100"},

                {"RESERVE","O1","P1","70"},

                {"REMOVE_STOCK","P1","40"},

                {"GET_STOCK","P1"}
        };

        String[] expected = {

                "true",

                "success",

                "insufficient_stock",

                "100"
        };

        runTest(
                "HIDDEN_REMOVE_RESERVED_STOCK",
                input,
                expected
        );
    }

    static void testHiddenMultipleReleaseRuns() {

        String[][] input = {

                {"ADD_PRODUCT","P1","100"},

                {"RESERVE","O1","P1","30","10"},

                {"RELEASE_EXPIRED","70"},

                {"RELEASE_EXPIRED","200"},

                {"AVAILABLE_STOCK","P1"}
        };

        String[] expected = {

                "true",

                "success",

                "1",

                "0",

                "100"
        };

        runTest(
                "HIDDEN_DOUBLE_RELEASE",
                input,
                expected
        );
    }

    static void testHiddenAvailableMissingProduct() {

        String[][] input = {

                {"AVAILABLE_STOCK","UNKNOWN"}
        };

        String[] expected = {

                ""
        };

        runTest(
                "HIDDEN_AVAILABLE_MISSING_PRODUCT",
                input,
                expected
        );
    }

    static void testHiddenExpiryBoundary48Hours() {

        String[][] input = {

                {"ADD_PRODUCT","P1","100"},

                {"RESERVE","O1","P1","30","10"},

                {"RELEASE_EXPIRED","58"},

                {"AVAILABLE_STOCK","P1"}
        };

        String[] expected = {

                "true",

                "success",

                /*
                 * Depends on interpretation:
                 *
                 * If expiry is >=48:
                 * expected -> "1"
                 *
                 * If expiry is >48:
                 * expected -> "0"
                 *
                 * Use this test to verify
                 * your business rule.
                 */
                "1",

                "100"
        };

        runTest(
                "HIDDEN_EXPIRY_BOUNDARY",
                input,
                expected
        );
    }

    // ======================================================
    // TEST HARNESS
    // ======================================================

    static void runTest(String testName, String[][] input, String[] expected) {

        InventoryManager manager = new InventoryManager();

        String[] actual = manager.process(input);

        boolean passed = Arrays.equals(expected, actual);

        System.out.println((passed ? "✅ " : "❌ ") + testName);

        if (!passed) {

            System.out.println("Expected: " + Arrays.toString(expected));

            System.out.println("Actual  : " + Arrays.toString(actual));
        }
    }
}

class InventoryManager {
    //9:09
    Map<String, Product> inventory = new HashMap<>();
    Map<String, Reserve> lookup = new HashMap<>();

    public String[] process(String[][] operations) {
        String[] output = new String[operations.length];
        for (int i = 0; i < operations.length; i++) {
            output[i] = switch (operations[i][0]) {
                case "ADD_PRODUCT" -> addProduct(operations[i][1], Integer.parseInt(operations[i][2]));
                case "ADD_STOCK" -> addStock(operations[i][1], Integer.parseInt(operations[i][2]));
                case "REMOVE_STOCK" -> removeStock(operations[i][1], Integer.parseInt(operations[i][2]));
                case "GET_STOCK" -> getStock(operations[i][1]);
                case "RESERVE" -> {
                    if (operations[i].length > 4) {
                        yield reserveStock(operations[i][1], operations[i][2], Integer.parseInt(operations[i][3]), Integer.parseInt(operations[i][4]));
                    } else {
                        yield reserveStock(operations[i][1], operations[i][2], Integer.parseInt(operations[i][3]), 0);
                    }
                }
                case "GET_RESERVED" -> getReserved(operations[i][1]); //9:57
                case "FULFILL" -> fulfill(operations[i][1]);
                case "TOP_PRODUCTS" -> topProductsFulifilled(Integer.parseInt(operations[i][1])); //10:06
                case "RELEASE_EXPIRED" -> releaseExpiredReservations(Integer.parseInt(operations[i][1]));
                case "AVAILABLE_STOCK" -> getAvailableStock(operations[i][1]);
                //case "RESERVE"
                default -> "";
            };
        }
        return output;
    }

    private String getAvailableStock(String productId) {
        if(inventory.containsKey(productId)) {
            return String.valueOf(inventory.get(productId).availableStock());
        }
        return "";
    }

    private String releaseExpiredReservations(int currentTime) {
        var expiredReservations = lookup.values().stream().filter(reserve -> reserve.isExpired(currentTime)).toList();
        expiredReservations
                .forEach(reserve -> inventory.get(reserve.productId).expire(reserve));
        return String.valueOf(expiredReservations.size());
    }

    private String topProductsFulifilled(int limit) {
        return inventory.values()
                .stream()
                .sorted(Comparator.comparingInt(Product::getFulfilled).reversed().thenComparing(product -> product.productId))
                .limit(limit)
                .map(Product::parseFulfill)
                .collect(Collectors.joining(","));
    }

    private String fulfill(String orderId) {
        if (lookup.containsKey(orderId) && lookup.get(orderId).status.equals(Reserve.State.PENDING)) {
            Reserve reserve = lookup.get(orderId);
            var product = inventory.get(reserve.productId);
            product.fulfill(reserve);
            return "success";
        }
        return "invalid_request";
    }

    private String getReserved(String productId) {
        var product = inventory.get(productId);
        if (product != null) {
            return String.valueOf(product.getReservedQty());
        }
        return "";
    }

    private String reserveStock(String orderId, String productId, int qty, int time) {
        if (!lookup.containsKey(orderId) && inventory.containsKey(productId)) {
            Reserve reserve = new Reserve(productId, qty, time);
            if (inventory.get(productId).reserve(reserve)) {
                lookup.put(orderId, reserve);
                return "success";
            } else {
                return "insufficient_stock";
            }
        }
        return "invalid_request";
    }

    private String getStock(String productId) {
        var product = inventory.get(productId);
        if (product == null) {
            return "";
        }

        return String.valueOf(product.getQty()); //9:23 - 9:37
    }

    private String removeStock(String productId, int qty) {
        var product = inventory.get(productId);
        if (product == null) {
            return "invalid_request";
        }
        int balance = product.removeStock(qty);
        if (balance < 0) {
            return "insufficient_stock";
        }
        return String.valueOf(balance);
    }

    private String addStock(String productId, int qty) {
        var product = inventory.get(productId);
        if (product == null) {
            return "invalid_request";
        }
        return String.valueOf(product.addStock(qty));
    }

    private String addProduct(String productId, int qty) {
        if (!inventory.containsKey(productId)) {
            inventory.put(productId, new Product(productId, qty));
            return "true";
        }
        return "false";
    }
}


class Product {
    String productId;

    Integer qty;
    List<Reserve> reservations;

    Integer reservedQty;
    Integer fulfilled;

    Product(String productId, Integer qty) {
        this.productId = productId;
        this.qty = qty;
        this.reservedQty = 0;
        this.fulfilled = 0;
        this.reservations = new ArrayList<>();
    }

    Integer addStock(Integer qty) {
        this.qty = this.qty + qty;
        return this.qty;
    }

    Integer removeStock(Integer qty) {
        if (qty <= this.qty - this.reservedQty) {
            this.qty = this.qty - qty;
            this.fulfilled = this.fulfilled + qty;
            return this.qty;
        }
        return -1;
    }

    Integer getQty() {
        return this.qty;
    }

    boolean reserve(Reserve reserve) {
        if (reserve.qty <= qty - reservedQty) {
            reservedQty = reservedQty + reserve.qty;
            reservations.add(reserve);
            return true;
        }
        return false;
    }

    void fulfill(Reserve reserve) {
        this.qty = this.qty - reserve.qty;
        reservedQty = reservedQty - reserve.qty;
        fulfilled = fulfilled + reserve.qty;
        reserve.fulfill();
    }

    void expire(Reserve reserve) {
        reservedQty = reservedQty - reserve.qty;
    }

    Integer getReservedQty() {
        return reservedQty;
    }

    Integer getFulfilled() {
        return fulfilled;
    }

    Integer availableStock() {
        return this.qty - this.reservedQty;
    }
    String parseFulfill() {
        return productId + "(" + fulfilled + ")";
    }

}

class Reserve {
    String productId;

    int qty;
    int time;

    State status;

    enum State {PENDING, FULFILLED, EXPIRED}

    Reserve(String productId, int qty, int time) {
        this.productId = productId;
        this.qty = qty;
        this.time = time;
        status = State.PENDING;
    }

    void fulfill() {
        status = State.FULFILLED;
    }

    boolean isExpired(int currentTime) {
        if (currentTime - time >= 48 && status.equals(State.PENDING)) {
            status = State.EXPIRED;
            return true;
        }
        return false;
    }
}
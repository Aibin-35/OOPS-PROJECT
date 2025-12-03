// src/model/StockMovement.java
package model;

import java.time.LocalDateTime;

public class StockMovement {
    private int id;
    private int productId;
    private int userId;
    private String direction; // "IN" or "OUT"
    private String reason;    // "restock", "wastage", etc.
    private int quantity;
    private double unitCost;
    private LocalDateTime createdAt;

    
}

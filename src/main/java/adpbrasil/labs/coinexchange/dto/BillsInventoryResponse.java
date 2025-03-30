package adpbrasil.labs.coinexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class BillsInventoryResponse {
    private Map<Integer, Integer> billInventory;
    private int totalBillsReceived;
}

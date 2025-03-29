package adpbrasil.labs.coinexchange.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ExchangeTransactionDto {
    private Long id;
    private int amount;
    private boolean minimal;
    private Map<Integer, Integer> change;
    private LocalDateTime transactionDate;
}

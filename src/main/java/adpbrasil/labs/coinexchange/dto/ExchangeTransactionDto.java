package adpbrasil.labs.coinexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeTransactionDto {
    private Long id;
    private int amount;
    private boolean minimal;
    private Map<Integer, Integer> change;
    private LocalDateTime transactionDate;
}

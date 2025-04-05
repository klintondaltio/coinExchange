package adpbrasil.labs.coinexchange.dto;

import java.time.LocalDateTime;

public record TransactionSummaryDTO(
        Integer amount,
        boolean minimal,
        int totalCoins
) {}
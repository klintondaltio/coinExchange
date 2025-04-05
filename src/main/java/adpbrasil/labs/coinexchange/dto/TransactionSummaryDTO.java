package adpbrasil.labs.coinexchange.dto;


public record TransactionSummaryDTO(
        Integer amount,
        boolean minimal,
        int totalCoins
) {}
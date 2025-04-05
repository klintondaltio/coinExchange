package adpbrasil.labs.coinexchange.mapper;

import adpbrasil.labs.coinexchange.dto.TransactionSummaryDTO;
import adpbrasil.labs.coinexchange.model.ExchangeTransaction;
import adpbrasil.labs.coinexchange.projection.TransactionSummaryProjection;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionSummaryDTO toSummaryDTO(TransactionSummaryProjection projection, ExchangeTransaction entity) {
        int totalCoins = entity.getChange().values().stream().mapToInt(Integer::intValue).sum();
        return new TransactionSummaryDTO(
                projection.getAmount(),
                projection.isMinimal(),
                totalCoins
        );
    }
}

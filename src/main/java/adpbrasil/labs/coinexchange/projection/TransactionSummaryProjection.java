package adpbrasil.labs.coinexchange.projection;

import java.time.LocalDateTime;

public interface TransactionSummaryProjection {
    Long getId();
    Integer getAmount();
    boolean isMinimal();
}

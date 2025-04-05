package adpbrasil.labs.coinexchange.mapper;
import adpbrasil.labs.coinexchange.dto.TransactionSummaryDTO;
import adpbrasil.labs.coinexchange.model.ExchangeTransaction;
import adpbrasil.labs.coinexchange.projection.TransactionSummaryProjection;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionMapperTest {

    @Test
    public void testToSummaryDTO() {
        TransactionSummaryProjection projection = mock(TransactionSummaryProjection.class);
        when(projection.getAmount()).thenReturn(20);
        when(projection.isMinimal()).thenReturn(true);

        ExchangeTransaction tx = new ExchangeTransaction();
        tx.setId(1L);
        tx.setAmount(20);
        tx.setMinimal(true);
        tx.setChange(Map.of(25, 2, 10, 1));
        tx.setTransactionDate(LocalDateTime.now());

        TransactionMapper mapper = new TransactionMapper();
        TransactionSummaryDTO dto = mapper.toSummaryDTO(projection, tx);

        assertEquals(20, dto.amount());
        assertTrue(dto.minimal());
        assertEquals(3, dto.totalCoins());
    }
}
package adpbrasil.labs.coinexchange.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "exchange_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int amount;

    private boolean minimal;

    @ElementCollection
    @CollectionTable(name = "transaction_change", joinColumns = @JoinColumn(name = "transaction_id"))
    @MapKeyColumn(name = "coin_value")
    @Column(name = "coin_count")
    private Map<Integer, Integer> change;

    private LocalDateTime transactionDate;
}

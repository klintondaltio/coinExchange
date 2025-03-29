package adpbrasil.labs.coinexchange.repository;

import adpbrasil.labs.coinexchange.model.ExchangeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeTransactionRepository extends JpaRepository<ExchangeTransaction, Long> {
}

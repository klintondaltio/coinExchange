package adpbrasil.labs.coinexchange.repository;

import adpbrasil.labs.coinexchange.model.ExchangeTransaction;
import adpbrasil.labs.coinexchange.projection.TransactionSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeTransactionRepository extends JpaRepository<ExchangeTransaction, Long> {

    @Query("SELECT t FROM ExchangeTransaction t WHERE t.minimal = :minimal")
    List<TransactionSummaryProjection> findByMinimalStrategy(@Param("minimal") boolean minimal);

    @Query("SELECT t.id as id, t.amount as amount, t.minimal as minimal FROM ExchangeTransaction t")
    List<TransactionSummaryProjection> findAllSummarized();
}

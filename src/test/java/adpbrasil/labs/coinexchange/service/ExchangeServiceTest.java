package adpbrasil.labs.coinexchange.service;

import adpbrasil.labs.coinexchange.config.CoinProperties;
import adpbrasil.labs.coinexchange.exception.InsufficientCoinsException;
import adpbrasil.labs.coinexchange.model.ExchangeTransaction;
import adpbrasil.labs.coinexchange.repository.ExchangeTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExchangeServiceTest {

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private ExchangeTransactionRepository transactionRepository;

    @Autowired
    private CoinProperties coinProperties;

    @BeforeEach
    public void setUp() {
        coinProperties.setInitialQuantity(100);
        exchangeService.resetInventory();
        transactionRepository.deleteAll();
    }

    @Test
    public void testSuccessfulExchangeWithMinimal() {
        // Cenário: valor válido com cédula única permitida (allowMultipleBills = false)
        var response = exchangeService.exchange(10, true, false);
        assertEquals("Exchange successful.", response.getMessage());
        assertNotNull(response.getChange());

        List<ExchangeTransaction> transactions = transactionRepository.findAll();
        assertFalse(transactions.isEmpty());
        ExchangeTransaction transaction = transactions.get(0);
        assertEquals(10, transaction.getAmount());
        assertTrue(transaction.isMinimal());
    }

    @Test
    public void testSuccessfulExchangeWithMaximal() {
        // Cenário: troca com estratégia maximal (allowMultipleBills = false, valor deve ser uma cédula válida)
        var response = exchangeService.exchange(10, false, false);
        assertEquals("Exchange successful.", response.getMessage());
        assertNotNull(response.getChange());

        List<ExchangeTransaction> transactions = transactionRepository.findAll();
        assertFalse(transactions.isEmpty());
        ExchangeTransaction transaction = transactions.get(0);
        assertEquals(10, transaction.getAmount());
        assertFalse(transaction.isMinimal());
    }

    @Test
    public void testSuccessfulExchangeWithMultipleBillsAllowed() {
        // Cenário: permite múltiplas cédulas (allowMultipleBills = true) e valor não precisa estar na lista
        var response = exchangeService.exchange(15, true, true);
        assertEquals("Exchange successful.", response.getMessage());
        assertNotNull(response.getChange());

        List<ExchangeTransaction> transactions = transactionRepository.findAll();
        assertFalse(transactions.isEmpty());
        ExchangeTransaction transaction = transactions.get(0);
        assertEquals(15, transaction.getAmount());
        assertTrue(transaction.isMinimal());
    }

    @Test
    public void testExchangeInvalidWhenMultipleBillsAllowed() {
        // Cenário: allowMultipleBills = true, mas amount <= 1 deve lançar exceção
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                exchangeService.exchange(1, true, true)
        );
        assertEquals("When multiple bills are allowed, amount must be greater than 1.", exception.getMessage());
    }

    @Test
    public void testExchangeInvalidBillWhenNotAllowedMultiple() {
        // Cenário: allowMultipleBills = false, e o valor (por exemplo, 15) não é uma cédula válida
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                exchangeService.exchange(15, true, false)
        );
        assertTrue(exception.getMessage().contains("Invalid bill denomination"));
    }

    @Test
    public void testInsufficientCoins() {
        // Testa que, ao tentar trocar $50 (5000 centavos) com múltiplas cédulas permitidas,
        // ocorre insuficiência de moedas (pois a máquina possui apenas 4100 centavos)
        assertThrows(InsufficientCoinsException.class, () ->
                exchangeService.exchange(50, true, true)
        );
    }

    @Test
    public void testStatusAfterExchange() {
        exchangeService.exchange(10, true, false);
        Map<String, Object> status = exchangeService.getStatus();
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> coinInventory = (Map<Integer, Integer>) status.get("coinInventory");
        // Verifica que a quantidade de moedas de 25 centavos diminuiu (por exemplo)
        assertTrue(coinInventory.get(25) < 100);
    }

    @Test
    public void testTransactionHistory() {
        List<ExchangeTransaction> history = exchangeService.getTransactionHistory();
        assertTrue(history.isEmpty());

        exchangeService.exchange(10, true, false);
        history = exchangeService.getTransactionHistory();
        assertFalse(history.isEmpty());
        assertTrue(history.stream().anyMatch(t -> t.getAmount() == 10));
    }

    @Test
    public void testRemoveCoinsInvalid() {
        // Tenta remover uma quantidade maior do que a disponível
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                exchangeService.removeCoins(25, 1000)
        );
        assertTrue(ex.getMessage().contains("Not enough coins"));
    }

    @Test
    public void testRemoveCoinsSuccess() {
        // Reinicia o inventário para garantir o estado inicial
        exchangeService.resetInventory();
        int initial = coinProperties.getInitialQuantity();

        // Remove 30 moedas de 25 centavos
        exchangeService.removeCoins(25, 30);

        // Obtém o inventário atualizado
        Map<Integer, Integer> inventory = exchangeService.getInventory();

        // O novo total para a cédula de 25 centavos deve ser initial - 30
        assertEquals(initial - 30, inventory.get(25).intValue());
    }


    @Test
    public void testGetInventory() {
        // Reinicia o inventário para garantir o estado inicial
        exchangeService.resetInventory();
        Map<Integer, Integer> inventory = exchangeService.getInventory();
        int initial = coinProperties.getInitialQuantity();
        // Verifica que as chaves esperadas estão definidas com o valor inicial
        assertEquals(initial, inventory.get(25));
        assertEquals(initial, inventory.get(10));
        assertEquals(initial, inventory.get(5));
        assertEquals(initial, inventory.get(1));
    }

    @Test
    public void testAddCoins() {
        // Reinicia o inventário e adiciona moedas à cédula de 25 centavos
        exchangeService.resetInventory();
        int initial = coinProperties.getInitialQuantity();
        exchangeService.addCoins(25, 20);
        Map<Integer, Integer> inventory = exchangeService.getInventory();
        // O novo total deve ser o inicial + 20
        assertEquals(initial + 20, inventory.get(25));
    }

    @Test
    public void testFilterTransactionHistory() {
        // Limpa o repositório para garantir que não haja transações pré-existentes
        transactionRepository.deleteAll();
        // Cria a primeira transação: amount 30, minimal true, data = agora
        ExchangeTransaction transaction1 = new ExchangeTransaction();
        transaction1.setAmount(30);
        transaction1.setMinimal(true);
        transaction1.setChange(Map.of(25, 2));
        LocalDateTime now = LocalDateTime.now();
        transaction1.setTransactionDate(now);

        // Cria a segunda transação: amount 20, minimal false, data = agora menos 10 minutos
        ExchangeTransaction transaction2 = new ExchangeTransaction();
        transaction2.setAmount(20);
        transaction2.setMinimal(false);
        transaction2.setChange(Map.of(10, 2));
        transaction2.setTransactionDate(now.minusMinutes(10));

        transactionRepository.saveAndFlush(transaction1);
        transactionRepository.saveAndFlush(transaction2);

        // 1) Filtra transações com amount entre 25 e 35 e minimal = true (deve retornar apenas transaction1)
        List<ExchangeTransaction> filtered = exchangeService.filterTransactionHistory(
                null, null, 25, 35, true);
        assertEquals(1, filtered.size());
        assertEquals(30, filtered.get(0).getAmount());

        // 2) Filtra por data: usando um intervalo que inclua somente a transaction1
        LocalDateTime start = now.minusMinutes(5);
        LocalDateTime end = now.plusMinutes(5);
        filtered = exchangeService.filterTransactionHistory(start, end, null, null, null);
        // Apenas transaction1 deve estar dentro desse intervalo (transaction2 foi feita 10 minutos antes)
        assertEquals(1, filtered.size());
        assertEquals(30, filtered.get(0).getAmount());

        // 3) Sem filtros (deve retornar todas as transações)
        filtered = exchangeService.filterTransactionHistory(null, null, null, null, null);
        assertEquals(2, filtered.size());
    }

    @Test
    public void testGetBillsInventory() {
        // Limpa os inventários de bills
        // Supondo que em setUp() os inventários são zerados ou não alterados.
        // Realiza uma troca com allowMultipleBills = false (cédula única)
        exchangeService.exchange(10, true, false); // deve registrar uma cédula de $10
        Map<String, Object> bills = exchangeService.getBillsInventory();
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> billInv = (Map<Integer, Integer>) bills.get("billInventory");
        int total = (int) bills.get("totalBillsReceived");
        // Como allowMultipleBills = false, totalBillsReceived não é incrementado
        assertEquals(0, total);
        // Verifica que a cédula de $10 foi registrada
        assertEquals(1, billInv.get(10));
    }

    @Test
    public void testAddCoinsMethod() {
        exchangeService.resetInventory();
        int initial = coinProperties.getInitialQuantity();
        exchangeService.addCoins(25, 20);
        Map<Integer, Integer> inventory = exchangeService.getInventory();
        assertEquals(initial + 20, inventory.get(25).intValue());
    }

    @Test
    public void testIsMachineOperational() {
        // Com inventário reiniciado, a máquina deve estar operacional
        exchangeService.resetInventory();
        assertTrue(exchangeService.isMachineOperational());
        // Simula esgotamento das moedas
        coinInventoryExhaustion();
        assertFalse(exchangeService.isMachineOperational());
    }

    // Método auxiliar para esgotar as moedas do inventário
    private void coinInventoryExhaustion() {
        // Remove todas as moedas de cada tipo
        Map<Integer, Integer> inv = exchangeService.getInventory();
        for (Integer coin : inv.keySet()) {
            exchangeService.removeCoins(coin, inv.get(coin));
        }
    }


}

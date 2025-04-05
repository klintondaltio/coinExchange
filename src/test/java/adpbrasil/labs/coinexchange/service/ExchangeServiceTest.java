package adpbrasil.labs.coinexchange.service;

import adpbrasil.labs.coinexchange.config.CoinProperties;
import adpbrasil.labs.coinexchange.dto.BillsInventoryResponse;
import adpbrasil.labs.coinexchange.exception.InsufficientCoinsException;
import adpbrasil.labs.coinexchange.mapper.TransactionMapper;
import adpbrasil.labs.coinexchange.model.ExchangeTransaction;
import adpbrasil.labs.coinexchange.repository.ExchangeTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
public class ExchangeServiceTest {

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private ExchangeTransactionRepository transactionRepository;

    @Autowired
    private CoinProperties coinProperties;

    private ExchangeService mockService;

    @BeforeEach
    public void setUp() {
        coinProperties.setInitialQuantity(100);
        exchangeService.resetInventory();
        transactionRepository.deleteAll();
        mockService = new ExchangeService(coinProperties, mock(ExchangeTransactionRepository.class), mock(TransactionMapper.class));
    }

    @Test
    public void shouldExchangeWithMinimalStrategySuccessfully() {
        var response = exchangeService.exchange(10, true, false);
        assertEquals("Exchange successful.", response.getMessage());
        assertNotNull(response.getChange());
    }

    @Test
    public void shouldExchangeWithMaximalStrategySuccessfully() {
        var response = exchangeService.exchange(10, false, false);
        assertEquals("Exchange successful.", response.getMessage());
        assertNotNull(response.getChange());
    }

    @Test
    public void shouldAllowMultipleBillsAndExchangeSuccessfully() {
        var response = exchangeService.exchange(15, true, true);
        assertEquals("Exchange successful.", response.getMessage());
        assertNotNull(response.getChange());
    }

    @Test
    public void shouldThrowExceptionForInvalidMultipleBillAmount() {
        var ex = assertThrows(IllegalArgumentException.class, () -> exchangeService.exchange(1, true, true));
        assertEquals("When multiple bills are allowed, amount must be greater than 1.", ex.getMessage());
    }

    @Test
    public void shouldThrowExceptionForInvalidSingleBill() {
        var ex = assertThrows(IllegalArgumentException.class, () -> exchangeService.exchange(15, true, false));
        assertTrue(ex.getMessage().contains("Invalid bill denomination"));
    }

    @Test
    public void shouldThrowInsufficientCoinsException() {
        assertThrows(InsufficientCoinsException.class, () -> exchangeService.exchange(50, true, true));
    }

    @Test
    public void shouldUpdateCoinInventoryAfterExchange() {
        exchangeService.exchange(10, true, false);
        Map<String, Object> status = exchangeService.getStatus();
        Map<Integer, Integer> inventory = (Map<Integer, Integer>) status.get("coinInventory");
        assertTrue(inventory.get(25) < 100);
    }

    @Test
    public void shouldTrackTransactionHistory() {
        transactionRepository.deleteAll();
        exchangeService.exchange(10, true, false);
        List<ExchangeTransaction> history = exchangeService.getTransactionHistory();
        assertEquals(1, history.size());
        assertEquals(10, history.get(0).getAmount());
    }

    @Test
    public void shouldRemoveCoinsCorrectly() {
        int initial = coinProperties.getInitialQuantity();
        exchangeService.removeCoins(25, 30);
        Map<Integer, Integer> inventory = exchangeService.getInventory();
        assertEquals(initial - 30, inventory.get(25));
    }

    @Test
    public void shouldThrowWhenRemovingMoreThanAvailable() {
        assertThrows(IllegalArgumentException.class, () -> exchangeService.removeCoins(25, 1000));
    }

    @Test
    public void shouldReturnCorrectInventory() {
        Map<Integer, Integer> inventory = exchangeService.getInventory();
        int expected = coinProperties.getInitialQuantity();
        assertEquals(expected, inventory.get(1));
        assertEquals(expected, inventory.get(5));
        assertEquals(expected, inventory.get(10));
        assertEquals(expected, inventory.get(25));
    }

    @Test
    public void shouldAddCoinsCorrectly() {
        int initial = coinProperties.getInitialQuantity();
        exchangeService.addCoins(25, 20);
        Map<Integer, Integer> inventory = exchangeService.getInventory();
        assertEquals(initial + 20, inventory.get(25));
    }

    @Test
    public void shouldFilterTransactionHistoryCorrectly() {
        transactionRepository.deleteAll();
        ExchangeTransaction t1 = new ExchangeTransaction();
        t1.setAmount(30);
        t1.setMinimal(true);
        t1.setChange(Map.of(25, 2));
        t1.setTransactionDate(LocalDateTime.now());

        ExchangeTransaction t2 = new ExchangeTransaction();
        t2.setAmount(20);
        t2.setMinimal(false);
        t2.setChange(Map.of(10, 2));
        t2.setTransactionDate(LocalDateTime.now().minusMinutes(10));

        transactionRepository.saveAndFlush(t1);
        transactionRepository.saveAndFlush(t2);

        List<ExchangeTransaction> filtered = exchangeService.filterTransactionHistory(null, null, 25, 35, true);
        assertEquals(1, filtered.size());
        assertEquals(30, filtered.get(0).getAmount());
    }

    @Test
    public void shouldGetBillsInventoryCorrectly() {
        exchangeService.exchange(10, true, false);
        BillsInventoryResponse response = exchangeService.getBillsInventory();
        assertEquals(1, response.getBillInventory().get(10));
    }

    @Test
    public void shouldDetectMachineOperationalState() {
        exchangeService.resetInventory();
        assertTrue(exchangeService.isMachineOperational());
        drainInventory();
        assertFalse(exchangeService.isMachineOperational());
    }

    @Test
    public void shouldRejectInvalidCoinOnAdd() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> mockService.addCoins(3, 10));
        assertEquals("Coin value must be one of: 1, 5, 10, 25.", ex.getMessage());
    }

    @Test
    public void shouldRejectInvalidCoinOnRemove() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> mockService.removeCoins(50, 1));
        assertEquals("Coin value must be one of: 1, 5, 10, 25.", ex.getMessage());
    }

    private void drainInventory() {
        exchangeService.getInventory().forEach((coin, qty) -> exchangeService.removeCoins(coin, qty));
    }
}
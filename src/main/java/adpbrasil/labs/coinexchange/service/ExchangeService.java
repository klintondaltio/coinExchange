package adpbrasil.labs.coinexchange.service;

import adpbrasil.labs.coinexchange.config.CoinProperties;
import adpbrasil.labs.coinexchange.dto.BillsInventoryResponse;
import adpbrasil.labs.coinexchange.dto.ExchangeResponse;
import adpbrasil.labs.coinexchange.exception.InsufficientCoinsException;
import adpbrasil.labs.coinexchange.model.ExchangeTransaction;
import adpbrasil.labs.coinexchange.repository.ExchangeTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExchangeService {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeService.class);

    private Map<Integer, Integer> coinInventory;
    // Campos para os valores das moedas (em centavos)
    public static final int TWENTYFIVECENTS = 25;
    public static final int TENCENTS = 10;
    public static final int FIVECENTS = 5;
    public static final int ONECENT = 1;

    private final int[] coinValues = {TWENTYFIVECENTS, TENCENTS, FIVECENTS, ONECENT};
    private final int[] coinValuesMax = {ONECENT, FIVECENTS, TENCENTS, TWENTYFIVECENTS};

    private final CoinProperties coinProperties;
    private final ExchangeTransactionRepository transactionRepository;

    // Novo inventário para os bills recebidos (quando allowMultipleBills = false)
    private Map<Integer, Integer> billInventory = new HashMap<>();
    // Acumulador para o total de bills recebidos (quando allowMultipleBills = true)
    private int totalBillsReceived = 0;

    public ExchangeService(CoinProperties coinProperties, ExchangeTransactionRepository transactionRepository) {
        this.coinProperties = coinProperties;
        this.transactionRepository = transactionRepository;
        resetInventory();
    }

    /**
     * Reinicia o inventário de moedas para o valor inicial definido nas propriedades.
     */
    public void resetInventory() {
        coinInventory = new HashMap<>();
        int initial = coinProperties.getInitialQuantity();
        for (int coin : coinValues) {
            coinInventory.put(coin, initial);
        }

        // Reset também do inventário de bills e acumulador
        billInventory = new HashMap<>();
        totalBillsReceived = 0;

        logger.info("Inventory reset to {} coins for each type.", initial);
    }

    /**
     * Registra o recebimento de um bill.
     * Se allowMultipleBills for false, o valor deve ser uma das cédulas permitidas.
     * Caso contrário, acumula o total recebido.
     */
    private void registerBill(int amount, boolean allowMultipleBills) {
        if (!allowMultipleBills) {
            // Valor deve ser uma cédula válida; assume que a validação já ocorreu
            billInventory.put(amount, billInventory.getOrDefault(amount, 0) + 1);
            logger.info("Registered bill of ${}. New count: {}.", amount, billInventory.get(amount));
        } else {
            totalBillsReceived += amount;
            logger.info("Added ${} to total bills. New total: ${}.", amount, totalBillsReceived);
        }
    }

    /**
     * Adiciona moedas manualmente ao inventário.
     */
    public void addCoins(int coinValue, int quantity) {
        if (!Arrays.stream(coinValues).anyMatch(v -> v == coinValue)) {
            throw new IllegalArgumentException("Coin value must be one of: 1, 5, 10, 25.");
        }
        coinInventory.put(coinValue, coinInventory.getOrDefault(coinValue, 0) + quantity);
        logger.info("Added {} coins of {} centavos. New count: {}.", quantity, coinValue, coinInventory.get(coinValue));
    }

    /**
     * Remove moedas manualmente do inventário.
     */
    public void removeCoins(int coinValue, int quantity) {
        if (!Arrays.stream(coinValues).anyMatch(v -> v == coinValue)) {
            throw new IllegalArgumentException("Coin value must be one of: 1, 5, 10, 25.");
        }
        int available = coinInventory.getOrDefault(coinValue, 0);
        if (quantity > available) {
            throw new IllegalArgumentException("Not enough coins of " + coinValue + " centavos to remove.");
        }
        coinInventory.put(coinValue, available - quantity);
    }


    public Map<Integer, Integer> getInventory() {
        return coinInventory;
    }

    /**
     * Realiza a troca de um valor (em dólares) para moedas.
     * Atualiza o inventário de bills conforme o parâmetro allowMultipleBills.
     */
    public ExchangeResponse exchange(int amount, boolean minimal, boolean allowMultipleBills) {
        // Validação de bills:
        if (allowMultipleBills) {
            if (amount <= 1) {
                throw new IllegalArgumentException("When multiple bills are allowed, amount must be greater than 1.");
            }
        } else {
            // Lista de cédulas permitidas
            List<Integer> allowedBills = List.of(1, 2, 5, 10, 20, 50, 100);
            if (!allowedBills.contains(amount)) {
                throw new IllegalArgumentException("Invalid bill denomination. Allowed denominations are: " + allowedBills);
            }
        }

        // Registra o bill recebido
        registerBill(amount, allowMultipleBills);

        int remaining = amount * 100; // converte dólares para centavos
        Map<Integer, Integer> change = new HashMap<>();
        int[] coins = minimal ? coinValues : coinValuesMax;

        for (int coin : coins) {
            int available = coinInventory.getOrDefault(coin, 0);
            int numCoins = Math.min(remaining / coin, available);
            if (numCoins > 0) {
                change.put(coin, numCoins);
                remaining -= numCoins * coin;
                coinInventory.put(coin, available - numCoins);
                logger.debug("Used {} coins of {} cents. Remaining in inventory: {}.", numCoins, coin, coinInventory.get(coin));
            }
        }

        if (remaining > 0) {
            String errorMsg = "Not enough coins available for the exchange.";
            logger.warn(errorMsg);
            throw new InsufficientCoinsException(errorMsg);
        } else {
            String successMsg = "Exchange successful.";
            logger.info(successMsg);
            ExchangeTransaction transaction = new ExchangeTransaction();
            transaction.setAmount(amount);
            transaction.setMinimal(minimal);
            transaction.setChange(change);
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);
            return new ExchangeResponse(successMsg, change);
        }
    }

    /**
     * Retorna o status atual do inventário de moedas.
     */
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("coinInventory", coinInventory);
        int totalCents = coinInventory.entrySet().stream()
                .mapToInt(e -> e.getKey() * e.getValue())
                .sum();
        status.put("totalValue", "$" + (totalCents / 100.0));
        return status;
    }

    /**
     * Retorna o histórico de transações.
     */
    public List<ExchangeTransaction> getTransactionHistory() {
        return transactionRepository.findAll();
    }

    /**
     * Filtra o histórico de transações com base em parâmetros opcionais.
     */
    public List<ExchangeTransaction> filterTransactionHistory(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Integer minAmount,
            Integer maxAmount,
            Boolean minimal) {
        List<ExchangeTransaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .filter(t -> startDate == null || !t.getTransactionDate().isBefore(startDate))
                .filter(t -> endDate == null || !t.getTransactionDate().isAfter(endDate))
                .filter(t -> minAmount == null || t.getAmount() >= minAmount)
                .filter(t -> maxAmount == null || t.getAmount() <= maxAmount)
                .filter(t -> minimal == null || t.isMinimal() == minimal)
                .collect(Collectors.toList());
    }

    /**
     * Retorna o inventário dos bills recebidos.
     * Se múltiplas cédulas estiverem permitidas, retorna o total acumulado.
     */
    public BillsInventoryResponse getBillsInventory() {
        return new BillsInventoryResponse(billInventory, totalBillsReceived);
    }

    /**
     * Retorna se a máquina está operacional (ou seja, se há moedas disponíveis).
     */
    public boolean isMachineOperational() {
        int totalCoins = coinInventory.values().stream().mapToInt(i -> i).sum();
        return totalCoins > 0;
    }
}

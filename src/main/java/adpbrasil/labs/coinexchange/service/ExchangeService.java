package adpbrasil.labs.coinexchange.service;

import adpbrasil.labs.coinexchange.config.CoinProperties;
import adpbrasil.labs.coinexchange.dto.BillsInventoryResponse;
import adpbrasil.labs.coinexchange.dto.ExchangeResponse;
import adpbrasil.labs.coinexchange.dto.TransactionSummaryDTO;
import adpbrasil.labs.coinexchange.exception.InsufficientCoinsException;
import adpbrasil.labs.coinexchange.model.ExchangeTransaction;
import adpbrasil.labs.coinexchange.projection.TransactionSummaryProjection;
import adpbrasil.labs.coinexchange.repository.ExchangeTransactionRepository;
import adpbrasil.labs.coinexchange.mapper.TransactionMapper;
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
    public static final int TWENTYFIVECENTS = 25;
    public static final int TENCENTS = 10;
    public static final int FIVECENTS = 5;
    public static final int ONECENT = 1;

    private final int[] coinValues = {TWENTYFIVECENTS, TENCENTS, FIVECENTS, ONECENT};
    private final int[] coinValuesMax = {ONECENT, FIVECENTS, TENCENTS, TWENTYFIVECENTS};

    private final CoinProperties coinProperties;
    private final ExchangeTransactionRepository transactionRepository;
    private Map<Integer, Integer> billInventory = new HashMap<>();
    private int totalBillsReceived = 0;
    private final TransactionMapper transactionMapper;

    public ExchangeService(CoinProperties coinProperties, ExchangeTransactionRepository transactionRepository,
                           TransactionMapper transactionMapper) {
        this.coinProperties = coinProperties;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        resetInventory();
    }

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
    /*
        * Registra uma cédula recebida. Se allowMultipleBills for true, adiciona o valor
        * ao total de cédulas recebidas. Caso contrário, registra a cédula no inventário.
        *
        * O uso de metodo privado é para encapsular a lógica de registro de cédulas.
        * Metodos privados são usados para evitar que o código seja acessado fora da classe,
        * o que é útil para manter a integridade do estado interno da classe.
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


    public List<TransactionSummaryDTO> getSummarizedTransactions() {
        List<TransactionSummaryProjection> projections = transactionRepository.findAllSummarized();

        return projections.stream()
                .map(p -> {
                    ExchangeTransaction tx = transactionRepository.findById(p.getId()).orElseThrow();
                    return transactionMapper.toSummaryDTO(p, tx);
                })
                .toList();
    }

    public void addCoins(int coinValue, int quantity) {
        if (!Arrays.stream(coinValues).anyMatch(v -> v == coinValue)) {
            throw new IllegalArgumentException("Coin value must be one of: 1, 5, 10, 25.");
        }
        coinInventory.put(coinValue, coinInventory.getOrDefault(coinValue, 0) + quantity);
        logger.info("Added {} coins of {} centavos. New count: {}.", quantity, coinValue, coinInventory.get(coinValue));
    }

    public void removeCoins(int coinValue, int quantity) {
        if (!Arrays.stream(coinValues).anyMatch(v -> v == coinValue)) {
            throw new IllegalArgumentException("Coin value must be one of: 1, 5, 10, 25.");
        }
        int available = coinInventory.getOrDefault(coinValue, 0);
        if (quantity > available) {
            throw new IllegalArgumentException("Not enough coins of " + coinValue + " cents to remove.");
        }
        coinInventory.put(coinValue, available - quantity);
    }


    public Map<Integer, Integer> getInventory() {
        return coinInventory;
    }

    public ExchangeResponse exchange(int amount, boolean minimal, boolean allowMultipleBills) {
        if (allowMultipleBills) {
            if (amount <= 1) {
                throw new IllegalArgumentException("When multiple bills are allowed, amount must be greater than 1.");
            }
        } else {
            List<Integer> allowedBills = List.of(1, 2, 5, 10, 20, 50, 100);
            if (!allowedBills.contains(amount)) {
                throw new IllegalArgumentException("Invalid bill denomination. Allowed denominations are: " + allowedBills);
            }
        }

        // Antes de registrar o bill, valida se é possível realizar a troca
        int remaining = amount * 100;
        Map<Integer, Integer> changePreview = new HashMap<>();
        int[] coins = minimal ? coinValues : coinValuesMax;

        for (int coin : coins) {
            int available = coinInventory.getOrDefault(coin, 0);
            int use = Math.min(remaining / coin, available);
            if (use > 0) {
                changePreview.put(coin, use);
                remaining -= use * coin;
            }
        }

        if (remaining > 0) {
            throw new InsufficientCoinsException("Not enough coins available for the exchange.");
        }

        // Agora que sabemos que é possível, registramos o bill
        registerBill(amount, allowMultipleBills);

        // E aplicamos de fato a troca
        for (Map.Entry<Integer, Integer> entry : changePreview.entrySet()) {
            int coin = entry.getKey();
            int used = entry.getValue();
            coinInventory.put(coin, coinInventory.get(coin) - used);
        }

        ExchangeTransaction transaction = new ExchangeTransaction();
        transaction.setAmount(amount);
        transaction.setMinimal(minimal);
        transaction.setChange(changePreview);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        return new ExchangeResponse("Exchange successful.", changePreview);
    }


    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("coinInventory", coinInventory);
        //totalCents vai rescebeer o valor total em centavos
        //O stream vai percorrer o inventário de moedas e multiplicar o valor da moeda pela quantidade
        //de moedas disponíveis. O resultado é somado para obter o valor total em centavos.
        int totalCents = coinInventory.entrySet().stream()
                .mapToInt(e -> e.getKey() * e.getValue())
                .sum();
        // vai settar o valor total em dolares
        status.put("totalValue", "$" + (totalCents / 100.0));
        return status;
    }

    public List<ExchangeTransaction> getTransactionHistory() {
        return transactionRepository.findAll();
    }

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

    public BillsInventoryResponse getBillsInventory() {
        return new BillsInventoryResponse(billInventory, totalBillsReceived);
    }

    public boolean isMachineOperational() {
        int totalCoins = coinInventory.values().stream().mapToInt(i -> i).sum();
        return totalCoins > 0;
    }
}

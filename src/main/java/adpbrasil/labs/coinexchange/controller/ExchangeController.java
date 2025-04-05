package adpbrasil.labs.coinexchange.controller;

import adpbrasil.labs.coinexchange.dto.*;
import adpbrasil.labs.coinexchange.model.ExchangeTransaction;
import adpbrasil.labs.coinexchange.service.ExchangeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeController {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeController.class);
    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping
    public ResponseEntity<?> exchange(@Valid @RequestBody ExchangeRequest request) {
        logger.info("Exchange request received: {}", request);
        ExchangeResponse response = exchangeService.exchange(
                request.getAmount(),
                request.isMinimal(),
                request.isAllowMultipleBills()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        Map<String, Object> status = exchangeService.getStatus();
        return ResponseEntity.ok(status);
    }

    @PostMapping("/replenish")
    public ResponseEntity<?> replenish() {
        exchangeService.resetInventory();
        return ResponseEntity.ok(Map.of("message", "Coin inventory replenished successfully."));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory() {
        List<ExchangeTransaction> history = exchangeService.getTransactionHistory();

        List<ExchangeTransactionDto> dtos = history.stream()
                .map(tx -> new ExchangeTransactionDto(
                        tx.getId(),
                        tx.getAmount(),
                        tx.isMinimal(),
                        tx.getChange(),
                        tx.getTransactionDate()
                ))
                .toList();

        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/history/filter")
    public ResponseEntity<?> filterHistory(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer minAmount,
            @RequestParam(required = false) Integer maxAmount,
            @RequestParam(required = false) Boolean minimal) {

        LocalDateTime start = (startDate != null) ? LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME) : null;
        LocalDateTime end = (endDate != null) ? LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME) : null;

        List<ExchangeTransaction> result = exchangeService.filterTransactionHistory(start, end, minAmount, maxAmount, minimal);
        List<ExchangeTransactionDto> dtos = result.stream()
                .map(tx -> new ExchangeTransactionDto(
                        tx.getId(),
                        tx.getAmount(),
                        tx.isMinimal(),
                        tx.getChange(),
                        tx.getTransactionDate()
                ))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/inventory")
    public ResponseEntity<InventoryResponse> getInventory() {
        Map<Integer, Integer> inventory = exchangeService.getInventory();
        int total = inventory.entrySet().stream()
                .mapToInt(e -> e.getKey() * e.getValue())
                .sum();

        return ResponseEntity.ok(new InventoryResponse(inventory, total));
    }


    @PostMapping("/inventory/add")
    public ResponseEntity<?> addInventory(@Valid @RequestBody InventoryUpdateRequest request) {
        exchangeService.addCoins(request.getCoinValue(), request.getQuantity());
        return ResponseEntity.ok(Map.of("message", "Inventory updated successfully", "inventory", exchangeService.getInventory()));
    }

    @PostMapping("/inventory/remove")
    public ResponseEntity<?> removeInventory(@Valid @RequestBody InventoryUpdateRequest request) {
        exchangeService.removeCoins(request.getCoinValue(), request.getQuantity());
        return ResponseEntity.ok(Map.of("message", "Inventory updated successfully", "inventory", exchangeService.getInventory()));
    }

    @GetMapping("/bills")
    public ResponseEntity<BillsInventoryResponse> getBillsInventory() {
        return ResponseEntity.ok(exchangeService.getBillsInventory());
    }

    @GetMapping("/admin/status")
    public ResponseEntity<MachineStatusResponse> getMachineStatus() {
        boolean operational = exchangeService.isMachineOperational();
        if (!operational) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new MachineStatusResponse("Machine out of coins"));
        }
        return ResponseEntity.ok(new MachineStatusResponse("Machine operational"));
    }

    @GetMapping("/history/summary")
    public ResponseEntity<List<TransactionSummaryDTO>> getSummaryHistory() {
        return ResponseEntity.ok(exchangeService.getSummarizedTransactions());
    }

}

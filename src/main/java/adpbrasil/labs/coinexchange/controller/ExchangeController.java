package adpbrasil.labs.coinexchange.controller;

import adpbrasil.labs.coinexchange.dto.ExchangeRequest;
import adpbrasil.labs.coinexchange.dto.ExchangeResponse;
import adpbrasil.labs.coinexchange.dto.InventoryUpdateRequest;
import adpbrasil.labs.coinexchange.dto.ExchangeTransactionDto;
import adpbrasil.labs.coinexchange.model.ExchangeTransaction;
import adpbrasil.labs.coinexchange.service.ExchangeService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
    public ResponseEntity<?> getInventory() {
        return ResponseEntity.ok(exchangeService.getInventory());
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

    // Novo endpoint para consultar o inventário de bills
    @GetMapping("/bills")
    public ResponseEntity<?> getBillsInventory() {
        return ResponseEntity.ok(exchangeService.getBillsInventory());
    }

    // Novo endpoint de administração para status da máquina
    @GetMapping("/admin/status")
    public ResponseEntity<?> getMachineStatus() {
        boolean operational = exchangeService.isMachineOperational();
        if (!operational) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("status", "Machine out of coins"));
        }
        return ResponseEntity.ok(Map.of("status", "Machine operational"));
    }
}

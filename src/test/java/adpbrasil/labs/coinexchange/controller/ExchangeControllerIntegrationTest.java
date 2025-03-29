package adpbrasil.labs.coinexchange.controller;

import adpbrasil.labs.coinexchange.config.CoinProperties;
import adpbrasil.labs.coinexchange.model.ExchangeTransaction;
import adpbrasil.labs.coinexchange.repository.ExchangeTransactionRepository;
import adpbrasil.labs.coinexchange.service.ExchangeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ExchangeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExchangeTransactionRepository transactionRepository;

    @Autowired
    private CoinProperties coinProperties;

    @Autowired
    private ExchangeService exchangeService;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        coinProperties.setInitialQuantity(100);
        exchangeService.resetInventory();
        transactionRepository.deleteAll();
    }

    @Test
    @WithMockUser
    public void testExchangeEndpointSingleBill() throws Exception {
        String json = "{ \"amount\": 10, \"allowMultipleBills\": false, \"minimal\": true }";
        mockMvc.perform(post("/api/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Exchange successful."));
    }

    @Test
    @WithMockUser
    public void testExchangeEndpointMultipleBillsAllowed() throws Exception {
        String json = "{ \"amount\": 15, \"allowMultipleBills\": true, \"minimal\": true }";
        mockMvc.perform(post("/api/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Exchange successful."));
    }

    @Test
    @WithMockUser
    public void testExchangeEndpointInvalidBill() throws Exception {
        // Valor 15 com allowMultipleBills false (não permitido)
        String json = "{ \"amount\": 15, \"allowMultipleBills\": false, \"minimal\": true }";
        mockMvc.perform(post("/api/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser
    public void testExchangeEndpointInvalidWhenMultipleBillsAllowed() throws Exception {
        // Valor 1 com allowMultipleBills true (inválido, pois deve ser > 1)
        String json = "{ \"amount\": 1, \"allowMultipleBills\": true, \"minimal\": true }";
        mockMvc.perform(post("/api/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser
    public void testStatusEndpoint() throws Exception {
        mockMvc.perform(get("/api/exchange/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coinInventory").exists())
                .andExpect(jsonPath("$.totalValue").exists());
    }

    @Test
    @WithMockUser
    public void testReplenishEndpoint() throws Exception {
        mockMvc.perform(post("/api/exchange/replenish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Coin inventory replenished successfully."));
    }

    @Test
    @WithMockUser
    public void testGetHistoryEndpoint() throws Exception {
        // Cria duas transações para teste
        ExchangeTransaction transaction1 = new ExchangeTransaction();
        transaction1.setAmount(30);
        transaction1.setMinimal(true);
        transaction1.setChange(Map.of(25, 2));
        transaction1.setTransactionDate(LocalDateTime.now());
        transactionRepository.saveAndFlush(transaction1);

        ExchangeTransaction transaction2 = new ExchangeTransaction();
        transaction2.setAmount(20);
        transaction2.setMinimal(false);
        transaction2.setChange(Map.of(10, 2));
        transaction2.setTransactionDate(LocalDateTime.now());
        transactionRepository.saveAndFlush(transaction2);

        // Executa a requisição GET para /api/exchange/history e verifica o retorno
        mockMvc.perform(get("/api/exchange/history"))
                .andExpect(status().isOk())
                // Verifica se a lista retornada possui 2 itens
                .andExpect(jsonPath("$.length()").value(2))
                // Verifica que há uma transação com amount 30 e outra com amount 20.
                .andExpect(jsonPath("$[?(@.amount==30)]").exists())
                .andExpect(jsonPath("$[?(@.amount==20)]").exists());
    }

    @Test
    @WithMockUser
    public void testInventoryEndpoint() throws Exception {
        mockMvc.perform(get("/api/exchange/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    @WithMockUser
    public void testInventoryAddEndpoint() throws Exception {
        String json = "{ \"coinValue\": 25, \"quantity\": 10 }";
        mockMvc.perform(post("/api/exchange/inventory/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Inventory updated successfully"))
                .andExpect(jsonPath("$.inventory['25']").value(110));
    }

    @Test
    @WithMockUser
    public void testInventoryRemoveEndpoint() throws Exception {
        // Primeiro adiciona moedas para garantir que há suficientes
        String jsonAdd = "{ \"coinValue\": 25, \"quantity\": 10 }";
        mockMvc.perform(post("/api/exchange/inventory/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAdd))
                .andExpect(status().isOk());

        String jsonRemove = "{ \"coinValue\": 25, \"quantity\": 5 }";
        mockMvc.perform(post("/api/exchange/inventory/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRemove))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Inventory updated successfully"))
                .andExpect(jsonPath("$.inventory['25']").value(105));
    }

    @Test
    @WithMockUser
    public void testFilterHistoryEndpoint() throws Exception {
        // Insere uma transação para teste
        ExchangeTransaction transaction = new ExchangeTransaction();
        transaction.setAmount(30);
        transaction.setMinimal(true);
        transaction.setChange(java.util.Map.of(25, 2));
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.saveAndFlush(transaction);

        LocalDateTime now = LocalDateTime.now();
        String start = now.minusMinutes(5).format(DateTimeFormatter.ISO_DATE_TIME);
        String end = now.plusMinutes(5).format(DateTimeFormatter.ISO_DATE_TIME);

        mockMvc.perform(get("/api/exchange/history/filter")
                        .param("startDate", start)
                        .param("endDate", end)
                        .param("minAmount", "20")
                        .param("maxAmount", "40")
                        .param("minimal", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(30));
    }

    @Test
    @WithMockUser
    public void testGetBillsInventoryEndpoint() throws Exception {
        // Realiza uma troca com allowMultipleBills = false para registrar um bill
        String jsonExchange = "{ \"amount\": 10, \"allowMultipleBills\": false, \"minimal\": true }";
        mockMvc.perform(post("/api/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonExchange))
                .andExpect(status().isOk());

        // Consulta o inventário de bills
        mockMvc.perform(get("/api/exchange/bills"))
                .andExpect(status().isOk())
                // Verifica que no inventário existe um registro para a cédula 10 (com valor 1)
                .andExpect(jsonPath("$.billInventory['10']").value(1))
                // E que o totalBillsReceived é 0 (pois allowMultipleBills era false)
                .andExpect(jsonPath("$.totalBillsReceived").value(0));
    }

    @Test
    @WithMockUser
    public void testGetMachineStatusEndpoint() throws Exception {
        // Inicialmente, com inventário reiniciado, a máquina deve estar operacional
        mockMvc.perform(get("/api/exchange/admin/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Machine operational"));

        // Agora, esgota as moedas
        coinInventoryExhaustion();

        mockMvc.perform(get("/api/exchange/admin/status"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("Machine out of coins"));
    }

    private void coinInventoryExhaustion() {
        // Remove todas as moedas de cada tipo
        Map<Integer, Integer> inv = exchangeService.getInventory();
        for (Integer coin : inv.keySet()) {
            exchangeService.removeCoins(coin, inv.get(coin));
        }
    }

}

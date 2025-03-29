package adpbrasil.labs.coinexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeResponse {
    private String message;
    private Map<Integer, Integer> change;
}

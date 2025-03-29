package adpbrasil.labs.coinexchange.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequest {
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least 1")
    private Integer amount;

    // Se true, permite qualquer valor > 1; caso contrário, o valor deve ser uma das cédulas permitidas.
    private boolean allowMultipleBills = false;

    private boolean minimal = true;
}

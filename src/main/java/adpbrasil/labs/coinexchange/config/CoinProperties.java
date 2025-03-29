package adpbrasil.labs.coinexchange.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "coin")
public class CoinProperties {
    // Valor inicial configurável para cada moeda, padrão 100
    private int initialQuantity = 100;

    public int getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(int initialQuantity) {
        this.initialQuantity = initialQuantity;
    }
}
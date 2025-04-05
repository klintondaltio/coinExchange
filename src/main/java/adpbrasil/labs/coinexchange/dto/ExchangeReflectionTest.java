package adpbrasil.labs.coinexchange.dto;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ExchangeReflectionTest {
    public static void main(String[] args) throws Exception {
        // 1. Obter a classe
        Class<?> clazz = ExchangeRequest.class;

        // 2. Criar uma instância via construtor padrão
        Object dto = clazz.getDeclaredConstructor().newInstance();

        /*for (Method method : clazz.getDeclaredMethods()) {
            System.out.println(method.getName());
        }*/

        /*for (Method method : clazz.getDeclaredMethods()) {
            System.out.println(method.getName() + " - " + Arrays.toString(method.getParameterTypes()));
        }*/

        // 3. Usar setters via reflexão
        Method setAmount = clazz.getMethod("setAmount", Integer.class);
        Method setMinimal = clazz.getMethod("setMinimal", boolean.class);
        Method setAllowMultipleBills = clazz.getMethod("setAllowMultipleBills", boolean.class);

        setAmount.invoke(dto, 20);
        setMinimal.invoke(dto, true);
        setAllowMultipleBills.invoke(dto, false);

        // 4. Ler os valores via getters
        Method getAmount = clazz.getMethod("getAmount");
        Method isMinimal = clazz.getMethod("isMinimal");
        Method isAllowMultipleBills = clazz.getMethod("isAllowMultipleBills");

        System.out.println("Amount: " + getAmount.invoke(dto));
        System.out.println("Minimal: " + isMinimal.invoke(dto));
        System.out.println("AllowMultipleBills: " + isAllowMultipleBills.invoke(dto));

        // 5. Acessar diretamente o campo privado "amount"
        Field amountField = clazz.getDeclaredField("amount");
        amountField.setAccessible(true);
        int value = (int) amountField.get(dto);
        System.out.println("Private access to amount: " + value);
    }
}
// ExchangePage.tsx
import { useState } from "react";
import axios from "axios";

const ExchangePage = () => {
    const [amount, setAmount] = useState(10);
    const [allowMultiple, setAllowMultiple] = useState(false);
    const [strategy, setStrategy] = useState("MINIMAL");
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    const handleExchange = async () => {
        setError(null);
        setSuccess(null);
        try {
            const response = await axios.post(
                "http://localhost:8080/api/exchange",
                {
                    amount,
                    allowMultipleBills: allowMultiple,
                    minimal: strategy === "MINIMAL",
                },
                {
                    auth: {
                        username: "klinton",
                        password: "klinton123",
                    },
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );
            setSuccess("Troca realizada com sucesso!");
            console.log(response.data);
        } catch (err) {
            console.error(err);
            setError("Erro ao trocar moeda. Verifique os dados e tente novamente.");
        }
    };

    return (
        <div className="max-w-xl mx-auto p-6 bg-white dark:bg-gray-900 rounded-2xl shadow-lg border border-gray-200 dark:border-gray-700">
            <h1 className="text-xl font-bold mb-4 text-gray-800 dark:text-white">
                🪙 Coin Exchange
            </h1>

            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    Valor da Cédula ($):
                </label>
                <input
                    type="number"
                    value={amount}
                    onChange={(e) => setAmount(parseInt(e.target.value))}
                    className="w-full p-2 rounded-md border dark:bg-gray-800 border-gray-300 dark:border-gray-600 text-gray-900 dark:text-white"
                />
            </div>

            <div className="mb-4 flex items-center">
                <input
                    id="multipleBills"
                    type="checkbox"
                    checked={allowMultiple}
                    onChange={(e) => setAllowMultiple(e.target.checked)}
                    className="mr-2"
                />
                <label htmlFor="multipleBills" className="text-sm text-gray-700 dark:text-gray-300">
                    Permitir múltiplas cédulas
                </label>
            </div>

            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    Estratégia:
                </label>
                <select
                    value={strategy}
                    onChange={(e) => setStrategy(e.target.value)}
                    className="w-full p-2 rounded-md border dark:bg-gray-800 border-gray-300 dark:border-gray-600 text-gray-900 dark:text-white"
                >
                    <option value="MINIMAL">Menor quantidade de moedas</option>
                    <option value="MAXIMAL">Maior variedade de moedas</option>
                </select>
            </div>

            <button
                onClick={handleExchange}
                className="w-full py-2 px-4 bg-blue-600 text-white font-semibold rounded-md hover:bg-blue-700 transition"
            >
                Trocar
            </button>

            {error && <div className="mt-4 text-sm text-red-500 font-medium">{error}</div>}
            {success && <div className="mt-4 text-sm text-green-600 font-medium">{success}</div>}
        </div>
    );
};

export default ExchangePage;

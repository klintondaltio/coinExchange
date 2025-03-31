import { useState } from "react";
import axios from "axios";

interface ExchangeResponse {
    message: string;
    change: Record<number, number>;
}

const ExchangePage = () => {
    const [amount, setAmount] = useState(10);
    const [allowMultiple, setAllowMultiple] = useState(false);
    const [strategy, setStrategy] = useState("MINIMAL");
    const [error, setError] = useState<string | null>(null);
    const [result, setResult] = useState<ExchangeResponse | null>(null);

    const handleExchange = async () => {
        setError(null);
        setResult(null);
        try {
            const response = await axios.post<ExchangeResponse>(
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
            setResult(response.data);
        } catch (err) {
            console.error(err);
            setError("Error while exchanging coins. Please check the input and try again.");
        }
    };

    return (
        <div className="max-w-xl mx-auto p-6 bg-white dark:bg-gray-900 rounded-2xl shadow-lg border border-gray-200 dark:border-gray-700">
            <h1 className="text-xl font-bold mb-4 text-gray-800 dark:text-white">Coin Exchange</h1>

            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    Bill Amount ($):
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
                    Allow multiple bills
                </label>
            </div>

            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    Strategy:
                </label>
                <select
                    value={strategy}
                    onChange={(e) => setStrategy(e.target.value)}
                    className="w-full p-2 rounded-md border dark:bg-gray-800 border-gray-300 dark:border-gray-600 text-gray-900 dark:text-white"
                >
                    <option value="MINIMAL">Minimal number of coins</option>
                    <option value="MAXIMAL">Maximal variety of coins</option>
                </select>
            </div>

            <button
                onClick={handleExchange}
                className="w-full py-2 px-4 bg-blue-600 text-white font-semibold rounded-md hover:bg-blue-700 transition"
            >
                Exchange
            </button>

            {error && (
                <div className="mt-4 text-sm text-red-500 font-medium">
                    {error}
                </div>
            )}

            {result && (
                <div className="mt-6 bg-green-100 dark:bg-green-800 text-green-900 dark:text-green-100 p-4 rounded shadow">
                    <h3 className="font-semibold mb-2">✅ {result.message}</h3>
                    <ul className="list-disc list-inside text-sm">
                        {Object.entries(result.change).map(([coin, qty]) => (
                            <li key={coin}>
                                {qty} coin(s) of {coin}¢
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
};

export default ExchangePage;

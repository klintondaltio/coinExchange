// InventoryPage.tsx
import React, { useEffect, useState } from 'react';
import axios from 'axios';

const InventoryPage: React.FC = () => {
    const [inventory, setInventory] = useState<Record<number, number>>({});
    const [coinValue, setCoinValue] = useState(25);
    const [quantity, setQuantity] = useState(1);
    const [message, setMessage] = useState<string | null>(null);

    const fetchInventory = () => {
        axios
            .get('http://localhost:8080/api/exchange/inventory', {
                auth: { username: 'klinton', password: 'klinton123' },
                withCredentials: true,
            })
            .then((res) => {
                setInventory(res.data.inventory);
            })
            .catch(() => {
                setInventory({});
                setMessage('Erro ao carregar inventário.');
            });
    };

    useEffect(fetchInventory, []);

    const handleAdd = () => {
        axios
            .post(
                'http://localhost:8080/api/exchange/inventory/add',
                { coinValue, quantity },
                {
                    auth: { username: 'klinton', password: 'klinton123' },
                    withCredentials: true,
                }
            )
            .then((res) => {
                setInventory(res.data.inventory);
                setMessage(res.data.message);
            })
            .catch(() => {
                setMessage('Erro ao adicionar moedas.');
            });
    };

    return (
        <div className="max-w-2xl mx-auto p-6 bg-white dark:bg-gray-900 rounded-2xl shadow-lg border border-gray-200 dark:border-gray-700">
            <h2 className="text-xl font-bold text-gray-800 dark:text-white mb-4">
                🪙 Inventário de Moedas
            </h2>

            <div className="flex flex-wrap items-center gap-4 mb-4">
                <input
                    type="number"
                    value={coinValue}
                    onChange={(e) => setCoinValue(parseInt(e.target.value))}
                    className="border border-gray-300 dark:border-gray-600 dark:bg-gray-800 text-gray-900 dark:text-white rounded px-3 py-2 w-28"
                    placeholder="Valor da moeda"
                />
                <input
                    type="number"
                    value={quantity}
                    onChange={(e) => setQuantity(parseInt(e.target.value))}
                    className="border border-gray-300 dark:border-gray-600 dark:bg-gray-800 text-gray-900 dark:text-white rounded px-3 py-2 w-28"
                    placeholder="Quantidade"
                />
                <button
                    onClick={handleAdd}
                    className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
                >
                    ➕ Adicionar
                </button>
                <button
                    onClick={fetchInventory}
                    className="ml-auto text-sm text-blue-600 dark:text-blue-400 underline"
                >
                    🔄 Recarregar
                </button>
            </div>

            {message && <p className="text-sm text-green-700 dark:text-green-400">{message}</p>}

            <table className="w-full mt-4 border border-gray-200 dark:border-gray-700 text-sm">
                <thead className="bg-gray-100 dark:bg-gray-800 text-left">
                <tr>
                    <th className="p-2 border-b border-gray-200 dark:border-gray-700 text-gray-700 dark:text-gray-300">Moeda (¢)</th>
                    <th className="p-2 border-b border-gray-200 dark:border-gray-700 text-gray-700 dark:text-gray-300">Quantidade</th>
                </tr>
                </thead>
                <tbody>
                {Object.entries(inventory).map(([coin, qty]) => (
                    <tr key={coin} className="border-b border-gray-100 dark:border-gray-800">
                        <td className="p-2 text-gray-900 dark:text-white">{coin}</td>
                        <td className="p-2 text-gray-900 dark:text-white">{qty}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default InventoryPage;
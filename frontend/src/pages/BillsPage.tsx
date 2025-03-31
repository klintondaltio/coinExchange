import React, { useEffect, useState } from 'react';
import axios from 'axios';

interface BillsInventoryResponse {
    totalBillsReceived: number;
    billInventory: Record<number, number>;
}

const BillsPage: React.FC = () => {
    const [bills, setBills] = useState<BillsInventoryResponse | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        axios
            .get('http://localhost:8080/api/exchange/bills', {
                auth: { username: 'klinton', password: 'klinton123' },
                withCredentials: true,
            })
            .then((res) => setBills(res.data))
            .catch(() => setError('Error loading bill information.'));
    }, []);

    return (
        <div className="max-w-3xl mx-auto p-6 bg-white dark:bg-gray-900 rounded-2xl shadow-lg border border-gray-200 dark:border-gray-700">
            <h2 className="text-xl font-bold text-gray-800 dark:text-white mb-4">Bill Inventory</h2>

            {error && <p className="text-red-600 dark:text-red-400 mb-4">{error}</p>}

            {bills ? (
                <div className="space-y-4">
                    <p className="text-gray-700 dark:text-gray-300 text-sm">
                        Total bills received: <strong>{bills.totalBillsReceived}</strong>
                    </p>

                    <table className="w-full border border-gray-200 dark:border-gray-700 text-sm">
                        <thead className="bg-gray-100 dark:bg-gray-800">
                        <tr>
                            <th className="p-2 border-b dark:border-gray-600 text-left text-gray-700 dark:text-white">Bill ($)</th>
                            <th className="p-2 border-b dark:border-gray-600 text-left text-gray-700 dark:text-white">Quantity</th>
                        </tr>
                        </thead>
                        <tbody>
                        {Object.entries(bills.billInventory).map(([bill, qty]) => (
                            <tr key={bill} className="border-b border-gray-200 dark:border-gray-700">
                                <td className="p-2 text-gray-800 dark:text-gray-200">{bill}</td>
                                <td className="p-2 text-gray-800 dark:text-gray-200">{qty}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            ) : (
                <p className="text-gray-600 dark:text-gray-400 text-sm">Loading data...</p>
            )}
        </div>
    );
};

export default BillsPage;

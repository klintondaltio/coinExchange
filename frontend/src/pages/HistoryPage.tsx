import React, { useEffect, useState } from 'react';
import axios from 'axios';

interface ExchangeTransaction {
    id: number;
    amount: number;
    minimal: boolean;
    transactionDate: string;
    change: Record<number, number>;
}

const HistoryPage: React.FC = () => {
    const [transactions, setTransactions] = useState<ExchangeTransaction[]>([]);
    const [filters, setFilters] = useState({
        startDate: '',
        endDate: '',
        minAmount: '',
        maxAmount: '',
        minimal: '',
    });

    const fetchHistory = () => {
        const params: any = {};

        if (filters.startDate) params.startDate = new Date(filters.startDate).toISOString();
        if (filters.endDate) params.endDate = new Date(filters.endDate).toISOString();
        if (filters.minAmount) params.minAmount = filters.minAmount;
        if (filters.maxAmount) params.maxAmount = filters.maxAmount;
        if (filters.minimal !== '') params.minimal = filters.minimal;

        axios
            .get('http://localhost:8080/api/exchange/history/filter', {
                params,
                auth: { username: 'klinton', password: 'klinton123' },
                withCredentials: true,
            })
            .then((res) => setTransactions(res.data))
            .catch(() => setTransactions([]));
    };

    const clearFilters = () => {
        setFilters({
            startDate: '',
            endDate: '',
            minAmount: '',
            maxAmount: '',
            minimal: '',
        });
        fetchHistory();
    };

    useEffect(() => {
        fetchHistory();
    }, []);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFilters((prev) => ({ ...prev, [name]: value }));
    };

    return (
        <div className="max-w-5xl mx-auto p-6 bg-white dark:bg-gray-900 rounded-2xl shadow-lg border border-gray-200 dark:border-gray-700">
            <h2 className="text-xl font-bold text-gray-800 dark:text-white mb-4">Transaction History</h2>

            <div className="grid grid-cols-2 md:grid-cols-3 gap-4 mb-6">
                <input
                    type="datetime-local"
                    name="startDate"
                    value={filters.startDate}
                    onChange={handleChange}
                    className="border p-2 rounded text-sm dark:bg-gray-800 dark:text-white dark:border-gray-600"
                    placeholder="Start date"
                />
                <input
                    type="datetime-local"
                    name="endDate"
                    value={filters.endDate}
                    onChange={handleChange}
                    className="border p-2 rounded text-sm dark:bg-gray-800 dark:text-white dark:border-gray-600"
                    placeholder="End date"
                />
                <input
                    type="number"
                    name="minAmount"
                    value={filters.minAmount}
                    onChange={handleChange}
                    className="border p-2 rounded text-sm dark:bg-gray-800 dark:text-white dark:border-gray-600"
                    placeholder="Minimum amount"
                />
                <input
                    type="number"
                    name="maxAmount"
                    value={filters.maxAmount}
                    onChange={handleChange}
                    className="border p-2 rounded text-sm dark:bg-gray-800 dark:text-white dark:border-gray-600"
                    placeholder="Maximum amount"
                />
                <select
                    name="minimal"
                    value={filters.minimal}
                    onChange={handleChange}
                    className="border p-2 rounded text-sm dark:bg-gray-800 dark:text-white dark:border-gray-600"
                >
                    <option value="">Any strategy</option>
                    <option value="true">Minimal coins</option>
                    <option value="false">Maximal coins</option>
                </select>
            </div>

            <div className="flex gap-4 mb-6">
                <button
                    onClick={fetchHistory}
                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition text-sm"
                >
                    Filter History
                </button>

                <button
                    onClick={clearFilters}
                    className="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400 transition text-sm dark:bg-gray-600 dark:text-white dark:hover:bg-gray-500"
                >
                    Clear Filters
                </button>
            </div>

            <div className="space-y-4">
                {transactions.length === 0 ? (
                    <p className="text-gray-500 dark:text-gray-400">No transactions found.</p>
                ) : (
                    transactions.map((t) => (
                        <div key={t.id} className="border border-gray-200 dark:border-gray-700 p-4 rounded bg-gray-50 dark:bg-gray-800">
                            <p className="text-sm text-gray-700 dark:text-gray-300">
                                <strong>ID:</strong> {t.id} | <strong>Amount:</strong> ${t.amount} |{' '}
                                <strong>Strategy:</strong> {t.minimal ? 'Minimal' : 'Maximal'}
                            </p>
                            <p className="text-sm text-gray-600 dark:text-gray-400">
                                <strong>Date:</strong> {new Date(t.transactionDate).toLocaleString()}
                            </p>
                            <ul className="list-disc ml-5 text-sm mt-2 text-gray-800 dark:text-white">
                                {Object.entries(t.change).map(([coin, qty]) => (
                                    <li key={coin}>
                                        {qty} coin(s) of {coin}¢
                                    </li>
                                ))}
                            </ul>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default HistoryPage;

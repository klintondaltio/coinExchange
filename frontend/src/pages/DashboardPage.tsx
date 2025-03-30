// DashboardPage.tsx
import React, { useEffect, useState } from 'react';
import axios from 'axios';

const DashboardPage: React.FC = () => {
    const [status, setStatus] = useState<string>('Carregando...');
    const [inventory, setInventory] = useState<Record<number, number>>({});
    const [totalValue, setTotalValue] = useState<string>('');
    const [bills, setBills] = useState<Record<number, number>>({});
    const [totalBills, setTotalBills] = useState<number>(0);

    useEffect(() => {
        fetchStatus();
        fetchInventory();
        fetchBills();
    }, []);

    const fetchStatus = () => {
        axios
            .get('http://localhost:8080/api/exchange/admin/status', {
                auth: { username: 'klinton', password: 'klinton123' },
                withCredentials: true,
                validateStatus: () => true,
            })
            .then((res) => setStatus(res.data.status))
            .catch(() => setStatus('Erro ao obter status.'));
    };

    const fetchInventory = () => {
        axios
            .get('http://localhost:8080/api/exchange/status', {
                auth: { username: 'klinton', password: 'klinton123' },
                withCredentials: true,
            })
            .then((res) => {
                setInventory(res.data.coinInventory);
                setTotalValue(res.data.totalValue);
            })
            .catch(() => setInventory({}));
    };

    const fetchBills = () => {
        axios
            .get('http://localhost:8080/api/exchange/bills', {
                auth: { username: 'klinton', password: 'klinton123' },
                withCredentials: true,
            })
            .then((res) => {
                setBills(res.data.billInventory);
                setTotalBills(res.data.totalBillsReceived);
            })
            .catch(() => setBills({}));
    };

    return (
        <div className="max-w-3xl mx-auto mt-10 p-6 bg-white dark:bg-gray-900 rounded-2xl shadow-md">
            <h1 className="text-2xl font-bold text-gray-800 dark:text-white text-center">📊 Dashboard</h1>
            <p className="text-lg text-gray-600 dark:text-gray-300 mt-4 text-center">
                Bem-vindo ao sistema de trocas Coin Exchange!
            </p>

            <div className="mt-6 space-y-6">
                <div className="bg-gray-50 dark:bg-gray-800 p-4 rounded-lg shadow">
                    <h2 className="font-semibold text-gray-800 dark:text-white mb-2">⚙️ Status da Máquina</h2>
                    <p className={`text-sm ${status.includes('out') ? 'text-red-500' : 'text-green-600'}`}>{status}</p>
                </div>

                <div className="bg-gray-50 dark:bg-gray-800 p-4 rounded-lg shadow">
                    <h2 className="font-semibold text-gray-800 dark:text-white mb-2">🪙 Inventário de Moedas</h2>
                    <p className="text-sm text-gray-600 dark:text-gray-300 mb-2">Total em caixa: {totalValue}</p>
                    <ul className="text-sm space-y-1">
                        {Object.entries(inventory).map(([coin, qty]) => (
                            <li key={coin} className="text-gray-700 dark:text-gray-200">
                                {qty} moeda(s) de {coin}¢
                            </li>
                        ))}
                    </ul>
                </div>

                <div className="bg-gray-50 dark:bg-gray-800 p-4 rounded-lg shadow">
                    <h2 className="font-semibold text-gray-800 dark:text-white mb-2">💵 Cédulas Recebidas</h2>
                    <p className="text-sm text-gray-600 dark:text-gray-300 mb-2">Total acumulado quando solicitado multiplas cédulas: ${totalBills}</p>
                    <ul className="text-sm space-y-1">
                        {Object.entries(bills).map(([bill, qty]) => (
                            <li key={bill} className="text-gray-700 dark:text-gray-200">
                                {qty} cédula(s) de ${bill}
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default DashboardPage;

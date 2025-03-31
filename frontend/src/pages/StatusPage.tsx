import React, { useEffect, useState } from 'react';
import axios from 'axios';

const StatusPage: React.FC = () => {
    const [status, setStatus] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);

    const fetchStatus = () => {
        axios
            .get('http://localhost:8080/api/exchange/admin/status', {
                auth: {
                    username: 'klinton',
                    password: 'klinton123',
                },
                withCredentials: true,
                validateStatus: () => true,
            })
            .then((res) => {
                setStatus(res.data.status);
                setError(null);
            })
            .catch(() => {
                setError('Erro ao consultar status da máquina.');
            });
    };

    useEffect(() => {
        fetchStatus();
    }, []);

    return (
        <div className="max-w-xl mx-auto p-6 bg-white dark:bg-gray-900 rounded-2xl shadow-lg border border-gray-200 dark:border-gray-700">
            <h2 className="text-xl font-bold text-gray-800 dark:text-white mb-4">⚙️ Status da Máquina</h2>

            {error && <p className="text-red-600 dark:text-red-400 mb-4">{error}</p>}

            {status && (
                <p className={`text-lg font-semibold ${status.includes('out') ? 'text-red-600 dark:text-red-400' : 'text-green-600 dark:text-green-400'}`}>
                    {status === 'Machine operational' ? '✅ Máquina Operacional' : '❌ Máquina Fora de Operação'}
                </p>
            )}

            <div className="mt-4 text-center">
                <button
                    onClick={fetchStatus}
                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition text-sm"
                >
                    🔄 Atualizar Status
                </button>
            </div>
        </div>
    );
};

export default StatusPage;

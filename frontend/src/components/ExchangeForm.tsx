import React, { useState } from 'react';
import axios from 'axios';

const ExchangeForm: React.FC = () => {
    const [amount, setAmount] = useState(10);
    const [minimal, setMinimal] = useState(true);
    const [allowMultipleBills, setAllowMultipleBills] = useState(false);
    const [result, setResult] = useState<Record<number, number> | null>(null);
    const [error, setError] = useState<string | null>(null);

    const handleExchange = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setResult(null);

        try {
            const response = await axios.post(
                'http://localhost:8080/api/exchange',
                {
                    amount,
                    minimal,
                    allowMultipleBills,
                },
                {
                    withCredentials: true, // 👈 ESSENCIAL para enviar cookies/autenticação cruzada
                    auth: {
                        username: 'klinton',
                        password: 'klinton123',
                    },
                }
            );

            setResult(response.data.change);
        } catch (err: any) {
            if (err.response?.data?.error) {
                setError(err.response.data.error);
            } else {
                setError('Erro ao conectar com o servidor.');
            }
        }
    };

    return (
        <form onSubmit={handleExchange} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <label>
                Valor da Cédula ($):
                <input type="number" value={amount} onChange={e => setAmount(Number(e.target.value))} required />
            </label>

            <label>
                <input type="checkbox" checked={allowMultipleBills} onChange={e => setAllowMultipleBills(e.target.checked)} />
                Permitir múltiplas cédulas
            </label>

            <label>
                Estratégia:
                <select value={minimal ? 'minimal' : 'maximal'} onChange={e => setMinimal(e.target.value === 'minimal')}>
                    <option value="minimal">Menor quantidade de moedas</option>
                    <option value="maximal">Maior quantidade de moedas</option>
                </select>
            </label>

            <button type="submit">Trocar</button>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {result && (
                <div>
                    <h3>Resultado da troca:</h3>
                    <ul>
                        {Object.entries(result).map(([coin, qty]) => (
                            <li key={coin}>
                                {qty} moeda(s) de {coin} centavos
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </form>
    );
};

export default ExchangeForm;

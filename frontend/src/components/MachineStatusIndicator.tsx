import React, { useEffect, useState } from 'react';
import axios from 'axios';

const MachineStatusIndicator: React.FC = () => {
    const [status, setStatus] = useState<'ok' | 'out' | null>(null);

    useEffect(() => {
        axios
            .get('http://localhost:8080/api/exchange/admin/status', {
                auth: { username: 'klinton', password: 'klinton123' },
                withCredentials: true,
            })
            .then(() => setStatus('ok'))
            .catch(() => setStatus('out')); // <- trata erro como "fora de operação"
    }, []);

    if (status === 'out') {
        return <span className="text-red-600 font-semibold">❌ Machine out of coins</span>;
    }

    if (status === 'ok') {
        return <span className="text-green-600 font-semibold">✅ Machine operational</span>;
    }

    return null;
};

export default MachineStatusIndicator;

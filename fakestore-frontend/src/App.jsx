import {useEffect, useState} from 'react'

function App() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [token, setToken] = useState(localStorage.getItem('token')); // Hämta befintlig token
    const [products, setProducts] = useState([]);
    const USER_SERVICE_URL = "http://ec2-13-51-166-242.eu-north-1.compute.amazonaws.com:8080";
    const PRODUCT_SERVICE_URL = "http://ec2-51-21-168-99.eu-north-1.compute.amazonaws.com:8081";

    // Denna körs automatiskt när "token" ändras
    useEffect(() => {
        if (token) {
            fetchProducts();
        }
    }, [token]);

    const handleLogin = async () => {
        try {
            const response = await fetch(`${USER_SERVICE_URL}/request-token`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({email, password})
            });

            if (response.ok) {
                const data = await response.json();
                const jwt = data.token;
                localStorage.setItem('token', jwt);
                setToken(jwt);
            } else {
                alert('Inloggning misslyckades');
            }
        } catch (error) {
            console.error('Fel vid inloggning:', error);
        }
    };

    const fetchProducts = async () => {
        try {
            const response = await fetch(`${PRODUCT_SERVICE_URL}/products`, {
                headers: {'Authorization': `Bearer ${token}`}
            });
            if (response.ok) {
                const data = await response.json();
                setProducts(data);
            }
        } catch (error) {
            console.error('Kunde inte hämta produkter:', error);
        }
    };

    return (
        <div style={{padding: '20px'}}>
            {!token ? (
                <section>
                    <h1>Fakestore Login</h1>
                    <input type="text" placeholder="E-mail" onChange={(e) => setEmail(e.target.value)}/>
                    <input type="password" placeholder="Lösenord" onChange={(e) => setPassword(e.target.value)}/>
                    <button onClick={handleLogin}>Logga in</button>
                </section>
            ) : (
                <section>
                    <h1>Välkommen till butiken!</h1>
                    <button onClick={() => {
                        localStorage.removeItem('token');
                        setToken(null);
                    }}>Logga ut
                    </button>

                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(3, 1fr)',
                        gap: '20px',
                        marginTop: '20px'
                    }}>
                        {products.map(product => (
                            <div key={product.id}
                                 style={{border: '1px solid #ccc', padding: '10px', borderRadius: '8px'}}>
                                <img src={product.image} alt={product.title} style={{width: '100px'}}/>
                                <h3>{product.title}</h3>
                                <p>{product.price} kr</p>
                                <button onClick={() => alert(`Du la ${product.title} i varukorgen!`)}>Köp</button>
                            </div>
                        ))}
                    </div>
                </section>
            )}
        </div>
    )
}

export default App
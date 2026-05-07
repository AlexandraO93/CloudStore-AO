import {useEffect, useState} from "react";
import {useAuth} from "../context/useAuth.js";

const LikedProducts = () => {
    const {token} = useAuth();
    const [likedProducts, setLikedProducts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchLiked = async () => {

            if (!token) return;

            try {
                setLoading(true);
                const res = await fetch(`http://localhost:8081/products/liked`, {
                    headers: {Authorization: `Bearer ${token}`}
                });

                if (res.ok) {
                    const data = await res.json();
                    setLikedProducts(filtered);
                }
            } catch (err) {
                console.error("Kunde inte hämta gillade produkter", err);
            } finally {
                setLoading(false);
            }
        };

        fetchLiked();
    }, [token]);

    if (loading) return <p>Laddar dina favoriter...</p>

    return (
        <div>
            <h2>Mina Favoriter ♥</h2>
            <div className="product-grid" style={{display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '20px'}}>
                {likedProducts.length > 0 ? (
                    likedProducts.map(p => (
                        <div key={p.id} className="product-card" style={{border: '1px solid #ccc', padding: '10px'}}>
                            <img src={p.image} alt={p.title} style={{width: '80px'}}/>
                            <h3>{p.title}</h3>
                            <p>{p.price} kr</p>
                        </div>
                    ))
                ) : (
                    <p>Du har inte gillat några produkter än.</p>
                )}
            </div>
        </div>
    );
};

export default LikedProducts;
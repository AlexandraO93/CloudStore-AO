import {useEffect, useState} from "react";
import {useAuth} from "../context/useAuth.js";
import ProductCard from "../components/ProductCard.jsx";

const LikedProducts = () => {
    const {token, user} = useAuth();
    const [likedProducts, setLikedProducts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchLiked = async () => {

            if (!token || !user.email) return;

            try {
                setLoading(true);
                const res = await fetch(`http://localhost:8081/products/liked?email=${user.email}`, {
                    headers: {Authorization: `Bearer ${token}`}
                });

                if (res.ok) {
                    const data = await res.json();
                    setLikedProducts(data);
                }
            } catch (err) {
                console.error("Kunde inte hämta gillade produkter", err);
            } finally {
                setLoading(false);
            }
        };

        fetchLiked();
    }, [token, user?.email]);

    const handleStatusChange = (productId, isNowLiked) => {
        if (!isNowLiked) {
            setLikedProducts(prevProducts =>
                prevProducts.filter(p => p.id !== productId)
            );
        }
    };

    if (loading) return <p>Laddar dina favoriter...</p>

    return (
        <div className="liked-container">
            <h2>Mina Favoriter ♥</h2>
            {likedProducts.length === 0 ? (
                <p>Du har inte gillat några produkter än.</p>
            ) : (
                <div className="product-grid">
                    {likedProducts.map(product => (
                        <ProductCard
                            key={product.id}
                            product={product}
                            onStatusChange={handleStatusChange}
                        />
                    ))}
                </div>
            )}
        </div>
    );
};

export default LikedProducts;
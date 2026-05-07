import {useEffect, useState} from "react";
import {useAuth} from "../context/useAuth.js";
import ProductCard from "../components/ProductCard.jsx";
import "./ProductList.css";

const ProductList = () => {
    const {token, user} = useAuth();
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(false);

    const fetchProducts = async () => {
        if (!token) return;

        try {
            setLoading(true);
            const res = await fetch(`http://localhost:8081/products/fetch`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                },
            });

            if (!res.ok) {
                throw new Error("Produkter hämtades inte korrekt");
            }

            const data = await res.json();
            const productsWithLikeStatus = data.map(p => ({
                ...p,
                likedByMe: p.likedByEmails?.includes(user?.email)
            }));
            setProducts(productsWithLikeStatus);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        let isMounted = true;

        if (token && products.length === 0 && !loading) {
            fetchProducts();
        }

        return () => {
            isMounted = false;
        };
    }, [token]);


    return (
        <div>
            <h2>Våra Produkter</h2>
            {loading && <p>Laddar...</p>}
            <div className="product-grid">
                {products.map(product => (
                    <div className="product-card">
                        <ProductCard
                            key={product.id}
                            product={product}
                            isLiked={product.likedByMe}
                        />
                    </div>
                ))}
            </div>
        </div>
    );
}

export default ProductList;
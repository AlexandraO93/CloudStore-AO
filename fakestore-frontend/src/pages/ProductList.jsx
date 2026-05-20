import {useEffect, useState} from "react";
import {useAuth} from "../context/AuthContext.jsx";
import ProductCard from "../components/ProductCard.jsx";
import "./ProductList.css";
import {PRODUCT_API_URL} from "../config/api.js";

const ProductList = () => {
    const {token, user} = useAuth();
    const [products, setProducts] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState("all");
    const [loading, setLoading] = useState(false);

    const fetchProducts = async () => {
        if (!token) return;

        try {
            setLoading(true);
            const res = await fetch(`${PRODUCT_API_URL}/fetch`, {
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
            localStorage.setItem("all_products", JSON.stringify(data));
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

    const categories = ["all", ...new Set(products.map(p => p.category).filter(Boolean))];

    const filteredProducts = selectedCategory === "all"
        ? products
        : products.filter(product => product.category === selectedCategory);

    return (
        <div>
            <h2 className="product-card-title">Våra Produkter</h2>
            {products.length > 0 && (
                <div className="filter-container">
                    {categories.map(category => (
                        <button
                            key={category}
                            className={`filter-button ${selectedCategory === category ? "active" : ""}`}
                            onClick={() => setSelectedCategory(category)}
                        >
                            {category === "all" ? "Alla" : category.charAt(0).toUpperCase() + category.slice(1)}
                        </button>
                    ))}
                </div>
            )}

            {loading && <p>Laddar...</p>}
            <div className="product-grid">
                {filteredProducts.map(product => (
                    <ProductCard
                        key={product.id}
                        product={product}
                        isLiked={product.likedByMe}
                    />
                ))}
            </div>
            {!loading && filteredProducts.length === 0 && products.length > 0 && (
                <p className="no-products-text">Inga produkter hittades i denna kategori.</p>
            )}
        </div>
    );
}

export default ProductList;
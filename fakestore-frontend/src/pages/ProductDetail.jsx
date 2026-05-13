import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";
import "./ProductDetail.css"
import ProductCard from "../components/ProductCard.jsx";
import {PRODUCT_API_URL} from "../config/api.js";

const ProductDetail = () => {
    const {id} = useParams(); // Hämtar ID:t från URL:en
    const {token} = useAuth();
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchProduct = async () => {
            if (!token) return;

            try {
                setLoading(true);
                const res = await fetch(`${PRODUCT_API_URL}/products/${id}`, {
                    headers: {"Authorization": `Bearer ${token}`}
                });
                const data = await res.json();
                setProduct(data);
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        fetchProduct();
    }, [id, token]);

    if (!product) return <p>Laddar produkt...</p>;

    return (
        <div className="product-detail-container">
            {loading && <p>Laddar...</p>}
            <ProductCard
                product={product}
                showQuantityControls={true}
            />

            <div className="description-box">
                <h3 className="detail-description-title">Beskrivning</h3>
                <p className="detail-description">{product.description}</p>
            </div>
        </div>
    );
};

export default ProductDetail;
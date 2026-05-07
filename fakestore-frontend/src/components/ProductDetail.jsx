import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {useAuth} from "../context/useAuth";
import "./ProductDetail.css"

const ProductDetail = () => {
    const {id} = useParams(); // Hämtar ID:t från URL:en
    const {token} = useAuth();
    const [product, setProduct] = useState(null);

    useEffect(() => {
        const fetchProduct = async () => {
            const res = await fetch(`http://localhost:8081/products/${id}`, {
                headers: {"Authorization": `Bearer ${token}`}
            });
            const data = await res.json();
            setProduct(data);
        };
        fetchProduct();
    }, [id, token]);

    if (!product) return <p>Laddar produkt...</p>;

    return (
        <div className="product-detail-container">
            <img className="product-image" src={product.image} alt={product.title}/>

            <div className="product-info-big">
                <h1 className="detail-title">{product.title}</h1>
                <p className="detail-category">{product.category}</p>
                <h2 className="detail-price">{product.price * 10} kr</h2>

                <div className="description-box">
                    <h3 className="detail-description-title">Beskrivning</h3>
                    <p className="detail-description">{product.description}</p>
                </div>

                <button className="buy-button-big">Lägg i varukorg</button>
            </div>
        </div>
    );
};

export default ProductDetail;
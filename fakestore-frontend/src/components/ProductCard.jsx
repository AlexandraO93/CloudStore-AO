import React, {useState} from 'react';
import {useNavigate} from "react-router-dom";
import "./ProductCard.css";
import {useAuth} from "../context/useAuth.js";

export default function ProductCard({product, onStatusChange}) {
    const navigate = useNavigate();
    const {token, user} = useAuth();
    const [isLiked, setIsLiked] = useState(product.likedByEmails?.includes(user?.email));

    const handleLikeProduct = async (e) => {
        e.preventDefault();

        if (!token) return;

        try {
            const res = await fetch(`http://localhost:8081/products/${product.id}/like`, {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (res.ok) {
                const newLikedState = !isLiked;
                setIsLiked(newLikedState);

                if (onStatusChange) {
                    onStatusChange(product.id, newLikedState);
                }
            }
        } catch (error) {
            console.error("Kunde inte gilla inlägget:", error);
        }
    };

    return (
        <article className="product-card">
            <div onClick={() => navigate(`/products/${product.id}`)}>
                <img className="product-image" src={product.image} alt={product.title}/>
                <h3 className="product-title">{product.title}</h3>
                <small className="product-category">{product.category} </small>
                <p className="product-price"><strong>Pris: </strong>{product.price * 10} kr</p>
            </div>

            <div className="product-buttons">
                <div>
                    <button className="read-more-button" onClick={() => navigate(`/products/${product.id}`)}>Läs mer
                    </button>
                    <button className="like-button" onClick={handleLikeProduct}>
                        {isLiked ? '♥' : '♡'}
                    </button>
                </div>
                <div>
                    <button className="buy-button">Lägg i varukorgen</button>
                </div>
            </div>
        </article>
    );
}


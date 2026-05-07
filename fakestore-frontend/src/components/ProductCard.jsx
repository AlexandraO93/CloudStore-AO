import React from 'react';
import {useNavigate} from "react-router-dom";
import "./ProductCard.css";

export default function ProductCard({product, onLike, isLiked}) {
    const navigate = useNavigate();

    if (!product) return null;

    return (
        <article className="product-card">
            <div>
                <img className="product-image" src={product.image} alt={product.title}/>
                <h3 className="product-title">{product.title}</h3>
                <small className="product-category">{product.category} </small>
                <p className="product-price"><strong>Pris: </strong>{product.price * 10} kr</p>
            </div>

            <div className="product-buttons">
                <div>
                    <button className="read-more-button" onClick={() => navigate(`/products/${product.id}`)}>Läs mer
                    </button>
                    <button className="like-button" onClick={() => onLike(product.id)}>
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


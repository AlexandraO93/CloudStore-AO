import "./Navbar.css";
import LogoSmall from "../assets/logo-small.png"
import {useAuth} from "../context/AuthContext.jsx";
import Navbar from 'react-bootstrap/Navbar';
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import {Link} from "react-router-dom";
import {useCart} from "../context/CartContext.jsx";

function CloudStoreNavBar() {
    const {user, logout} = useAuth();
    const id = user?.id;
    const {cartItems} = useCart();

    const totalQuantity = cartItems.reduce((total, item) => total + Number(item.quantity), 0);

    return (
        <Navbar className="navBar">
            <Container fluid>
                <Navbar.Brand
                    as={Link}
                    to="/products"
                    className="headline"
                >
                    <img
                        className="logo-small"
                        src={LogoSmall}
                        alt="Cloudstore logo"
                    />
                </Navbar.Brand>

                <Nav className="ms-auto navLinks">
                    {user && (
                        <>
                            <Nav.Link as={Link} to={`/users/${id}`}>Min profil</Nav.Link>
                            <Nav.Link as={Link} to="/products/liked">♥</Nav.Link>
                            <div className="shopping-cart">
                                <Nav.Link as={Link} to="/shopping-cart">Varukorg</Nav.Link>
                                <span className="shopping-cart-number">{totalQuantity}</span>
                            </div>
                            <Nav.Link onClick={logout}>
                                Logga ut
                            </Nav.Link>
                        </>
                    )}

                    {!user && (
                        <>
                            <Nav.Link as={Link} to="/login">Logga in</Nav.Link>
                            <Nav.Link as={Link} to="/register">Skapa konto</Nav.Link>
                        </>
                    )}
                </Nav>
            </Container>
        </Navbar>
    )
}

export default CloudStoreNavBar;
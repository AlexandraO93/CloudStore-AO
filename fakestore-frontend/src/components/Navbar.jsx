import "./Navbar.css";
import LogoSmall from "../assets/logo-small.png"
import {useAuth} from "../context/useAuth.js";
import Navbar from 'react-bootstrap/Navbar';
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import {Link} from "react-router-dom";

function CloudStoreNavBar() {
    const {user, logout} = useAuth();
    const id = user?.id;

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
                            <Nav.Link as={Link} to="/products/checkout">Varukorg</Nav.Link>
                            <Nav.Link onClick={logout} style={{cursor: 'pointer'}}>
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
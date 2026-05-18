import "./Footer.css";
import LogoFooter from "../assets/logo-footer.png";

export default function Footer() {
    return (
        <footer className="site-footer" role="contentinfo">
            <div className="footer-grid">
                <div className="footer-left">
                    <p className="contact-line">
                        <strong>Telefon: </strong>
                        <span className="contact-value"> 070-123 45 67</span>
                    </p>
                    <p className="contact-line">
                        <strong>E-post: </strong>
                        <span className="contact-value"> info@cloudstore.se</span>
                    </p>
                    <p className="contact-line">
                        <strong>Adress: </strong>
                        <span className="contact-value"> Låtsasvägen 23,<br/> 123 45 Stockholm</span>
                    </p>
                </div>

                <div className="footer-center" aria-hidden="false">
                    <img
                        className="logo-footer"
                        src={LogoFooter}
                        alt="Roslagen Escape logotyp"
                    />
                    <p className="copyright-text"> © Cloudstore 2026</p>
                </div>

            </div>
        </footer>
    );
}

# CloudStore

En molnbaserad e-handelsapplikation byggd med mikrotjänstarkitektur. CloudStore integrerar med [FakeStore API](https://fakestoreapi.com) för produktdata och erbjuder funktioner för användarregistrering, inloggning, produktvisning, like-funktion och orderhantering.

---

## Live Demo

[cloudstore-ao.duckdns.org](https://cloudstore-ao.duckdns.org/)

---

## Innehållsförteckning

- [Höjdpunkter](#höjdpunkter)
- [Arkitektur](#arkitektur)
- [Funktioner](#funktioner)
- [Tech stack](#tech-stack)
- [API-endpoints](#api-endpoints)
- [Tester](#tester)
- [CI/CD](#cicd)
- [Deployment](#deployment)
- [Projektstruktur](#projektstruktur)
- [Vad jag lärt mig](#vadjaglärtmig)

---

## Höjdpunkter

- Mikrotjänstarkitektur med separata tjänster för användare och produkter
- JWT-baserad autentisering med asymmetriska RSA-nycklar (RS256)
- Automatisk nyckeldelning mellan tjänster via JWKS
- Containeriserad applikation med Docker och Docker Compose
- Automatiserad CI/CD-pipeline med GitHub Actions
- Drift i AWS med EC2, RDS och Nginx
- React-baserad frontend med skyddade vyer och autentisering
- Integration med FakeStore API för produktdata
- Enhetstester och säkerhetstester med JUnit, Mockito och Spring Security Test

---

## Arkitektur

CloudStore är uppdelat i tre oberoende tjänster som var och en körs i sin egen Docker-container och i produktion på sin egen AWS EC2-instans:

```
┌─────────────────────────────────────────────────────┐
│                    Användare                        │
└──────────────────────┬──────────────────────────────┘
                       │
              ┌────────▼────────┐
              │    Frontend     │  React + Vite
              │   (port 80)     │  Nginx
              └────┬───────┬───┘
                   │       │
        ┌──────────▼─┐  ┌──▼──────────────┐
        │   users-   │  │   product-      │
        │  service   │  │   service       │
        │ (port 8080)│  │  (port 8081)    │
        └──────┬─────┘  └──────┬──────────┘
               │               │
               └───────┬───────┘
                       │
              ┌────────▼────────┐
              │   AWS RDS       │
              │   MySQL         │
              └─────────────────┘
```

**users-service** — hanterar registrering, inloggning, användardata och ordrar. Utfärdar JWT-tokens vid inloggning och exponerar en JWKS-endpoint för nyckeldelning.

**product-service** — hanterar produkter via FakeStore API, sparar dem i databasen och hanterar like-funktionalitet. Skyddar alla endpoints med JWT-validering.

**frontend** — React-applikation serverad av Nginx. Kommunicerar med båda backend-tjänsterna via HTTP med JWT i Authorization-headern.

### JWT-säkerhet mellan tjänsterna

```
users-service          product-service
      │                       │
      │  Privat RSA-nyckel     │  Publik RSA-nyckel
      │  → Signerar tokens     │  ← Verifierar tokens
      │                       │
      └──── /.well-known/ ────►│
               jwks.json
```

users-service signerar tokens med en privat RSA-nyckel (RS256). product-service hämtar den publika nyckeln automatiskt från users-service JWKS-endpoint och verifierar inkommande tokens mot den. Den privata nyckeln lämnar aldrig users-service.

---

## Funktioner

### Besökare
- Visa produkter
- Filtrera produkter efter kategori
- Se produktdetaljer
- Registrera konto
- Logga in

### Inloggade användare
- Lägga produkter i kundvagn
- Hantera favoriter
- Genomföra köp
- Se orderhistorik
- Visa orderdetaljer
- Uppdatera profiluppgifter

---

## Tech stack

| Del | Teknik |
|---|---|
| Backend | Java 21, Spring Boot, Spring Security, Spring Data JPA |
| Frontend | React 19, Vite, React Router, React Bootstrap |
| Databas | MySQL 8 (produktion: AWS RDS, lokalt: MySQL i Docker) |
| Auth | JWT med RS256 (asymmetriskt nyckelpar), BCrypt |
| Container | Docker, Docker Compose |
| CI/CD | GitHub Actions |
| Molninfrastruktur | AWS EC2, AWS RDS |
| Webbserver | Nginx |
| Tester | JUnit 5, Mockito, Spring Security Test, H2 (testdatabas) |

---

## API-endpoints

### users-service (port 8080)

#### Autentisering
| Metod | Endpoint | Auth | Beskrivning |
|---|---|---|---|
| POST | `/users/register` | Nej | Registrera ny användare |
| POST | `/request-token` | Nej | Logga in, returnerar JWT |
| GET | `/.well-known/jwks.json` | Nej | Publik JWKS-nyckel |

#### Användare
| Metod | Endpoint | Auth | Beskrivning |
|---|---|---|---|
| GET | `/users/{id}` | Ja | Hämta användare |
| PUT | `/users/{id}` | Ja | Uppdatera användare |
| DELETE | `/users/{id}` | Ja | Ta bort användare |
| GET | `/users/{id}/orders-by-user` | Ja | Användare med alla ordrar |

#### Ordrar
| Metod | Endpoint | Auth | Beskrivning |
|---|---|---|---|
| POST | `/users/{id}/orders` | Ja | Skapa order |
| GET | `/users/{id}/orders` | Ja | Hämta alla ordrar för användare |
| GET | `/orders/{orderId}` | Ja | Hämta specifik order |
| PUT | `/orders/{orderId}/status` | Ja | Uppdatera orderstatus |
| DELETE | `/orders/{orderId}` | Ja | Avbryt order |

### product-service (port 8081)

| Metod | Endpoint | Auth | Beskrivning |
|---|---|---|---|
| POST | `/products/fetch` | Ja | Hämta och spara produkter från FakeStore API |
| GET | `/products` | Ja | Hämta alla produkter |
| GET | `/products/{id}` | Ja | Hämta produkt |
| POST | `/products/{id}/like` | Ja | Gilla/ogilla produkt |
| GET | `/products/liked?email=` | Ja | Hämta gillade produkter för användare |

Alla endpoints som kräver auth förväntar sig en JWT i headern:
```
Authorization: Bearer <token>
```

---

## Tester

Testerna körs med en H2 in-memory-databas och kräver inga externa tjänster.

### Testöversikt

| Testklass | Vad testas |
|---|---|
| `UserServiceTest` | Registrering, BCrypt-hashing, hämtning, uppdatering, borttagning |
| `OrderServiceTest` | Orderläggning, statusuppdatering, avbokning, felhantering |
| `ProductServiceTest` | FakeStore API-integration, produkthämtning, JWT-säkerhet |
| `UserControllerTest` | Endpoint-säkerhet, 401 utan token |

`OrderServiceTest` använder `@MockitoBean` för att mocka RestTemplate — inga riktiga anrop görs till product-service under testning.

`ProductServiceTest` inkluderar säkerhetstester med MockMvc som verifierar att anrop utan JWT ger 401 Unauthorized och anrop med giltig JWT ger 200 OK.

Kör tester med:

```bash
mvn test
```

---

## CI/CD

Pipelinen körs automatiskt via GitHub Actions vid varje push till `main`.

```
Push till main
      │
      ├── build-and-test-backend (parallellt)
      │     ├── users-service:   mvn test → Docker build → DockerHub push
      │     └── product-service: mvn test → Docker build → DockerHub push
      │
      ├── build-frontend
      │     └── Docker build (med API-URL:er som build-args) → DockerHub push
      │
      └── deploy (körs bara om alla byggen lyckades)
            ├── SSH → EC2 users-service   → docker pull + restart
            ├── SSH → EC2 product-service → docker pull + restart
            └── SSH → EC2 frontend        → docker pull + restart
```

Om ett test failar stoppas pipelinen och deploy körs aldrig.

---

## Deployment

Appen körs i produktion på AWS med tre separata EC2-instanser och en delad RDS MySQL-databas.

| Tjänst | Infrastruktur | Port |
|---|---|---|
| frontend | EC2 + Nginx + Docker | 80, 443 |
| users-service | EC2 + Docker | 8080 |
| product-service | EC2 + Docker | 8081 |
| Databas | AWS RDS MySQL | 3306 |

HTTPS hanteras av Nginx med TLS-certifikat från Let's Encrypt (Certbot). Certifikaten monteras in i frontend-containern som en read-only volym från EC2-hosten.

Varje container startas med `--restart always` vilket innebär att den automatiskt startar om vid krasch eller EC2-omstart.

---

## Projektstruktur

```
cloudstore/
├── docker-compose.yml
├── .env.example
├── fakestore-users-service/
│   ├── Dockerfile
│   ├── src/main/java/.../
│   │   ├── controller/
│   │   │   ├── security/   (AuthController, JwksController)
│   │   │   ├── user/       (UserController)
│   │   │   └── order/      (OrderController)
│   │   ├── service/        (UserService, OrderService, TokenService)
│   │   ├── security/       (JwtSigner, MyUserDetailsService)
│   │   ├── model/          (User, Order, OrderItem)
│   │   ├── dto/
│   │   ├── exception/      (GlobalExceptionHandler)
│   │   └── config/         (SecurityConfig)
│   └── src/test/
├── fakestore-product-service/
│   ├── Dockerfile
│   ├── src/main/java/.../
│   │   ├── controller/     (ProductController)
│   │   ├── service/        (ProductService)
│   │   ├── model/          (Product)
│   │   └── config/         (SecurityConfig)
│   └── src/test/
└── fakestore-frontend/
    ├── Dockerfile
    ├── nginx.conf
    ├── src/
    │   ├── pages/          (ProductList, ProductDetail, MyOrders, MyProfile, ...)
    │   ├── components/     (Navbar, ProductCard, ProfileCard, ...)
    │   └── context/        (AuthContext, CartContext)
    └── package.json
```

---

## Vad jag lärt mig

Under utvecklingen av CloudStore har jag fördjupat mina kunskaper inom:

- Mikrotjänstarkitektur och kommunikation mellan tjänster
- Säker autentisering och auktorisering med JWT och RSA-nyckelpar
- Spring Security och skydd av REST-API
- Containerisering med Docker
- Automatiserad bygg-, test- och deployprocess med GitHub Actions
- Drift och konfigurering av applikationer i AWS
- Databasdesign och hantering av relationer i MySQL
- Utveckling av responsiva webbapplikationer med React
- Testning med JUnit, Mockito och H2-databaser
- Felsökning, loggning och produktsättning av distribuerade system

---

## Utvecklad av

Alexandra Olsson

Studerande Javautvecklare med molninriktning.


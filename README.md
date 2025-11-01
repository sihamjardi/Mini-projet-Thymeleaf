# 🍽️ Application de gestion des réservations d’un restaurant

## 1. Description du projet

###  Contexte fonctionnel  
Le projet s’inscrit dans le cadre de la **gestion des réservations de tables** dans un restaurant.  
Les restaurants doivent souvent gérer un grand nombre de réservations réparties sur plusieurs tables et zones.  
L’objectif est d’automatiser cette gestion et d’optimiser l’occupation des tables.

###  Objectif de l’application  
Permettre au personnel du restaurant de **gérer facilement les clients, les tables et les réservations**, tout en visualisant les statistiques d’occupation.

###  Public cible / cas d’usage  
- Gérant ou réceptionniste du restaurant  
- Serveur souhaitant vérifier les tables disponibles  

###  En une phrase  
> L’application permet de **créer, modifier et suivre les réservations de tables** d’un restaurant, tout en affichant les statistiques d’occupation.

---

## 2. Architecture technique

###  Stack technologique  
- **Backend** : Spring Boot 3.2.x, Spring Data JPA (Hibernate)  
- **Frontend** : Thymeleaf, HTML5, CSS3, Bootstrap 5, Chart.js  
- **Base de données** : MySQL  
- **Build** : Maven  

### 🗂️ Structure du code 

src/main/java/com/example/restaurant/
│
├── model/ → Classes JPA : TableResto, Client, Reservation
├── repository/ → Interfaces JpaRepository
├── service/ → Logique métier
├── controller/ → Contrôleurs Spring MVC
│
src/main/resources/
├── templates/ → Pages Thymeleaf
├── static/ → CSS, JS, images
└── application.properties

<img width="330" height="430" alt="Capture d&#39;écran 2025-11-01 023041" src="https://github.com/user-attachments/assets/a1784fe9-1f52-4dd3-ab78-7723f805cea5" />

<img width="326" height="423" alt="Capture d&#39;écran 2025-11-01 023053" src="https://github.com/user-attachments/assets/aaf63123-ffd3-4595-86b0-2896ae9d8652" />

<img width="334" height="417" alt="Capture d&#39;écran 2025-11-01 023020" src="https://github.com/user-attachments/assets/bbd6e004-f629-489b-9bbb-f2c08b5fb57e" />


### 🧭 Diagramme d’architecture  
Navigateur → Contrôleur Spring → Service → Repository → Base de données → Vue Thymeleaf

---

## 3. Fonctionnalités principales

- **CRUD complet** sur :
  - Clients
  - Tables
  - Réservations  
- **Recherche / filtrage** :
  - Par nom de client, date ou statut  
- **Tableau de bord / statistiques** :
  - Taux d’occupation matin / soir / global  
  - Nombre de réservations confirmées / annulées  
- **Gestion des statuts** :
  - `En attente`, `Confirmée`, `Annulée`

---

## 4. Modèle de données

### 4.1 Entités principales

#### 👤 Client
| Champ | Type |
|--------|------|
| id | Long |
| nom | String |
| telephone | String |

#### 🍽️ TableResto
| Champ | Type |
|--------|------|
| id | Long |
| numero | int |
| places | int |
| zone | String |

#### 📅 Reservation
| Champ | Type | Description |
|--------|------|-------------|
| pk | ReservationPK | Clé primaire composite (contient la dateHeure, la table et le client) |
| nbCouverts | int | Nombre de couverts réservés |
| statut | StatutReservation (Enum) | Statut de la réservation (Confirmée, Annulée, En attente...) |
| tablersto | TableResto | Table réservée |
| client | Client | Client ayant fait la réservation |
| dateHeure | LocalDateTime | (dérivé de `pk`) Date et heure de la réservation |

### 4.2 Relations  
- Un **client** peut avoir plusieurs **réservations** (`@OneToMany`)  
- Une **table** peut avoir plusieurs **réservations** (`@OneToMany`)  
- Chaque **réservation** est liée à **un client** et **une table** (`@ManyToOne`)  

###  Schéma ER  
Client (1) ───< Reservation >─── (1) TableResto


### ⚙️ Configuration base de données
```properties
spring.application.name=Restaurant
server.port=8080
# ===============================
# = DATA SOURCE
# ===============================
# Connection url for the database "boot"
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# Username and password
spring.datasource.username=root
spring.datasource.password=
# ===============================
# = JPA / HIBERNATE
# ===============================
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
# Hibernate ddl auto (create, create-drop, update)
spring.jpa.hibernate.ddl-auto=update
```

---

## 5. Lancer le projet

### 5.1 Prérequis

Java 17 ou plus
Maven 3.x

### 5.2 Installation

**Cloner le dépôt**

git clone https://github.com/nom-utilisateur/restaurant-reservation.git
cd restaurant-reservation

**Lancer l’application**

mvn spring-boot:run

### 5.3 Accès
Page d’accueil : [http://localhost:8080](http://localhost:8080/)

Tableau de bord / statistiques : [http://localhost:8080/statistiques](http://localhost:8080/reservations/stats?date=2025-11-01)

---

# 6. Jeu de données initial

**👥 Clients**

| Nom                  |  Téléphone  |
| -------------------  | ----------- |
| Siham Jardi          | 0569832997  |
| Majeda BEN-LAGHFIRI  | 03467643787 |
| Nissrine GUIJDOU     | 098897677   |
| salma sara           | 09732111    |
| Aya NACIRI           | 0346764378  |

**🍽️ Tables**

| Numéro | Places | Zone           |
| ------ | ------ | --------       |
| 4      | 1      | Terrasse       |
| 6      | 2      | VIP            |
| 3      | 2      | Intérieur      |
| 4      | 5      | VIP            |
| 5      | 2      | VIP            |
| 10     | 8      | VIP            |
| 1      | 3      | Salle          |

**📅 Réservations**

| Client      | Table | Nb couverts | Date/Heure       | Statut     |
| ----------- | ----- | ----------- | ---------------- | ---------- |
| Siham Jardi | 5     | 2           | 2025-11-01 16:09 | Confirmée  |
| Aya NACIRI  | 1     | 3           | 2025-11-01 11:20 | No-Show    |

---

# 7. Démonstration (Vidéo)

## 🎬 Démo Vidéo

[![Regarder la vidéo](https://img.youtube.com/vi/XQd5ssVA0rY/0.jpg)](https://youtu.be/XQd5ssVA0rY)

**Ce que la vidéo montre :**

-Navigation dans les pages

-Création d’un client

-Ajout d’une table

-Prise d’une réservation

-Changement de statut

-Affichage du tableau de bord avec statistiques

---

# 8. Auteurs / Encadrement
- Nom de l'étudiant: JARDI Siham
- Encadrant: Mr.LACHGAR Mohamed 
- Module: Techniques des Programmation Avancées - JAVA Avancé
- Etablissement: Ecole Normale supérieur - MARRAKECH (ENS-M)






# Task Manager Pro API – Spring Boot 3

Une API REST moderne et sécurisée pour la gestion de tâches personnelles, conçue avec les meilleures pratiques de développement Java.  
Ce projet a été réalisé dans le cadre d'un **cycle d'apprentissage intensif de 7 semaines**.

---

## Fonctionnalités

- **Sécurité robuste**  
  Authentification par JWT (JSON Web Token) et hachage des mots de passe avec BCrypt.

- **Gestion des tâches**  
  CRUD complet avec filtrage des tâches par utilisateur.

- **Rôles & autorisations**  
  Sécurité granulaire (Admin vs User) via des annotations Spring Security.

- **Performance**  
  Pagination et tri des résultats pour les listes volumineuses.

- **Qualité de code**  
  Validation des données (JSR-303), gestion globale des exceptions et logging avec SLF4J.

- **Documentation interactive**  
  Interface Swagger UI complète via OpenAPI 3.

---

## Stack Technique

- **Langage** : Java 21
- **Framework** : Spring Boot 3.5
- **Sécurité** : Spring Security + JWT
- **Données** : Spring Data JPA (H2 / PostgreSQL)
- **Mapping** : MapStruct (conversion DTO ↔ Entity)
- **Documentation** : OpenAPI 3 (Swagger)
- **Tests** : JUnit 5, Mockito, JaCoCo (couverture de code)

---

## Installation & Lancement

### Prérequis

- JDK 21+
- Maven 3.8+

### Étapes

1. **Cloner le dépôt**
   ```bash
   git clone https://github.com/ton-pseudo/task-manager-api.git

2. **Lancer l'application**

    ```bash
    mvn spring-boot:run

3. **Accéder à la documentation Swagger**

    ```bash
    http://localhost:8080/swagger-ui.html
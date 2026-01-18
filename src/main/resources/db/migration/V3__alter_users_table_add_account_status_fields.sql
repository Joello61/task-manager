-- Migration: Ajout des colonnes de gestion de compte utilisateur
-- Version: V3__alter_users_table_add_account_status_fields.sql

-- Modifier le type de date_creation de TIMESTAMP à TIMESTAMP WITH TIME ZONE (pour Instant)
ALTER TABLE users
    ALTER COLUMN date_creation TYPE TIMESTAMP WITH TIME ZONE;

-- Ajouter les nouvelles colonnes pour la gestion du compte
ALTER TABLE users
    ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT true,
    ADD COLUMN account_non_expired BOOLEAN NOT NULL DEFAULT true,
    ADD COLUMN account_non_locked BOOLEAN NOT NULL DEFAULT true,
    ADD COLUMN credentials_non_expired BOOLEAN NOT NULL DEFAULT true;

-- Modifier la colonne role pour utiliser VARCHAR(50) si ce n'était pas déjà fait
ALTER TABLE users
    ALTER COLUMN role TYPE VARCHAR(50);

-- Ajouter une contrainte CHECK sur le rôle
ALTER TABLE users
    ADD CONSTRAINT chk_users_role CHECK (role IN ('USER', 'ADMIN', 'MODERATOR'));

-- Mettre à jour les données existantes si nécessaire
UPDATE users
SET
    enabled = true,
    account_non_expired = true,
    account_non_locked = true,
    credentials_non_expired = true
WHERE enabled IS NULL;
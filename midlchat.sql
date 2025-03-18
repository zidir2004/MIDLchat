-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : mar. 18 mars 2025 à 12:15
-- Version du serveur : 8.0.31
-- Version de PHP : 8.0.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `midlchat`
--

-- --------------------------------------------------------

--
-- Structure de la table `amis`
--

DROP TABLE IF EXISTS `amis`;
CREATE TABLE IF NOT EXISTS `amis` (
  `id` int NOT NULL AUTO_INCREMENT,
  `utilisateur_id1` int NOT NULL,
  `utilisateur_id2` int NOT NULL,
  `statut` enum('en attente','confirmé','bloqué') DEFAULT 'en attente',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_amis` (`utilisateur_id1`,`utilisateur_id2`),
  KEY `utilisateur_id2` (`utilisateur_id2`)
) ENGINE=MyISAM AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `amis`
--

INSERT INTO `amis` (`id`, `utilisateur_id1`, `utilisateur_id2`, `statut`) VALUES
(1, 6, 4, 'confirmé'),
(2, 7, 3, 'confirmé'),
(3, 3, 5, 'confirmé'),
(4, 8, 3, 'confirmé'),
(5, 2, 6, 'confirmé'),
(6, 4, 9, 'confirmé'),
(7, 2, 8, 'confirmé'),
(8, 5, 1, 'confirmé'),
(9, 6, 9, 'confirmé'),
(10, 4, 1, 'confirmé'),
(11, 4, 5, 'confirmé'),
(12, 100, 2, 'confirmé'),
(13, 100, 3, 'confirmé'),
(14, 4, 100, 'confirmé'),
(17, 100, 8, 'confirmé'),
(20, 101, 10, 'confirmé'),
(18, 100, 1, 'confirmé'),
(19, 101, 100, 'confirmé'),
(21, 6, 10, 'confirmé');

-- --------------------------------------------------------

--
-- Structure de la table `groupes`
--

DROP TABLE IF EXISTS `groupes`;
CREATE TABLE IF NOT EXISTS `groupes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nom` (`nom`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `groupes`
--

INSERT INTO `groupes` (`id`, `nom`) VALUES
(1, 'test');

-- --------------------------------------------------------

--
-- Structure de la table `groupe_membres`
--

DROP TABLE IF EXISTS `groupe_membres`;
CREATE TABLE IF NOT EXISTS `groupe_membres` (
  `groupe_id` int DEFAULT NULL,
  `utilisateur_id` int DEFAULT NULL,
  KEY `groupe_id` (`groupe_id`),
  KEY `utilisateur_id` (`utilisateur_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `groupe_membres`
--

INSERT INTO `groupe_membres` (`groupe_id`, `utilisateur_id`) VALUES
(1, 3),
(1, 8),
(1, 101);

-- --------------------------------------------------------

--
-- Structure de la table `historique_messages`
--

DROP TABLE IF EXISTS `historique_messages`;
CREATE TABLE IF NOT EXISTS `historique_messages` (
  `id` int NOT NULL AUTO_INCREMENT,
  `utilisateur_id` int DEFAULT NULL,
  `message_id` int DEFAULT NULL,
  `date_vu` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `utilisateur_id` (`utilisateur_id`),
  KEY `message_id` (`message_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `messages`
--

DROP TABLE IF EXISTS `messages`;
CREATE TABLE IF NOT EXISTS `messages` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int DEFAULT NULL,
  `receiver_id` int DEFAULT NULL,
  `groupe_id` int DEFAULT NULL,
  `contenu` text NOT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `sender_id` (`sender_id`),
  KEY `receiver_id` (`receiver_id`),
  KEY `groupe_id` (`groupe_id`)
) ENGINE=MyISAM AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `messages`
--

INSERT INTO `messages` (`id`, `sender_id`, `receiver_id`, `groupe_id`, `contenu`, `timestamp`) VALUES
(1, 5, 4, NULL, 'Aucune idee, demande a Alice.', '2025-03-10 13:14:37'),
(2, 7, 2, NULL, 'Salut, comment ca va ?', '2025-03-10 13:14:37'),
(3, 4, 1, NULL, 'On se retrouve demain ?', '2025-03-10 13:14:37'),
(4, 1, 2, NULL, 'Aucune idee, demande a Alice.', '2025-03-10 13:14:37'),
(5, 7, 1, NULL, 'Tu peux m\'aider ?', '2025-03-10 13:14:37'),
(6, 1, 2, NULL, 'Aucune idee, demande a Alice.', '2025-03-10 13:14:37'),
(7, 6, 5, NULL, 'C\'est une super nouvelle !', '2025-03-10 13:14:37'),
(8, 3, 5, NULL, 'J\'ai hate de voir ca.', '2025-03-10 13:14:37'),
(9, 5, 2, NULL, 'Tu peux m\'aider ?', '2025-03-10 13:14:37'),
(10, 1, 10, NULL, 'Aucune idee, demande a Alice.', '2025-03-10 13:14:37'),
(11, 9, 5, NULL, 'On mange ensemble ce midi ?', '2025-03-10 13:14:37'),
(12, 8, 9, NULL, 'J\'ai une question sur l\'exercice.', '2025-03-10 13:14:37'),
(13, 3, 8, NULL, 'Je pense que c\'est une bonne idee.', '2025-03-10 13:14:37'),
(14, 7, 4, NULL, 'Merci pour ton aide !', '2025-03-10 13:14:37'),
(15, 2, 6, NULL, 'Tu as fini le projet ?', '2025-03-10 13:14:37'),
(16, 9, 3, NULL, 'On se retrouve demain ?', '2025-03-10 13:14:37'),
(17, 2, 1, NULL, 'J\'ai une question sur l\'exercice.', '2025-03-10 13:14:37'),
(18, 6, 9, NULL, 'Je pense que c\'est une bonne idee.', '2025-03-10 13:14:37'),
(19, 8, 2, NULL, 'Tu as fini le projet ?', '2025-03-10 13:14:37'),
(20, 8, 10, NULL, 'On se retrouve demain ?', '2025-03-10 13:14:37'),
(21, 1, 3, NULL, 'Aucune idee, demande a Alice.', '2025-03-10 13:14:37'),
(22, 10, 1, NULL, 'Je t\'envoie le fichier dans la soiree.', '2025-03-10 13:14:37'),
(23, 3, 6, NULL, 'J\'ai hate de voir ca.', '2025-03-10 13:14:37'),
(24, 5, 6, NULL, 'J\'ai une question sur l\'exercice.', '2025-03-10 13:14:37'),
(25, 6, 4, NULL, 'C\'est une super nouvelle !', '2025-03-10 13:14:37'),
(26, 7, 8, NULL, 'Je t\'attends en ligne.', '2025-03-10 13:14:37'),
(27, 1, 7, NULL, 'Je pense que c\'est une bonne idee.', '2025-03-10 13:14:37'),
(28, 5, 10, NULL, 'Tu es dispo ce soir ?', '2025-03-10 13:14:37'),
(29, 10, 9, NULL, 'Merci pour ton aide !', '2025-03-10 13:14:37'),
(30, 8, 9, NULL, 'Tu as fini le projet ?', '2025-03-10 13:14:37'),
(31, 9, 10, NULL, 'Aucune idee, demande a Alice.', '2025-03-10 13:14:37'),
(32, 7, 8, NULL, 'Aucune idee, demande a Alice.', '2025-03-10 13:14:37'),
(33, 6, 8, NULL, 'J\'ai une question sur l\'exercice.', '2025-03-10 13:14:37'),
(34, 4, 9, NULL, 'Merci pour ton aide !', '2025-03-10 13:14:37'),
(35, 6, 5, NULL, 'Je t\'attends en ligne.', '2025-03-10 13:14:37'),
(36, 5, 6, NULL, 'On mange ensemble ce midi ?', '2025-03-10 13:14:37'),
(37, 1, 9, NULL, 'Je pense que c\'est une bonne idee.', '2025-03-10 13:14:37'),
(38, 4, 10, NULL, 'C\'est une super nouvelle !', '2025-03-10 13:14:37'),
(39, 4, 3, NULL, 'On mange ensemble ce midi ?', '2025-03-10 13:14:37'),
(40, 1, 10, NULL, 'Peux-tu me rappeler l\'heure ?', '2025-03-10 13:14:37'),
(41, 8, 4, NULL, 'Aucune idee, demande a Alice.', '2025-03-10 13:14:37'),
(42, 1, 8, NULL, 'Peux-tu me rappeler l\'heure ?', '2025-03-10 13:14:37'),
(43, 6, 3, NULL, 'Tu es dispo ce soir ?', '2025-03-10 13:14:37'),
(44, 2, 3, NULL, 'Je pense que c\'est une bonne idee.', '2025-03-10 13:14:37'),
(45, 1, 5, NULL, 'J\'ai hate de voir ca.', '2025-03-10 13:14:37'),
(46, 5, 2, NULL, 'On se fait un appel demain ?', '2025-03-10 13:14:37'),
(47, 3, 4, NULL, 'Tu as fini le projet ?', '2025-03-10 13:14:37'),
(48, 9, 5, NULL, 'Tu as fini le projet ?', '2025-03-10 13:14:37'),
(49, 2, 9, NULL, 'Tu es dispo ce soir ?', '2025-03-10 13:14:37'),
(50, 6, 4, NULL, 'Je pense que c\'est une bonne idee.', '2025-03-10 13:14:37'),
(51, 100, 3, NULL, 'coucou', '2025-03-17 16:54:24'),
(52, 100, 3, NULL, 'test', '2025-03-17 16:54:27'),
(53, 100, 3, NULL, 'test', '2025-03-17 16:54:29'),
(54, 100, 2, NULL, 'bob', '2025-03-17 16:54:38'),
(55, 3, 100, NULL, 'coucou ca va ?', '2025-03-17 17:13:44'),
(56, 100, 4, NULL, 'david c\'est idir', '2025-03-17 20:13:52'),
(57, 100, 4, NULL, 'je test les messages', '2025-03-17 20:15:44'),
(58, 4, 100, NULL, 'test moi aussi', '2025-03-17 20:16:25'),
(59, 4, 100, NULL, 'test', '2025-03-17 20:16:45'),
(60, 1, 100, NULL, 'bonjour test', '2025-03-17 20:25:06'),
(61, 100, 1, NULL, 'ca va ?', '2025-03-17 20:25:12'),
(62, 100, 1, NULL, 'j\'essaie l\'actualisation des messages', '2025-03-17 20:25:26'),
(63, 1, 100, NULL, 'ca marche non?', '2025-03-17 20:25:36'),
(64, 100, 4, NULL, 'test', '2025-03-17 20:30:17'),
(65, 4, 100, NULL, 'test', '2025-03-17 20:30:31'),
(66, 101, 100, NULL, 'bonjour test', '2025-03-17 20:33:34'),
(67, 100, 101, NULL, 'ah bonjour root', '2025-03-17 20:33:42'),
(68, 100, 101, NULL, 'comment vas tu', '2025-03-17 20:33:48'),
(69, 100, 101, NULL, 'dit moi ca marche mtn ?', '2025-03-17 20:39:34'),
(70, 100, 101, NULL, 'mais bzr non?', '2025-03-17 20:39:53'),
(71, 101, 100, NULL, 'non ca ne marche toujours pas', '2025-03-17 20:39:56'),
(72, 101, 100, NULL, 'regarde je t\'ai envoyé un message mais il n\'est pas la', '2025-03-17 20:40:33'),
(73, 101, 100, NULL, 'test v2', '2025-03-17 20:42:45'),
(74, 100, 101, NULL, 'pourquoiiiii', '2025-03-17 20:42:56'),
(75, 101, 100, NULL, 'test123', '2025-03-18 01:35:43'),
(76, 100, 101, NULL, 'test test', '2025-03-18 01:35:49'),
(77, 101, 100, NULL, 'test456', '2025-03-18 01:35:58'),
(78, 101, 100, NULL, 'test789', '2025-03-18 01:37:48'),
(79, 101, 100, NULL, 'test 101112', '2025-03-18 01:37:55'),
(80, 100, 101, NULL, 'test test', '2025-03-18 01:37:59'),
(81, 100, 101, NULL, 'salut', '2025-03-18 01:43:51'),
(82, 101, 100, NULL, 'salut ca va?', '2025-03-18 01:43:58'),
(83, 100, 101, NULL, 'ok', '2025-03-18 02:12:44'),
(84, 101, 100, NULL, 'test', '2025-03-18 02:13:13'),
(85, 100, 101, NULL, 'louiza', '2025-03-18 12:07:07'),
(86, 101, 100, NULL, 'ca va ?', '2025-03-18 12:07:16'),
(87, 100, 101, NULL, 'c\'est bon c\'est réglé', '2025-03-18 12:07:34');

-- --------------------------------------------------------

--
-- Structure de la table `utilisateurs`
--

DROP TABLE IF EXISTS `utilisateurs`;
CREATE TABLE IF NOT EXISTS `utilisateurs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `date_creation` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `utilisateurs`
--

INSERT INTO `utilisateurs` (`id`, `nom`, `email`, `mot_de_passe`, `date_creation`) VALUES
(1, 'Alice', 'alice@example.com', 'password123', '2025-03-10 13:05:57'),
(2, 'Bob', 'bob@example.com', 'password123', '2025-03-10 13:05:57'),
(3, 'Charlie', 'charlie@example.com', 'password123', '2025-03-10 13:05:57'),
(4, 'David', 'david@example.com', 'password123', '2025-03-10 13:05:57'),
(5, 'Eve', 'eve@example.com', 'password123', '2025-03-10 13:05:57'),
(6, 'Frank', 'frank@example.com', 'password123', '2025-03-10 13:05:57'),
(7, 'Grace', 'grace@example.com', 'password123', '2025-03-10 13:05:57'),
(8, 'Helen', 'helen@example.com', 'password123', '2025-03-10 13:05:57'),
(9, 'Isaac', 'isaac@example.com', 'password123', '2025-03-10 13:05:57'),
(10, 'Julia', 'julia@example.com', 'password123', '2025-03-10 13:05:57'),
(11, 'Nomidir', 'Emailidirzidour', 'idir', '2025-03-10 14:27:08'),
(100, 'test', 'test', 'test', '2025-03-17 16:45:56'),
(101, 'root', 'root', 'root', '2025-03-17 20:32:55');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

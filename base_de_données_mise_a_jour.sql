-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : ven. 04 avr. 2025 à 14:49
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
-- Base de données : `chatmidl`
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
  `statut` enum('en attente','confirmé') DEFAULT 'en attente',
  PRIMARY KEY (`id`),
  KEY `utilisateur_id1` (`utilisateur_id1`),
  KEY `utilisateur_id2` (`utilisateur_id2`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `amis`
--

INSERT INTO `amis` (`id`, `utilisateur_id1`, `utilisateur_id2`, `statut`) VALUES
(5, 9, 7, 'confirmé'),
(4, 8, 7, 'confirmé');

-- --------------------------------------------------------

--
-- Structure de la table `chat_groups`
--

DROP TABLE IF EXISTS `chat_groups`;
CREATE TABLE IF NOT EXISTS `chat_groups` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `chat_groups`
--

INSERT INTO `chat_groups` (`id`, `name`) VALUES
(5, 'tech'),
(6, 'tp');

-- --------------------------------------------------------

--
-- Structure de la table `group_members`
--

DROP TABLE IF EXISTS `group_members`;
CREATE TABLE IF NOT EXISTS `group_members` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `group_id` int NOT NULL,
  `role` enum('member','admin') COLLATE utf8mb4_general_ci DEFAULT 'member',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `group_id` (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `group_members`
--

INSERT INTO `group_members` (`id`, `user_id`, `group_id`, `role`) VALUES
(11, 7, 5, 'member'),
(12, 8, 5, 'member'),
(13, 7, 6, 'member'),
(14, 9, 6, 'member'),
(15, 8, 6, 'member');

-- --------------------------------------------------------

--
-- Structure de la table `messages`
--

DROP TABLE IF EXISTS `messages`;
CREATE TABLE IF NOT EXISTS `messages` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `receiver_id` int DEFAULT NULL,
  `group_id` int DEFAULT NULL,
  `content` text COLLATE utf8mb4_general_ci NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `sender_id` (`sender_id`),
  KEY `receiver_id` (`receiver_id`),
  KEY `group_id` (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `messages`
--

INSERT INTO `messages` (`id`, `sender_id`, `receiver_id`, `group_id`, `content`, `timestamp`) VALUES
(129, 8, 7, NULL, 'ca va ?', '2025-03-27 04:38:23'),
(130, 7, 8, NULL, 'oui et toi?', '2025-03-27 04:43:47'),
(131, 7, 8, NULL, 'tres bien', '2025-03-27 04:48:28'),
(132, 7, 8, NULL, 'ok', '2025-03-27 04:48:55'),
(133, 8, 7, NULL, 'je test les notif', '2025-03-27 04:51:00'),
(134, 8, 7, NULL, 'je test', '2025-03-27 05:02:57'),
(135, 8, 7, NULL, 'les test marche', '2025-03-27 05:06:17'),
(136, 7, 8, NULL, 'oui je vois ca', '2025-03-27 05:06:40'),
(137, 7, 8, NULL, 'il y\'a pas de bug j\'aime bien', '2025-03-27 05:07:00'),
(138, 7, 8, NULL, 'oui oui j\'adore', '2025-03-27 05:07:14'),
(139, 8, 7, NULL, 'mais pourquoi je suis le seul a parler ?', '2025-03-27 05:07:35'),
(140, 7, 8, NULL, 't\'aime bien?', '2025-03-27 05:10:19'),
(141, 8, 7, NULL, 'oui je valide aucun bug', '2025-03-27 05:10:29'),
(142, 8, 7, NULL, 'les notif marchent bien aussi', '2025-03-27 05:10:41'),
(143, 8, 7, NULL, 'le son aussi', '2025-03-27 05:10:46'),
(144, 7, 8, NULL, 'oui tout est nickel', '2025-03-27 05:10:52'),
(145, 7, 8, NULL, 'j\'ia un bug de notif', '2025-03-27 05:11:03'),
(146, 7, 8, NULL, 'je sais pas pourquoi', '2025-03-27 05:11:08'),
(147, 8, 7, NULL, 'je crois que c\'est un probléme d\'id', '2025-03-27 05:11:18'),
(148, 7, NULL, 5, 'tu vois la discu?', '2025-03-27 05:11:51'),
(149, 7, NULL, 5, 'oui je la vois', '2025-03-27 05:11:59'),
(150, 7, NULL, 6, 'bonjouuur', '2025-03-27 12:53:15'),
(151, 9, NULL, 6, 'ca va ?', '2025-03-27 12:53:26'),
(152, 7, 9, NULL, 'coucou', '2025-03-27 12:53:39');

-- --------------------------------------------------------

--
-- Structure de la table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
CREATE TABLE IF NOT EXISTS `notifications` (
  `id` int NOT NULL AUTO_INCREMENT,
  `message_id` int NOT NULL,
  `user_id` int DEFAULT NULL,
  `sender_id` int NOT NULL,
  `seen` tinyint(1) DEFAULT '0',
  `group_message` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `message_id` (`message_id`),
  KEY `user_id` (`user_id`),
  KEY `sender_id` (`sender_id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `notifications`
--

INSERT INTO `notifications` (`id`, `message_id`, `user_id`, `sender_id`, `seen`, `group_message`) VALUES
(56, 134, 7, 8, 1, 0),
(57, 135, 7, 8, 1, 0),
(58, 135, 7, 8, 1, 0),
(59, 136, 8, 7, 1, 0),
(60, 137, 8, 7, 1, 0),
(61, 137, 8, 7, 1, 0),
(62, 138, 8, 7, 1, 0),
(63, 139, 7, 8, 1, 0),
(64, 139, 7, 8, 1, 0),
(65, 142, 7, 8, 1, 0),
(66, 143, 7, 8, 1, 0),
(67, 147, 7, 8, 1, 0),
(68, 148, 8, 7, 0, 1),
(69, 149, 8, 7, 0, 1),
(70, 150, 9, 7, 1, 1),
(71, 150, 8, 7, 0, 1),
(72, 151, 7, 9, 1, 1),
(73, 151, 8, 9, 0, 1);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `status` enum('online','offline') COLLATE utf8mb4_general_ci DEFAULT 'offline',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `status`) VALUES
(7, 'idir159', '5a6c3a2ace9d99745aeef8e7aa8ea40216ae6695ec589e8e9cf77c75f512b840', 'online'),
(8, 'test', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 'online'),
(9, 'sameh', '075cdf7bc275d7677052a704e034b68547867f25a669efadf8827ebbd387c4b0', 'online');

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `group_members`
--
ALTER TABLE `group_members`
  ADD CONSTRAINT `group_members_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `group_members_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `chat_groups` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `messages_ibfk_3` FOREIGN KEY (`group_id`) REFERENCES `chat_groups` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`message_id`) REFERENCES `messages` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `notifications_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `notifications_ibfk_3` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

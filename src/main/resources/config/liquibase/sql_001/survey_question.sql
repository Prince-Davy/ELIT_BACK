-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: db:3306
-- Generation Time: Feb 17, 2023 at 10:51 AM
-- Server version: 5.7.41
-- PHP Version: 8.1.15

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `outilprisedecision`
--

-- --------------------------------------------------------

--
-- Table structure for table `survey_question`
--

CREATE TABLE `survey_question` (
  `id` bigint(20) NOT NULL,
  `question` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf32;

--
-- Dumping data for table `survey_question`
--

INSERT INTO `survey_question` (`id`, `question`, `type`) VALUES
(1, 'Quelle est votre satisfaction globale concernant votre expérience avec ELIT ?', 'satisfaction'),
(2, 'A quel point diriez-vous que ELIT est facile à utiliser ?', 'difficuté'),
(3, 'Diriez-vous que le processus de décision vous a aidé à identifier les limitations du projet ?', 'accord'),
(4, 'Diriez-vous que le processus de décision vous a aidé à résoudre les difficultés identifiées ?', 'accord'),
(5, 'Diriez-vous que l’évaluation des paramètres et des critères ainsi que leur terminologie était claire ?', 'accord'),
(6, 'Diriez-vous que les rapports générés via le processus sont utiles pour le projet ?', 'accord'),
(7, 'Diriez-vous que la présence d’un facilitateur pendant l’exécution du processus a été utile ?', 'accord'),
(8, 'Diriez-vous que les outils utilisés pendant l’exécution du processus de décision ont été utiles ?', 'accord'),
(9, 'Selon-vous, qu’est-ce qui pourrait améliorer votre expérience avec l’application ELIT ?', 'amélioration'),
(10, 'Sur une échelle de 0 à 10, quelle est la probabilité que vous recommandiez ELIT à un collègue ?', 'amélioration');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `survey_question`
--
ALTER TABLE `survey_question`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `survey_question`
--
ALTER TABLE `survey_question`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

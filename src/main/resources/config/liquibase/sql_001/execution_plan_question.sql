-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: db:3306
-- Generation Time: Feb 17, 2023 at 10:50 AM
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
-- Table structure for table `execution_plan_question`
--

CREATE TABLE `execution_plan_question` (
  `execution_plan_question_id` bigint(20) NOT NULL,
  `question` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf32;

--
-- Dumping data for table `execution_plan_question`
--

INSERT INTO `execution_plan_question` (`execution_plan_question_id`, `question`, `title`) VALUES
(1, 'Ce que l’entreprise privilégie au niveau méthodologies', 'Stratégie d’organisation'),
(2, 'Spécificités du projet, contraintes techniques', 'Environnement de travail'),
(3, 'D’éventuels outils ou pratiques spécifiques à ce secteur', 'Secteur d’activité économique'),
(4, 'Le domaine peut avoir des aspects ou spécificités bénéficiant de l’apport de l’informatique.', 'Domaine technique d’application');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `execution_plan_question`
--
ALTER TABLE `execution_plan_question`
  ADD PRIMARY KEY (`execution_plan_question_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `execution_plan_question`
--
ALTER TABLE `execution_plan_question`
  MODIFY `execution_plan_question_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

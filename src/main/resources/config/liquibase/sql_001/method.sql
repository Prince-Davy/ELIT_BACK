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
-- Table structure for table `method`
--

CREATE TABLE `method` (
  `id` bigint(20) NOT NULL,
  `advantages` text,
  `description` text,
  `disadvantages` text,
  `name` varchar(255) DEFAULT NULL,
  `methodology_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf32;

--
-- Dumping data for table `method`
--

INSERT INTO `method` (`id`, `advantages`, `description`, `disadvantages`, `name`, `methodology_id`) VALUES
(1, 'advantages', 'description', 'disadvantages', 'Cascade', 1),
(2, 'advantages', 'description', 'disadvantages', 'Cycle en V', 1),
(3, 'advantages', 'description', 'disadvantages', 'Spirale', 1),
(4, 'advantages', 'description', 'disadvantages', 'Cycle en Y', 1),
(5, 'advantages', 'description', 'disadvantages', 'Kanban', 2),
(6, 'advantages', 'description', 'disadvantages', 'Scrum', 2),
(7, 'advantages', 'description', 'disadvantages', 'XP', 2),
(8, 'advantages', 'description', 'disadvantages', 'Scrumban', 2),
(9, 'advantages', 'description', 'disadvantages', 'Lean', 2),
(10, 'advantages', 'description', 'disadvantages', 'Dad', 2),
(11, 'advantages', 'description', 'disadvantages', 'Safe', 2),
(12, 'advantages', 'description', 'disadvantages', 'Less', 2),
(13, 'advantages', 'description', 'disadvantages', 'Nexux', 2),
(14, 'advantages', 'description', 'disadvantages', 'Scrum of Scrums', 2);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `method`
--
ALTER TABLE `method`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKckwhud91k2g60biwi44ssgcbv` (`methodology_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `method`
--
ALTER TABLE `method`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `method`
--
ALTER TABLE `method`
  ADD CONSTRAINT `FKckwhud91k2g60biwi44ssgcbv` FOREIGN KEY (`methodology_id`) REFERENCES `methodology` (`methodology_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

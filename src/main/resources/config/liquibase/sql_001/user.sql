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
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_id` bigint(20) NOT NULL,
  `is_admin` bit(1) DEFAULT NULL,
  `is_email_verified` bit(1) DEFAULT NULL,
  `is_user_active` bit(1) DEFAULT NULL,
  `user_avatar_path` varchar(255) DEFAULT NULL,
  `user_email` varchar(255) DEFAULT NULL,
  `user_first_name` varchar(255) DEFAULT NULL,
  `user_job` varchar(255) DEFAULT NULL,
  `user_last_name` varchar(255) DEFAULT NULL,
  `user_password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf32;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `is_admin`, `is_email_verified`, `is_user_active`, `user_avatar_path`, `user_email`, `user_first_name`, `user_job`, `user_last_name`, `user_password`, `username`) VALUES
(1, b'0', b'1', b'1', NULL, 'user@user.com', 'John', 'Développeur front', 'Doe', '$2a$10$sFs03QnEpBUuD5FGy6IdJuq8tRj2NFo9vGaMsC.7sUO/JzG68nkPi', 'user'),
(2, b'1', b'1', b'1', NULL, 'admin@elite.fr', 'Nancy', 'Chef de projet', 'Michel', '$2a$10$PbvKIbpBblKTToc8DpcEJ.7SsSxQJQ7o6SOkPDOIxrI1nD80oiDWW', 'admin'),
(3, b'0', b'1', b'1', NULL, 'contributer@elit.com', 'Charles', 'Développeur back', 'Baker', '$2a$10$SUihe/6dBoNS/fdMADmFoO0xLHjdLvCuKRNX/en5CnkxPB3L2.1Hi', 'contributer'),
(4, b'0', b'1', b'1', NULL, 'observer@elit.com', 'Rochelle', 'Développeur fullstack', 'Wright', '$2a$10$38alLfVRYSp/BxBk4hmgtuHwHt41lnbV2ozHFg11/sWwtYN0RKgW6', 'observer'),
(5, NULL, NULL, NULL, NULL, 'tkhrbi9a@gmail.com', 'soulaymane', NULL, 'gouijane', NULL, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `user_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

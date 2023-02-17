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
-- Table structure for table `methodology`
--

CREATE TABLE `methodology` (
  `methodology_id` bigint(20) NOT NULL,
  `advantages` text,
  `description` text,
  `disadvantages` text,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf32;

--
-- Dumping data for table `methodology`
--

INSERT INTO `methodology` (`methodology_id`, `advantages`, `description`, `disadvantages`, `name`) VALUES
(1, 'Il y a assez de bibliographie sur ces méthodes, avec plusieurs cas réel d\'application, pouvant servir de base pour savoir quand appliquer, quand éviter, comment résoudre un problèm rencontré, etc. La maitrise du budget et du délai s\'avère plus facile. ', 'Avant l’avènement des méthodes agiles, des solutions dites « classiques/traditionnelle » sont considérées comme les précurseurs et sont toujours utilisées aujourd’hui. Les plus connus sont cycle en V,cascade, spirale, cycle en Y.', 'Ils ne sont pas centrés à l\'ouverture aux changements, même si  quelques uns (e.g. spirale) peuvent prendre les changements sans que le projet soit en danger. Pour quelques uns entre eux, l\'effet tunnel peut prendre des dimensions importantes, si le besoin du client change après la description initiale du besoin.', 'Classique'),
(2, 'L’apport de valeur constant : en effectuant des livraisons fonctionnelles et opérationnelles régulières, les équipes agiles mettent le fruit de leur travail à disposition des utilisateurs et clients en fonction de la valeur apportée. Un Time to market optimisé : l’accent étant mis sur la réalisation des fonctionnalités apportant le plus de valeur au client, un MVP peut être mis à disposition plus rapidement.', 'Une approche itérative et incrémentale du développement de logiciels, qui est réalisée de manière collaborative par des équipes auto-organisées dans un cadre de gouvernance efficace. La cérémonie est «juste assez» pour produire des logiciels de haute qualité de manière rentable et rapide et qui répondent aux l\'évolution des besoins de ses parties prenantes.', 'L’ambigüité de la vision produit : Les développements étant réalisés sur des fonctionnalités, un projet peut manquer de vision globale. La réalisation de fonctionnalités, réalisées séparément les unes des autres, peut apporter de la confusion dans la réalisation d’un projet global. La gestion à long terme : La volatilité des priorités ajoute un risque sur l’homogénéisation du produit. Le développement sans priorisation, ni vision globale apporte un risque d’éparpillement non négligeable.', 'Agile'),
(3, 'La coexistence des deux précédents méthodes a pour but d\'atténuer leurs inconvénients tout en gardant leurs avantages. Alors, lister les avantages et inconvénients n\'est plus le cas. Il faut par contre faire attention aux points :  \n- bien mesurer le périmètre de ce qui sera prédictif ou agile, en s’assurant de la bonne couverture des exigences fonctionnelles et/ou techniques dans le délai et le budget consenti.\n- l’articulation des méthodes ou la dominante méthodologique dépendra des usages des équipes, de leur acculturation à l’agilité, ou du périmètre technique ou fonctionnel impacté. \n- dans le cas d ‘une sous-traitance d’un ou plusieurs lots auprès d’une ESN, il conviendra de bien apprécier l’incertitude liée aux exigences techniques, le taux de changement ou le risque minimum d\'exécution. \n- la disponibilité et la qualité des composants sous-traités sera également un risque à couvrir pour ne pas mettre en péril les itérations à venir et/ou le déploiement des fonctionnalités. ', 'Une association d’approches prédictives, itératives, incrémentales ou agiles. Cette méthode hybride ferait coexister une méthodologie de projet Agile, avec une méthodologie de projet prédictive, dans le cas où certaines parties du cycle de développement seraient confiées à des entités tierces.', '', 'Hybride');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `methodology`
--
ALTER TABLE `methodology`
  ADD PRIMARY KEY (`methodology_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `methodology`
--
ALTER TABLE `methodology`
  MODIFY `methodology_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

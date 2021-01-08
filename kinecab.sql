-- phpMyAdmin SQL Dump
-- version 4.9.5deb2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jan 08, 2021 at 10:36 PM
-- Server version: 8.0.22-0ubuntu0.20.04.3
-- PHP Version: 7.4.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `kinecab`
--

-- --------------------------------------------------------

--
-- Table structure for table `ADMIN`
--

CREATE TABLE `ADMIN` (
  `id` int NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `tel` varchar(10) NOT NULL,
  `adeli` varchar(50) NOT NULL,
  `rpps` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(535) NOT NULL,
  `license` int NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ADMIN`
--

INSERT INTO `ADMIN` (`id`, `nom`, `prenom`, `tel`, `adeli`, `rpps`, `email`, `password`, `license`) VALUES
(1, 'Orellana', 'Aurélie', '0688378492', '23456789', '10005320345', 'oreaure@hotmail.com', '4a6772414963d8d2a3a7f6f6dcda128122ef2192aa47cc93013da9ad3927ca99', 1),
(7, 'Dupont', 'Jean', '0199203943', '123456', '23456', 'penetueur@hotmail.com', '71c5d44dd7d27c1ac4b0baaf43ec02eb7dc18cf2c97bc6ac7dfd97202102f24e', 0),
(8, 'HIJAZI', 'Ali', '0612345678', '1234', '23456', 'hijazi.a00@gmail.com', 'b4122df58879a26381c7b297b530f605bef52b82c4f1c114d6a2e814f2ed6f9e', 1);

-- --------------------------------------------------------

--
-- Table structure for table `CAB`
--

CREATE TABLE `CAB` (
  `id` int NOT NULL,
  `email` varchar(50) NOT NULL,
  `url` varchar(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `pres` text NOT NULL,
  `phone` varchar(50) NOT NULL,
  `nomrue` varchar(60) NOT NULL,
  `codePostal` varchar(10) NOT NULL,
  `ville` varchar(50) NOT NULL,
  `paiment` varchar(60) NOT NULL,
  `cartevital` varchar(50) NOT NULL,
  `tarif` varchar(50) NOT NULL,
  `convention` varchar(50) NOT NULL,
  `adminID` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `CAB`
--

INSERT INTO `CAB` (`id`, `email`, `url`, `name`, `pres`, `phone`, `nomrue`, `codePostal`, `ville`, `paiment`, `cartevital`, `tarif`, `convention`, `adminID`) VALUES
(1, '', 'KOPSAULNAY93', 'ORELLANA Aurélie', 'Bienvenue au cabinet de kinésithérapie du boulevard de l’hôtel de ville à Aulnay-sous-Bois !\r\n\r\nRécemment ouvert, le cabinet est orienté en pathologies ostéo-articulaires, en particulier traumatologie et pathologies du sportif. \r\nNous sommes également spécialisés dans la femme et l’enfant par la pratique de la rééducation périnéale et abdominale et les soins de kinésithérapie pédiatrique. \r\n\r\nNous assurons des prises en charge individuelles de 30min et adaptées à chaque patient afin de vous accompagner et vous aider à retrouver vos capacités optimales. \r\n\r\nLes séances s’organisent entre thérapie manuelle et travail ACTIF dans notre salle de rééducation  toute équipée. Nous disposons également d’un matériel de pointe nous permettant d’effectuer des soins par Ondes de choc, pressothérapie, cryothérapie compressive, ventouses et bandes de taping.\r\n\r\nPour la kinésithérapie respiratoire du nourrisson (type bronchiolite), les soins sont assurés en urgence si possible dans la journée. Veuillez contacter directement le cabinet par téléphone. \r\n\r\nORELLANA Aurélie', '0980427714', '5 Boulevard de l\'Hôtel de ville ', '93600', 'Aulnay-Sous-Bois', 'Chèque, espèce', 'Tiers-payant partie CPAM seulement', '25 euros', '', '1'),
(2, '', 'test', 'Cab Test', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', '0933748340', '9 Rue du te', '75000', 'Paris', '', '', '25 euro', '', '8');

-- --------------------------------------------------------

--
-- Table structure for table `CAB_PERSON`
--

CREATE TABLE `CAB_PERSON` (
  `id` int NOT NULL,
  `idPerson` int NOT NULL,
  `idCab` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `CAB_PERSON`
--

INSERT INTO `CAB_PERSON` (`id`, `idPerson`, `idCab`) VALUES
(1, 27, 2),
(5, 31, 1),
(6, 32, 1),
(7, 33, 1),
(8, 34, 1),
(9, 36, 1),
(10, 37, 1),
(11, 38, 1),
(12, 39, 1),
(13, 40, 1),
(14, 41, 1),
(15, 42, 1),
(16, 43, 1),
(17, 44, 1),
(18, 45, 1),
(19, 46, 1),
(20, 47, 1),
(21, 48, 1),
(22, 49, 1),
(23, 50, 1),
(24, 51, 1),
(25, 52, 1),
(26, 53, 1),
(27, 54, 2),
(28, 55, 2),
(29, 56, 1),
(30, 57, 1),
(31, 58, 1),
(32, 59, 1),
(33, 60, 1),
(34, 61, 1),
(35, 62, 1),
(36, 63, 1),
(37, 64, 1),
(38, 65, 1),
(39, 66, 1),
(40, 67, 1),
(41, 68, 1),
(42, 69, 1);

-- --------------------------------------------------------

--
-- Table structure for table `COLAB`
--

CREATE TABLE `COLAB` (
  `id` int NOT NULL,
  `idCab` int NOT NULL,
  `idAdmin` int NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `COLAB`
--

INSERT INTO `COLAB` (`id`, `idCab`, `idAdmin`, `name`) VALUES
(1, 1, 1, 'Orellana Aurelie'),
(10, 2, 7, 'Jean Dupont'),
(11, 2, 8, 'Ali Hijazi');

-- --------------------------------------------------------

--
-- Table structure for table `Event`
--

CREATE TABLE `Event` (
  `id` int NOT NULL,
  `idAdmin` int NOT NULL,
  `status` varchar(255) NOT NULL,
  `idPatient` int NOT NULL,
  `idMotif` varchar(255) NOT NULL,
  `duration` int NOT NULL,
  `info` text NOT NULL,
  `pointe` tinyint(1) NOT NULL,
  `paye` tinyint(1) NOT NULL,
  `nomPrenom` varchar(60) NOT NULL,
  `start` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `listIdMotif` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Event`
--

INSERT INTO `Event` (`id`, `idAdmin`, `status`, `idPatient`, `idMotif`, `duration`, `info`, `pointe`, `paye`, `nomPrenom`, `listIdMotif`) VALUES
(4879, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4880, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4881, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4882, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4883, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4884, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4885, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4886, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4887, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4888, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4889, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4890, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4891, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4892, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4893, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4894, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4895, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4896, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4897, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4898, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4899, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4900, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4901, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4902, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4903, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4904, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4905, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4906, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4907, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4908, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4909, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4910, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4911, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6'),
(4912, 11, 'FREE', 0, '', 30, '', 0, 0, '', '5,6,5,6');

-- --------------------------------------------------------

--
-- Table structure for table `MOTIF_CAB`
--

CREATE TABLE `MOTIF_CAB` (
  `id` int NOT NULL,
  `idCab` int NOT NULL,
  `motif` varchar(250) NOT NULL,
  `resource` int NOT NULL,
  `color` varchar(255) NOT NULL,
  `duree` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `MOTIF_CAB`
--

INSERT INTO `MOTIF_CAB` (`id`, `idCab`, `motif`, `resource`, `color`, `duree`) VALUES
(2, 1, 'Suivi de consultation', 0, '#3687b2', 30),
(3, 1, 'Téléconsultation', 0, '#3687b2', 30),
(4, 1, 'Première consultation', 0, '#1CD5FF', 30),
(5, 2, 'Suivi de consultation', 0, '#3687b2', 30),
(6, 2, 'Teleconsulte', 0, '#1CD5FF', 60),
(7, 1, 'Rééduction périnéale', 0, '#FFCBF2', 30),
(8, 1, 'Pressotherapie', 0, '#DE33FF', 30);

-- --------------------------------------------------------

--
-- Table structure for table `MOTIF_COLAB`
--

CREATE TABLE `MOTIF_COLAB` (
  `id` int NOT NULL,
  `idColab` int NOT NULL,
  `idMotifCab` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `MOTIF_COLAB`
--

INSERT INTO `MOTIF_COLAB` (`id`, `idColab`, `idMotifCab`) VALUES
(1, 1, 2),
(2, 1, 3),
(5, 1, 4),
(6, 11, 5),
(7, 11, 6),
(8, 1, 7),
(9, 1, 8),
(11, 11, 5),
(12, 11, 6);

-- --------------------------------------------------------

--
-- Table structure for table `PERSON`
--

CREATE TABLE `PERSON` (
  `id` int NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `tel` varchar(10) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(535) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `PERSON`
--

INSERT INTO `PERSON` (`id`, `nom`, `prenom`, `tel`, `email`, `password`) VALUES
(27, 'Ferreira', 'Stéphane', '0633784027', 'takyto@laposte.net', '71c5d44dd7d27c1ac4b0baaf43ec02eb7dc18cf2c97bc6ac7dfd97202102f24e'),
(31, 'Ferreira', 'Rui', '0633784024', 'FerreiraRui0633784024', NULL),
(32, 'Claudio', 'ROumain', '0633872489', 'ClaudioROumain0633872489', NULL),
(33, 'Kolly', 'M', '0624726395', 'KollyM0624726395', NULL),
(34, 'Brasset', 'Barbara', '0670622365', 'BrassetBarbara0670622365', NULL),
(36, 'Danckof', 'Alexia', '0661782992', 'DanckofAlexia0661782992', NULL),
(37, 'Gautier', 'J', '0607794886', 'GautierJ0607794886', NULL),
(38, 'DINEL', 'Amandine', '0783633193', 'DINELAmandine0783633193', NULL),
(39, 'SALHI', 'NI', '0624726395', 'SALHINI0624726395', NULL),
(40, 'LEMOIGNE', 'BRUNO', '0624726395', 'LEMOIGNEBRUNO0624726395', NULL),
(41, 'abichou', 'mme', '0624726395', 'abichoumme0624726395', NULL),
(240, 'Hijazi', 'Ali', '0612345678', 'hijazi.a00@gmail.comljlfdsajlfdsajfdsj', 'b4122df58879a26381c7b297b530f605bef52b82c4f1c114d6a2e814f2ed6f9e');

-- --------------------------------------------------------

--
-- Table structure for table `PERSON_TEMP`
--

CREATE TABLE `PERSON_TEMP` (
  `id` int NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `tel` varchar(10) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(535) NOT NULL,
  `token` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `TOKEN`
--

CREATE TABLE `TOKEN` (
  `id` int NOT NULL,
  `token` varchar(250) NOT NULL,
  `admin` varchar(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `TOKEN`
--

INSERT INTO `TOKEN` (`id`, `token`, `admin`) VALUES
(1, 'ea0b0cdfe26cb8888da42202b5d2056066200ededeeb56719ddaf3cd79bca4a9', '1'),
(7, 'a261414e7acda9269aa513924eb057a927f6f83af0d236379d97ef05b81e97d4', '1'),
(8, '9861b2ae257304b20395e2a3dbdd31cf279b999620a358a64eaf270a21230c45', '1'),
(240, 'f3d42811e067ee8a1b40fc1fa2fbe2e2f514a5835fc80c399cfaa2ca2370e5b0', '1');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ADMIN`
--
ALTER TABLE `ADMIN`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `CAB`
--
ALTER TABLE `CAB`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `url` (`url`);

--
-- Indexes for table `CAB_PERSON`
--
ALTER TABLE `CAB_PERSON`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id` (`id`);

--
-- Indexes for table `COLAB`
--
ALTER TABLE `COLAB`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `Event`
--
ALTER TABLE `Event`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id` (`id`);

--
-- Indexes for table `MOTIF_CAB`
--
ALTER TABLE `MOTIF_CAB`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `MOTIF_COLAB`
--
ALTER TABLE `MOTIF_COLAB`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `PERSON`
--
ALTER TABLE `PERSON`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `PERSON_TEMP`
--
ALTER TABLE `PERSON_TEMP`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `TOKEN`
--
ALTER TABLE `TOKEN`
  ADD UNIQUE KEY `id` (`id`,`token`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ADMIN`
--
ALTER TABLE `ADMIN`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `CAB`
--
ALTER TABLE `CAB`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `CAB_PERSON`
--
ALTER TABLE `CAB_PERSON`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=198;

--
-- AUTO_INCREMENT for table `COLAB`
--
ALTER TABLE `COLAB`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `Event`
--
ALTER TABLE `Event`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4913;

--
-- AUTO_INCREMENT for table `MOTIF_CAB`
--
ALTER TABLE `MOTIF_CAB`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `MOTIF_COLAB`
--
ALTER TABLE `MOTIF_COLAB`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `PERSON`
--
ALTER TABLE `PERSON`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=241;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

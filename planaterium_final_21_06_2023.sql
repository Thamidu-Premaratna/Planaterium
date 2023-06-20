CREATE DATABASE  IF NOT EXISTS `planaterium_final` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `planaterium_final`;
-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: localhost    Database: planaterium_final
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `employee_id` int NOT NULL AUTO_INCREMENT,
  `fname` varchar(45) NOT NULL,
  `lname` varchar(45) NOT NULL,
  `dob` date NOT NULL,
  `mobile` varchar(10) NOT NULL,
  `address` text NOT NULL,
  `role_id` int NOT NULL,
  `status_id` int DEFAULT NULL,
  PRIMARY KEY (`employee_id`),
  KEY `fk_employee_role1_idx` (`role_id`),
  KEY `fk_employee_status1` (`status_id`),
  CONSTRAINT `fk_employee_role1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `fk_employee_status1` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,'Thamidu','Premaratna','2000-06-15','0703334059','134/2A, Home Rd',1,1),(2,'Jhon','Snow','2023-04-23','0717777772','123/4A',2,1),(3,'Kavindu','Sudotha','2000-03-07','0701234567','134/AA',3,1),(4,'Kasun','Kalhara','2001-06-10','0701234567','134/AA',2,1),(5,'Vidul','Pramith','2000-05-10','0703457899','123/4A, Normal Rd, Kadawatha',2,2);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login`
--

DROP TABLE IF EXISTS `login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(45) NOT NULL,
  `employee_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_login_employee1_idx` (`employee_id`),
  CONSTRAINT `fk_login_employee1` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login`
--

LOCK TABLES `login` WRITE;
/*!40000 ALTER TABLE `login` DISABLE KEYS */;
INSERT INTO `login` VALUES (1,'Admin','admin123',1),(2,'Jhon','jhon123',2),(3,'Test','test123',4),(4,'Kavindu','kavindu123',3);
/*!40000 ALTER TABLE `login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `given` double NOT NULL,
  `total_amount` double NOT NULL,
  `date` datetime NOT NULL,
  `payment_method_id` int NOT NULL,
  `reservation_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_payment_payment_method1_idx` (`payment_method_id`),
  KEY `fk_payment_reservation1_idx` (`reservation_id`),
  CONSTRAINT `fk_payment_payment_method1` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_method` (`id`),
  CONSTRAINT `fk_payment_reservation1` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,500,500,'2023-05-26 00:00:00',1,11),(2,350,300,'2023-05-26 00:00:00',1,12),(3,500,450,'2023-05-26 00:00:00',1,13),(4,300,300,'2023-05-26 00:00:00',1,14),(5,600,550,'2023-05-26 00:00:00',1,15),(6,500,500,'2023-05-26 00:00:00',1,16),(7,200,200,'2023-05-26 00:00:00',1,17),(8,350,300,'2023-05-29 00:00:00',1,18),(9,500,400,'2023-06-06 00:00:00',1,19),(10,300.5,300,'2023-06-20 00:00:00',1,20);
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `payment_history`
--

DROP TABLE IF EXISTS `payment_history`;
/*!50001 DROP VIEW IF EXISTS `payment_history`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `payment_history` AS SELECT 
 1 AS `sid`,
 1 AS `show_name`,
 1 AS `start_time`,
 1 AS `end_time`,
 1 AS `show_date`,
 1 AS `sempid`,
 1 AS `rid`,
 1 AS `r_date`,
 1 AS `r_time`,
 1 AS `rempid`,
 1 AS `rsid`,
 1 AS `pid`,
 1 AS `given`,
 1 AS `total_amount`,
 1 AS `date`,
 1 AS `payment_method_id`,
 1 AS `reservation_id`,
 1 AS `pmid`,
 1 AS `type`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `payment_method`
--

DROP TABLE IF EXISTS `payment_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_method` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_method`
--

LOCK TABLES `payment_method` WRITE;
/*!40000 ALTER TABLE `payment_method` DISABLE KEYS */;
INSERT INTO `payment_method` VALUES (1,'Cash'),(2,'Debit'),(3,'Credit'),(4,'Cheque');
/*!40000 ALTER TABLE `payment_method` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `id` int NOT NULL AUTO_INCREMENT,
  `r_date` date NOT NULL,
  `r_time` time NOT NULL,
  `employee_id` int NOT NULL,
  `show_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_reservation_employee1_idx` (`employee_id`),
  KEY `fk_reservation_show1_idx` (`show_id`),
  CONSTRAINT `fk_reservation_employee1` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`),
  CONSTRAINT `fk_reservation_show1` FOREIGN KEY (`show_id`) REFERENCES `show` (`show_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (9,'2023-05-26','00:06:19',1,4),(10,'2023-05-26','00:06:28',1,1),(11,'2023-05-26','00:14:48',1,4),(12,'2023-05-26','00:26:23',1,4),(13,'2023-05-26','00:31:23',1,4),(14,'2023-05-26','00:34:55',1,4),(15,'2023-05-26','00:58:08',1,4),(16,'2023-05-26','01:01:53',1,3),(17,'2023-05-26','01:06:13',1,1),(18,'2023-05-29','08:51:04',3,2),(19,'2023-06-06','21:40:06',3,5),(20,'2023-06-20','21:56:57',3,5);
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'Admin'),(2,'Receptionist'),(3,'Manager');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seat`
--

DROP TABLE IF EXISTS `seat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seat` (
  `seat_id` int NOT NULL AUTO_INCREMENT,
  `seat_no` varchar(10) NOT NULL,
  PRIMARY KEY (`seat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seat`
--

LOCK TABLES `seat` WRITE;
/*!40000 ALTER TABLE `seat` DISABLE KEYS */;
INSERT INTO `seat` VALUES (1,'A1'),(2,'A2'),(3,'A3'),(4,'A4'),(5,'A5'),(6,'A6'),(7,'A7'),(8,'A8'),(9,'A9'),(10,'A10'),(11,'A11'),(12,'A12'),(13,'A13'),(14,'A14');
/*!40000 ALTER TABLE `seat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `seat_map`
--

DROP TABLE IF EXISTS `seat_map`;
/*!50001 DROP VIEW IF EXISTS `seat_map`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `seat_map` AS SELECT 
 1 AS `rid`,
 1 AS `r_date`,
 1 AS `r_time`,
 1 AS `employee_id`,
 1 AS `show_id`,
 1 AS `tid`,
 1 AS `sid`,
 1 AS `seat_no`,
 1 AS `stid`,
 1 AS `price`,
 1 AS `name`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `seat_type`
--

DROP TABLE IF EXISTS `seat_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seat_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `price` double NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seat_type`
--

LOCK TABLES `seat_type` WRITE;
/*!40000 ALTER TABLE `seat_type` DISABLE KEYS */;
INSERT INTO `seat_type` VALUES (1,150,'Adult'),(2,100,'Child'),(3,50,'Student');
/*!40000 ALTER TABLE `seat_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `show`
--

DROP TABLE IF EXISTS `show`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `show` (
  `show_id` int NOT NULL AUTO_INCREMENT,
  `show_name` varchar(45) NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `show_date` date NOT NULL,
  `show_img` varchar(150) DEFAULT NULL,
  `employee_id` int NOT NULL,
  PRIMARY KEY (`show_id`),
  KEY `fk_show_employee1_idx` (`employee_id`),
  CONSTRAINT `fk_show_employee1` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `show`
--

LOCK TABLES `show` WRITE;
/*!40000 ALTER TABLE `show` DISABLE KEYS */;
INSERT INTO `show` VALUES (1,'Test Show 1','08:30:00','09:30:00','2023-06-20','/images/imports/5-scaled.jpg',2),(2,'Test Show 2','08:00:00','10:30:00','2023-06-25','/images/imports/2-scaled.jpg',1),(3,'Test Show 3 - On hold','08:00:00','11:30:00','2023-06-30','/images/imports/default.png',1),(4,'Test Show 4','08:00:00','11:37:13','2023-06-20','/images/imports/1-scaled.jpg',2),(5,'Show 4','08:00:00','10:30:00','2023-06-08','',3);
/*!40000 ALTER TABLE `show` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status`
--

DROP TABLE IF EXISTS `status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status`
--

LOCK TABLES `status` WRITE;
/*!40000 ALTER TABLE `status` DISABLE KEYS */;
INSERT INTO `status` VALUES (1,'Active'),(2,'Inactive');
/*!40000 ALTER TABLE `status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket`
--

DROP TABLE IF EXISTS `ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket` (
  `id` int NOT NULL AUTO_INCREMENT,
  `seat_id` int NOT NULL,
  `seat_type_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `fk_ticket_seat1_idx` (`seat_id`),
  KEY `fk_ticket_seat_type2_idx` (`seat_type_id`),
  CONSTRAINT `fk_ticket_seat1` FOREIGN KEY (`seat_id`) REFERENCES `seat` (`seat_id`),
  CONSTRAINT `fk_ticket_seat_type2` FOREIGN KEY (`seat_type_id`) REFERENCES `seat_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket`
--

LOCK TABLES `ticket` WRITE;
/*!40000 ALTER TABLE `ticket` DISABLE KEYS */;
INSERT INTO `ticket` VALUES (25,1,1),(26,1,2),(27,1,3),(28,2,1),(29,2,2),(30,2,3),(31,3,1),(32,3,2),(33,3,3),(34,4,1),(35,4,2),(36,4,3),(37,5,1),(38,5,2),(39,5,3),(40,6,1),(41,6,2),(42,6,3),(43,7,1),(44,7,2),(45,7,3),(46,8,1),(47,8,2),(48,8,3),(49,9,1),(50,9,2),(51,9,3),(52,10,1),(53,10,2),(54,10,3),(55,11,1),(56,11,2),(57,11,3),(58,12,1),(59,12,2),(60,12,3),(61,13,1),(62,13,2),(63,13,3),(64,14,1),(65,14,2),(66,14,3);
/*!40000 ALTER TABLE `ticket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket_has_reservation`
--

DROP TABLE IF EXISTS `ticket_has_reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket_has_reservation` (
  `ticket_id` int NOT NULL,
  `reservation_id` int NOT NULL,
  KEY `fk_ticket_has_reservation_reservation1_idx` (`reservation_id`),
  KEY `fk_ticket_has_reservation_ticket1_idx` (`ticket_id`),
  CONSTRAINT `fk_ticket_has_reservation_reservation1` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`),
  CONSTRAINT `fk_ticket_has_reservation_ticket1` FOREIGN KEY (`ticket_id`) REFERENCES `ticket` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket_has_reservation`
--

LOCK TABLES `ticket_has_reservation` WRITE;
/*!40000 ALTER TABLE `ticket_has_reservation` DISABLE KEYS */;
INSERT INTO `ticket_has_reservation` VALUES (25,14),(26,14),(28,9),(27,9),(34,15),(37,15),(55,15),(62,15),(31,16),(34,16),(44,16),(47,16),(30,17),(33,17),(36,17),(39,17),(31,18),(34,18),(34,19),(37,19),(53,19),(40,20),(43,20);
/*!40000 ALTER TABLE `ticket_has_reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `payment_history`
--

/*!50001 DROP VIEW IF EXISTS `payment_history`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `payment_history` AS select `s`.`show_id` AS `sid`,`s`.`show_name` AS `show_name`,`s`.`start_time` AS `start_time`,`s`.`end_time` AS `end_time`,`s`.`show_date` AS `show_date`,`s`.`employee_id` AS `sempid`,`r`.`id` AS `rid`,`r`.`r_date` AS `r_date`,`r`.`r_time` AS `r_time`,`r`.`employee_id` AS `rempid`,`r`.`show_id` AS `rsid`,`p`.`id` AS `pid`,`p`.`given` AS `given`,`p`.`total_amount` AS `total_amount`,`p`.`date` AS `date`,`p`.`payment_method_id` AS `payment_method_id`,`p`.`reservation_id` AS `reservation_id`,`pm`.`id` AS `pmid`,`pm`.`type` AS `type` from (((`show` `s` join `reservation` `r` on((`s`.`show_id` = `r`.`show_id`))) join `payment` `p` on((`r`.`id` = `p`.`reservation_id`))) join `payment_method` `pm` on((`p`.`payment_method_id` = `pm`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `seat_map`
--

/*!50001 DROP VIEW IF EXISTS `seat_map`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `seat_map` AS select `r`.`id` AS `rid`,`r`.`r_date` AS `r_date`,`r`.`r_time` AS `r_time`,`r`.`employee_id` AS `employee_id`,`r`.`show_id` AS `show_id`,`t`.`id` AS `tid`,`s`.`seat_id` AS `sid`,`s`.`seat_no` AS `seat_no`,`st`.`id` AS `stid`,`st`.`price` AS `price`,`st`.`name` AS `name` from ((((`reservation` `r` join `ticket_has_reservation` `thr` on((`r`.`id` = `thr`.`reservation_id`))) join `ticket` `t` on((`thr`.`ticket_id` = `t`.`id`))) join `seat` `s` on((`t`.`seat_id` = `s`.`seat_id`))) join `seat_type` `st` on((`t`.`seat_type_id` = `st`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-06-21  0:11:55

-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 03, 2025 at 02:53 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `inventory_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `name` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`) VALUES
(5, 'Cosmetics'),
(8, 'Electricals'),
(1, 'Food'),
(9, 'Stationary'),
(4, 'Toys'),
(3, 'Utensils'),
(7, 'Utilities'),
(12, 'xyz');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL,
  `name` varchar(120) NOT NULL,
  `category_id` int(11) DEFAULT NULL,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `unit_cost` decimal(10,2) DEFAULT 0.00,
  `expiry_date` date DEFAULT NULL,
  `added_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `name`, `category_id`, `quantity`, `unit_cost`, `expiry_date`, `added_by`, `created_at`) VALUES
(1, 'Rice Bag', 1, 15, 55.50, '2025-12-01', 2, '2025-10-28 17:13:38'),
(2, 'Toothpaste', 5, 20, 45.00, '2026-01-15', 2, '2025-10-28 17:13:38'),
(3, 'Frying Pan', 3, 20, 250.00, '2030-01-01', 2, '2025-10-28 17:13:38'),
(4, 'Yogurt Cup', 1, 20, 12.00, '2025-11-02', 2, '2025-10-28 17:13:38'),
(12, 'Lays', 1, 50, 10.00, '2025-11-11', 1, '2025-10-29 07:27:04'),
(14, 'Kurkure', 1, 70, 20.00, '2026-01-06', 3, '2025-10-29 07:28:19'),
(15, 'Barbie doll', 4, 100, 99.00, NULL, 3, '2025-10-29 07:29:15'),
(16, 'Washing powder', 7, 5, 350.00, '2026-07-06', 3, '2025-10-29 07:31:20'),
(17, 'Face cream', 5, 0, 89.00, '2025-11-01', 3, '2025-10-29 07:33:52'),
(18, 'Philips bulb', 8, 100, 99.00, '2027-10-20', 5, '2025-10-29 07:40:21'),
(21, 'iihdd', 12, 19, 50.00, '2025-10-29', 1, '2025-10-29 09:05:13');

-- --------------------------------------------------------

--
-- Stand-in structure for view `product_summary`
-- (See below for the actual view)
--
CREATE TABLE `product_summary` (
`product_id` int(11)
,`product_name` varchar(120)
,`category` varchar(80)
,`added_by` varchar(120)
,`quantity` int(11)
,`unit_cost` decimal(10,2)
,`expiry_date` date
,`stock_status` varchar(16)
,`expiry_status` varchar(13)
);

-- --------------------------------------------------------

--
-- Table structure for table `stock_movements`
--

CREATE TABLE `stock_movements` (
  `id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `direction` enum('IN','OUT') NOT NULL,
  `quantity` int(11) NOT NULL,
  `unit_cost` decimal(10,2) DEFAULT 0.00,
  `reason` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `selling_price` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `stock_movements`
--

INSERT INTO `stock_movements` (`id`, `product_id`, `user_id`, `direction`, `quantity`, `unit_cost`, `reason`, `created_at`, `selling_price`) VALUES
(1, 4, 1, 'IN', 50, 10.00, 'in', '2025-10-28 19:31:02', NULL),
(2, 4, 1, 'OUT', 30, 0.00, 'usage', '2025-10-28 19:31:31', NULL),
(3, 3, 1, 'IN', 10, 220.00, 'in', '2025-10-28 20:06:50', NULL),
(4, 1, 1, 'OUT', 5, 0.00, 'usage', '2025-10-28 20:07:21', NULL),
(5, 1, 1, 'OUT', 20, NULL, 'usage/sale', '2025-10-28 20:52:32', 0),
(6, 1, 1, 'IN', 50, 0.00, 'in', '2025-10-28 20:53:30', NULL),
(7, 1, 1, 'OUT', 40, NULL, 'usage/sale', '2025-10-28 21:12:13', 0),
(8, 1, 1, 'IN', 50, 50.00, 'in', '2025-10-28 21:15:56', NULL),
(9, 1, 1, 'OUT', 45, NULL, 'usage/sale', '2025-10-28 21:16:36', 60),
(10, 4, 3, 'IN', 50, 13.00, 'in', '2025-10-28 21:43:53', NULL),
(11, 4, 3, 'OUT', 30, NULL, 'sale', '2025-10-28 21:46:04', 15),
(12, 14, 3, 'IN', 50, 20.00, 'in', '2025-10-29 07:34:32', NULL),
(13, 14, 3, 'OUT', 20, NULL, 'sale/usage', '2025-10-29 07:35:26', 20),
(14, 17, 1, 'OUT', 2, NULL, 'usage/sale', '2025-10-29 09:00:31', 89),
(15, 21, 1, 'OUT', 2, NULL, 'usage/sale', '2025-10-29 09:05:51', 50);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `fullname` varchar(120) NOT NULL,
  `username` varchar(60) NOT NULL,
  `password` varchar(60) NOT NULL,
  `role` enum('ADMIN','EMPLOYEE') NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `fullname`, `username`, `password`, `role`, `created_at`) VALUES
(1, 'System Admin', 'admin', 'admin123', 'ADMIN', '2025-10-28 17:13:38'),
(2, 'Employee One', 'emp1', 'emp123', 'EMPLOYEE', '2025-10-28 17:13:38'),
(3, 'Hans Tom Sojan', 'hans', '123', 'EMPLOYEE', '2025-10-28 21:42:54'),
(5, 'Stivance', 'stiv', '123', 'EMPLOYEE', '2025-10-29 07:37:54'),
(6, 'Anand', 'anand', '123', 'EMPLOYEE', '2025-10-29 07:38:12');

-- --------------------------------------------------------

--
-- Structure for view `product_summary`
--
DROP TABLE IF EXISTS `product_summary`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `product_summary`  AS SELECT `p`.`id` AS `product_id`, `p`.`name` AS `product_name`, `c`.`name` AS `category`, `u`.`fullname` AS `added_by`, `p`.`quantity` AS `quantity`, `p`.`unit_cost` AS `unit_cost`, `p`.`expiry_date` AS `expiry_date`, CASE WHEN `p`.`quantity` = 0 THEN 'Out of Stock' WHEN `p`.`quantity` < 5 THEN 'Low Stock' WHEN `p`.`quantity` < 20 THEN 'Medium Stock' ELSE 'Sufficient Stock' END AS `stock_status`, CASE WHEN `p`.`expiry_date` is null THEN 'No Expiry' WHEN `p`.`expiry_date` < curdate() THEN 'Expired' WHEN `p`.`expiry_date` < curdate() + interval 7 day THEN 'Expiring Soon' ELSE 'Good' END AS `expiry_status` FROM ((`products` `p` left join `categories` `c` on(`p`.`category_id` = `c`.`id`)) left join `users` `u` on(`p`.`added_by` = `u`.`id`)) ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_product_user` (`added_by`),
  ADD KEY `idx_category` (`category_id`),
  ADD KEY `idx_expiry` (`expiry_date`),
  ADD KEY `idx_qty` (`quantity`);

--
-- Indexes for table `stock_movements`
--
ALTER TABLE `stock_movements`
  ADD PRIMARY KEY (`id`),
  ADD KEY `product_id` (`product_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT for table `stock_movements`
--
ALTER TABLE `stock_movements`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_product_user` FOREIGN KEY (`added_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `stock_movements`
--
ALTER TABLE `stock_movements`
  ADD CONSTRAINT `stock_movements_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `stock_movements_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

DELIMITER $$
--
-- Events
--
CREATE DEFINER=`root`@`localhost` EVENT `ev_delete_expired_products` ON SCHEDULE EVERY 1 DAY STARTS '2025-10-29 20:13:38' ON COMPLETION NOT PRESERVE ENABLE DO DELETE FROM products WHERE expiry_date IS NOT NULL AND expiry_date < CURDATE()$$

DELIMITER ;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

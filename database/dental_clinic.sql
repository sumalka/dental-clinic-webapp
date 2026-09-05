-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 05, 2026 at 06:12 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dental_clinic`
--

-- --------------------------------------------------------

--
-- Table structure for table `appointments`
--

CREATE TABLE `appointments` (
  `appointment_id` int(11) NOT NULL,
  `appointment_number` varchar(20) NOT NULL,
  `patient_id` int(11) NOT NULL,
  `dentist_id` int(11) NOT NULL,
  `treatment_id` int(11) DEFAULT NULL,
  `appointment_date` date NOT NULL,
  `appointment_time` time NOT NULL,
  `duration_minutes` int(11) DEFAULT 30,
  `status` varchar(20) DEFAULT 'SCHEDULED',
  `notes` text DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `appointments`
--

INSERT INTO `appointments` (`appointment_id`, `appointment_number`, `patient_id`, `dentist_id`, `treatment_id`, `appointment_date`, `appointment_time`, `duration_minutes`, `status`, `notes`, `created_by`, `created_at`, `updated_at`) VALUES
(5, 'APP-20260103-005', 5, 2, 6, '2026-01-03', '11:00:00', 120, 'CANCELLED', 'Braces installation', NULL, '2026-08-20 23:29:27', '2026-09-01 15:16:40'),
(13, 'APP-20260904-002', 15, 3, 2, '2026-09-01', '01:14:00', 30, 'COMPLETED', '', 1, '2026-09-04 05:39:08', '2026-09-04 08:17:20'),
(14, 'APP-20260904-003', 14, 3, 2, '2026-09-24', '14:01:00', 30, 'CANCELLED', '', 1, '2026-09-04 05:42:08', '2026-09-05 02:36:00'),
(30, 'APP-20260904-004', 4, 3, 5, '2026-09-24', '18:00:00', 30, 'COMPLETED', '', 1, '2026-09-04 06:23:15', '2026-09-04 08:21:28'),
(31, 'APP-20260904-001', 2, 1, 4, '2026-09-12', '17:00:00', 60, 'COMPLETED', '', 1, '2026-09-04 09:08:29', '2026-09-04 09:09:59');

-- --------------------------------------------------------

--
-- Table structure for table `bills`
--

CREATE TABLE `bills` (
  `bill_id` int(11) NOT NULL,
  `appointment_id` int(11) NOT NULL,
  `patient_id` int(11) NOT NULL,
  `patient_name` varchar(255) DEFAULT NULL,
  `patient_email` varchar(255) DEFAULT NULL,
  `patient_phone` varchar(50) DEFAULT NULL,
  `dentist_name` varchar(255) DEFAULT NULL,
  `treatment_name` varchar(255) DEFAULT NULL,
  `room_number` varchar(50) DEFAULT NULL,
  `treatment_cost` decimal(10,2) DEFAULT 0.00,
  `consultation_fee` decimal(10,2) DEFAULT 0.00,
  `subtotal` decimal(10,2) DEFAULT 0.00,
  `tax_rate` decimal(5,2) DEFAULT 10.00,
  `tax_amount` decimal(10,2) DEFAULT 0.00,
  `discount_amount` decimal(10,2) DEFAULT 0.00,
  `discount_reason` varchar(255) DEFAULT NULL,
  `total_amount` decimal(10,2) DEFAULT 0.00,
  `payment_method` varchar(50) DEFAULT NULL,
  `payment_status` varchar(20) DEFAULT 'PENDING',
  `issued_by` int(11) DEFAULT NULL,
  `invoice_number` varchar(50) DEFAULT NULL,
  `bill_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bills`
--

INSERT INTO `bills` (`bill_id`, `appointment_id`, `patient_id`, `patient_name`, `patient_email`, `patient_phone`, `dentist_name`, `treatment_name`, `room_number`, `treatment_cost`, `consultation_fee`, `subtotal`, `tax_rate`, `tax_amount`, `discount_amount`, `discount_reason`, `total_amount`, `payment_method`, `payment_status`, `issued_by`, `invoice_number`, `bill_date`) VALUES
(1, 10, 7, 'KAV jp', 's@gmail.com', '+94 11987654321', 'Dr. KAaaaaxdaserfg u', 'Cavity Filling', 'N/A', 3500.00, 0.13, 3500.13, 10.00, 350.01, 0.00, NULL, 3850.14, 'Credit Card', 'PAID', 1, 'INV-20260902-8837', '2026-09-02 05:36:03'),
(2, 13, 15, 'Sithara Nethmini Perera', 'sitharaperera@gmail.com', '+94 773875643', 'Dr. Priya Kumar', 'Cavity Filling', 'N/A', 3500.00, 2000.00, 5500.00, 10.00, 550.00, 0.00, NULL, 6050.00, 'Credit Card', 'PAID', 1, 'INV-20260904-7913', '2026-09-04 08:17:20'),
(3, 30, 4, 'Emily Fernando', 'emily.fernando@email.com', '+94 77 444 5555', 'Dr. Priya Kumar', 'Crown Placement', 'N/A', 12000.00, 2000.00, 14000.00, 10.00, 1400.00, 0.00, NULL, 15400.00, 'Credit Card', 'PAID', 1, 'INV-20260904-5903', '2026-09-04 08:21:28'),
(4, 31, 2, 'Jane Smith', 'jane.smith@email.com', '+94 77 222 3333', 'Dr. Sarah Johnson', 'Tooth Extraction', 'N/A', 3000.00, 1000.00, 4000.00, 10.00, 400.00, 0.00, NULL, 4400.00, 'Credit Card', 'PAID', 1, 'INV-20260904-4018', '2026-09-04 09:09:59');

-- --------------------------------------------------------

--
-- Table structure for table `dentists`
--

CREATE TABLE `dentists` (
  `dentist_id` int(11) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `specialization` varchar(100) NOT NULL,
  `license_number` varchar(50) NOT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `consultation_fee` decimal(10,2) DEFAULT 0.00,
  `working_days` varchar(100) DEFAULT NULL,
  `working_hours` varchar(100) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `dentists`
--

INSERT INTO `dentists` (`dentist_id`, `first_name`, `last_name`, `specialization`, `license_number`, `contact_number`, `email`, `consultation_fee`, `working_days`, `working_hours`, `is_active`, `created_at`) VALUES
(1, 'Sarah', 'Johnson', 'General Dentistry', 'DENT-001', '+94 77 123 4567', 'sarah.johnson@dentalclinic.com', 1000.00, NULL, NULL, 1, '2026-08-20 23:29:27'),
(2, 'Michael', 'Chen', 'Orthodontics', 'DENT-002', '+94 77 234 5678', 'michael.chen@dentalclinic.com', 1500.00, 'Mon-Fri', '', 0, '2026-08-20 23:29:27'),
(3, 'Priya', 'Kumar', 'Oral Surgery', 'DENT-003', '+94 77 345 6789', 'priya.kumar@dentalclinic.com', 2000.00, NULL, NULL, 1, '2026-08-20 23:29:27'),
(4, 'Robert', 'Silva', 'Periodontics', 'DENT-004', '+94 77 456 7890', 'robert.silva@dentalclinic.com', 1200.00, NULL, NULL, 1, '2026-08-20 23:29:27');

-- --------------------------------------------------------

--
-- Table structure for table `patients`
--

CREATE TABLE `patients` (
  `patient_id` int(11) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `date_of_birth` date NOT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `contact_number` varchar(20) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` varchar(255) NOT NULL,
  `city` varchar(50) DEFAULT NULL,
  `state` varchar(50) DEFAULT NULL,
  `postal_code` varchar(20) DEFAULT NULL,
  `nationality` varchar(50) DEFAULT NULL,
  `id_type` varchar(20) DEFAULT 'NIC',
  `id_number` varchar(50) DEFAULT NULL,
  `emergency_contact_name` varchar(100) DEFAULT NULL,
  `emergency_contact_number` varchar(20) DEFAULT NULL,
  `medical_notes` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `status` varchar(20) DEFAULT 'Active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `patients`
--

INSERT INTO `patients` (`patient_id`, `first_name`, `last_name`, `date_of_birth`, `gender`, `contact_number`, `email`, `address`, `city`, `state`, `postal_code`, `nationality`, `id_type`, `id_number`, `emergency_contact_name`, `emergency_contact_number`, `medical_notes`, `created_at`, `updated_at`, `status`) VALUES
(1, 'John', 'Doe', '1985-06-15', 'Male', '+94 77 111 2222', 'john.doe@email.com', '123 Main Street', 'Colombo', NULL, NULL, 'Sri Lankan', 'NIC', '851234567V', 'Mary Doe', '+94 77 111 3333', NULL, '2026-08-20 23:29:27', '2026-08-20 23:29:27', 'Active'),
(2, 'Jane', 'Smith', '1990-09-22', 'Female', '+94 77 222 3333', 'jane.smith@email.com', '456 Park Avenue', 'Kandy', NULL, NULL, 'British', 'Passport', 'GB1234567', 'Tom Smith', '+94 77 222 4444', NULL, '2026-08-20 23:29:27', '2026-08-20 23:29:27', 'Active'),
(3, 'David', 'Perera', '1978-03-10', 'Male', '+94 77 333 4444', 'david.perera@email.com', '789 Beach Road', 'Galle', NULL, NULL, 'Sri Lankan', 'NIC', '781234567V', 'Lisa Perera', '+94 77 333 5555', NULL, '2026-08-20 23:29:27', '2026-08-20 23:29:27', 'Active'),
(4, 'Emily', 'Fernando', '1995-12-01', 'Female', '+94 77 444 5555', 'emily.fernando@email.com', '321 Lake View', 'Negombo', NULL, NULL, 'Sri Lankan', 'NIC', '951234567V', 'Mark Fernando', '+94 77 444 6666', NULL, '2026-08-20 23:29:27', '2026-08-20 23:29:27', 'Active'),
(5, 'Michael', 'Wijesinghe', '1980-07-19', 'Male', '+94 77 555 6666', 'michael.w@email.com', '555 Temple Road', 'Colombo', NULL, NULL, 'Sri Lankan', 'NIC', '801234567V', 'Susan Wijesinghe', '+94 77 555 7777', NULL, '2026-08-20 23:29:27', '2026-08-20 23:29:27', 'Active'),
(14, 'Vishara Devindhi', 'Silva', '2008-09-03', 'Female', '+94 703897612', 'sitharaperera@gmail.com', 'No. 18/2, Lake Crescent, Colombo 08', 'Colombo', 'Western Province', '00800', 'Sri Lankan', 'NIC', '200845234517', 'Maheshi Wickramasinghe', '+94 742890123', '', '2026-09-04 04:43:32', '2026-09-04 04:43:32', 'Active'),
(15, 'Sithara Nethmini', 'Perera', '2008-09-12', 'Female', '+94 773875643', 'sitharaperera@gmail.com', 'No. 76, Peradeniya Road, Kandy', 'Kandy', 'Central Province', '20000', 'Sri Lankan', 'NIC', '2001376523412', 'Neha Wijethunga', '+94 773875643', '', '2026-09-04 05:11:59', '2026-09-05 02:41:48', 'Active');

-- --------------------------------------------------------

--
-- Table structure for table `recovery_tokens`
--

CREATE TABLE `recovery_tokens` (
  `token_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `token` varchar(64) NOT NULL,
  `expiry_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `used` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

CREATE TABLE `staff` (
  `staff_id` int(11) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` varchar(50) DEFAULT 'receptionist',
  `status` varchar(20) DEFAULT 'Active',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `staff`
--

INSERT INTO `staff` (`staff_id`, `first_name`, `last_name`, `username`, `password`, `email`, `phone`, `role`, `status`, `created_at`) VALUES
(1, 'Admin', 'User', 'admin', 'admin123', 'admin@dentalclinic.com', '+94 77 123 4567', 'admin', 'Active', '2026-09-02 07:49:27'),
(2, 'Sarah', 'Johnson', 'sarah', 'password123', 'sarah@dentalclinic.com', '+94 77 222 3333', 'dentist', 'Active', '2026-09-02 07:49:27'),
(3, 'Jane', 'Reception', 'jane', 'password123', 'jane@dentalclinic.com', '+94 77 444 5555', 'receptionist', 'Inactive', '2026-09-02 07:49:27'),
(4, 'KAV', 'Costa', 'kav', 'kav123', 'erer@gmail.com', '+5511987654321', 'receptionist', 'Active', '2026-09-02 08:16:22'),
(5, 'Elena', 'Martinez', 'elena', '123^^En2', 'elenamartinez22@gmail.com', '+94774567673', 'receptionist', 'Inactive', '2026-09-04 02:26:46'),
(6, 'Sneha', 'Perera', 'sneha_12', '27*Eg^', 'snehaperera12@gmail.com', '+94774567671', 'manager', 'Inactive', '2026-09-04 09:31:06'),
(7, 'Christina', 'Perera', 'dentist01', 'mV2nXD3go7', 'christinapereraa@gmail.com', '+94765641534', 'dentist', 'Active', '2026-09-05 00:35:39'),
(8, 'Vishmi', 'Silva', 'recept01', 'recept123', 'vishmisilva@gmail.com', '+94773456347', 'receptionist', 'Active', '2026-09-05 01:52:23');

-- --------------------------------------------------------

--
-- Table structure for table `treatments`
--

CREATE TABLE `treatments` (
  `treatment_id` int(11) NOT NULL,
  `treatment_name` varchar(100) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `base_price` decimal(10,2) NOT NULL,
  `duration_minutes` int(11) DEFAULT 30,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `treatments`
--

INSERT INTO `treatments` (`treatment_id`, `treatment_name`, `category`, `description`, `base_price`, `duration_minutes`, `is_active`, `created_at`) VALUES
(1, 'Dental Cleaning', 'Preventive', 'Professional teeth cleaning and polishing', 2500.00, 45, 1, '2026-08-20 23:29:27'),
(2, 'Cavity Filling', 'Restorative', 'Tooth cavity filling with composite material', 3500.00, 60, 1, '2026-08-20 23:29:27'),
(3, 'Root Canal', 'Endodontic', 'Root canal treatment', 8000.00, 90, 1, '2026-08-20 23:29:27'),
(4, 'Tooth Extraction', 'Oral Surgery', 'Simple tooth extraction', 3000.00, 45, 1, '2026-08-20 23:29:27'),
(5, 'Crown Placement', 'Restorative', 'Dental crown placement', 12000.00, 90, 1, '2026-08-20 23:29:27'),
(6, 'Braces Installation', 'Orthodontic', 'Orthodontic braces installation', 25000.00, 120, 1, '2026-08-20 23:29:27'),
(7, 'Teeth Whitening', 'Cosmetic', 'Professional teeth whitening', 5000.00, 60, 1, '2026-08-20 23:29:27'),
(8, 'Dental Implant', 'Oral Surgery', 'Dental implant placement', 35000.00, 120, 1, '2026-08-20 23:29:27'),
(9, 'Gum Treatment', 'Periodontic', 'Gum disease treatment', 4500.00, 60, 1, '2026-08-20 23:29:27'),
(10, 'Root Canal Retreat', 'Endodontic', 'Root canal retreatment', 9500.00, 90, 1, '2026-08-20 23:29:27'),
(11, 'Wisdom Tooth Removal', 'Oral Surgery', 'Wisdom tooth extraction', 5000.00, 60, 1, '2026-08-20 23:29:27'),
(12, 'Denture Fitting', 'Restorative', 'Complete denture fitting', 15000.00, 90, 1, '2026-08-20 23:29:27');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` varchar(30) DEFAULT 'receptionist',
  `status` varchar(20) DEFAULT 'Active',
  `profile_picture` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_by` int(11) DEFAULT NULL,
  `last_login` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `full_name`, `email`, `phone`, `role`, `status`, `profile_picture`, `created_at`, `created_by`, `last_login`) VALUES
(1, 'admin', 'admin123', 'Dr. Admin User', 'admin@dentalclinic.com', NULL, 'admin', 'Active', NULL, '2026-08-20 23:29:27', NULL, NULL),
(2, 'reception1', 'recep123', 'Jane Reception', 'reception@dentalclinic.com', NULL, 'receptionist', 'Active', NULL, '2026-08-20 23:29:27', NULL, NULL),
(3, 'dentist1', 'dentist123', 'Dr. Sarah Johnson', 'sarah.johnson@dentalclinic.com', NULL, 'dentist', 'Active', NULL, '2026-08-20 23:29:27', NULL, NULL),
(4, 'dentist2', 'dentist123', 'Dr. Michael Chen', 'michael.chen@dentalclinic.com', NULL, 'dentist', 'Active', NULL, '2026-08-20 23:29:27', NULL, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `appointments`
--
ALTER TABLE `appointments`
  ADD PRIMARY KEY (`appointment_id`),
  ADD UNIQUE KEY `appointment_number` (`appointment_number`),
  ADD KEY `patient_id` (`patient_id`),
  ADD KEY `dentist_id` (`dentist_id`),
  ADD KEY `treatment_id` (`treatment_id`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `bills`
--
ALTER TABLE `bills`
  ADD PRIMARY KEY (`bill_id`),
  ADD UNIQUE KEY `invoice_number` (`invoice_number`),
  ADD KEY `idx_bills_appointment_id` (`appointment_id`),
  ADD KEY `idx_bills_patient_id` (`patient_id`),
  ADD KEY `idx_bills_payment_status` (`payment_status`),
  ADD KEY `idx_bills_bill_date` (`bill_date`);

--
-- Indexes for table `dentists`
--
ALTER TABLE `dentists`
  ADD PRIMARY KEY (`dentist_id`),
  ADD UNIQUE KEY `license_number` (`license_number`);

--
-- Indexes for table `patients`
--
ALTER TABLE `patients`
  ADD PRIMARY KEY (`patient_id`);

--
-- Indexes for table `recovery_tokens`
--
ALTER TABLE `recovery_tokens`
  ADD PRIMARY KEY (`token_id`),
  ADD UNIQUE KEY `token` (`token`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `staff`
--
ALTER TABLE `staff`
  ADD PRIMARY KEY (`staff_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `treatments`
--
ALTER TABLE `treatments`
  ADD PRIMARY KEY (`treatment_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `appointments`
--
ALTER TABLE `appointments`
  MODIFY `appointment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `bills`
--
ALTER TABLE `bills`
  MODIFY `bill_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `dentists`
--
ALTER TABLE `dentists`
  MODIFY `dentist_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `patients`
--
ALTER TABLE `patients`
  MODIFY `patient_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `recovery_tokens`
--
ALTER TABLE `recovery_tokens`
  MODIFY `token_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `staff`
--
ALTER TABLE `staff`
  MODIFY `staff_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `treatments`
--
ALTER TABLE `treatments`
  MODIFY `treatment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `appointments`
--
ALTER TABLE `appointments`
  ADD CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`),
  ADD CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`dentist_id`) REFERENCES `dentists` (`dentist_id`),
  ADD CONSTRAINT `appointments_ibfk_3` FOREIGN KEY (`treatment_id`) REFERENCES `treatments` (`treatment_id`),
  ADD CONSTRAINT `appointments_ibfk_4` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `bills`
--
ALTER TABLE `bills`
  ADD CONSTRAINT `bills_ibfk_1` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`),
  ADD CONSTRAINT `bills_ibfk_2` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`);

--
-- Constraints for table `recovery_tokens`
--
ALTER TABLE `recovery_tokens`
  ADD CONSTRAINT `recovery_tokens_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

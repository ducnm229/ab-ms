CREATE TABLE IF NOT EXISTS `customer` (
  `id` int AUTO_INCREMENT  PRIMARY KEY,
  `name` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `mobile_number` varchar(20) NOT NULL,
  `active` boolean NOT NULL,
  `created_at` date NOT NULL,
  `created_by` varchar(20) NOT NULL,
  `updated_at` date DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,

  UNIQUE (`email`),
  UNIQUE (`mobile_number`)
);
 `bda`.CREATE DATABASE `bda` /*!40100 DEFAULT CHARACTER SET utf8 */;

DROP TABLE IF EXISTS `bda`.`delivery_request`;
CREATE TABLE  `bda`.`delivery_request` (
  `delivery_correlation_hash` varchar(255) NOT NULL DEFAULT '',
  `customer_hash` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`delivery_correlation_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `bda`.`delivery_request_item`;
CREATE TABLE  `bda`.`delivery_request_item` (
  `delivery_correlation_hash` varchar(255) NOT NULL DEFAULT '',
  `item_hash` varchar(255) NOT NULL DEFAULT '',
  `quantity` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`delivery_correlation_hash`,`item_hash`),
  CONSTRAINT `FK_delivery_request_items_delivery_correlation_hash` FOREIGN KEY (`delivery_correlation_hash`) REFERENCES `delivery_request` (`delivery_correlation_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `bda`.`order`;
CREATE TABLE  `bda`.`order` (
  `hash` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `bda`.`order_item`;
CREATE TABLE  `bda`.`order_item` (
  `order_hash` varchar(255) NOT NULL DEFAULT '',
  `item_hash` varchar(255) NOT NULL DEFAULT '',
  `quantity` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`order_hash`,`item_hash`),
  CONSTRAINT `FK_order_item_order_hash` FOREIGN KEY (`order_hash`) REFERENCES `order` (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `bda`.`order_saga`;
CREATE TABLE  `bda`.`order_saga` (
  `order_hash` varchar(255) NOT NULL DEFAULT '',
  `customer_hash` varchar(255) DEFAULT NULL,
  `payment_result` varchar(45) DEFAULT NULL,
  `order_result` varchar(45) DEFAULT NULL,
  `saga_result` varchar(45) DEFAULT NULL,
  `version` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`order_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `bda`.`order_saga_item`;
CREATE TABLE  `bda`.`order_saga_item` (
  `order_hash` varchar(255) NOT NULL DEFAULT '',
  `item_hash` varchar(255) NOT NULL DEFAULT '',
  `quantity` int(10) unsigned DEFAULT NULL,
  `price` int(10) unsigned DEFAULT NULL,
  `reserved` tinyint(1) DEFAULT NULL,
  `out_of_stock` tinyint(1) DEFAULT NULL,
  `returned` tinyint(1) DEFAULT NULL,
  `confirmed` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`order_hash`,`item_hash`),
  CONSTRAINT `FK_order_saga_item_order_hash` FOREIGN KEY (`order_hash`) REFERENCES `order_saga` (`order_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `bda`.`stock_item`;
CREATE TABLE  `bda`.`stock_item` (
  `hash` varchar(255) NOT NULL DEFAULT '',
  `in_stock_quantity` int(10) unsigned DEFAULT NULL,
  `version` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `bda`.`stock_item_reservation`;
CREATE TABLE  `bda`.`stock_item_reservation` (
  `item_hash` varchar(255) NOT NULL DEFAULT '',
  `reservation_hash` varchar(255) NOT NULL DEFAULT '',
  `quantity` int(10) unsigned DEFAULT NULL,
  `confirmed` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`item_hash`,`reservation_hash`),
  CONSTRAINT `FK_stock_item_reservation_item_hash` FOREIGN KEY (`item_hash`) REFERENCES `stock_item` (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


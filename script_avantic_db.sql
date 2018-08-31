CREATE DATABASE IF NOT EXISTS db_avant_test;

use db_avant_test;

CREATE TABLE IF NOT EXISTS tb_custumer(
	cus_id VARCHAR(16) PRIMARY KEY,
	cus_name VARCHAR(100) NOT NULL DEFAULT '',
	cus_lastname VARCHAR(150) NOT NULL DEFAULT '',
	cus_email VARCHAR(190) NOT NULL DEFAULT '',
	cus_phone VARCHAR(20) NOT NULL DEFAULT '',
	cus_birthday DATE,
	cus_date_register TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	cus_comment VARCHAR(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tb_user(
	usr_name VARCHAR(10) PRIMARY KEY,
	usr_password VARCHAR(64) NOT NULL,
	usr_email VARCHAR(190) NOT NULL DEFAULT '',
	usr_active TINYINT NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tb_role(
	ro_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	ro_name VARCHAR(35) NOT NULL,
	ro_description VARCHAR(100) NOT NULL DEFAULT ''
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tb_user_role(
	urol_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	ro_id INT UNSIGNED NOT NULL,
	usr_name VARCHAR(10) NOT NULL,
	CONSTRAINT FK_usr_role_usr FOREIGN KEY (usr_name)
		REFERENCES tb_user (usr_name),
	CONSTRAINT FK_user_role_role FOREIGN KEY (ro_id)
		REFERENCES tb_role (ro_id)
) ENGINE=InnoDB;

CREATE USER 'avant_user'@'localhost' IDENTIFIED BY 'P@$5W0rD;2o18!';

GRANT SELECT, INSERT, UPDATE, DELETE
	ON db_avant_test.* TO 'avant_user'@'localhost';
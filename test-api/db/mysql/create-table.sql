-- PROCEDURE
DELIMITER $$

CREATE PROCEDURE `proc_in` (IN `id` INT)  BEGIN
	select * from r_student where student_id = id;
END$$

CREATE PROCEDURE `proc_inout` (INOUT `s` INT)  BEGIN 
	SELECT COUNT(*) INTO s FROM r_student;
END$$

CREATE PROCEDURE `proc_in_out` (IN `i` INT, OUT `s` INT)  BEGIN 
	SELECT COUNT(*) INTO s FROM r_student ;  
END$$

CREATE PROCEDURE `proc_out` (OUT `s` INT)  BEGIN 
	SELECT COUNT(*) INTO s FROM r_student ;  
END$$

CREATE PROCEDURE `proc_return` ()  BEGIN
	SELECT count(*) as c FROM r_student ; 
END$$

CREATE PROCEDURE `proc_return_rs` ()  BEGIN
	SELECT * FROM r_student  limit 1,10; 
    SELECT * FROM r_student  limit 10,10; 
END$$

DELIMITER ;

-- table
CREATE TABLE `r_student` (
  `student_id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `sex` tinyint(1) NOT NULL,
  `birthday` date NOT NULL,
  `birth_time` time NOT NULL,
  `major` smallint(6) NOT NULL,
  `photo` blob,
  `remark` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `r_student` ADD PRIMARY KEY (`student_id`);
ALTER TABLE `r_student` MODIFY `student_id` int(11) NOT NULL AUTO_INCREMENT;

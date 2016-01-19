-- phpMyAdmin SQL Dump
-- version 4.5.3.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: 2016-01-17 16:51:44
-- 服务器版本： 5.7.10-log
-- PHP Version: 5.6.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `test_db`
--

DELIMITER $$
--
-- 存储过程
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_in` (IN `id` INT)  BEGIN

select * from r_student where student_id = id;

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_inout` (INOUT `s` INT)  BEGIN 
	SELECT COUNT(*) INTO s FROM r_student;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_in_out` (IN `i` INT, OUT `s` INT)  BEGIN 
	SELECT COUNT(*) INTO s FROM r_student ;  
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_out` (OUT `s` INT)  BEGIN 
	SELECT COUNT(*) INTO s FROM r_student ;  
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_return` ()  BEGIN
	SELECT count(*) as c FROM r_student ; 
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_return_rs` ()  BEGIN
	SELECT * FROM r_student  limit 1,10; 
    SELECT * FROM r_student  limit 10,10; 
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- 表的结构 `r_student`
--

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

--
-- 转存表中的数据 `r_student`
--

INSERT INTO `r_student` (`student_id`, `name`, `sex`, `birthday`, `birth_time`, `major`, `photo`, `remark`) VALUES
(1, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(2, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(3, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(4, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(5, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(6, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(7, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(8, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(9, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(10, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(11, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(12, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(13, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(14, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(15, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(16, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(17, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(18, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(19, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(20, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(21, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(22, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(23, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(24, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(25, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(26, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(27, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(28, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(29, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(30, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(31, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(32, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(33, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(34, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(35, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(36, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(38, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(39, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(40, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(41, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(42, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(43, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(44, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(45, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(46, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(47, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(48, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(49, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(50, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(51, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(52, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(53, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(54, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL),
(55, 'Jim', 1, '1990-01-01', '01:01:01', 10000, NULL, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `r_student`
--
ALTER TABLE `r_student`
  ADD PRIMARY KEY (`student_id`);

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `r_student`
--
ALTER TABLE `r_student`
  MODIFY `student_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=56;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

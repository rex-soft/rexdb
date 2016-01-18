CREATE TABLE `r_student` (
  `student_id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `sex` tinyint(1) NOT NULL,
  `birthday` date NOT NULL,
  `birth_time` time NOT NULL,
  `major` smallint(6) NOT NULL,
  `photo` blob,
  `remark` text
);

ALTER TABLE `r_student`  MODIFY `student_id` int(11) NOT NULL AUTO_INCREMENT;
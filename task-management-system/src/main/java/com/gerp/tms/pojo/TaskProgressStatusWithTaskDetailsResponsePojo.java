package com.gerp.tms.pojo;

import com.gerp.tms.pojo.response.TaskProgressStatusResponsePojo;
import com.gerp.tms.pojo.response.TaskResponsePojo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskProgressStatusWithTaskDetailsResponsePojo extends TaskProgressStatusResponsePojo {

    private List<TaskResponsePojo> taskList;
}

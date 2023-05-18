package com.gerp.tms.mapper;

import com.gerp.tms.model.project.ProjectPhase;
import com.gerp.tms.pojo.response.MemberDetailsResponsePojo;
import com.gerp.tms.pojo.response.PhaseMemberResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ProjectPhaseMapper {


    ProjectPhase getProjectIdAndPhaseId(@Param("projectId") Integer projectId,@Param("phaseId") Long phaseId);

    List<PhaseMemberResponsePojo> getProjectPhaseMembers(@Param("projectId") Integer projectId, @Param("phaseId") Integer phaseId);

    List<MemberDetailsResponsePojo> getProjectMembers(@Param("projectId") Integer projectId);


    @Select("select pm.id from project_phase pp inner join phase_member pm on pm.project_phase_id = pp.id where pp.project_id = #{projectId} and pp.phase_id= #{phaseId} and pm.member_id = #{memberId} ")
    Long getProjectPhaseMemberId(@Param("projectId") Integer projectId,@Param("phaseId") Integer phaseId,@Param("memberId") Integer memberId);


    @Select("select * from project_phase where phase_id = #{id}")
    List<ProjectPhase> checkPhaseExistInProject(@Param("id") Long id);
}

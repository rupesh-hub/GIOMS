package com.gerp.tms.pojo.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class PhaseMemberResponsePojo extends PhaseResponsePojo {

    private List<MemberDetailsResponsePojo> memberDetailsResponsePojos;

}

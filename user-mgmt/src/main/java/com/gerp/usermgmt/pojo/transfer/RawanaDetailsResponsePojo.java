package com.gerp.usermgmt.pojo.transfer;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawanaDetailsResponsePojo extends RawanaDetailsPojo {
    private TransferSubmissionResponsePojo transferDetails;
}

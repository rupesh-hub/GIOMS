package com.gerp.dartachalani.converter;

import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
import com.gerp.dartachalani.dto.DetailPojo;
import com.gerp.dartachalani.dto.DraftShareDto;
import com.gerp.dartachalani.dto.DraftSharePojo;
import com.gerp.dartachalani.dto.SectionPojo;
import com.gerp.dartachalani.model.draft.share.DraftShare;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DtoConverter {

    public static DetailPojo convert(SectionPojo sectionPojo) {
        if (sectionPojo == null)
            return null;
        DetailPojo detailPojo = new DetailPojo();
        detailPojo.setCode(sectionPojo.getId().toString());
        detailPojo.setNameEn(sectionPojo.getNameEn());
        detailPojo.setNameNp(sectionPojo.getNameNp());
        return detailPojo;
    }

    public static DraftShare convert(DraftShareDto draftShareDto) {
        if (draftShareDto == null)
            return null;
        DraftShare draftShare = new DraftShare();
        draftShare.setReceiverPisCode(draftShareDto.getReceiverPisCode());
        draftShare.setReceiverSectionCode(draftShareDto.getReceiverSectionCode());
        draftShare.setLetterType(draftShareDto.getLetterType());
        draftShare.setStatus(draftShareDto.getStatus());
        draftShare.setDispatchId(draftShareDto.getDispatchId());
        draftShare.setMemoId(draftShareDto.getMemoId());
        return draftShare;
    }

}

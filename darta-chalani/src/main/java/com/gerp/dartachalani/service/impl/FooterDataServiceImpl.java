package com.gerp.dartachalani.service.impl;

import com.gerp.dartachalani.Proxy.LetterTemplateProxy;
import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
import com.gerp.dartachalani.dto.Bodartha;
import com.gerp.dartachalani.dto.FooterDataDto;
import com.gerp.dartachalani.dto.GeneralTemplate;
import com.gerp.dartachalani.dto.OfficePojo;
import com.gerp.dartachalani.mapper.FooterDataMapper;
import com.gerp.dartachalani.model.FooterData;
import com.gerp.dartachalani.repo.FooterDataRepo;
import com.gerp.dartachalani.service.FooterDataService;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FooterDataServiceImpl extends GenericServiceImpl<FooterData, Long> implements FooterDataService {

    private final FooterDataRepo footerDataRepo;
    private final FooterDataMapper footerDataMapper;
    private final UserMgmtServiceData userMgmtServiceData;

    @Autowired
    private LetterTemplateProxy letterTemplateProxy;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    public FooterDataServiceImpl(FooterDataRepo footerDataRepo,
                                 FooterDataMapper footerDataMapper,
                                 UserMgmtServiceData userMgmtServiceData) {
        super(footerDataRepo);
        this.footerDataRepo = footerDataRepo;
        this.footerDataMapper = footerDataMapper;
        this.userMgmtServiceData = userMgmtServiceData;
    }

    @Override
    public Long saveFooter(FooterDataDto data) {

        FooterDataDto footerDataDto = footerDataMapper.getFooterByOfficeCode(tokenProcessorService.getOfficeCode());

        if (footerDataDto != null && footerDataDto.getId() != null) {
            Optional<FooterData> data1 = footerDataRepo.findById(footerDataDto.getId());
            if (data1.isPresent()) {
                FooterData data2 = data1.get();
                data2.setActive(false);
                footerDataRepo.save(data2);
            }
        }

        FooterData footerData = FooterData.builder()
                .officeCode(tokenProcessorService.getOfficeCode())
                .footer(data.getFooter())
                .build();
        footerDataRepo.save(footerData);

        return footerData.getId();
    }

    @Override
    public Long updateFooter(FooterDataDto data) {
        Optional<FooterData> update = footerDataRepo.findById(data.getId());

        if (!update.isPresent())
            throw new CustomException("Footer does not exist");

        FooterData updateData = update.get();

        FooterData footerData = FooterData.builder()
                .footer(data.getFooter())
                .build();
        footerData.setActive(data.getIsActive());

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(updateData, footerData);
        } catch (Exception e) {
            throw new CustomException("It does not exist");
        }

        footerDataRepo.save(updateData);

        return updateData.getId();
    }

    @Override
    public FooterDataDto getById(Long id, String lang) {
        Optional<FooterData> data = footerDataRepo.findById(id);
        FooterDataDto footerDataDto = new FooterDataDto();
        if (data.isPresent()) {
            FooterData footerData = data.get();
            footerDataDto.setId(footerData.getId());
            footerDataDto.setFooter(footerData.getFooter());
            footerDataDto.setOfficeCode(footerData.getOfficeCode());
            footerDataDto.setIsActive(footerData.getActive());
            footerDataDto.setTemplate(this.getModelTemplate(lang, footerData.getFooter()));
        } else
            throw new CustomException("Not Found");

        return footerDataDto;
    }

    @Override
    public List<FooterDataDto> getByOfficeCode() {
        List<FooterData> data = footerDataRepo.findByOfficeCode(tokenProcessorService.getOfficeCode());
        List<FooterDataDto> footerDataDtos = new ArrayList<>();

        if (data == null || data.isEmpty())
            throw new CustomException("Data Not Found");

        data.forEach(x -> {
            FooterDataDto dataDto = FooterDataDto.builder()
                    .id(x.getId())
                    .footer(x.getFooter())
                    .officeCode(x.getOfficeCode())
                    .isActive(x.isActive())
                    .build();
            footerDataDtos.add(dataDto);
        });

        return footerDataDtos;
    }

    public String getModelTemplate(String lang, String footer) {
        String img = "data:image/png;base64,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";

        OfficePojo officeDetail = userMgmtServiceData.getOfficeDetail(tokenProcessorService.getOfficeCode());
        OfficePojo parentOffice = userMgmtServiceData.getOfficeDetail(officeDetail != null ? officeDetail.getParentCode() : null);

        if (parentOffice != null && parentOffice.getCode().equals("8886"))
            parentOffice = null;

        boolean isEnglish = false;

        String lan = "";
        if (lang.equals("en")) {
            isEnglish = true;
            lan = "EN";
        } else
            lan = "NEP";


        List<Bodartha> bodarthas = new ArrayList<>();

        GeneralTemplate generalTemplate = GeneralTemplate.builder()
                .logo_url(img)
                .organization(isEnglish ? "Government Of Nepal" : "नेपाल सरकार")
                .ministry(getOfficeDetails(officeDetail, isEnglish, false))
                .department(getOfficeDetails(parentOffice, isEnglish, false))
                .address_top(getOfficeDetails(officeDetail, isEnglish, true))
                .letter_date("")
                .section_header("")
                .chali_no("")
                .letter_no("")
                .request_to_office("")
                .request_to_office_address("")
                .body_message("")
                .subject("")
                .requester_name("...")
                .requester_position("")
                .bodartha(bodarthas)
                .bodartha_karyartha(bodarthas)
                .saadar_awagataartha(bodarthas)
                .resource_type("C")
                .resource_id(1L)
                .footer(footer)
                .build();

        return letterTemplateProxy.getGeneralTemplate(generalTemplate, lan);
    }

    private String getOfficeDetails(OfficePojo officeDetail, boolean isEnglish, boolean isAddress) {
        if (officeDetail == null) {
            return " ";
        }
        if(isEnglish) {
           return isAddress ? getStringPascalCase(officeDetail.getAddressEn()) : getStringPascalCase(officeDetail.getNameEn());
        }
        return isAddress ? officeDetail.getAddressNp() : officeDetail.getNameNp();
    }

    private String getStringPascalCase(String name) {

        if (name == null || name.length() == 0)
            return name;

        String lowerCaseString = name.toLowerCase();
        return Arrays.stream(lowerCaseString.split(" "))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));

    }

    @Override
    public Long toggleActive(Long id) {
        footerDataRepo.setIsActiveByOfficeCode(tokenProcessorService.getOfficeCode());
        footerDataRepo.softDeleteFooterById(id);
        return id;
    }

}

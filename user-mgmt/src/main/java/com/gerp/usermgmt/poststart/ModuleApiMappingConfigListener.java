package com.gerp.usermgmt.poststart;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gerp.usermgmt.converter.ModuleApiMappingConverter;
import com.gerp.usermgmt.enums.AuthMode;
import com.gerp.usermgmt.mapper.ModuleApiMappingMapper;
import com.gerp.usermgmt.model.auth.ApiMappingLog;
import com.gerp.usermgmt.pojo.auth.ModuleApiMappingPojo;
import com.gerp.usermgmt.repo.auth.ApiMappingLogRepo;
import com.gerp.usermgmt.repo.auth.ModuleApiMappingRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ModuleApiMappingConfigListener {

    @Autowired
    ModuleApiMappingMapper moduleApiMappingMapper;
    @Autowired
    ModuleApiMappingRepo moduleApiMappingRepo;
    @Autowired
    ApiMappingLogRepo apiMappingLogRepo;
    @Autowired
    ModuleApiMappingConverter moduleApiMappingConverter;

    @Value("${authorization-mode}")
    private String authMode;


    @EventListener
//    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            if (authMode.equals(AuthMode.WRITE.toString())) authWriteMode(); else authReadMode();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    void authWriteMode() throws JsonProcessingException {
        log.info("\n -------executing write mode of authorization--------");
        ObjectMapper oj = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectWriter ow = oj.
                writer().withDefaultPrettyPrinter();
        List<ModuleApiMappingPojo> dataPojo = moduleApiMappingMapper.getAllAPIMapping();
        String jsonData = ow.writeValueAsString(dataPojo);
//        ApiMappingLog apiMappingPreviousLog = apiMappingLogRepo.getApiMappingLogByCreatedDateAndIsActive();
//        || (!apiMappingPreviousLog.getData().equals(jsonData))
//        if((apiMappingPreviousLog == null) ) {
            ApiMappingLog apiMappingLog = ApiMappingLog.builder()
                    .data(jsonData)
                    .build();
            apiMappingLogRepo.save(apiMappingLog);
//        } else {
//            log.info("\n---------------------------------No change in Mapping ---------------------");
//        }
    }

    private void authReadMode() throws JsonProcessingException {
        log.info("\n -------executing read mode of authorization--------");
        ObjectReader or = new ObjectMapper().readerFor(ModuleApiMappingPojo.class);
        ApiMappingLog apiMappingLog = apiMappingLogRepo.getApiMappingLogByCreatedDateAndIsActive();
        if (apiMappingLog != null) {
            log.info("-------mapping data found.. \n writing data mapping--------");
            List<ModuleApiMappingPojo> moduleApiMappings = parseJsonString(apiMappingLog.getData());
            moduleApiMappings.stream().forEach(mapping -> {
                try {
                    if(mapping.getModuleKey() == null || mapping.getPrivilegeKey() == null) {
                        System.out.println(mapping.getModuleKey());
                    }
                    moduleApiMappingMapper.insertModuleApiMapping
                            (mapping.getApi(), mapping.getModuleKey(),
                                    mapping.getPrivilegeKey(),
                                    mapping.getMethod().toString(), mapping.getIsActive());
                } catch (Exception exception) {
                    exception.printStackTrace();
                    log.error("error for module: " + mapping.getModuleKey() + "\n api:" + mapping.getApi()
                            + "\n method: " + mapping.getMethod());
                }
            });
            apiMappingLog.setIsActive(Boolean.FALSE);
            apiMappingLogRepo.save(apiMappingLog);
        }

    }

//    public void mapModuleMappingFromjson() {
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            // get path from yml
//            File resource = new ClassPathResource("auth/apimapping.json").getFile();
//            String data = new String(Files.readAllBytes(resource.toPath()));
//            List<AuthMap> authDatas = mapper.readValue(data, new TypeReference<List<AuthMap>>() {
//            });
//            authDatas.parallelStream().forEach(apis ->
//                    apis.getApis().parallelStream().forEach(apiDetail
//                                    ->
//                            {
//                                try {
//                                    moduleApiMappingMapper.insertModuleApiMapping
//                                            (apiDetail.getName(), apis.getModuleKey(),
//                                                    apiDetail.getPrivilege().toString(),
//                                                    apiDetail.getMethod().toString());
//                                } catch (Exception exception) {
//                                    exception.printStackTrace();
//                                    log.error("error for module: " + apis.getModuleKey() + "\n api:" + apiDetail.getName()
//                                            + "\n method: " + apiDetail.getMethod());
//                                }
//
//                            }
//
//                    ));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    List<ModuleApiMappingPojo> parseJsonString(String data) throws JsonProcessingException {
        ObjectMapper o = new ObjectMapper();
        JsonNode jsonNode = o.readTree(data);
        if(jsonNode.isArray()) {
            TypeReference<List<ModuleApiMappingPojo>> typeReference = new TypeReference<List<ModuleApiMappingPojo>>(){};

            return Arrays.asList(o.readValue(data, ModuleApiMappingPojo[].class));
        } else {
            log.error("unable to parse json data for mapping");
            return Collections.emptyList();
        }
    }
}

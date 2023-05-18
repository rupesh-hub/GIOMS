//package com.gerp.usermgmt.pisconfig.schedule;
//
//import com.gerp.shared.enums.BloodGroup;
//import com.gerp.shared.enums.MaritalStatus;
//import com.gerp.usermgmt.model.address.Country;
//import com.gerp.usermgmt.model.address.District;
//import com.gerp.usermgmt.model.address.DistrictVdc;
//import com.gerp.usermgmt.model.address.MunicipalityVdc;
//import com.gerp.usermgmt.model.deisgnation.DesignationDetail;
//import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
//import com.gerp.usermgmt.model.employee.*;
//import com.gerp.usermgmt.model.office.Office;
//import com.gerp.usermgmt.model.office.OfficeCategory;
//import com.gerp.usermgmt.model.office.OrganizationLevel;
//import com.gerp.usermgmt.pisconfig.repo.PisDataLoader;
//import com.gerp.usermgmt.repo.designation.DesignationDetailRepo;
//import com.gerp.usermgmt.repo.employee.EducationLevelRepo;
//import com.gerp.usermgmt.repo.employee.EmployeeRepo;
//import com.gerp.usermgmt.repo.employee.FacultyRepo;
//import com.gerp.usermgmt.repo.office.OfficeRepo;
//import com.gerp.usermgmt.services.organization.Location.CountryService;
//import com.gerp.usermgmt.services.organization.Location.DistrictService;
//import com.gerp.usermgmt.services.organization.Location.MunicipalityVdcService;
//import com.gerp.usermgmt.services.organization.designation.FunctionalDesignationService;
//import com.gerp.usermgmt.services.organization.designation.PositionService;
//import com.gerp.usermgmt.services.organization.employee.EmployeeServiceStatusService;
//import com.gerp.usermgmt.services.organization.employee.EmployeeServiceTypeService;
//import com.gerp.usermgmt.services.organization.employee.ReligionService;
//import com.gerp.usermgmt.services.organization.employee.ServiceGroupService;
//import com.gerp.usermgmt.services.organization.office.OfficeCategoryService;
//import com.gerp.usermgmt.services.organization.office.OfficeLevelService;
//import com.gerp.usermgmt.services.organization.office.OfficeService;
//import lombok.extern.slf4j.Slf4j;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.util.ObjectUtils;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Component
//@Slf4j
//public class PisDataSetScheduler {
//
//	@Autowired
//	PisDataLoader pisDataLoader;
//
//	@Autowired
//	ModelMapper mapper;
//
////	@Autowired
////	private EmployeeService employeeService;
//
//	@Autowired
//	private DistrictService districtService;
//
//	@Autowired
//	private MunicipalityVdcService municipalityVdcService;
//
//	@Autowired
//	private CountryService countryService;
//
//	@Autowired
//	private ReligionService religionService;
//
//	@Autowired
//	private OfficeCategoryService officeCategoryService;
//
//	@Autowired
//	private OfficeLevelService officeLevelService;
//
//	@Autowired
//	private FunctionalDesignationService designationService;
//
//	@Autowired
//	private PositionService positionService;
//
//	@Autowired
//	private EmployeeServiceStatusService serviceStatusService;
//
//	@Autowired
//	private EmployeeServiceTypeService serviceTypeService;
//
//	@Autowired
//	private ServiceGroupService serviceGroupService;
//
//	@Autowired
//	private OfficeService officeService;
//	@Autowired
//	private OfficeRepo officeRepo;
//
//	@Autowired
//	private EmployeeRepo employeeRepo;
//
//	@Autowired
//	private DesignationDetailRepo designationDetailRepo;
//
//	@Autowired
//	private EducationLevelRepo educationLevelRepo;
//
//	@Autowired
//	private FacultyRepo facultyRepo;
//
//
//	@Scheduled(fixedRate = 57000000)
//	public void insertPisData() {
//		System.out.println("data insert process starting----");
//		log.info("data insert process started");
////		this.saveCountry();
////		this.saveDistrict();
////		this.saveMunicipalityVdc();
////		this.saveOfficeCategory();
////		this.saveOrganizationLevel();
////		this.savePosition();
////		this.saveDesignations();
////		this.saveServiceStatus();
////		this.saveServiceType();
////		this.saveService();
////		this.saveReigion();
////		this.saveOffice();
//
////		this.saveEmployee();
////		this.saveDesignationDetail();
//
//	}
//
//	private LocalDate getMaxOfficeCreateDate() {
//		LocalDate date = officeRepo.getOfficeCreateDate();
//		return  date == null ? LocalDate.now() : date;
//	}
//
//	boolean checkDataExist(String pisCode, List<String> insertedPisCode){
//		return insertedPisCode.stream().anyMatch(pisCode::equals);
//	}
//
//	void saveEmployee(){
//		//		// calling data from oracle
//		try {
//			List<String> insertedPis = employeeRepo.insertedPisCodes();
////			log.info(String.valueOf(insertedPis.size()));
//			Long count = pisDataLoader.countEmployee();
//			System.out.println(count);
//
//			List<Map<String, Object>> filteredData = null;
//			for(int i =0; i < count; i+= 100){
//				List<Map<String, Object>> data = pisDataLoader.findEmployee(i ,i+100);
//				log.info("prev data size:" +data.size());
//				log.info("filter started time"+ new Date());
//				data = data.parallelStream().parallel().filter(map -> !checkDataExist(map.get("pis_code").toString(), insertedPis)).collect(Collectors.toList());
//				log.info("end time"+ new Date());
//				log.info("count " + i);
//				log.info("after data size:" +data.size());
//				List<Employee> employeeList = new ArrayList<>();
//			data.forEach(e -> {
//
//				Employee employee = new Employee();
//				mapper.map(e, employee);
////					if (!employeeRepo.existsByPisCode(employee.getPisCode())) {
//				if(e.get("blood_group") != null) {
//					employee.setBloodGroup(BloodGroup.getEnumFromCode(e.get("blood_group").toString()));
//				}
//				if(e.get("marital_status") != null) {
//					employee.setMaritalStatus(MaritalStatus.getEnumFromCode(e.get("marital_status").toString()));
//				}
////			employee service status
//				if(e.get("employee_service_status_code") != null) {
//					EmployeeServiceStatus employeeServiceStatus = new EmployeeServiceStatus();
//					employeeServiceStatus.setCode(e.get("employee_service_status_code").toString());
//					employee.setEmployeeServiceStatus(employeeServiceStatus);
//				}
//
//				if(e.get("app_service_status_code") != null) {
//					EmployeeServiceStatus appEmployeeServiceStatus = new EmployeeServiceStatus();
//					appEmployeeServiceStatus.setCode(e.get("app_service_status_code").toString());
//					employee.setAppEmployeeServiceStatus(appEmployeeServiceStatus);
//				}
//
//				//position
//				if(e.get("position_code") != null) {
//					Position position = new Position();
//					position.setCode(e.get("position_code").toString());
//					employee.setPosition(position);
//				}
//				if(e.get("app_position_code") != null) {
//					Position appPosition = new Position();
//					appPosition.setCode(e.get("app_position_code").toString());
//					employee.setAppPosition(appPosition);
//				}
//
//				//FunctionalDesignation
//				if(e.get("designation_code") != null) {
//					FunctionalDesignation designation = new FunctionalDesignation();
//					designation.setCode(e.get("designation_code").toString());
//					employee.setDesignation(designation);
//				}
//				if(e.get("app_designation_code") != null) {
//					FunctionalDesignation appDesignation = new FunctionalDesignation();
//					appDesignation.setCode(e.get("app_designation_code").toString());
//					employee.setAppDesignation(appDesignation);
//				}
//
//				// office
//				if(e.get("office_code") != null) {
//					Office office = new Office();
//					office.setCode(e.get("office_code").toString());
//					employee.setOffice(office);
//				}
//
//				// service
//				if(e.get("service_code") != null) {
//					Service service = new Service();
//					service.setCode(e.get("service_code").toString());
//					employee.setService(service);
//				}
//				if(e.get("app_service_code") != null) {
//					Service appService = new Service();
//					appService.setCode(e.get("app_service_code").toString());
//					employee.setAppService(appService);
//				}
//
//				//religion
//				if(e.get("religion_code") != null) {
//					Religion religion = new Religion();
//					religion.setCode(e.get("religion_code").toString());
//					employee.setReligion(religion);
//				}
//
//				//service type
//				if(e.get("employee_service_type_code") != null) {
//					EmployeeServiceType employeeServiceType = new EmployeeServiceType();
//					employeeServiceType.setCode(e.get("employee_service_type_code").toString());
//					employee.setEmployeeServiceType(employeeServiceType);
//				}
//
//				if(e.get("app_service_type_code") != null) {
//					EmployeeServiceType employeeServiceType = new EmployeeServiceType();
//					employeeServiceType.setCode(e.get("app_service_type_code").toString());
//					employee.setAppEmployeeServiceType(employeeServiceType);
//				}
//
//				if(e.get("per_district_code") != null) {
//					District perDistrict = new District();
//					perDistrict.setCode(e.get("per_district_code").toString());
//					employee.setPermanentDistrict(perDistrict);
//				}
//
//				if(e.get("temp_district_code") != null) {
//					District tempDistrict = new District();
//					tempDistrict.setCode(e.get("temp_district_code").toString());
//					employee.setTemporaryDistrict(tempDistrict);
//				}
//
//				if(e.get("temp_municipality_vdc") != null) {
//					MunicipalityVdc tempMunicipalityVdc = new MunicipalityVdc();
//					tempMunicipalityVdc.setCode(e.get("temp_municipality_vdc").toString());
//					employee.setTemporaryMunicipalityVdc(tempMunicipalityVdc);
//				}
//
//				if(e.get("per_municipality_vdc") != null) {
//					MunicipalityVdc perMunicipalityVdc = new MunicipalityVdc();
//					perMunicipalityVdc.setCode(e.get("per_municipality_vdc").toString());
//					employee.setPermanentMunicipalityVdc(perMunicipalityVdc);
//				}
//
////					persistEmployee(employee);
//
//
////					}
//					employeeList.add(employee);
//				});
//				log.info("mapping time"+ new Date());
//
//				employeeRepo.saveAll(employeeList);
//				}
//
//		} catch (Exception ex){
//			ex.printStackTrace();
//			throw  new RuntimeException();
//		}
//	}
//
//	@Async
//	void persistEmployee(Employee employee) {
//		System.out.println("persistence starting "+employee.getPisCode());
//		employeeRepo.save(employee);
//	}
//
//	void saveReigion(){
//		log.info("------saving religion-------");
//		List<Map<String, Object>> data = pisDataLoader.findReligion();
//		log.info("size of Religion: " + data.size());
//		data.forEach(e -> {
//			Religion religion = new Religion();
//			mapper.map(e, religion);
//			log.info("code:" + religion.getCode());
//			religionService.create(religion);
//		});
//	}
//	void saveCountry(){
//		log.info("------saving country-------");
//		List<Map<String, Object>> data = pisDataLoader.findCountry();
//		log.info("size of country: " + data.size());
//		data.forEach(e -> {
//			Country country = new Country();
//			mapper.map(e, country);
//			log.info("code:" + country.getCode());
//			countryService.create(country);
//		});
//	}
//
//	void saveDistrict(){
//		log.info("saving district");
//				List<Map<String, Object>> data = pisDataLoader.findDistrict();
//		System.out.println("size of data: " + data.size());
//		data.forEach(e -> {
//			District district = new District();
//			mapper.map(e, district);
//			log.info("code:" + district.getCode());
//			districtService.create(district);
//		});
//		// mapping started
////		List<District> districts = mapper.map(data, new TypeToken<List<Employee>>() {}.getType());
////		System.out.println("after mapping size value : " + districts.size());
////		districtService.saveMany(districts);
//	}
//
//	void saveMunicipalityVdc(){
//		log.info("------saving municipality-------");
//		List<Map<String, Object>> data = pisDataLoader.findMunicipality();
//		log.info("size of municipality: " + data.size());
//		data.forEach(e -> {
//			MunicipalityVdc municipalityVdc = new MunicipalityVdc();
//			mapper.map(e, municipalityVdc);
//			log.info("code:" + municipalityVdc.getCode());
//			municipalityVdcService.create(municipalityVdc);
//		});
//	}
//
//	void saveOfficeCategory(){
//		log.info("--------saving office category------------");
//		List<Map<String, Object>> data = pisDataLoader.findOfficeCategory();
//		log.info("size of office category: " + data.size());
//		data.forEach(e -> {
//			OfficeCategory officeCategory = new OfficeCategory();
//			mapper.map(e, officeCategory);
//			log.info("code:" + officeCategory.getCode());
//			officeCategoryService.create(officeCategory);
//		});
//	}
//
//	void saveOrganizationLevel(){
//		log.info("--------saving office level------------");
//		List<Map<String, Object>> data = pisDataLoader.findOrganizationLevel();
//		log.info("size of office level: " + data.size());
//		data.forEach(e -> {
//			OrganizationLevel organizationLevel = new OrganizationLevel();
//			mapper.map(e, organizationLevel);
//			log.info("code:" + organizationLevel.getCode());
//			officeLevelService.create(organizationLevel);
//		});
//	}
//
//	void saveDesignations(){
//		log.info("--------saving Designations------------");
//		List<Map<String, Object>> data = pisDataLoader.findDesignation();
//		log.info("size of Designation: " + data.size());
//		data.forEach(e -> {
//			FunctionalDesignation functionalDesignation = new FunctionalDesignation();
//			mapper.map(e, functionalDesignation);
//			log.info("code:" + functionalDesignation.getCode());
//			designationService.create(functionalDesignation);
//		});
//	}
//
//	void savePosition(){
//		log.info("--------saving position------------");
//		List<Map<String, Object>> data = pisDataLoader.findPosition();
//		log.info("size of position: " + data.size());
//		data.forEach(e -> {
//			Position position = new Position();
//			if(!ObjectUtils.isEmpty(e.get("parent_position_code"))){
//				Position parent = new Position();
//				parent.setCode((String) e.get("parent_position_code"));
//				position.setParent(parent);
//			}
//			mapper.map(e, position);
//			log.info("code:" + position.getCode());
//			positionService.create(position);
//		});
//	}
//
//	void saveServiceStatus(){
//		log.info("--------saving service status------------");
//		List<Map<String, Object>> data = pisDataLoader.findServiceStatus();
//		log.info("size of service status: " + data.size());
//		data.forEach(e -> {
//			EmployeeServiceStatus employeeServiceStatus = new EmployeeServiceStatus();
//			mapper.map(e, employeeServiceStatus);
//			log.info("code:" + employeeServiceStatus.getCode());
//			serviceStatusService.create(employeeServiceStatus);
//		});
//	}
//
//	void saveServiceType(){
//		log.info("--------saving service type------------");
//		List<Map<String, Object>> data = pisDataLoader.findServiceType();
//		log.info("size of service type: " + data.size());
//		data.forEach(e -> {
//			EmployeeServiceType employeeServiceType = new EmployeeServiceType();
//			mapper.map(e, employeeServiceType);
//			log.info("code:" + employeeServiceType.getCode());
//			serviceTypeService.create(employeeServiceType);
//		});
//	}
//
//	void saveService(){
//		log.info("--------saving service------------");
//		List<Map<String, Object>> data = pisDataLoader.findService();
//		log.info("size of service : " + data.size());
//		data.forEach(e -> {
//			Service service = new Service();
//			mapper.map(e, service);
//			if(e.get("parent_code") != null){
//				System.out.println("parent" + e.get("parent_code"));;
//				Service parent = new Service();
//				parent.setCode(e.get("parent_code").toString());
//				service.setParent(parent);
//			}
//			log.info("code:" + service.getCode());
//			serviceGroupService.create(service);
//		});
//	}
//
//	void saveOffice(){
//		log.info("--------saving office------------");
//		LocalDate maxOfficeCreateDate = getMaxOfficeCreateDate();
//		List<Map<String, Object>> data = pisDataLoader.findOffice();
//		log.info("size of office : " + data.size());
//
//		data.parallelStream().forEach(e -> {
////			if(officeRepo.existsByCode(e.get("code").toString())) {
////
////			} else {
//				Office office = new Office();
//				mapper.map(e, office);
//				if (e.get("parent_code") != null) {
//					Office parent = new Office();
//					parent.setCode(e.get("parent_code").toString());
//					office.setParent(parent);
//				}
//				office.setOfficeCategory(null);
////			System.out.println(!e.get("office_category_code").toString().equals("0") + e.get("office_category_code").toString());
////			if(e.get("office_category_code") != null && !e.get("office_category_code").toString().equals("0")){
////
////			}
//
//
//				if (e.get("organization_level_code ") != null) {
//					OrganizationLevel organizationLevel = new OrganizationLevel();
//					organizationLevel.setCode(e.get("organization_level_code ").toString());
//					office.setOrganizationLevel(organizationLevel);
//				}
//				if (e.get("municipality_vdc_code ") != null) {
//					MunicipalityVdc municipalityVdc = new MunicipalityVdc();
//					municipalityVdc.setCode(e.get("municipality_vdc_code ").toString());
//					office.setMunicipalityVdc(municipalityVdc);
//				}
//				if (e.get("district_code ") != null) {
//					District district = new District();
//					district.setCode(e.get("district_code").toString());
//					office.setDistrict(district);
//				}
//				if (e.get("country_code ") != null) {
//					Country country = new Country();
//					country.setCode(e.get("country_code").toString());
//					office.setCountry(country);
//				}
//				log.info("saving office code:" + office.getCode());
//				if (!officeRepo.existsByCode(office.getCode())) {
//					officeRepo.save(office);
//				}
//
//		});
//
//	}
//
//	public void saveDesignationDetail(){
//		log.info("--------saving designation detail------------");
//		List<Map<String, Object>> data = pisDataLoader.findDesignationDetail();
//		log.info("size of designation detail : " + data.size());
//
//		data.parallelStream().forEach(e -> {
//			DesignationDetail designationDetail = new DesignationDetail();
//			mapper.map(e, designationDetail);
//			if(designationDetailRepo.findDesignationDetail(designationDetail.getService().getServiceTypeCode(),
//					designationDetail.getPosition().getCode(), designationDetail.getDesignation().getCode()) == null)
//			designationDetailRepo.save(designationDetail);
//		});
//	}
//
//	public void saveEducationLevel(){
//		log.info("--------saving education level------------");
//		List<Map<String, Object>> data = pisDataLoader.findEducationLevels();
//		log.info("size of education level : " + data.size());
//
//		data.parallelStream().forEach(e -> {
//			EducationLevel educationLevel = new EducationLevel();
//			mapper.map(e, educationLevel);
//			if(e.get("parent_code") != null) {
//				EducationLevel parent = new EducationLevel();
//				parent.setCode(e.get("parent_code").toString());
//				educationLevel.setParent(parent);
//			}
//			educationLevelRepo.save(educationLevel);
//		});
//	}
//
//	public void saveDistrictMunicipalityVdc(){
//		log.info("--------saving District and municiaplity relation------------");
//		List<Map<String, Object>> data = pisDataLoader.findMunicipalityVdcRelation();
//		log.info("size of District and municiaplity : " + data.size());
//
//		data.parallelStream().forEach(e -> {
//			DistrictVdc districtVdc = new DistrictVdc();
//			mapper.map(e, districtVdc);
////			if(designationDetailRepo.findDesignationDetail(designationDetail.getService().getServiceTypeCode(),
////					designationDetail.getPosition().getCode(), designationDetail.getDesignation().getCode()) == null)
////				designationDetailRepo.save(designationDetail);
//		});
//	}
//
//	public void saveFaculty(){
//		log.info("--------saving District and municiaplity relation------------");
//		List<Map<String, Object>> data = pisDataLoader.findFacultyData();
//		log.info("size of District and municiaplity : " + data.size());
//
//		data.parallelStream().forEach(e -> {
//			Faculty faculty = new Faculty();
//			mapper.map(e, faculty);
//			facultyRepo.save(faculty);
//		});
//	}
//
//
//}
